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

package io.github.sonic_liberation.spotify_gql_client

import dev.krtirtho.plugin_interfaces.host_apis.HttpResponse
import io.github.sonic_liberation.spotify_gql_client.gql.user.UserClient
import io.github.sonic_liberation.spotify_gql_client.loader.ResourceLoader
import io.github.sonic_liberation.spotify_gql_client.mocks.MockHTTPClientAPI
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class UserClientTest {
    private val httpClient = MockHTTPClientAPI()
    private val user = UserClient(httpClient)
    private val loader = ResourceLoader()

    @Test
    fun `UserClient profileAttributes Deserialization from Spotify API response`() = runTest {
        val mockResponseJsonStr = loader.readText("profile_200.json")

        httpClient.mockPostResponse = HttpResponse(
            statusCode = 200,
            headers = emptyMap(),
            body = mockResponseJsonStr
        )

        val res = user.profileAttributes()

        assertEquals(true, res != null)
        assertEquals("I am Groot", res?.data?.me?.profile?.name)
        assertEquals("7xp96b5ec9w6mjjn9xuxt6oui", res?.data?.me?.profile?.username)
    }
}