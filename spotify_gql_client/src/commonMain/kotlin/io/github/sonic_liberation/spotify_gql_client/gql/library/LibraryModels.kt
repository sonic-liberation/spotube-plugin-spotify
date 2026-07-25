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

import io.github.sonic_liberation.spotify_gql_client.gql.GQLRequestExtensions
import io.github.sonic_liberation.spotify_gql_client.gql.LibraryPage
import io.github.sonic_liberation.spotify_gql_client.gql.UserLibraryTrackPage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class LibraryV3Request(
    val variables: LibraryV3Variables,
    val operationName: String = "libraryV3",
    val extensions: GQLRequestExtensions,
)

@Serializable
data class LibraryV3Variables(
    val filters: List<String>,
    val order: String? = null,
    val textFilter: String = "",
    val features: List<String> = emptyList(),
    val limit: Int = 50,
    val offset: Int = 0,
    val flatten: Boolean = true,
    val expandedFolders: List<String> = emptyList(),
    val folderUri: String? = null,
    val includeFoldersWhenFlattening: Boolean = true,
)

@Serializable
data class SavedTracksRequest(
    val variables: SavedTracksVariables,
    val operationName: String = "fetchLibraryTracks",
    val extensions: GQLRequestExtensions,
)

@Serializable
data class SavedTracksVariables(
    val offset: Int = 0,
    val limit: Int = 50,
)

@Serializable
data class AreEntitiesInLibraryRequest(
    val variables: AreEntitiesInLibraryVariables,
    val operationName: String = "areEntitiesInLibrary",
    val extensions: GQLRequestExtensions,
)

@Serializable
data class AreEntitiesInLibraryVariables(
    val uris: List<String>,
)


@Serializable
data class LibraryResponse(
    val data: Data,
    val errors: List<JsonObject>? = null,
) {
    @Serializable
    data class Data(
        val me: Me? = null,
    )

    @Serializable
    data class Me(
        val libraryV3: LibraryPage
    )

}
@Serializable
data class LibrarySavedTracksResponse(
    val data: Data,
) {
    @Serializable
    data class Data(
        val me: Me,
    )

    @Serializable
    data class Me(
        val library: UserLibraryTrackPage
    )
}

@Serializable
data class LibraryAreEntitiesInLibraryResponse(
    val data: Data
) {
    @Serializable
    data class Data(
        val lookup: List<EntityWrapper>
    ) {
        @Serializable
        data class EntityWrapper(
            @SerialName("__typename")
            val typename: String,
            val data: EntityStatus? = null,
        )

        @Serializable
        data class EntityStatus(
            @SerialName("__typename")
            val typename: String,
            val saved: Boolean,
        )
    }
}