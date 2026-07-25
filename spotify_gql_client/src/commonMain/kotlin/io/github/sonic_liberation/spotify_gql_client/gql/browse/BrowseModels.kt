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

import io.github.sonic_liberation.spotify_gql_client.gql.AddedAt
import io.github.sonic_liberation.spotify_gql_client.gql.AlbumResponseWrapper
import io.github.sonic_liberation.spotify_gql_client.gql.BrowseSectionContainer
import io.github.sonic_liberation.spotify_gql_client.gql.GQLRequestExtensions
import io.github.sonic_liberation.spotify_gql_client.gql.HomeResponsePayload
import io.github.sonic_liberation.spotify_gql_client.gql.HomeSectionCollection
import io.github.sonic_liberation.spotify_gql_client.gql.PagingInfo
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class HomeRequest(
    val variables: HomeVariables,
    val operationName: String = "home",
    val extensions: GQLRequestExtensions,
)

@Serializable
data class HomeVariables(
    val timeZone: String,
    val sp_t: String,
    val facet: String = "",
    val sectionItemsLimit: Int = 50,
)

@Serializable
data class SectionRequest(
    val variables: SectionVariables,
    val operationName: String = "homeSection",
    val extensions: GQLRequestExtensions,
)

@Serializable
data class SectionVariables(
    val uri: String,
    val timeZone: String,
    val sp_t: String,
    val sectionItemsOffset: Int = 0,
    val sectionItemsLimit: Int = 50,
)

@Serializable
data class ReleasesRequest(
    val variables: ReleasesVariables,
    val operationName: String = "queryWhatsNewFeed",
    val extensions: GQLRequestExtensions,
)

@Serializable
data class ReleasesVariables(
    val offset: Int = 0,
    val limit: Int = 20,
    val onlyUnPlayedItems: Boolean = false,
    val includedContentTypes: List<String> = listOf("ALBUM", "SINGLE", "EP"),
)

// Response models
@Serializable
data class BrowseResponse(
    val data: Data,
) {
    @Serializable
    data class Data(
        val home: HomeResponsePayload
    )
}

@Serializable
data class BrowseSectionResponse(
    val data: Data,
) {
    @Serializable
    data class Data(
        val homeSections: HomeSectionCollection
    )
}


@Serializable
data class BrowseAllRequest(
    val variables: BrowseAllVariables,
    val operationName: String = "browseAll",
    val extensions: GQLRequestExtensions,
)

@Serializable
data class BrowseAllVariables(
    val pagePagination: Pagination,
    val sectionPagination: Pagination,
    val browseEndUserIntegration: String = "INTEGRATION_WEB_PLAYER",
)

@Serializable
data class Pagination(
    val offset: Int,
    val limit: Int,
)

@Serializable
data class BrowsePageRequest(
    val variables: BrowsePageVariables,
    val operationName: String = "browsePage",
    val extensions: GQLRequestExtensions,
)

@Serializable
data class BrowsePageVariables(
    val pagePagination: Pagination,
    val sectionPagination: Pagination,
    val uri: String,
    val browseEndUserIntegration: String = "INTEGRATION_WEB_PLAYER",
    val includeEpisodeContentRatingsV2: Boolean = true,
)

@Serializable
data class BrowseAllResponse(
    val data: Data,
) {
    @Serializable
    data class Data(
        val browseStart: BrowseSectionContainer,
    )
}

@Serializable
data class BrowsePageResponse(
    val data: Data,
) {
    @Serializable
    data class Data(
        val browse: BrowseSectionContainer,
    )
}

@Serializable
data class BrowseReleaseResponse(
    val data: Data? = null,
    val errors: List<JsonObject>? = null,
) {
    @Serializable
    data class Data(
        val whatsNewFeedItems: WhatsNewFeedItems
    )

    @Serializable
    data class WhatsNewFeedItems(
        val items: List<Item>,
        val totalCount: Int,
        val pagingInfo: PagingInfo
    )

    @Serializable
    data class Item(
        val content: AlbumResponseWrapper,
        val id: String,
        val state: ItemState,
        val timestamp: AddedAt? = null,
    )

    @Serializable
    data class ItemState(
        val state: String,
        val timestamp: AddedAt? = null,
    )
}