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

package io.github.sonic_liberation.spotify_gql_client.gql.user

import dev.krtirtho.plugin_interfaces.host_apis.HttpClientAPI
import io.github.sonic_liberation.spotify_gql_client.gql.GQLRequestExtensions
import io.github.sonic_liberation.spotify_gql_client.gql.PersistedQuery
import io.github.sonic_liberation.spotify_gql_client.gql.SpotifyGQLBaseClient

class UserClient(client: HttpClientAPI): SpotifyGQLBaseClient(client) {
    suspend fun profileAttributes(): ProfileResponse? {
        val request = ProfileRequest(
            operationName = "profileAttributes",
            extensions = GQLRequestExtensions(
                persistedQuery = PersistedQuery(
                    version = 1,
                    sha256Hash = "53bcb064f6cd18c23f752bc324a791194d20df612d8e1239c735144ab0399ced",
                ),
            ),
        )

        return post<ProfileResponse, ProfileRequest>(SPOTIFY_GQL_ENDPOINT, request)
    }
}