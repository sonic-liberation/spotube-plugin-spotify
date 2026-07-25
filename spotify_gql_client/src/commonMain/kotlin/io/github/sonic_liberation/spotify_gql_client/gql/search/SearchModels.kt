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

package io.github.sonic_liberation.spotify_gql_client.gql.search

import io.github.sonic_liberation.spotify_gql_client.gql.AlbumOrPrereleasePage
import io.github.sonic_liberation.spotify_gql_client.gql.ArtistResponseWrapper
import io.github.sonic_liberation.spotify_gql_client.gql.GQLRequestExtensions
import io.github.sonic_liberation.spotify_gql_client.gql.PlaylistResponseWrapper
import io.github.sonic_liberation.spotify_gql_client.gql.ResponseWrapper
import io.github.sonic_liberation.spotify_gql_client.gql.TrackResponseWrapper
import io.github.sonic_liberation.spotify_gql_client.gql.UserResponseWrapper
import kotlinx.serialization.Serializable

@Serializable
data class SearchRequest(
    val variables: SearchVariables,
    val operationName: String = "searchDesktop",
    val extensions: GQLRequestExtensions,
)

@Serializable
data class SearchVariables(
    val searchTerm: String,
    val offset: Int = 0,
    val limit: Int = 10,
    val numberOfTopResults: Int = 5,
    val includeAudiobooks: Boolean = true,
    val includeArtistHasConcertsField: Boolean = false,
    val includePreReleases: Boolean = true,
    val includeLocalConcertsField: Boolean = false,
    val includeAuthors: Boolean = false,
)

@Serializable
data class SearchTracksRequest(
    val variables: SearchTracksVariables,
    val operationName: String = "searchTracks",
    val extensions: GQLRequestExtensions,
)

@Serializable
data class SearchTracksVariables(
    val searchTerm: String,
    val offset: Int = 0,
    val limit: Int = 20,
    val includePreReleases: Boolean = false,
    val numberOfTopResults: Int = 20,
    val includeAudiobooks: Boolean = true,
    val includeAuthors: Boolean = false,
)

@Serializable
data class SearchPlaylistsRequest(
    val variables: SearchPlaylistsVariables,
    val operationName: String = "searchPlaylists",
    val extensions: GQLRequestExtensions,
)

@Serializable
data class SearchPlaylistsVariables(
    val searchTerm: String,
    val offset: Int = 0,
    val limit: Int = 30,
    val includePreReleases: Boolean = false,
    val numberOfTopResults: Int = 20,
    val includeAudiobooks: Boolean = true,
    val includeAuthors: Boolean = false,
)

@Serializable
data class SearchArtistsRequest(
    val variables: SearchArtistsVariables,
    val operationName: String = "searchArtists",
    val extensions: GQLRequestExtensions,
)

@Serializable
data class SearchArtistsVariables(
    val searchTerm: String,
    val offset: Int = 0,
    val limit: Int = 30,
    val includePreReleases: Boolean = false,
    val numberOfTopResults: Int = 20,
    val includeAudiobooks: Boolean = true,
    val includeAuthors: Boolean = false,
)

@Serializable
data class SearchAlbumsRequest(
    val variables: SearchAlbumsVariables,
    val operationName: String = "searchAlbums",
    val extensions: GQLRequestExtensions,
)

@Serializable
data class SearchAlbumsVariables(
    val searchTerm: String,
    val offset: Int = 0,
    val limit: Int = 30,
    val includePreReleases: Boolean = false,
    val numberOfTopResults: Int = 20,
    val includeAudiobooks: Boolean = true,
    val includeAuthors: Boolean = false,
)

@Serializable
data class SearchUsersRequest(
    val variables: SearchUsersVariables,
    val operationName: String = "searchUsers",
    val extensions: GQLRequestExtensions,
)

@Serializable
data class SearchUsersVariables(
    val searchTerm: String,
    val offset: Int = 0,
    val limit: Int = 30,
    val includePreReleases: Boolean = false,
    val numberOfTopResults: Int = 20,
    val includeAudiobooks: Boolean = true,
    val includeAuthors: Boolean = false,
)


// Response models
@Serializable
data class SearchResponse(
    val data: Data,
) {
    @Serializable
    data class Data(
        val searchV2: SearchV2,
    ) {
        @Serializable
        data class SearchV2(
            val query: String? = null,
            val albumsV2: AlbumOrPrereleasePage? = null,
            val artists: Wrapper<ArtistResponseWrapper>? = null,
            val playlists: Wrapper<PlaylistResponseWrapper>? = null,
            val tracksV2: Wrapper<ItemWrapper<TrackResponseWrapper>>? = null,
            val users: Wrapper<UserResponseWrapper>? = null,
        ) {
            @Serializable
            data class Wrapper<T>(
                val items: List<T>,
                val totalCount: Int,
            )

            @Serializable
            data class ItemWrapper<T>(
                val item: T,
                val matchedFields: List<String>,
            )
        }
    }
}