/*
 * Copyright (C) 2026 Sonic Liberation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */



package io.github.sonic_liberation.spotube_plugin_spotify.core

import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.album.MetadataAlbum
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.album.MetadataAlbumType
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.artist.MetadataArtist
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.browse.MetadataBrowseAPI
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.browse.MetadataBrowseGenre
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.browse.MetadataBrowseItem
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.browse.MetadataBrowseSection
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.common.PaginationResult
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.common.PaginationStrategy
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.common.Thumbnail
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.playlist.MetadataPlaylist
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.track.MetadataTrack
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.user.MetadataUser
import io.github.sonic_liberation.spotify_gql_client.gql.AlbumResponseWrapper
import io.github.sonic_liberation.spotify_gql_client.gql.ArtistResponseWrapper
import io.github.sonic_liberation.spotify_gql_client.gql.BrowseGenericSectionData
import io.github.sonic_liberation.spotify_gql_client.gql.BrowseSectionContainerWrapper
import io.github.sonic_liberation.spotify_gql_client.gql.BrowseSectionItem
import io.github.sonic_liberation.spotify_gql_client.gql.HomeGenericSectionData
import io.github.sonic_liberation.spotify_gql_client.gql.HomeRecentlyPlayedSectionData
import io.github.sonic_liberation.spotify_gql_client.gql.HomeSectionItem
import io.github.sonic_liberation.spotify_gql_client.gql.PlaylistResponseWrapper
import io.github.sonic_liberation.spotify_gql_client.gql.ResponseWrapper
import io.github.sonic_liberation.spotify_gql_client.gql.SpotifyGQLClient
import io.github.sonic_liberation.spotify_gql_client.gql.SpotifyGQLBaseClient
import io.github.sonic_liberation.spotify_gql_client.gql.TrackResponseWrapper

class RealMetadataBrowseAPI(val spotifyGQLClient: SpotifyGQLClient) : MetadataBrowseAPI {
    private val spToken: String
        get() = SpotifyGQLBaseClient.credentials?.cookies?.firstOrNull { it.name == "sp_t" }?.value.orEmpty()

    companion object {
        private val HTML_REGEX = Regex("<[^>]*>")
    }

    /// Descriptions can contain HTML tags, which we want to remove.
    /// Example: `<a href=spotify:playlist:37i9dQZF1EIVLRVklebOIE>Twenty One Pilots</a>, <a href=spotify:playlist:37i9dQZF1EIZGIjaxNcp18>Foster The People</a>, <a href=spotify:playlist:37i9dQZF1EIVZK789cNw1H>Thomas Rhett</a> and more`
    private fun cleanDescription(description: String?): String? {
        return description?.replace(HTML_REGEX, "")
    }

    override suspend fun featured(): List<MetadataBrowseItem> {
        val response = spotifyGQLClient.browse.releases() ?: return emptyList()

        val feed = response.data?.whatsNewFeedItems
        return feed?.items?.mapNotNull { it.content.toBrowseItem() } ?: emptyList()
    }

    override suspend fun genres(): List<MetadataBrowseGenre> {
        val genres = mutableListOf(
            MetadataBrowseGenre(
                "home-genre",
                "All"
            )
        )
        val genresResponse = spotifyGQLClient.browse.browseAll(
            pageOffset = 0,
            pageLimit = 10,
            sectionOffset = 0,
            sectionLimit = 99,
        ) ?: return genres

        val browseItems = genresResponse.data.browseStart.sections?.items?.firstOrNull()?.sectionItems?.items
            ?: return genres

        browseItems.forEach { item ->
            val wrapper = item.content as? BrowseSectionContainerWrapper ?: return@forEach
            val title = wrapper.data?.data?.cardRepresentation?.title?.transformedLabel ?: return@forEach
            val uri = item.uri ?: return@forEach
            genres.add(MetadataBrowseGenre(id = uri, name = title))
        }

        return genres
    }

    override suspend fun list(
        genreId: String,
        pagination: PaginationStrategy?
    ): PaginationResult<MetadataBrowseSection> {
        if (genreId == "home-genre") {
            return homeFeed(pagination)
        }

        val paging = pagination.getOffsetOrDefault()
        val response = spotifyGQLClient.browse.browsePage(
            uri = genreId,
            pageOffset = paging.offset,
            pageLimit = paging.limit,
            sectionOffset = 0,
            sectionLimit = 10,
        ) ?: return PaginationResult(emptyList(), 0, null)

        val sections = response.data.browse.sections?.items ?: return PaginationResult(emptyList(), 0, null)
        val items = sections.mapNotNull { section ->
            val title = (section.data as? BrowseGenericSectionData)?.title?.transformedLabel ?: return@mapNotNull null
            val sectionItems = section.sectionItems?.items?.mapNotNull { it.toBrowseItem() } ?: emptyList()
            MetadataBrowseSection(
                title = title,
                description = null,
                items = sectionItems,
                moreLink = section.uri,
            )
        }

        return PaginationResult(
            items = items,
            totalCount = sections.size,
            nextPagination = null,
        )
    }

    override suspend fun sublist(
        genreId: String,
        sectionId: String,
        pagination: PaginationStrategy?
    ): PaginationResult<MetadataBrowseItem> {
        if (genreId == "home-genre") {
            return homeFeedSection(genreId, sectionId, pagination)
        }

        val paging = pagination.getOffsetOrDefault()
        val response = spotifyGQLClient.browse.browsePage(
            uri = genreId,
            pageOffset = 0,
            pageLimit = 10,
            sectionOffset = 0,
            sectionLimit = 99,
        ) ?: return PaginationResult(emptyList(), 0, null)

        val sections = response.data.browse.sections?.items ?: return PaginationResult(emptyList(), 0, null)
        val targetSection = sections.firstOrNull { it.uri == sectionId }
            ?: return PaginationResult(emptyList(), 0, null)

        val contentItems = targetSection.sectionItems?.items?.mapNotNull { it.toBrowseItem() } ?: emptyList()
        val paginatedItems = contentItems.drop(paging.offset).take(paging.limit)

        return PaginationResult(
            items = paginatedItems,
            totalCount = contentItems.size,
            nextPagination = if (paginatedItems.size < contentItems.size) paging.nextOffset(paginatedItems.size) else null,
        )
    }

    private suspend fun homeFeed(pagination: PaginationStrategy?): PaginationResult<MetadataBrowseSection> {
        val paging = pagination.getOffsetOrDefault()
        val response = spotifyGQLClient.browse.home(
            spT = spToken,
            sectionItemsLimit = paging.limit,
        ) ?: return PaginationResult(emptyList(), 0, null)

        val home = response.data.home
        val sections = home.sectionContainer?.sections?.items ?: return PaginationResult(
            emptyList(),
            0,
            null
        )
        val items = sections.mapNotNull { it.toBrowseSection() }

        return PaginationResult(
            items = items,
            totalCount = sections.size,
            nextPagination = null,
        )
    }

    private suspend fun homeFeedSection(
        genreId: String,
        sectionId: String,
        pagination: PaginationStrategy?
    ): PaginationResult<MetadataBrowseItem> {
        val uri = if (sectionId.startsWith("spotify:")) sectionId else "spotify:section:$sectionId"
        val paging = pagination.getOffsetOrDefault()

        val response = spotifyGQLClient.browse.section(
            uri = uri,
            spT = spToken,
            sectionItemsOffset = paging.offset,
            sectionItemsLimit = paging.limit,
        ) ?: return PaginationResult(emptyList(), 0, null)

        val homeSection = response.data.homeSections
        val firstSection = homeSection.sections.firstOrNull()
            ?: return PaginationResult(emptyList(), 0, null)

        val contentItems =
            firstSection.sectionItems?.items?.mapNotNull { it.content?.toBrowseItem() }.orEmpty()
        val totalCount = contentItems.size

        return PaginationResult(
            items = contentItems,
            totalCount = totalCount,
            nextPagination = paging.nextOffset(contentItems.size),
        )
    }

    private fun HomeSectionItem.toBrowseSection(): MetadataBrowseSection? {
        val titleText = when (data) {
            is HomeGenericSectionData -> (data as HomeGenericSectionData).title?.transformedLabel
                ?: return null

            is HomeRecentlyPlayedSectionData -> (data as HomeRecentlyPlayedSectionData).title?.transformedLabel
                ?: return null

            else -> return null
        }
        val subtitleText = when (data) {
            is HomeGenericSectionData -> (data as HomeGenericSectionData).subtitle?.transformedLabel
            is HomeRecentlyPlayedSectionData -> (data as HomeRecentlyPlayedSectionData).subtitle?.transformedLabel
            else -> null
        }
        val contentItems = sectionItems?.items?.mapNotNull { it.content?.toBrowseItem() }.orEmpty()

        return MetadataBrowseSection(
            title = titleText,
            description = cleanDescription(subtitleText),
            items = contentItems,
            moreLink = uri,
        )
    }

    private fun ResponseWrapper.toBrowseItem(): MetadataBrowseItem? {
        return when (this) {
            is AlbumResponseWrapper -> {
                val uri = data?.uri ?: return null
                val name = data?.name ?: return null
                val thumbnails =
                    data?.coverArt?.sources?.map {
                        Thumbnail(
                            it.url,
                            it.width ?: 0,
                            it.height ?: 0
                        )
                    }
                        .orEmpty()

                MetadataBrowseItem.Album(
                    MetadataAlbum.Basic(
                        id = uri.removePrefix("spotify:album:"),
                        title = name,
                        description = cleanDescription(data?.description),
                        thumbnails = thumbnails,
                        albumType = MetadataAlbumType.Album,
                        artists = data?.artists?.items?.mapNotNull {
                            MetadataArtist.Basic(
                                id = it.uri.removePrefix("spotify:artist:"),
                                name = it.profile?.name ?: return@mapNotNull null,
                                thumbnails = emptyList(),
                                externalUri = it.uri,
                            )
                        } ?: emptyList(),
                        externalUri = uri,
                    )
                )
            }

            is PlaylistResponseWrapper -> {
                val uri = data?.uri ?: return null
                val name = data?.name ?: return null
                val thumbnails =
                    data?.images?.items?.firstOrNull()?.sources?.map {
                        Thumbnail(
                            it.url,
                            it.width ?: 0,
                            it.height ?: 0
                        )
                    }
                        .orEmpty()

                MetadataBrowseItem.Playlist(
                    MetadataPlaylist(
                        id = uri.removePrefix("spotify:playlist:"),
                        title = name,
                        description = cleanDescription(data?.description),
                        thumbnails = thumbnails,
                        trackCount = data?.content?.totalCount ?: 0,
                        externalUri = uri,
                        owner = if (data?.ownerV2 != null) MetadataUser(
                            id = data?.ownerV2?.data?.uri?.removePrefix("spotify:user:") ?: "",
                            username = data?.ownerV2?.data?.username ?: "",
                            displayName = data?.ownerV2?.data?.name ?: "",
                            thumbnails = data?.ownerV2?.data?.avatar?.sources?.map {
                                Thumbnail(
                                    it.url,
                                    it.width ?: 0,
                                    it.height ?: 0
                                )
                            }.orEmpty(),
                            externalUri = data?.ownerV2?.data?.uri ?: "",
                        ) else null,
                    )
                )
            }

            is ArtistResponseWrapper -> {
                val uri = data?.uri ?: return null
                val name = data?.profile?.name ?: return null
                val thumbnails =
                    data?.visuals?.avatarImage?.sources?.map {
                        Thumbnail(
                            it.url,
                            it.width ?: 0,
                            it.height ?: 0
                        )
                    }
                        .orEmpty()

                MetadataBrowseItem.Artist(
                    MetadataArtist.Basic(
                        id = uri.removePrefix("spotify:artist:"),
                        name = name,
                        thumbnails = thumbnails,
                        externalUri = uri,
                    )
                )
            }

            is TrackResponseWrapper -> {
                val uri = data?.uri ?: return null
                val name = data?.name ?: return null
                val thumbnails =
                    data?.albumOfTrack?.coverArt?.sources?.map {
                        Thumbnail(
                            it.url,
                            it.width ?: 0,
                            it.height ?: 0
                        )
                    }
                        .orEmpty()
                val albumUri = data?.albumOfTrack?.uri ?: return null
                val albumName = data?.albumOfTrack?.name ?: return null

                MetadataBrowseItem.Track(
                    MetadataTrack(
                        id = uri.removePrefix("spotify:track:"),
                        title = name,
                        artists = data?.artists?.items?.mapNotNull { artist ->
                            val artistUri = artist.uri
                            val artistName = artist.profile?.name ?: return@mapNotNull null

                            MetadataArtist.Basic(
                                id = artistUri.removePrefix("spotify:artist:"),
                                name = artistName,
                                thumbnails = emptyList(),
                                externalUri = artistUri,
                            )
                        }.orEmpty(),
                        album = MetadataAlbum.Detailed(
                            id = albumUri.removePrefix("spotify:album:"),
                            title = albumName,
                            description = null,
                            thumbnails = thumbnails,
                            albumType = MetadataAlbumType.Album,
                            artists = emptyList(),
                            externalUri = albumUri,
                            trackCount = data?.albumOfTrack?.tracks?.totalCount ?: 0,
                            genres = emptyList(),
                            releaseDate = data?.albumOfTrack?.date?.isoString
                        ),
                        externalUri = uri,
                        explicit = data?.contentRating?.label == "EXPLICIT",
                        isrcCode = null,
                        discNumber = data?.discNumber ?: 1,
                        durationMs = data?.duration?.totalMilliseconds
                            ?: data?.trackDuration?.totalMilliseconds ?: 0,
                        popularity = null,
                        trackNumber = data?.trackNumber ?: 0,
                        thumbnails = null,
                    )
                )
            }

            else -> null
        }
    }

    private fun BrowseSectionItem.toBrowseItem(): MetadataBrowseItem? {
        return content?.toBrowseItem()
    }
}
