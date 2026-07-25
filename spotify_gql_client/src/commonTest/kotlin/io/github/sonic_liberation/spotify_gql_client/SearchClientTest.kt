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
import io.github.sonic_liberation.spotify_gql_client.gql.search.SearchClient
import io.github.sonic_liberation.spotify_gql_client.loader.ResourceLoader
import io.github.sonic_liberation.spotify_gql_client.mocks.MockHTTPClientAPI
import io.github.sonic_liberation.spotify_gql_client.mocks.MockSystemInfoAPI
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SearchClientTest {
    private val httpClient = MockHTTPClientAPI()
    private val search = SearchClient(httpClient)
    private val loader = ResourceLoader()

    @Test
    fun `search deserializes response correctly`() = runTest {
        val mockResponseJsonStr = loader.readText("search_200.json")
        httpClient.mockPostResponse = HttpResponse(
            statusCode = 200,
            headers = emptyMap(),
            body = mockResponseJsonStr
        )
        val res = search.search(searchTerm = "Twenty One Pilots")
        assertEquals(true, res != null)
    }

    @Test
    fun `tracksV2 deserializes response correctly`() = runTest {
        val mockResponseJsonStr = loader.readText("tracksV2_200.json")
        httpClient.mockPostResponse = HttpResponse(
            statusCode = 200,
            headers = emptyMap(),
            body = mockResponseJsonStr
        )
        val res = search.tracksV2(searchTerm = "Stressed Out")
        assertEquals(true, res != null)
    }

    @Test
    fun `playlists deserializes response correctly`() = runTest {
        val mockResponseJsonStr = loader.readText("playlists_200.json")
        httpClient.mockPostResponse = HttpResponse(
            statusCode = 200,
            headers = emptyMap(),
            body = mockResponseJsonStr
        )
        val res = search.playlists(searchTerm = "Twenty One Pilots")
        assertEquals(true, res != null)
    }

    @Test
    fun `artists deserializes response correctly`() = runTest {
        val mockResponseJsonStr = loader.readText("artists_200.json")
        httpClient.mockPostResponse = HttpResponse(
            statusCode = 200,
            headers = emptyMap(),
            body = mockResponseJsonStr
        )
        val res = search.artists(searchTerm = "Twenty One Pilots")
        assertEquals(true, res != null)
    }

    @Test
    fun `albums deserializes response correctly`() = runTest {
        val mockResponseJsonStr = loader.readText("albums_200.json")
        httpClient.mockPostResponse = HttpResponse(
            statusCode = 200,
            headers = emptyMap(),
            body = mockResponseJsonStr
        )
        val res = search.albums(searchTerm = "Blurryface")
        assertEquals(true, res != null)
    }
}