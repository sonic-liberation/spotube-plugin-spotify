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
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.album.MetadataAlbumAPI
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.album.MetadataAlbumType
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.artist.MetadataArtist
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.common.PaginationResult
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.common.PaginationStrategy
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.common.Thumbnail
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.track.MetadataTrack
import io.github.sonic_liberation.spotify_gql_client.gql.Album
import io.github.sonic_liberation.spotify_gql_client.gql.AlbumResponseWrapper
import io.github.sonic_liberation.spotify_gql_client.gql.SpotifyGQLClient
import io.github.sonic_liberation.spotify_gql_client.gql.TrackV2
import io.github.sonic_liberation.spotify_gql_client.gql.album.GetAlbumResponse

class RealMetadataAlbumAPI(val spotifyGQLClient: SpotifyGQLClient) : MetadataAlbumAPI {
    override suspend fun getAlbum(id: String): MetadataAlbum.Detailed {
        val uri = "spotify:album:$id"
        val response = spotifyGQLClient.album.getAlbum(uri)
            ?: throw Exception("Album not found: $id")
        return response.toMetadataAlbumDetailed()
    }

    override suspend fun getTrackAlbum(track: MetadataTrack): MetadataAlbum.Detailed {
        return getAlbum(track.album!!.id)
    }

    override suspend fun getAlbumTracks(
        id: String,
        pagination: PaginationStrategy?
    ): PaginationResult<MetadataTrack> {
        val paging = pagination.getOffsetOrDefault()
        val uri = "spotify:album:$id"
        val response = spotifyGQLClient.album.getAlbum(
            uri = uri,
            offset = paging.offset,
            limit = paging.limit,
        ) ?: return PaginationResult(emptyList(), 0, null)

        val album = response.data.albumUnion
        val tracksV2 = album.tracksV2 ?: return PaginationResult(emptyList(), 0, null)
        val items = tracksV2.items.mapNotNull { it.track?.toMetadataTrack(album) }
        val totalCount = tracksV2.totalCount

        return PaginationResult(
            items = items,
            totalCount = totalCount,
            nextPagination = paging.nextOffset(items.size),
        )
    }

    override suspend fun savedAlbums(pagination: PaginationStrategy?): PaginationResult<MetadataAlbum.Detailed> {
        val paging = pagination.getOffsetOrDefault()
        val response = spotifyGQLClient.library.albums(
            offset = paging.offset,
            limit = paging.limit,
        ) ?: return PaginationResult(emptyList(), 0, null)

        val library = response.data.me?.libraryV3 ?: return PaginationResult(
            emptyList(),
            0,
            null,
        )

        val items = library.items.mapNotNull { libItem ->
            val itemData = when (val itemWrapper = libItem.item) {
                is AlbumResponseWrapper -> itemWrapper.data
                else -> null
            } ?: return@mapNotNull null
            val uri = itemData.uri
            val id = uri.removePrefix("spotify:album:")

            MetadataAlbum.Detailed(
                id = id,
                title = itemData.name,
                description = null,
                thumbnails = itemData.coverArt?.sources?.map {
                    Thumbnail(
                        it.url,
                        it.width ?: 0,
                        it.height ?: 0
                    )
                }.orEmpty(),
                albumType = MetadataAlbumType.Album,
                artists = itemData.artists?.items?.map { artist ->
                    MetadataArtist.Basic(
                        id = artist.uri.removePrefix("spotify:artist:"),
                        name = artist.profile?.name ?: "",
                        thumbnails = emptyList(),
                        externalUri = artist.uri,
                    )
                }.orEmpty(),
                externalUri = uri,
                releaseDate = itemData.date?.isoString,
                genres = emptyList(),
                trackCount = itemData.tracks?.totalCount ?: 0,
            )
        }

        return PaginationResult(
            items = items,
            totalCount = library.totalCount,
            nextPagination = paging.nextOffset(items.size),
        )
    }

    override suspend fun isSavedAlbums(ids: List<String>): List<Boolean> {
        if (ids.isEmpty()) return emptyList()
        val uris = ids.map { "spotify:album:$it" }
        val response = spotifyGQLClient.library.areEntitiesInLibrary(uris)
            ?: return ids.map { false }

        return response.data.lookup.map { it.data?.saved ?: false }
    }

    override suspend fun saveAlbums(ids: List<String>) {
        if (ids.isEmpty()) return
        val uris = ids.map { "spotify:album:$it" }
        spotifyGQLClient.album.saveAlbums(uris)
    }

    override suspend fun removeSavedAlbums(ids: List<String>) {
        if (ids.isEmpty()) return
        val uris = ids.map { "spotify:album:$it" }
        spotifyGQLClient.album.unsaveAlbums(uris)
    }

    private fun GetAlbumResponse.toMetadataAlbumDetailed(): MetadataAlbum.Detailed {
        return data.albumUnion.toMetadataAlbumDetailed()
    }

    private fun Album.toMetadataAlbumDetailed(): MetadataAlbum.Detailed {
        return MetadataAlbum.Detailed(
            id = uri.removePrefix("spotify:album:"),
            title = name,
            description = null,
            thumbnails = coverArt?.sources?.map { Thumbnail(it.url, it.width ?: 0, it.height ?: 0) }
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
            trackCount = tracksV2?.totalCount ?: 0,
        )
    }

    private fun TrackV2.toMetadataTrack(album: Album): MetadataTrack {
        return MetadataTrack(
            id = uri.removePrefix("spotify:track:"),
            title = name,
            durationMs = duration?.totalMilliseconds ?: 0L,
            trackNumber = trackNumber ?: 0,
            discNumber = discNumber ?: 0,
            artists = artists?.items?.map { artist ->
                MetadataArtist.Basic(
                    id = artist.uri.removePrefix("spotify:artist:"),
                    name = artist.profile?.name ?: "",
                    thumbnails = emptyList(),
                    externalUri = artist.uri,
                )
            }.orEmpty(),
            album = album.toMetadataAlbumDetailed(),
            explicit = contentRating?.label?.let { it != "NONE" } ?: false,
            popularity = null,
            isrcCode = null,
            externalUri = uri,
            thumbnails = null,
        )
    }
}
