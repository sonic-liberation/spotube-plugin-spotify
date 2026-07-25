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
import io.github.sonic_liberation.spotify_gql_client.gql.library.LibraryClient
import io.github.sonic_liberation.spotify_gql_client.loader.ResourceLoader
import io.github.sonic_liberation.spotify_gql_client.mocks.MockHTTPClientAPI
import io.github.sonic_liberation.spotify_gql_client.mocks.MockSystemInfoAPI
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class LibraryClientTest {
    private val httpClient = MockHTTPClientAPI()
    private val library = LibraryClient(httpClient)
    private val loader = ResourceLoader()

    @Test
    fun `albums deserializes response correctly`() = runTest {
        val mockResponseJsonStr = loader.readText("libraryAlbums_200.json")
        httpClient.mockPostResponse = HttpResponse(
            statusCode = 200,
            headers = emptyMap(),
            body = mockResponseJsonStr
        )
        val res = library.albums()
        assertNotNull(res)
    }

    @Test
    fun `artists deserializes response correctly`() = runTest {
        val mockResponseJsonStr = loader.readText("libraryArtists_200.json")
        httpClient.mockPostResponse = HttpResponse(
            statusCode = 200,
            headers = emptyMap(),
            body = mockResponseJsonStr
        )
        val res = library.artists()
        assertNotNull(res)
    }

    @Test
    fun `playlists deserializes response correctly`() = runTest {
        val mockResponseJsonStr = loader.readText("libraryPlaylists_200.json")
        httpClient.mockPostResponse = HttpResponse(
            statusCode = 200,
            headers = emptyMap(),
            body = mockResponseJsonStr
        )
        val res = library.playlists()
        assertNotNull(res)
    }

    @Test
    fun `savedTracks deserializes response correctly`() = runTest {
        val mockResponseJsonStr = loader.readText("librarySavedTracks_200.json")
        httpClient.mockPostResponse = HttpResponse(
            statusCode = 200,
            headers = emptyMap(),
            body = mockResponseJsonStr
        )
        val res = library.savedTracks()
        assertNotNull(res)
    }

    @Test
    fun `areEntitiesInLibrary deserializes response correctly`() = runTest {
        val mockResponseJsonStr = loader.readText("libraryAreEntitiesInLibrary_200.json")
        httpClient.mockPostResponse = HttpResponse(
            statusCode = 200,
            headers = emptyMap(),
            body = mockResponseJsonStr
        )
        val res = library.areEntitiesInLibrary(
            listOf(
                "spotify:album:0Rkv5iqjF2uenfL0OVB8hg",
                "spotify:artist:06eRdiCBgFUhiuFjei0eH2"
            )
        )
        assertNotNull(res)
    }
}