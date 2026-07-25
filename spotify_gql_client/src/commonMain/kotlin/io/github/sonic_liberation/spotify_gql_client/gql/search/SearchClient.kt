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

import dev.krtirtho.plugin_interfaces.host_apis.HttpClientAPI
import io.github.sonic_liberation.spotify_gql_client.gql.GQLRequestExtensions
import io.github.sonic_liberation.spotify_gql_client.gql.PersistedQuery
import io.github.sonic_liberation.spotify_gql_client.gql.SpotifyGQLBaseClient

class SearchClient(client: HttpClientAPI) : SpotifyGQLBaseClient(client) {
    suspend fun search(
        searchTerm: String,
        offset: Int = 0,
        limit: Int = 10,
        numberOfTopResults: Int = 5,
        includeAudiobooks: Boolean = true,
        includeArtistHasConcertsField: Boolean = false,
        includePreReleases: Boolean = true,
        includeLocalConcertsField: Boolean = false,
        includeAuthors: Boolean = false,
    ): SearchResponse? {
        val request = SearchRequest(
            variables = SearchVariables(
                searchTerm = searchTerm,
                offset = offset,
                limit = limit,
                numberOfTopResults = numberOfTopResults,
                includeAudiobooks = includeAudiobooks,
                includeArtistHasConcertsField = includeArtistHasConcertsField,
                includePreReleases = includePreReleases,
                includeLocalConcertsField = includeLocalConcertsField,
                includeAuthors = includeAuthors,
            ),
            extensions = GQLRequestExtensions(
                persistedQuery = PersistedQuery(
                    version = 1,
                    sha256Hash = "d9f785900f0710b31c07818d617f4f7600c1e21217e80f5b043d1e78d74e6026",
                ),
            ),
        )
        return post<SearchResponse, SearchRequest>(SPOTIFY_GQL_ENDPOINT, request)
    }

    suspend fun tracksV2(
        searchTerm: String,
        offset: Int = 0,
        limit: Int = 20,
        includePreReleases: Boolean = false,
        numberOfTopResults: Int = 20,
        includeAudiobooks: Boolean = true,
        includeAuthors: Boolean = false,
    ): SearchResponse? {
        val request = SearchTracksRequest(
            variables = SearchTracksVariables(
                searchTerm = searchTerm,
                offset = offset,
                limit = limit,
                includePreReleases = includePreReleases,
                numberOfTopResults = numberOfTopResults,
                includeAudiobooks = includeAudiobooks,
                includeAuthors = includeAuthors,
            ),
            extensions = GQLRequestExtensions(
                persistedQuery = PersistedQuery(
                    version = 1,
                    sha256Hash = "bc1ca2fcd0ba1013a0fc88e6cc4f190af501851e3dafd3e1ef85840297694428",
                ),
            ),
        )
        return post<SearchResponse, SearchTracksRequest>(SPOTIFY_GQL_ENDPOINT, request)
    }

    suspend fun playlists(
        searchTerm: String,
        offset: Int = 0,
        limit: Int = 30,
        includePreReleases: Boolean = false,
        numberOfTopResults: Int = 20,
        includeAudiobooks: Boolean = true,
        includeAuthors: Boolean = false,
    ): SearchResponse? {
        val request = SearchPlaylistsRequest(
            variables = SearchPlaylistsVariables(
                searchTerm = searchTerm,
                offset = offset,
                limit = limit,
                includePreReleases = includePreReleases,
                numberOfTopResults = numberOfTopResults,
                includeAudiobooks = includeAudiobooks,
                includeAuthors = includeAuthors,
            ),
            extensions = GQLRequestExtensions(
                persistedQuery = PersistedQuery(
                    version = 1,
                    sha256Hash = "fc3a690182167dbad20ac7a03f842b97be4e9737710600874cb903f30112ad58",
                ),
            ),
        )
        return post<SearchResponse, SearchPlaylistsRequest>(SPOTIFY_GQL_ENDPOINT, request)
    }

    suspend fun artists(
        searchTerm: String,
        offset: Int = 0,
        limit: Int = 30,
        includePreReleases: Boolean = false,
        numberOfTopResults: Int = 20,
        includeAudiobooks: Boolean = true,
        includeAuthors: Boolean = false,
    ): SearchResponse? {
        val request = SearchArtistsRequest(
            variables = SearchArtistsVariables(
                searchTerm = searchTerm,
                offset = offset,
                limit = limit,
                includePreReleases = includePreReleases,
                numberOfTopResults = numberOfTopResults,
                includeAudiobooks = includeAudiobooks,
                includeAuthors = includeAuthors,
            ),
            extensions = GQLRequestExtensions(
                persistedQuery = PersistedQuery(
                    version = 1,
                    sha256Hash = "0e6f9020a66fe15b93b3bb5c7e6484d1d8cb3775963996eaede72bac4d97e909",
                ),
            ),
        )
        return post<SearchResponse, SearchArtistsRequest>(SPOTIFY_GQL_ENDPOINT, request)
    }

    suspend fun albums(
        searchTerm: String,
        offset: Int = 0,
        limit: Int = 30,
        includePreReleases: Boolean = false,
        numberOfTopResults: Int = 20,
        includeAudiobooks: Boolean = true,
        includeAuthors: Boolean = false,
    ): SearchResponse? {
        val request = SearchAlbumsRequest(
            variables = SearchAlbumsVariables(
                searchTerm = searchTerm,
                offset = offset,
                limit = limit,
                includePreReleases = includePreReleases,
                numberOfTopResults = numberOfTopResults,
                includeAudiobooks = includeAudiobooks,
                includeAuthors = includeAuthors,
            ),
            extensions = GQLRequestExtensions(
                persistedQuery = PersistedQuery(
                    version = 1,
                    sha256Hash = "a71d2c993fc98e1c880093738a55a38b57e69cc4ce5a8c113e6c5920f9513ee2",
                ),
            ),
        )
        return post<SearchResponse, SearchAlbumsRequest>(SPOTIFY_GQL_ENDPOINT, request)
    }

    suspend fun users(
        searchTerm: String,
        offset: Int = 0,
        limit: Int = 30,
        includePreReleases: Boolean = false,
        numberOfTopResults: Int = 20,
        includeAudiobooks: Boolean = true,
        includeAuthors: Boolean = false,
    ): SearchResponse? {
        val request = SearchUsersRequest(
            variables = SearchUsersVariables(
                searchTerm = searchTerm,
                offset = offset,
                limit = limit,
                includePreReleases = includePreReleases,
                numberOfTopResults = numberOfTopResults,
                includeAudiobooks = includeAudiobooks,
                includeAuthors = includeAuthors,
            ),
            extensions = GQLRequestExtensions(
                persistedQuery = PersistedQuery(
                    version = 1,
                    sha256Hash = "d3f7547835dc86a4fdf3997e0f79314e7580eaf4aaf2f4cb1e71e189c5dfcb1f",
                ),
            ),
        )
        return post<SearchResponse, SearchUsersRequest>(SPOTIFY_GQL_ENDPOINT, request)
    }
}
