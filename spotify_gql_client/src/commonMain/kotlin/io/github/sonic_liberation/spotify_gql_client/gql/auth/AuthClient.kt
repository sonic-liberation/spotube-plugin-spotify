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

package io.github.sonic_liberation.spotify_gql_client.gql.auth

import dev.krtirtho.plugin_interfaces.host_apis.HttpClientAPI
import io.github.sonic_liberation.spotify_gql_client.gql.SpotifyGQLBaseClient

class AuthClient(client: HttpClientAPI) : SpotifyGQLBaseClient(client) {
    suspend fun token(
        reason: String = "transport",
        productType: String = "premium",
        totp: String? = null,
        totpServer: String? = null,
        totpVer: String? = null,
        sTime: String? = null,
        cTime: String? = null,
        buildVer: String? = null,
        buildDate: String? = null,
        totpValidUntil: String? = null,
    ): TokenResponse? {
        val queryParams = mutableMapOf(
            "reason" to reason,
            "productType" to productType,
        )
        totp?.let { queryParams["totp"] = it }
        totpServer?.let { queryParams["totpServer"] = it }
        totpVer?.let { queryParams["totpVer"] = it }
        sTime?.let { queryParams["sTime"] = it }
        cTime?.let { queryParams["cTime"] = it }
        buildVer?.let { queryParams["buildVer"] = it }
        buildDate?.let { queryParams["buildDate"] = it }
        totpValidUntil?.let { queryParams["totpValidUntil"] = it }

        val url = buildUrl(SPOTIFY_TOKEN_ENDPOINT, queryParams)
        return get<TokenResponse>(url)
    }

    suspend fun serverTime(): ServerTimeResponse? {
        return get<ServerTimeResponse>(SPOTIFY_SERVER_TIME_ENDPOINT)
    }

    private fun buildUrl(baseUrl: String, params: Map<String, String>): String {
        if (params.isEmpty()) return baseUrl
        val queryString = params.entries.joinToString("&") { "${it.key}=${it.value}" }
        return "$baseUrl?$queryString"
    }
}
