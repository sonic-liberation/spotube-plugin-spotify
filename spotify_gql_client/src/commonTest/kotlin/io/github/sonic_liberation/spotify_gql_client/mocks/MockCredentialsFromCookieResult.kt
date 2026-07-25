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

package io.github.sonic_liberation.spotify_gql_client.mocks

import dev.krtirtho.plugin_interfaces.host_apis.Cookie
import io.github.sonic_liberation.spotify_gql_client.BuildKonfig
import io.github.sonic_liberation.spotify_gql_client.gql.CredentialsFromCookieResult
import io.github.sonic_liberation.spotify_gql_client.gql.SpotifyGQLBaseClient
import kotlin.time.Clock
fun getMockCredentialsFromCookieResult(): CredentialsFromCookieResult {
    return CredentialsFromCookieResult(
        accessToken = BuildKonfig.ACCESS_TOKEN,
        cookies = listOf(
            Cookie(
                name = "sp_dc",
                value = BuildKonfig.SP_DC_COOKIE,
                domain = ".spotify.com",
                path = "/",
                secure = true,
                httpOnly = true,
            ),
            Cookie(
                name = "sp_t",
                value = BuildKonfig.SP_T_COOKIE,
                domain = ".spotify.com",
                path = "/",
                secure = true,
                httpOnly = true,
            )
        ),
        // unneeded
        expiration = Clock.System.now().epochSeconds + 3600 * 1000, // 1 hour from now
    )
}

fun initMockCredentialsFromCookieResult() {
    SpotifyGQLBaseClient.credentials = getMockCredentialsFromCookieResult()
}

fun getSpTFromMockCredentials(): String {
    val credentials = getMockCredentialsFromCookieResult()
    val spTCookie = credentials.cookies.find { it.name == "sp_t" }
    return spTCookie?.value ?: throw Exception("sp_t cookie not found in mock credentials")
}