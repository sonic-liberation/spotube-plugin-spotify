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

package io.github.sonic_liberation.spotify_gql_client.gql.artist

import io.github.sonic_liberation.spotify_gql_client.gql.Artist
import io.github.sonic_liberation.spotify_gql_client.gql.GQLRequestExtensions
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetArtistRequest(
    val variables: GetArtistVariables,
    val operationName: String = "queryArtistOverview",
    val extensions: GQLRequestExtensions,
)

@Serializable
data class GetArtistVariables(
    val uri: String,
    val locale: String = "",
    val preReleaseV2: Boolean = false,
)

@Serializable
data class FollowArtistRequest(
    val variables: FollowArtistVariables,
    val operationName: String = "addToLibrary",
    val extensions: GQLRequestExtensions,
)

@Serializable
data class FollowArtistVariables(
    val uris: List<String>,
)

@Serializable
data class UnfollowArtistRequest(
    val variables: UnfollowArtistVariables,
    val operationName: String = "removeFromLibrary",
    val extensions: GQLRequestExtensions,
)

@Serializable
data class UnfollowArtistVariables(
    val uris: List<String>,
)

@Serializable
data class DiscographyOverviewRequest(
    val variables: DiscographyOverviewVariables,
    val operationName: String = "queryArtistDiscographyOverview",
    val extensions: GQLRequestExtensions,
)

@Serializable
data class DiscographyOverviewVariables(
    val uri: String,
)

@Serializable
data class DiscographyAllRequest(
    val variables: DiscographyAllVariables,
    val operationName: String = "queryArtistDiscographyAll",
    val extensions: GQLRequestExtensions,
)

@Serializable
data class DiscographyAllVariables(
    val uri: String,
    val offset: Int = 0,
    val limit: Int = 20,
    val order: String = "DATE_DESC",
)

@Serializable
data class AppearsOnPlaylistsRequest(
    val variables: AppearsOnPlaylistsVariables,
    val operationName: String = "queryArtistAppearsOn",
    val extensions: GQLRequestExtensions,
)

@Serializable
data class AppearsOnPlaylistsVariables(
    val uri: String,
)

@Serializable
data class DiscoveredOnPlaylistsRequest(
    val variables: DiscoveredOnPlaylistsVariables,
    val operationName: String = "queryArtistDiscoveredOn",
    val extensions: GQLRequestExtensions,
)

@Serializable
data class DiscoveredOnPlaylistsVariables(
    val uri: String,
)

@Serializable
data class FeaturedPlaylistsRequest(
    val variables: FeaturedPlaylistsVariables,
    val operationName: String = "queryArtistFeaturing",
    val extensions: GQLRequestExtensions,
)

@Serializable
data class FeaturedPlaylistsVariables(
    val uri: String,
)

@Serializable
data class RelatedArtistsRequest(
    val variables: RelatedArtistsVariables,
    val operationName: String = "queryArtistRelated",
    val extensions: GQLRequestExtensions,
)

@Serializable
data class RelatedArtistsVariables(
    val uri: String,
)

// Response models

@Serializable
data class ArtistResponse(
    val data: Data,
) {
    @Serializable
    data class Data(
        val artistUnion: Artist,
    )
}

@Serializable
data class MutationResponse(
    val data: MutationData,
)

@Serializable
data class MutationData(
    val addLibraryItems: MutationResult? = null,
    val removeLibraryItems: MutationResult? = null,
)

@Serializable
data class MutationResult(
    @SerialName("__typename") val typename: String? = null,
)
