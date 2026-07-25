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

package io.github.sonic_liberation.spotify_gql_client.gql.library

import dev.krtirtho.plugin_interfaces.host_apis.HttpClientAPI
import io.github.sonic_liberation.spotify_gql_client.gql.GQLRequestExtensions
import io.github.sonic_liberation.spotify_gql_client.gql.PersistedQuery
import io.github.sonic_liberation.spotify_gql_client.gql.SpotifyGQLBaseClient

class LibraryClient(client: HttpClientAPI) : SpotifyGQLBaseClient(client) {
    suspend fun albums(
        order: String? = null,
        textFilter: String = "",
        limit: Int = 50,
        offset: Int = 0,
        flatten: Boolean = true,
        expandedFolders: List<String> = emptyList(),
        folderUri: String? = null,
        includeFoldersWhenFlattening: Boolean = true,
    ): LibraryResponse? {
        val request = LibraryV3Request(
            variables = LibraryV3Variables(
                order = order,
                textFilter = textFilter,
                filters = listOf("Albums"),
                limit = limit,
                offset = offset,
                flatten = flatten,
                expandedFolders = expandedFolders,
                folderUri = folderUri,
                includeFoldersWhenFlattening = includeFoldersWhenFlattening,
            ),
            extensions = GQLRequestExtensions(
                persistedQuery = PersistedQuery(
                    version = 1,
                    sha256Hash = "0082bf82412db50128add72dbdb73e2961d59100b9cbf41fb25c568bd8bc358b",
                ),
            ),
        )
        val res = post<LibraryResponse, LibraryV3Request>(SPOTIFY_GQL_ENDPOINT, request)
        return res
    }

    suspend fun artists(
        order: String? = null,
        textFilter: String = "",
        limit: Int = 50,
        offset: Int = 0,
        flatten: Boolean = true,
        expandedFolders: List<String> = emptyList(),
        folderUri: String? = null,
        includeFoldersWhenFlattening: Boolean = true,
    ): LibraryResponse? {
        val request = LibraryV3Request(
            variables = LibraryV3Variables(
                order = order,
                textFilter = textFilter,
                filters = listOf("Artists"),
                limit = limit,
                offset = offset,
                flatten = flatten,
                expandedFolders = expandedFolders,
                folderUri = folderUri,
                includeFoldersWhenFlattening = includeFoldersWhenFlattening,
            ),
            extensions = GQLRequestExtensions(
                persistedQuery = PersistedQuery(
                    version = 1,
                    sha256Hash = "0082bf82412db50128add72dbdb73e2961d59100b9cbf41fb25c568bd8bc358b",
                ),
            ),
        )
        return post<LibraryResponse, LibraryV3Request>(SPOTIFY_GQL_ENDPOINT, request)
    }

    suspend fun playlists(
        order: String? = null,
        textFilter: String = "",
        limit: Int = 50,
        offset: Int = 0,
        flatten: Boolean = true,
        expandedFolders: List<String> = emptyList(),
        folderUri: String? = null,
        includeFoldersWhenFlattening: Boolean = true,
    ): LibraryResponse? {
        val request = LibraryV3Request(
            variables = LibraryV3Variables(
                filters = listOf("Playlists"),
                order = order,
                textFilter = textFilter,
                limit = limit,
                offset = offset,
                flatten = flatten,
                expandedFolders = expandedFolders,
                folderUri = folderUri,
                includeFoldersWhenFlattening = includeFoldersWhenFlattening,
            ),
            extensions = GQLRequestExtensions(
                persistedQuery = PersistedQuery(
                    version = 1,
                    sha256Hash = "973e511ca44261fda7eebac8b653155e7caee3675abb4fb110cc1b8c78b091c3",
                ),
            ),
        )
        return post<LibraryResponse, LibraryV3Request>(SPOTIFY_GQL_ENDPOINT, request)
    }

    suspend fun savedTracks(
        offset: Int = 0,
        limit: Int = 50,
    ): LibrarySavedTracksResponse? {
        val request = SavedTracksRequest(
            variables = SavedTracksVariables(offset = offset, limit = limit),
            extensions = GQLRequestExtensions(
                persistedQuery = PersistedQuery(
                    version = 1,
                    sha256Hash = "087278b20b743578a6262c2b0b4bcd20d879c503cc359a2285baf083ef944240",
                ),
            ),
        )
        return post<LibrarySavedTracksResponse, SavedTracksRequest>(SPOTIFY_GQL_ENDPOINT, request)
    }

    suspend fun areEntitiesInLibrary(uris: List<String>): LibraryAreEntitiesInLibraryResponse? {
        val request = AreEntitiesInLibraryRequest(
            variables = AreEntitiesInLibraryVariables(uris = uris),
            extensions = GQLRequestExtensions(
                persistedQuery = PersistedQuery(
                    version = 1,
                    sha256Hash = "134337999233cc6fdd6b1e6dbf94841409f04a946c5c7b744b09ba0dfe5a85ed",
                ),
            ),
        )
        return post<LibraryAreEntitiesInLibraryResponse, AreEntitiesInLibraryRequest>(
            SPOTIFY_GQL_ENDPOINT,
            request
        )
    }
}
