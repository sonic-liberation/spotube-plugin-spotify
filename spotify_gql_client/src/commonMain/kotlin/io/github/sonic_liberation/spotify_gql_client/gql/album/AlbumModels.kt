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

package io.github.sonic_liberation.spotify_gql_client.gql.album

import io.github.sonic_liberation.spotify_gql_client.gql.Album
import io.github.sonic_liberation.spotify_gql_client.gql.GQLRequestExtensions
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetAlbumRequest(
    val variables: GetAlbumVariables,
    val operationName: String = "getAlbum",
    val extensions: GQLRequestExtensions,
)

@Serializable
data class GetAlbumVariables(
    val uri: String,
    val locale: String = "",
    val offset: Int = 0,
    val limit: Int = 50,
)

@Serializable
data class SaveAlbumsRequest(
    val variables: SaveAlbumsVariables,
    val operationName: String = "addToLibrary",
    val extensions: GQLRequestExtensions,
)

@Serializable
data class SaveAlbumsVariables(
    val uris: List<String>,
)

@Serializable
data class UnsaveAlbumsRequest(
    val variables: UnsaveAlbumsVariables,
    val operationName: String = "removeFromLibrary",
    val extensions: GQLRequestExtensions,
)

@Serializable
data class UnsaveAlbumsVariables(
    val uris: List<String>,
)

@Serializable
data class GetAlbumResponse(
    val data: Data,
) {
    @Serializable
    data class Data(
        val albumUnion: Album,
    )
}