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

package io.github.sonic_liberation.spotube_plugin_spotify.core

import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.common.PaginationStrategy
import kotlinx.serialization.Serializable

@Serializable
data class ServerTimeResponse(val serverTime: Long)

@Serializable
data class NuanceInfo(val v: Int, val s: String)

@Serializable
data class NuanceFile(val content: String)

@Serializable
data class NuancesResponse(val files: Map<String, NuanceFile>)

@Serializable
data class SpotifyTokenResponse(
    val accessToken: String, val accessTokenExpirationTimestampMs: Long, val isAnonymous: Boolean
)

@Serializable
data class GetTokenResponse(
    val body: SpotifyTokenResponse, val headers: Map<String, String>
)

internal fun PaginationStrategy?.getOffsetOrDefault(): PaginationStrategy.Offset {
    return when (this) {
        is PaginationStrategy.Offset -> this
        else -> PaginationStrategy.Offset(0, 50)
    }
}

internal fun PaginationStrategy.nextOffset(size: Int): PaginationStrategy? {
    if (this is PaginationStrategy.Offset) {
        return if (size < limit) null else PaginationStrategy.Offset(offset + limit, limit)
    }
    return null
}