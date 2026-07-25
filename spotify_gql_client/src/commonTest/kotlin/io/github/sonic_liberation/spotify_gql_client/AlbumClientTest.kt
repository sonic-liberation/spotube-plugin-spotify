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
import io.github.sonic_liberation.spotify_gql_client.gql.album.AlbumClient
import io.github.sonic_liberation.spotify_gql_client.loader.ResourceLoader
import io.github.sonic_liberation.spotify_gql_client.mocks.MockHTTPClientAPI
import io.github.sonic_liberation.spotify_gql_client.mocks.MockSystemInfoAPI
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AlbumClientTest {
    private val httpClient = MockHTTPClientAPI()
    private val album = AlbumClient(httpClient)
    private val loader = ResourceLoader()

    @Test
    fun `getAlbum deserializes response correctly`() = runTest {
        val mockResponseJsonStr = loader.readText("getAlbum_200.json")
        httpClient.mockPostResponse = HttpResponse(
            statusCode = 200,
            headers = emptyMap(),
            body = mockResponseJsonStr
        )
        val res = album.getAlbum(uri = "spotify:album:1El3k8dU3sKyoGUeuyrolH")
        assertEquals(true, res != null)
    }

    @Test
    fun `saveAlbums deserializes response correctly`() = runTest {
        val mockResponseJsonStr = loader.readText("saveAlbums_200.json")
        httpClient.mockPostResponse = HttpResponse(
            statusCode = 200,
            headers = emptyMap(),
            body = mockResponseJsonStr
        )
        val res = album.saveAlbums(listOf("spotify:album:1El3k8dU3sKyoGUeuyrolH"))
        assertEquals(true, res != null)
    }

    @Test
    fun `unsaveAlbums deserializes response correctly`() = runTest {
        val mockResponseJsonStr = loader.readText("unsaveAlbums_200.json")
        httpClient.mockPostResponse = HttpResponse(
            statusCode = 200,
            headers = emptyMap(),
            body = mockResponseJsonStr
        )
        val res = album.unsaveAlbums(listOf("spotify:album:1El3k8dU3sKyoGUeuyrolH"))
        assertEquals(true, res != null)
    }
}