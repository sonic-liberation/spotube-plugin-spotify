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

package io.github.sonic_liberation.spotify_gql_client.gql.browse

import dev.krtirtho.plugin_interfaces.host_apis.HttpClientAPI
import dev.krtirtho.plugin_interfaces.host_apis.SystemInformationAPI
import io.github.sonic_liberation.spotify_gql_client.gql.GQLRequestExtensions
import io.github.sonic_liberation.spotify_gql_client.gql.PersistedQuery
import io.github.sonic_liberation.spotify_gql_client.gql.SpotifyGQLBaseClient

class BrowseClient(client: HttpClientAPI, private val systemInfo: SystemInformationAPI) : SpotifyGQLBaseClient(client) {
    suspend fun home(
        spT: String,
        facet: String = "",
        sectionItemsLimit: Int = 50,
    ): BrowseResponse? {
        val request = HomeRequest(
            variables = HomeVariables(
                timeZone = systemInfo.getTimeZone(),
                sp_t = spT,
                facet = facet,
                sectionItemsLimit = sectionItemsLimit,
            ),
            extensions = GQLRequestExtensions(
                persistedQuery = PersistedQuery(
                    version = 1,
                    sha256Hash = "3357ffed7961629ba92b4e0a41514e4d5004a14355c964c23ce442205c9e44a1",
                ),
            ),
        )
        return post<BrowseResponse, HomeRequest>(SPOTIFY_GQL_ENDPOINT, request)
    }

    suspend fun section(
        uri: String,
        spT: String,
        sectionItemsOffset: Int = 0,
        sectionItemsLimit: Int = 50,
    ): BrowseSectionResponse? {
        val request = SectionRequest(
            variables = SectionVariables(
                uri = uri,
                timeZone = systemInfo.getTimeZone(),
                sp_t = spT,
                sectionItemsOffset = sectionItemsOffset,
                sectionItemsLimit = sectionItemsLimit,
            ),
            operationName = "homeSection",
            extensions = GQLRequestExtensions(
                persistedQuery = PersistedQuery(
                    version = 1,
                    sha256Hash = "d62af2714f2623c923cc9eeca4b9545b4363abaa9188a9e94e2b63b823419a2c",
                ),
            ),
        )
        return post<BrowseSectionResponse, SectionRequest>(SPOTIFY_GQL_ENDPOINT, request)
    }

    suspend fun releases(
        offset: Int = 0,
        limit: Int = 20,
        onlyUnPlayedItems: Boolean = false,
        includedContentTypes: List<String> = listOf("ALBUM"),
    ): BrowseReleaseResponse? {
        val request = ReleasesRequest(
            variables = ReleasesVariables(
                offset = offset,
                limit = limit,
                onlyUnPlayedItems = onlyUnPlayedItems,
                includedContentTypes = includedContentTypes,
            ),
            extensions = GQLRequestExtensions(
                persistedQuery = PersistedQuery(
                    version = 1,
                    sha256Hash = "3b53dede3c6054e8b7c962dd280eb6761c5d1c82b06b039f4110d76a62b4966b",
                ),
            ),
        )

        return post<BrowseReleaseResponse, ReleasesRequest>(SPOTIFY_GQL_ENDPOINT, request)
    }

    suspend fun browseAll(
        pageOffset: Int = 0,
        pageLimit: Int = 10,
        sectionOffset: Int = 0,
        sectionLimit: Int = 99,
    ): BrowseAllResponse? {
        val request = BrowseAllRequest(
            variables = BrowseAllVariables(
                pagePagination = Pagination(offset = pageOffset, limit = pageLimit),
                sectionPagination = Pagination(offset = sectionOffset, limit = sectionLimit),
            ),
            extensions = GQLRequestExtensions(
                persistedQuery = PersistedQuery(
                    version = 1,
                    sha256Hash = "dbd8b55e09a58afc52eab438bc228ba28fd72ac2f2148c6c26354980e4579001",
                ),
            ),
        )
        return post<BrowseAllResponse, BrowseAllRequest>(SPOTIFY_GQL_ENDPOINT, request)
    }

    suspend fun browsePage(
        uri: String,
        pageOffset: Int = 0,
        pageLimit: Int = 10,
        sectionOffset: Int = 0,
        sectionLimit: Int = 10,
    ): BrowsePageResponse? {
        val request = BrowsePageRequest(
            variables = BrowsePageVariables(
                pagePagination = Pagination(offset = pageOffset, limit = pageLimit),
                sectionPagination = Pagination(offset = sectionOffset, limit = sectionLimit),
                uri = uri,
            ),
            extensions = GQLRequestExtensions(
                persistedQuery = PersistedQuery(
                    version = 1,
                    sha256Hash = "f5c4e6d668f5716464a231c1cc8b22c1cbf6ad68b09929fd7de813a30581298b",
                ),
            ),
        )
        return post<BrowsePageResponse, BrowsePageRequest>(SPOTIFY_GQL_ENDPOINT, request)
    }
}
