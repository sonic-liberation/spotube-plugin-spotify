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

import dev.krtirtho.plugin_interfaces.host_apis.HttpClientAPI
import io.github.sonic_liberation.spotify_gql_client.gql.GQLRequestExtensions
import io.github.sonic_liberation.spotify_gql_client.gql.PersistedQuery
import io.github.sonic_liberation.spotify_gql_client.gql.SpotifyGQLBaseClient

class AlbumClient(client: HttpClientAPI) : SpotifyGQLBaseClient(client) {
    suspend fun getAlbum(
        uri: String,
        locale: String = "",
        offset: Int = 0,
        limit: Int = 50,
    ): GetAlbumResponse? {
        val request = GetAlbumRequest(
            variables = GetAlbumVariables(uri = uri, locale = locale, offset = offset, limit = limit),
            extensions = GQLRequestExtensions(
                persistedQuery = PersistedQuery(
                    version = 1,
                    sha256Hash = "b9bfabef66ed756e5e13f68a942deb60bd4125ec1f1be8cc42769dc0259b4b10",
                ),
            ),
        )
        return post(SPOTIFY_GQL_ENDPOINT, request)
    }

    suspend fun saveAlbums(uris: List<String>): Unit? {
        val request = SaveAlbumsRequest(
            variables = SaveAlbumsVariables(uris = uris),
            extensions = GQLRequestExtensions(
                persistedQuery = PersistedQuery(
                    version = 1,
                    sha256Hash = "a3c1ff58e6a36fec5fe1e3a193dc95d9071d96b9ba53c5ba9c1494fb1ee73915",
                ),
            ),
        )
        return post(SPOTIFY_GQL_ENDPOINT, request)
    }

    suspend fun unsaveAlbums(uris: List<String>): Unit? {
        val request = UnsaveAlbumsRequest(
            variables = UnsaveAlbumsVariables(uris = uris),
            extensions = GQLRequestExtensions(
                persistedQuery = PersistedQuery(
                    version = 1,
                    sha256Hash = "a3c1ff58e6a36fec5fe1e3a193dc95d9071d96b9ba53c5ba9c1494fb1ee73915",
                ),
            ),
        )
        return post(SPOTIFY_GQL_ENDPOINT, request)
    }
}
