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
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.playlist.MetadataPlaylistAPI
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.track.MetadataTrack
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.user.MetadataUser
import io.github.sonic_liberation.spotify_gql_client.gql.LibraryPseudoPlaylistResponseWrapper
import io.github.sonic_liberation.spotify_gql_client.gql.Playlist
import io.github.sonic_liberation.spotify_gql_client.gql.PlaylistResponseWrapper
import io.github.sonic_liberation.spotify_gql_client.gql.SpotifyGQLClient
import io.github.sonic_liberation.spotify_gql_client.gql.Track
import io.github.sonic_liberation.spotify_gql_client.gql.playlist.PlaylistItem

class RealMetadataPlaylistAPI(val spotifyGQLClient: SpotifyGQLClient) : MetadataPlaylistAPI {
    override suspend fun getPlaylist(id: String): MetadataPlaylist {
        val uri = "spotify:playlist:$id"
        val response = spotifyGQLClient.playlist.fetchPlaylist(uri)
            ?: throw Exception("Playlist not found: $id")
        return response.data.playlistV2.toMetadataPlaylist()
    }

    override suspend fun getPlaylistTracks(
        id: String,
        pagination: PaginationStrategy?
    ): PaginationResult<MetadataTrack> {
        val paging = pagination.getOffsetOrDefault()
        val uri = "spotify:playlist:$id"
        val response = spotifyGQLClient.playlist.fetchPlaylist(
            uri = uri,
            offset = paging.offset,
            limit = paging.limit,
        ) ?: return PaginationResult(emptyList(), 0, null)

        val playlist = response.data.playlistV2
        val items =
            playlist.content?.items?.mapNotNull {
                it.itemV2?.takeIf { itemV2 ->
                    // Some items in the playlist can be episodes or other non-track items, we need to filter them out
                    itemV2.__typename == "TrackResponseWrapper" && itemV2.data != null
                            && itemV2.data?.__typename == "Track"
                }?.data?.toMetadataTrack()
            }
                .orEmpty()
        val totalCount = playlist.content?.totalCount ?: 0

        return PaginationResult(
            items = items,
            totalCount = totalCount,
            nextPagination = paging.nextOffset(items.size),
        )
    }

    override suspend fun savedPlaylists(pagination: PaginationStrategy?): PaginationResult<MetadataPlaylist> {
        val paging = pagination.getOffsetOrDefault()
        val response = spotifyGQLClient.library.playlists(
            offset = paging.offset,
            limit = paging.limit,
        ) ?: return PaginationResult(emptyList(), 0, null)

        val library = response.data.me?.libraryV3 ?: return PaginationResult(
            emptyList(),
            0,
            null
        )

        val items = library.items.mapNotNull { libItem ->
            when (val itemWrapper = libItem.item) {
                is PlaylistResponseWrapper -> {
                    val itemData = itemWrapper.data ?: return@mapNotNull null
                    val uri = itemData.uri
                    val id = uri.removePrefix("spotify:playlist:")
                    val thumbnails =
                        itemData.images?.items?.firstOrNull()?.sources?.map {
                            Thumbnail(
                                it.url,
                                it.width ?: 0,
                                it.height ?: 0
                            )
                        }.orEmpty()
                    MetadataPlaylist(
                        id = id,
                        title = itemData.name,
                        description = null,
                        thumbnails = thumbnails,
                        trackCount = 0,
                        externalUri = uri,
                        owner = itemData.ownerV2?.data?.let { owner ->
                            MetadataUser(
                                id = owner.username,
                                username = owner.username,
                                displayName = owner.name,
                                thumbnails = owner.avatar?.sources?.map {
                                    Thumbnail(
                                        it.url,
                                        it.width ?: 0,
                                        it.height ?: 0
                                    )
                                }.orEmpty(),
                                externalUri = owner.uri,
                            )
                        },
                    )
                }
// LibraryPseudoPlaylistResponseWrapper is Liked Songs basically
//                is LibraryPseudoPlaylistResponseWrapper -> {
//                    val itemData = itemWrapper.data ?: return@mapNotNull null
//                    val uri = itemData.uri
//                    val id = uri.removePrefix("spotify:playlist:")
//                    val thumbnails =
//                        itemData.image?.sources?.map {
//                            Thumbnail(
//                                it.url,
//                                it.width ?: 0,
//                                it.height ?: 0
//                            )
//                        }.orEmpty()
//
//                    MetadataPlaylist(
//                        id = id,
//                        title = itemData.name,
//                        description = null,
//                        thumbnails = thumbnails,
//                        trackCount = 0,
//                        externalUri = uri,
//                        owner = null,
//                    )
//                }

                else -> null
            }
        }

        return PaginationResult(
            items = items,
            totalCount = library.totalCount,
            nextPagination = paging.nextOffset(items.size),
        )
    }

    override suspend fun isSavedPlaylists(ids: List<String>): List<Boolean> {
        if (ids.isEmpty()) return emptyList()
        val uris = ids.map { "spotify:playlist:$it" }
        val response = spotifyGQLClient.library.areEntitiesInLibrary(uris)
            ?: return ids.map { false }

        return response.data.lookup.map { it.data?.saved ?: false }
    }

    override suspend fun savePlaylists(ids: List<String>) {
        if (ids.isEmpty()) return
        val uris = ids.map { "spotify:playlist:$it" }
        spotifyGQLClient.playlist.followPlaylist(uris)
    }

    override suspend fun removeSavedPlaylists(ids: List<String>) {
        if (ids.isEmpty()) return
        val uris = ids.map { "spotify:playlist:$it" }
        spotifyGQLClient.playlist.unfollowPlaylist(uris)
    }

    override suspend fun createPlaylist(
        name: String,
        description: String?,
        isPublic: Boolean,
        isCollaborating: Boolean,
        imageBase64: String,
        trackIds: List<String>
    ): MetadataPlaylist {
        val createResponse = spotifyGQLClient.playlist.createPlaylist(
            name = name,
            description = description,
        ) ?: throw Exception("Failed to create playlist")

        val uri = createResponse.uri
        val id = uri.removePrefix("spotify:playlist:")

        spotifyGQLClient.playlist.followPlaylist(listOf(uri))

        val userProfile = spotifyGQLClient.user.profileAttributes()
            ?: throw Exception("Failed to get user profile")
        val userId = userProfile.data.me.profile.username

        spotifyGQLClient.playlist.changePermission(
            userId = userId,
            playlistUri = uri,
            isPublic = isPublic,
        )

        return getPlaylist(id)
    }

    override suspend fun updatePlaylist(
        id: String,
        name: String?,
        description: String?,
        isPublic: Boolean?,
        isCollaborating: Boolean?,
        imageBase64: String?,
        trackIds: List<String>?
    ): MetadataPlaylist {
        spotifyGQLClient.playlist.updatePlaylist(
            id = id,
            name = name,
            description = description,
        )

        if (isPublic != null) {
            val userProfile = spotifyGQLClient.user.profileAttributes()
                ?: throw Exception("Failed to get user profile")
            val userId = userProfile.data.me.profile.username
            val uri = "spotify:playlist:$id"

            spotifyGQLClient.playlist.changePermission(
                userId = userId,
                playlistUri = uri,
                isPublic = isPublic,
            )
        }

        return getPlaylist(id)
    }

    override suspend fun deletePlaylist(id: String) {
        return removeSavedPlaylists(listOf(id))
    }

    override suspend fun addTracksToPlaylist(
        playlistId: String,
        trackIds: List<String>
    ) {
        if (trackIds.isEmpty()) return
        val playlistUri = "spotify:playlist:$playlistId"
        val trackUris = trackIds.map { "spotify:track:$it" }
        spotifyGQLClient.playlist.addTracksToPlaylist(
            playlistUri = playlistUri,
            trackUris = trackUris,
        )
    }

    override suspend fun removeTracksFromPlaylist(
        playlistId: String,
        trackIds: List<String>
    ) {
        if (trackIds.isEmpty()) return
        val playlistUri = "spotify:playlist:$playlistId"

        spotifyGQLClient.playlist.removeTracksFromPlaylist(
            playlistUri = playlistUri,
            uids = trackIds,
        )
    }

    private fun Playlist.toMetadataPlaylist(): MetadataPlaylist {
        return MetadataPlaylist(
            id = uri.removePrefix("spotify:playlist:"),
            title = name,
            description = description,
            thumbnails = images?.items?.firstOrNull()?.sources?.map {
                Thumbnail(
                    it.url,
                    it.width ?: 0,
                    it.height ?: 0
                )
            }.orEmpty(),
            trackCount = content?.totalCount ?: 0,
            externalUri = uri,
            owner = ownerV2?.data?.let { owner ->
                MetadataUser(
                    id = owner.username,
                    username = owner.username,
                    displayName = owner.name,
                    thumbnails = owner.avatar?.sources?.map {
                        Thumbnail(
                            it.url,
                            it.width ?: 0,
                            it.height ?: 0
                        )
                    }.orEmpty(),
                    externalUri = owner.uri,
                )
            },
        )
    }
}

fun Track.toMetadataTrack(): MetadataTrack {
    val uri = uri
    val id = uri.removePrefix("spotify:track:")
    val artists = artists?.items?.map {
        MetadataArtist.Basic(
            id = it.uri.removePrefix("spotify:artist:"),
            name = it.profile?.name ?: "",
            thumbnails = emptyList(),
            externalUri = it.uri,
        )
    }.orEmpty()
    val albumUri = albumOfTrack?.uri ?: ""
    val albumId = albumUri.removePrefix("spotify:album:")

    return MetadataTrack(
        id = id,
        title = name,
        durationMs = trackDuration?.totalMilliseconds ?: 0,
        trackNumber = trackNumber,
        discNumber = trackNumber,
        artists = artists,
        album = MetadataAlbum.Detailed(
            id = albumId,
            title = albumOfTrack?.name ?: "",
            description = null,
            thumbnails = albumOfTrack?.coverArt?.sources?.map {
                Thumbnail(
                    it.url,
                    it.width ?: 0,
                    it.height ?: 0
                )
            }.orEmpty(),
            albumType = MetadataAlbumType.Album,
            artists = emptyList(),
            externalUri = albumUri,
            releaseDate = null,
            genres = emptyList(),
            trackCount = 0,
        ),
        explicit = contentRating?.label?.let { it != "NONE" } ?: false,
        popularity = playcount?.toIntOrNull() ?: 0,
        isrcCode = null,
        externalUri = uri,
        thumbnails = null,
    )
}