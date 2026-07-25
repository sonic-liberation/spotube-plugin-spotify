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
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.common.PaginationResult
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.common.PaginationStrategy
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.common.Thumbnail
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.playlist.MetadataPlaylist
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.search.MetadataSearchAPI
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.search.MetadataSearchResult
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.search.MetadataSupportedSearchType
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.track.MetadataTrack
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.user.MetadataUser
import io.github.sonic_liberation.spotify_gql_client.gql.AlbumResponseWrapper
import io.github.sonic_liberation.spotify_gql_client.gql.ArtistResponseWrapper
import io.github.sonic_liberation.spotify_gql_client.gql.PlaylistResponseWrapper
import io.github.sonic_liberation.spotify_gql_client.gql.SpotifyGQLClient
import io.github.sonic_liberation.spotify_gql_client.gql.TrackResponseWrapper
import io.github.sonic_liberation.spotify_gql_client.gql.UserResponseWrapper

class RealMetadataSearchAPI(val spotifyGQLClient: SpotifyGQLClient) : MetadataSearchAPI {

    override val supportedSearchTypes: List<MetadataSupportedSearchType> = listOf(
        MetadataSupportedSearchType.ALL,
        MetadataSupportedSearchType.TRACK,
        MetadataSupportedSearchType.ARTIST,
        MetadataSupportedSearchType.ALBUM,
        MetadataSupportedSearchType.PLAYLIST,
        MetadataSupportedSearchType.USER,
    )

    override suspend fun search(query: String): List<MetadataSearchResult> {
        val response = spotifyGQLClient.search.search(searchTerm = query) ?: return emptyList()

        val results = response.data.searchV2
        val items = mutableListOf<MetadataSearchResult>()

        results.artists?.items?.forEach { searchItem: ArtistResponseWrapper ->
            items.add(
                MetadataSearchResult.Artist(
                    searchItem.toSearchArtist()
                )
            )
        }
        results.tracksV2?.items?.forEach { searchItem ->
            items.add(
                MetadataSearchResult.Track(
                    searchItem.item.toSearchTrack()
                )
            )
        }
        results.albumsV2?.items?.forEach { searchItem: AlbumResponseWrapper ->
            items.add(
                MetadataSearchResult.Album(
                    searchItem.toSearchAlbum()
                )
            )
        }
        results.playlists?.items?.forEach { searchItem: PlaylistResponseWrapper ->
            items.add(
                MetadataSearchResult.Playlist(
                    searchItem.toSearchPlaylist()
                )
            )
        }
        results.users?.items?.forEach { searchItem: UserResponseWrapper ->
            items.add(
                MetadataSearchResult.User(
                    searchItem.toSearchUser()
                )
            )
        }

        return items
    }

    override suspend fun searchTracks(
        query: String, pagination: PaginationStrategy?
    ): PaginationResult<MetadataSearchResult.Track> {
        val paging = pagination.getOffsetOrDefault()
        val response = spotifyGQLClient.search.tracksV2(
            searchTerm = query,
            offset = paging.offset,
            limit = paging.limit,
        ) ?: return PaginationResult(emptyList(), 0, null)

        val searchResult = response.data.searchV2.tracksV2 ?: return PaginationResult(
            emptyList(), 0, null
        )

        val items = searchResult.items.map { MetadataSearchResult.Track(it.item.toSearchTrack()) }
        val totalCount = searchResult.totalCount

        return PaginationResult(
            items = items,
            totalCount = totalCount,
            nextPagination = paging.nextOffset(items.size),
        )
    }

    override suspend fun searchArtists(
        query: String, pagination: PaginationStrategy?
    ): PaginationResult<MetadataSearchResult.Artist> {
        val paging = pagination.getOffsetOrDefault()
        val response = spotifyGQLClient.search.artists(
            searchTerm = query,
            offset = paging.offset,
            limit = paging.limit,
        ) ?: return PaginationResult(emptyList(), 0, null)

        val searchResult = response.data.searchV2.artists ?: return PaginationResult(
            emptyList(), 0, null
        )

        val items = searchResult.items.map { MetadataSearchResult.Artist(it.toSearchArtist()) }
        val totalCount = searchResult.totalCount

        return PaginationResult(
            items = items,
            totalCount = totalCount,
            nextPagination = paging.nextOffset(items.size),
        )
    }

    override suspend fun searchAlbums(
        query: String, pagination: PaginationStrategy?
    ): PaginationResult<MetadataSearchResult.Album> {
        val paging = pagination.getOffsetOrDefault()
        val response = spotifyGQLClient.search.albums(
            searchTerm = query,
            offset = paging.offset,
            limit = paging.limit,
        ) ?: return PaginationResult(emptyList(), 0, null)

        val searchResult = response.data.searchV2.albumsV2 ?: return PaginationResult(
            emptyList(), 0, null
        )

        val items = searchResult.items.mapNotNull {
            if (it.data?.uri?.startsWith("spotify:prerelease") == true) null else
                MetadataSearchResult.Album(it.toSearchAlbum())
        }
        val totalCount = searchResult.totalCount

        return PaginationResult(
            items = items,
            totalCount = totalCount,
            nextPagination = paging.nextOffset(items.size),
        )
    }

    override suspend fun searchPlaylists(
        query: String, pagination: PaginationStrategy?
    ): PaginationResult<MetadataSearchResult.Playlist> {
        val paging = pagination.getOffsetOrDefault()
        val response = spotifyGQLClient.search.playlists(
            searchTerm = query,
            offset = paging.offset,
            limit = paging.limit,
        ) ?: return PaginationResult(emptyList(), 0, null)

        val searchResult = response.data.searchV2.playlists ?: return PaginationResult(
            emptyList(), 0, null
        )

        val items = searchResult.items.map { MetadataSearchResult.Playlist(it.toSearchPlaylist()) }
        val totalCount = searchResult.totalCount

        return PaginationResult(
            items = items,
            totalCount = totalCount,
            nextPagination = paging.nextOffset(items.size),
        )
    }

    override suspend fun searchUsers(
        query: String, pagination: PaginationStrategy?
    ): PaginationResult<MetadataSearchResult.User> {
        val paging = pagination.getOffsetOrDefault()
        val response = spotifyGQLClient.search.users(
            searchTerm = query,
            offset = paging.offset,
            limit = paging.limit,
        ) ?: return PaginationResult(emptyList(), 0, null)

        val searchResult = response.data.searchV2.users ?: return PaginationResult(
            emptyList(), 0, null
        )

        val items = searchResult.items.map { MetadataSearchResult.User(it.toSearchUser()) }
        val totalCount = searchResult.totalCount

        return PaginationResult(
            items = items,
            totalCount = totalCount,
            nextPagination = paging.nextOffset(items.size),
        )
    }

    private fun TrackResponseWrapper.toSearchTrack(): MetadataTrack {
        return MetadataTrack(
            id = data?.uri?.removePrefix("spotify:track:") ?: "",
            title = data?.name ?: "",
            durationMs = data?.duration?.totalMilliseconds ?: data?.trackDuration?.totalMilliseconds
            ?: 0,
            trackNumber = data?.trackNumber ?: 0,
            discNumber = data?.discNumber ?: 0,
            artists = data?.artists?.items?.map { artist ->
                MetadataArtist.Basic(
                    id = artist.uri.removePrefix("spotify:artist:"),
                    name = artist.profile?.name ?: "",
                    thumbnails = emptyList(),
                    externalUri = artist.uri,
                )
            }.orEmpty(),
            album = MetadataAlbum.Detailed(
                id = data?.albumOfTrack?.uri?.removePrefix("spotify:album:") ?: "",
                title = data?.albumOfTrack?.name ?: "",
                description = null,
                thumbnails = data?.albumOfTrack?.coverArt?.sources?.map {
                    Thumbnail(
                        it.url, it.width ?: 0, it.height ?: 0
                    )
                }.orEmpty(),
                albumType = when (data?.albumOfTrack?.type) {
                    "SINGLE" -> MetadataAlbumType.Single
                    "ALBUM" -> MetadataAlbumType.Album
                    "COMPILATION" -> MetadataAlbumType.Collection
                    else -> MetadataAlbumType.Album
                },
                artists = data?.albumOfTrack?.artists?.items?.map { artist ->
                    MetadataArtist.Basic(
                        id = artist.uri.removePrefix("spotify:artist:"),
                        name = artist.profile?.name ?: "",
                        thumbnails = emptyList(),
                        externalUri = artist.uri,
                    )
                }.orEmpty(),
                externalUri = data?.albumOfTrack?.uri,
                releaseDate = null,
                genres = emptyList(),
                trackCount = 0,
            ),
            explicit = data?.contentRating?.label?.let { it != "NONE" } ?: false,
            popularity = null,
            isrcCode = null,
            externalUri = data?.uri,
            thumbnails = null,
        )
    }

    private fun ArtistResponseWrapper.toSearchArtist(): MetadataArtist.Basic {
        return MetadataArtist.Basic(
            id = data?.uri?.removePrefix("spotify:artist:") ?: "",
            name = data?.profile?.name ?: "",
            thumbnails = data?.visuals?.avatarImage?.sources?.map {
                Thumbnail(
                    it.url, it.width ?: 0, it.height ?: 0
                )
            }.orEmpty(),
            externalUri = data?.uri,
        )
    }

    private fun AlbumResponseWrapper.toSearchAlbum(): MetadataAlbum.Basic {
        return MetadataAlbum.Basic(
            id = data?.uri?.removePrefix("spotify:album:") ?: "",
            title = data?.name ?: "",
            description = data?.description,
            thumbnails = data?.coverArt?.sources?.map {
                Thumbnail(
                    it.url, it.width ?: 0, it.height ?: 0
                )
            }.orEmpty(),
            albumType = when (data?.albumType) {
                "SINGLE" -> MetadataAlbumType.Single
                "ALBUM" -> MetadataAlbumType.Album
                "COMPILATION" -> MetadataAlbumType.Collection
                else -> MetadataAlbumType.Album
            },
            artists = data?.artists?.items?.map { artist ->
                MetadataArtist.Basic(
                    id = artist.uri.removePrefix("spotify:artist:"),
                    name = artist.profile?.name ?: "",
                    thumbnails = emptyList(),
                    externalUri = artist.uri,
                )
            }.orEmpty(),
            externalUri = data?.uri,
        )
    }

    private fun PlaylistResponseWrapper.toSearchPlaylist(): MetadataPlaylist {
        return MetadataPlaylist(
            id = data?.uri?.removePrefix("spotify:playlist:") ?: "",
            title = data?.name ?: "",
            description = null,
            thumbnails = data?.images?.items?.flatMap { it.sources }?.map {
                Thumbnail(
                    it.url, it.width ?: 0, it.height ?: 0
                )
            }.orEmpty(),
            trackCount = 0,
            externalUri = data?.uri,
            owner = data?.ownerV2?.data?.let {
                MetadataUser(
                    id = it.uri.removePrefix("spotify:user:"),
                    username = it.name,
                    displayName = it.name,
                    thumbnails = it.avatar?.sources?.map { src ->
                        Thumbnail(
                            src.url, src.width ?: 0, src.height ?: 0
                        )
                    }.orEmpty(),
                    externalUri = it.uri,
                )
            },
        )
    }

    private fun UserResponseWrapper.toSearchUser(): MetadataUser {
        return MetadataUser(
            id = data?.uri?.removePrefix("spotify:user:") ?: "",
            username = data?.username ?: "",
            displayName = data?.displayName,
            thumbnails = data?.avatar?.sources?.map {
                Thumbnail(
                    it.url, it.width ?: 0, it.height ?: 0
                )
            }.orEmpty(),
            externalUri = data?.uri,
        )
    }
}
