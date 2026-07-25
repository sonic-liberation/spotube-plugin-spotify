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
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.track.MetadataTrack
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.track.MetadataTrackAPI
import io.github.sonic_liberation.spotify_gql_client.gql.SpotifyGQLClient
import io.github.sonic_liberation.spotify_gql_client.gql.UserLibraryTrackResponse
import io.github.sonic_liberation.spotify_gql_client.gql.tracks.AlbumOfTrackUnion
import io.github.sonic_liberation.spotify_gql_client.gql.tracks.GetTrackResponse
import io.github.sonic_liberation.spotify_gql_client.gql.tracks.TrackUnion

class RealMetadataTrackAPI(val spotifyGQLClient: SpotifyGQLClient) : MetadataTrackAPI {
    override suspend fun getTrack(id: String): MetadataTrack {
        val uri = "spotify:track:$id"
        val response = spotifyGQLClient.tracks.getTrack(uri)
            ?: throw Exception("Track not found: $id")
        return response.toMetadataTrack()
    }

    override suspend fun savedTracks(pagination: PaginationStrategy?): PaginationResult<MetadataTrack> {
        val paging = pagination.getOffsetOrDefault()
        val response = spotifyGQLClient.playlist.fetchPlaylist(
            uri = "spotify:playlist:37i9dQZF1F5p3rmiWPIYgZ",
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
            }.orEmpty()
        val totalCount = playlist.content?.totalCount ?: 0

        return PaginationResult(
            items = items,
            totalCount = totalCount,
            nextPagination = paging.nextOffset(items.size),
        )
    }

    override suspend fun isSavedTracks(ids: List<String>): List<Boolean> {
        if (ids.isEmpty()) return emptyList()
        val uris = ids.map { "spotify:track:$it" }
        val response = spotifyGQLClient.tracks.areTracksSaved(uris) ?: return ids.map { false }

        return response.data.lookup.map { it.data.isCurated }
    }

    override suspend fun saveTracks(ids: List<String>) {
        if (ids.isEmpty()) return
        val uris = ids.map { "spotify:track:$it" }
        spotifyGQLClient.tracks.saveTracks(uris)
    }

    override suspend fun removeSavedTracks(ids: List<String>) {
        if (ids.isEmpty()) return
        val uris = ids.map { "spotify:track:$it" }
        spotifyGQLClient.tracks.unsaveTracks(uris)
    }

    override suspend fun recommendationsBasedOnTracks(
        seedTrackIds: List<String>,
        limit: Int
    ): List<MetadataTrack> {
        if (seedTrackIds.isEmpty()) return emptyList()

        val radioResponse = spotifyGQLClient.tracks.radioPlaylist(seedTrackIds.first())
            ?: return emptyList()

        val playlistUri = radioResponse.mediaItems.firstOrNull()?.uri ?: return emptyList()
        val playlist = spotifyGQLClient.playlist.fetchPlaylist(playlistUri, limit)
            ?: return emptyList()

        return playlist.data.playlistV2.content?.items
            ?.mapNotNull { it.itemV2?.data?.toMetadataTrack() }.orEmpty()
    }

    private fun UserLibraryTrackResponse.toMetadataTrack(): MetadataTrack? {
        val trackWrapper = track ?: return null
        val trackData = trackWrapper.data ?: return null
        val uri = trackData.uri
        val id = uri.removePrefix("spotify:track:")
        val albumUri = trackData.albumOfTrack?.uri ?: ""
        val albumId = albumUri.removePrefix("spotify:album:")
        val artistItems = trackData.artists?.items.orEmpty()
        val artists = artistItems.map {
            MetadataArtist.Basic(
                id = it.uri.removePrefix("spotify:artist:"),
                name = it.profile?.name ?: "",
                thumbnails = emptyList(),
                externalUri = it.uri,
            )
        }

        return MetadataTrack(
            id = id,
            title = trackData.name,
            durationMs = trackData.duration?.totalMilliseconds ?: 0,
            trackNumber = trackData.trackNumber,
            discNumber = trackData.discNumber,
            artists = artists,
            album = MetadataAlbum.Detailed(
                id = albumId,
                title = trackData.albumOfTrack?.name ?: "",
                description = null,
                thumbnails = trackData.albumOfTrack?.coverArt?.sources?.map { s ->
                    Thumbnail(
                        s.url,
                        s.width ?: 0,
                        s.height ?: 0
                    )
                }.orEmpty(),
                albumType = MetadataAlbumType.Album,
                artists = emptyList(),
                externalUri = albumUri,
                releaseDate = null,
                genres = emptyList(),
                trackCount = 0,
            ),
            explicit = trackData.contentRating?.label?.let { it != "NONE" },
            popularity = null,
            isrcCode = null,
            externalUri = uri,
            thumbnails = null,
        )
    }

    private fun GetTrackResponse.toMetadataTrack(): MetadataTrack {
        val track = data.trackUnion
        return MetadataTrack(
            id = track.id,
            title = track.name,
            durationMs = track.duration.totalMilliseconds,
            trackNumber = track.trackNumber,
            discNumber = track.discNumber,
            artists = track.toArtists(),
            album = track.albumOfTrack.toMetadataAlbum(),
            explicit = track.contentRating.label.let { it != "NONE" },
            popularity = track.playcount?.toIntOrNull(),
            isrcCode = null,
            externalUri = track.uri,
            thumbnails = null,
        )
    }

    private fun TrackUnion.toArtists(): List<MetadataArtist.Basic> {
        val items = firstArtist?.items.orEmpty()
        return items.mapNotNull { item ->
            val uri = item.uri ?: return@mapNotNull null
            val id = uri.removePrefix("spotify:artist:")
            MetadataArtist.Basic(
                id = id,
                name = item.profile?.name ?: "",
                thumbnails = emptyList(),
                externalUri = uri,
            )
        }
    }

    private fun AlbumOfTrackUnion.toMetadataAlbum(): MetadataAlbum.Detailed {
        return MetadataAlbum.Detailed(
            id = id,
            title = name,
            description = null,
            thumbnails = coverArt?.sources?.map { Thumbnail(it.url, it.width ?: 0, it.height ?: 0) }
                .orEmpty(),
            albumType = when (type.uppercase()) {
                "SINGLE" -> MetadataAlbumType.Single
                "COMPILATION" -> MetadataAlbumType.Collection
                else -> MetadataAlbumType.Album
            },
            artists = emptyList(),
            externalUri = uri,
            releaseDate = date?.isoString,
            genres = emptyList(),
            trackCount = tracks?.totalCount ?: 0,
        )
    }

}
