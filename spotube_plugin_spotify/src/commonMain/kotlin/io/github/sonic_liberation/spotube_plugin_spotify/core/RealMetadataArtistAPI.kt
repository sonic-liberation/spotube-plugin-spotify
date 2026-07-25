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
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.artist.MetadataArtistAPI
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.artist.MetadataArtistOverview
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.common.PaginationResult
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.common.PaginationStrategy
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.common.Thumbnail
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.playlist.MetadataPlaylist
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.track.MetadataTrack
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.user.MetadataUser
import io.github.sonic_liberation.spotify_gql_client.gql.Artist
import io.github.sonic_liberation.spotify_gql_client.gql.ArtistResponseWrapper
import io.github.sonic_liberation.spotify_gql_client.gql.ReleaseItem
import io.github.sonic_liberation.spotify_gql_client.gql.SpotifyGQLClient
import io.github.sonic_liberation.spotify_gql_client.gql.TopTrack
import io.github.sonic_liberation.spotify_gql_client.gql.artist.ArtistResponse

class RealMetadataArtistAPI(val spotifyGQLClient: SpotifyGQLClient) : MetadataArtistAPI {
    override suspend fun getArtist(id: String): MetadataArtist.Detailed {
        val uri = "spotify:artist:$id"
        val response = spotifyGQLClient.artist.getArtist(uri)
            ?: throw Exception("Artist not found: $id")
        return response.toMetadataArtistDetailed()
    }

    override suspend fun artistOverview(id: String): MetadataArtistOverview {
        val uri = "spotify:artist:$id"
        val response = spotifyGQLClient.artist.getArtist(uri)
            ?: throw Exception("Artist not found: $id")

        val artist = response.data.artistUnion.toMetadataArtistDetailed()
        val top10Tracks = response.data.artistUnion.discography?.topTracks?.items
            ?.take(10)?.mapNotNull { it.track?.toMetadataTrack() } ?: emptyList()

        val discography = response.data.artistUnion.discography
        val albums = discography?.popularReleasesAlbums?.items?.map { it.toMetadataAlbumDetailed() } ?: emptyList()

        val relatedArtistsItems = response.data.artistUnion.relatedContent?.relatedArtists?.items?.map {
            MetadataArtist.Basic(
                id = it.uri.removePrefix("spotify:artist:"),
                name = it.profile?.name ?: "",
                thumbnails = it.visuals?.avatarImage?.sources?.map { source ->
                    Thumbnail(source.url, source.height ?: 0, source.width ?: 0)
                }.orEmpty(),
                externalUri = it.uri,
            )
        } ?: emptyList()

        val featuredPlaylistsItems = response.data.artistUnion.relatedContent?.featuringV2?.items?.mapNotNull {
            val playlist = it.data ?: return@mapNotNull null
            MetadataPlaylist(
                id = playlist.uri.removePrefix("spotify:playlist:"),
                title = playlist.name,
                description = playlist.description,
                thumbnails = playlist.images?.items?.firstOrNull()?.sources?.map { source ->
                    Thumbnail(source.url, source.height ?: 0, source.width ?: 0)
                }.orEmpty(),
                trackCount = 0,
                externalUri = playlist.uri,
                owner = if (playlist.ownerV2 != null) MetadataUser(
                    id = playlist.ownerV2?.data?.uri?.removePrefix("spotify:user:") ?: "",
                    username = playlist.ownerV2?.data?.username ?: "",
                    displayName = playlist.ownerV2?.data?.name ?: "",
                    thumbnails = playlist.ownerV2?.data?.avatar?.sources?.map { source ->
                        Thumbnail(source.url, source.height ?: 0, source.width ?: 0)
                    }.orEmpty(),
                    externalUri = playlist.ownerV2?.data?.uri ?: "",
                ) else null,
            )
        } ?: emptyList()

        return MetadataArtistOverview(
            artist = artist,
            top10Tracks = top10Tracks,
            albums = PaginationResult(
                items = albums,
                totalCount = discography?.popularReleasesAlbums?.totalCount ?: albums.size,
                nextPagination = if (albums.isNotEmpty()) PaginationStrategy.Offset(offset = albums.size, limit = 20) else null,
            ),
            relatedArtists = PaginationResult(
                items = relatedArtistsItems,
                totalCount = relatedArtistsItems.size,
                nextPagination = if (relatedArtistsItems.isNotEmpty()) PaginationStrategy.Offset(offset = relatedArtistsItems.size, limit = 20) else null,
            ),
            featuredPlaylists = PaginationResult(
                items = featuredPlaylistsItems,
                totalCount = featuredPlaylistsItems.size,
                nextPagination = if (featuredPlaylistsItems.isNotEmpty()) PaginationStrategy.Offset(offset = featuredPlaylistsItems.size, limit = 20) else null,
            ),
        )
    }

    override suspend fun getArtistTop10Tracks(id: String): List<MetadataTrack> {
        val uri = "spotify:artist:$id"
        val response = spotifyGQLClient.artist.getArtist(uri)
            ?: return emptyList()

        val topTracks =
            response.data.artistUnion.discography?.topTracks?.items ?: return emptyList()
        return topTracks.take(10).mapNotNull { it.track?.toMetadataTrack() }
    }

    override suspend fun relatedArtists(
        id: String,
        pagination: PaginationStrategy?
    ): PaginationResult<MetadataArtist.Basic> {
        val paging = pagination.getOffsetOrDefault()
        val uri = "spotify:artist:$id"
        val response = spotifyGQLClient.artist.relatedArtists(uri)
            ?: return PaginationResult(emptyList(), 0, null)

        val allItems = response.data.artistUnion.relatedContent?.relatedArtists?.items?.map {
            MetadataArtist.Basic(
                id = it.uri.removePrefix("spotify:artist:"),
                name = it.profile?.name ?: "",
                thumbnails = it.visuals?.avatarImage?.sources?.map { source ->
                    Thumbnail(source.url, source.height ?: 0, source.width ?: 0)
                }.orEmpty(),
                externalUri = it.uri,
            )
        } ?: emptyList()

        val paginatedItems = allItems.drop(paging.offset).take(paging.limit)

        return PaginationResult(
            items = paginatedItems,
            totalCount = allItems.size,
            nextPagination = paging.nextOffset(paginatedItems.size),
        )
    }

    override suspend fun featuredPlaylists(
        id: String,
        pagination: PaginationStrategy?
    ): PaginationResult<MetadataPlaylist> {
        val paging = pagination.getOffsetOrDefault()
        val uri = "spotify:artist:$id"
        val response = spotifyGQLClient.artist.featuredPlaylists(uri)
            ?: return PaginationResult(emptyList(), 0, null)

        val allItems = response.data.artistUnion.relatedContent?.featuringV2?.items?.mapNotNull {
            val playlist = it.data ?: return@mapNotNull null
            MetadataPlaylist(
                id = playlist.uri.removePrefix("spotify:playlist:"),
                title = playlist.name,
                description = playlist.description,
                thumbnails = playlist.images?.items?.firstOrNull()?.sources?.map { source ->
                    Thumbnail(source.url, source.height ?: 0, source.width ?: 0)
                }.orEmpty(),
                trackCount = 0,
                externalUri = playlist.uri,
                owner = if (playlist.ownerV2 != null) MetadataUser(
                    id = playlist.ownerV2?.data?.uri?.removePrefix("spotify:user:") ?: "",
                    username = playlist.ownerV2?.data?.username ?: "",
                    displayName = playlist.ownerV2?.data?.name ?: "",
                    thumbnails = playlist.ownerV2?.data?.avatar?.sources?.map { source ->
                        Thumbnail(source.url, source.height ?: 0, source.width ?: 0)
                    }.orEmpty(),
                    externalUri = playlist.ownerV2?.data?.uri ?: "",
                ) else null,
            )
        } ?: emptyList()

        val paginatedItems = allItems.drop(paging.offset).take(paging.limit)

        return PaginationResult(
            items = paginatedItems,
            totalCount = allItems.size,
            nextPagination = paging.nextOffset(paginatedItems.size),
        )
    }

    override suspend fun getArtistAlbums(
        id: String,
        pagination: PaginationStrategy?
    ): PaginationResult<MetadataAlbum.Detailed> {
        val paging = pagination.getOffsetOrDefault()
        val uri = "spotify:artist:$id"
        val response = spotifyGQLClient.artist.discographyAll(
            uri = uri,
            offset = paging.offset,
            limit = paging.limit,
        ) ?: return PaginationResult(emptyList(), 0, null)

        val discography = response.data.artistUnion.discography?.all
            ?: return PaginationResult(emptyList(), 0, null)

        val items = discography.items.flatMap { it.releases?.items ?: emptyList() }
            .map { it.toMetadataAlbumDetailed() }
        val totalCount = discography.totalCount

        // Spotify's API for artist albums doesn't support pagination in the traditional sense.
        return PaginationResult(
            items = items,
            totalCount = totalCount,
            nextPagination = null,
        )
    }

    override suspend fun savedArtists(pagination: PaginationStrategy?): PaginationResult<MetadataArtist.Detailed> {
        val paging = pagination.getOffsetOrDefault()
        val response = spotifyGQLClient.library.artists(
            offset = paging.offset,
            limit = paging.limit,
        ) ?: return PaginationResult(emptyList(), 0, null)

        val library = response.data.me?.libraryV3 ?: return PaginationResult(
            emptyList(),
            0,
            null
        )

        val items = library.items.mapNotNull { libItem ->
            val itemData = when (val itemWrapper = libItem.item) {
                is ArtistResponseWrapper -> itemWrapper.data
                else -> null
            } ?: return@mapNotNull null
            val uri = itemData.uri
            val id = uri.removePrefix("spotify:artist:")

            MetadataArtist.Detailed(
                id = id,
                name = itemData.profile?.name ?: "",
                thumbnails = itemData.visuals?.avatarImage?.sources?.map {
                    Thumbnail(
                        it.url,
                        it.width ?: 0,
                        it.height ?: 0
                    )
                }.orEmpty(),
                externalUri = uri,
                genres = emptyList(),
                biography = null,
                followersCount = null,
            )
        }

        return PaginationResult(
            items = items,
            totalCount = library.totalCount,
            nextPagination = paging.nextOffset(items.size),
        )
    }

    override suspend fun isSavedArtists(ids: List<String>): List<Boolean> {
        if (ids.isEmpty()) return emptyList()
        val uris = ids.map { "spotify:artist:$it" }
        val response = spotifyGQLClient.library.areEntitiesInLibrary(uris)
            ?: return ids.map { false }

        return response.data.lookup.map { it.data?.saved ?: false }
    }

    override suspend fun saveArtists(ids: List<String>) {
        if (ids.isEmpty()) return
        val uris = ids.map { "spotify:artist:$it" }
        spotifyGQLClient.artist.follow(uris)
    }

    override suspend fun removeSavedArtists(ids: List<String>) {
        if (ids.isEmpty()) return
        val uris = ids.map { "spotify:artist:$it" }
        spotifyGQLClient.artist.unfollow(uris)
    }

    private fun ArtistResponse.toMetadataArtistDetailed(): MetadataArtist.Detailed {
        return data.artistUnion.toMetadataArtistDetailed()
    }

    private fun Artist.toMetadataArtistDetailed(): MetadataArtist.Detailed {
        return MetadataArtist.Detailed(
            id = id ?: "",
            name = profile?.name ?: "",
            thumbnails = visuals?.avatarImage?.sources?.map {
                Thumbnail(
                    it.url,
                    it.height ?: 0,
                    it.width ?: 0
                )
            }.orEmpty(),
            externalUri = sharingInfo?.shareUrl,
            genres = emptyList(),
            biography = profile?.biography?.text,
            followersCount = stats?.followers,
        )
    }

    private fun TopTrack.toMetadataTrack(): MetadataTrack {
        val albumData = albumOfTrack ?: return MetadataTrack(
            id = id,
            title = name,
            durationMs = duration?.totalMilliseconds ?: 0,
            trackNumber = trackNumber,
            discNumber = discNumber,
            artists = artists?.items?.map { artist ->
                MetadataArtist.Basic(
                    id = artist.uri.removePrefix("spotify:artist:"),
                    name = artist.profile?.name ?: "",
                    thumbnails = emptyList(),
                    externalUri = artist.uri,
                )
            }.orEmpty(),
            album = MetadataAlbum.Detailed(
                id = "",
                title = "",
                description = null,
                thumbnails = emptyList(),
                albumType = MetadataAlbumType.Album,
                artists = emptyList(),
                externalUri = null,
                releaseDate = null,
                genres = emptyList(),
                trackCount = 0,
            ),
            explicit = contentRating?.label?.let { it != "NONE" } ?: false,
            popularity = null,
            isrcCode = null,
            externalUri = uri,
            thumbnails = null,
        )

        return MetadataTrack(
            id = id,
            title = name,
            durationMs = duration?.totalMilliseconds ?: 0,
            trackNumber = trackNumber,
            discNumber = discNumber,
            artists = artists?.items?.map { artist ->
                MetadataArtist.Basic(
                    id = artist.uri.removePrefix("spotify:artist:"),
                    name = artist.profile?.name ?: "",
                    thumbnails = emptyList(),
                    externalUri = artist.uri,
                )
            }.orEmpty(),
            album = MetadataAlbum.Detailed(
                id = albumData.uri.removePrefix("spotify:album:"),
                title = albumData.name ?: "",
                description = null,
                thumbnails = albumData.coverArt?.sources?.map {
                    Thumbnail(
                        it.url,
                        it.height ?: 0,
                        it.width ?: 0
                    )
                }.orEmpty(),
                albumType = MetadataAlbumType.Album,
                artists = emptyList(),
                externalUri = albumData.uri,
                releaseDate = null,
                genres = emptyList(),
                trackCount = 0,
            ),
            explicit = contentRating?.label?.let { it != "NONE" } ?: false,
            popularity = null,
            isrcCode = null,
            externalUri = uri,
            thumbnails = null,
        )
    }

    private fun ReleaseItem.toMetadataAlbumDetailed(): MetadataAlbum.Detailed {
        return MetadataAlbum.Detailed(
            id = id ?: "",
            title = name ?: "",
            description = null,
            thumbnails = coverArt?.sources?.map { Thumbnail(it.url, it.height ?: 0, it.width ?: 0) }
                .orEmpty(),
            albumType = when (type?.uppercase()) {
                "SINGLE" -> MetadataAlbumType.Single
                "COMPILATION" -> MetadataAlbumType.Collection
                else -> MetadataAlbumType.Album
            },
            artists = artists?.items?.map { artist ->
                MetadataArtist.Basic(
                    id = artist.uri.removePrefix("spotify:artist:"),
                    name = artist.profile?.name ?: "",
                    thumbnails = emptyList(),
                    externalUri = artist.uri,
                )
            }.orEmpty(),
            externalUri = uri,
            releaseDate = date?.isoString,
            genres = emptyList(),
            trackCount = tracks?.totalCount ?: 0,
        )
    }
}
