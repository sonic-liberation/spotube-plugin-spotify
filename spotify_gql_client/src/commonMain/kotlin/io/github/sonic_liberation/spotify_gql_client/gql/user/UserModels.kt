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

import io.github.sonic_liberation.spotify_gql_client.gql.GQLRequestExtensions
import io.github.sonic_liberation.spotify_gql_client.gql.ImageSource
import kotlinx.serialization.Serializable

@Serializable
data class ProfileRequest(
    val variables: Map<String, String> = emptyMap(),
    val operationName: String,
    val extensions: GQLRequestExtensions,
)

@Serializable
data class ProfileResponse(
    val data: Data,
) {
    @Serializable
    data class Data(
        val me: Me,
    ) {
        @Serializable
        data class Me(
            val profile: Profile,
        ) {
            @Serializable
            data class Profile(
                val name: String,
                val username: String,
                val uri: String,
                val avatarBackgroundColor: Int?,
                val avatar: Avatar?,
            ) {
                @Serializable
                data class Avatar(
                    val sources: List<ImageSource>,
                )
            }
        }
    }

}

