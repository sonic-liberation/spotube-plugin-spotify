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
import io.github.sonic_liberation.spotify_gql_client.gql.artist.ArtistClient
import io.github.sonic_liberation.spotify_gql_client.loader.ResourceLoader
import io.github.sonic_liberation.spotify_gql_client.mocks.MockHTTPClientAPI
import io.github.sonic_liberation.spotify_gql_client.mocks.MockSystemInfoAPI
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ArtistClientTest {
    private val httpClient = MockHTTPClientAPI()
    private val artist = ArtistClient(httpClient)
    private val loader = ResourceLoader()

    @Test
    fun `getArtist deserializes response correctly`() = runTest {
        val mockResponseJsonStr = loader.readText("getArtist_200.json")
        httpClient.mockPostResponse = HttpResponse(
            statusCode = 200,
            headers = emptyMap(),
            body = mockResponseJsonStr
        )
        val res = artist.getArtist(uri = "spotify:artist:3YQKmKGau1PzlVlkL1iodx")
        assertEquals(true, res != null)
    }

    @Test
    fun `follow artist deserializes response correctly`() = runTest {
        val mockResponseJsonStr = loader.readText("followArtist_200.json")
        httpClient.mockPostResponse = HttpResponse(
            statusCode = 200,
            headers = emptyMap(),
            body = mockResponseJsonStr
        )
        val res = artist.follow(listOf("spotify:artist:3YQKmKGau1PzlVlkL1iodx"))
        assertEquals(true, res != null)
    }

    @Test
    fun `unfollow artist deserializes response correctly`() = runTest {
        val mockResponseJsonStr = loader.readText("unfollowArtist_200.json")
        httpClient.mockPostResponse = HttpResponse(
            statusCode = 200,
            headers = emptyMap(),
            body = mockResponseJsonStr
        )
        val res = artist.unfollow(listOf("spotify:artist:3YQKmKGau1PzlVlkL1iodx"))
        assertEquals(true, res != null)
    }

    @Test
    fun `discographyOverview deserializes response correctly`() = runTest {
        val mockResponseJsonStr = loader.readText("discographyOverview_200.json")
        httpClient.mockPostResponse = HttpResponse(
            statusCode = 200,
            headers = emptyMap(),
            body = mockResponseJsonStr
        )
        val res = artist.discographyOverview(uri = "spotify:artist:3YQKmKGau1PzlVlkL1iodx")
        assertEquals(true, res != null)
    }

    @Test
    fun `discographyAll deserializes response correctly`() = runTest {
        val mockResponseJsonStr = loader.readText("discographyAll_200.json")
        httpClient.mockPostResponse = HttpResponse(
            statusCode = 200,
            headers = emptyMap(),
            body = mockResponseJsonStr
        )
        val res = artist.discographyAll(uri = "spotify:artist:3YQKmKGau1PzlVlkL1iodx")
        assertEquals(true, res != null)
    }

    @Test
    fun `appearsOnPlaylists deserializes response correctly`() = runTest {
        val mockResponseJsonStr = loader.readText("appearsOnPlaylists_200.json")
        httpClient.mockPostResponse = HttpResponse(
            statusCode = 200,
            headers = emptyMap(),
            body = mockResponseJsonStr
        )
        val res = artist.appearsOnPlaylists(uri = "spotify:artist:3YQKmKGau1PzlVlkL1iodx")
        assertEquals(true, res != null)
    }

    @Test
    fun `discoveredOnPlaylists deserializes response correctly`() = runTest {
        val mockResponseJsonStr = loader.readText("discoveredOnPlaylists_200.json")
        httpClient.mockPostResponse = HttpResponse(
            statusCode = 200,
            headers = emptyMap(),
            body = mockResponseJsonStr
        )
        val res = artist.discoveredOnPlaylists(uri = "spotify:artist:3YQKmKGau1PzlVlkL1iodx")
        assertEquals(true, res != null)
    }

    @Test
    fun `featuredPlaylists deserializes response correctly`() = runTest {
        val mockResponseJsonStr = loader.readText("featuredPlaylists_200.json")
        httpClient.mockPostResponse = HttpResponse(
            statusCode = 200,
            headers = emptyMap(),
            body = mockResponseJsonStr
        )
        val res = artist.featuredPlaylists(uri = "spotify:artist:3YQKmKGau1PzlVlkL1iodx")
        assertEquals(true, res != null)
    }

    @Test
    fun `relatedArtists deserializes response correctly`() = runTest {
        val mockResponseJsonStr = loader.readText("relatedArtists_200.json")
        httpClient.mockPostResponse = HttpResponse(
            statusCode = 200,
            headers = emptyMap(),
            body = mockResponseJsonStr
        )
        val res = artist.relatedArtists(uri = "spotify:artist:3YQKmKGau1PzlVlkL1iodx")
        assertEquals(true, res != null)
    }
}