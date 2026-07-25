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
import io.github.sonic_liberation.spotify_gql_client.gql.playlist.PlaylistClient
import io.github.sonic_liberation.spotify_gql_client.loader.ResourceLoader
import io.github.sonic_liberation.spotify_gql_client.mocks.MockHTTPClientAPI
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class PlaylistClientTest {
    private val httpClient = MockHTTPClientAPI()
    private val playlist = PlaylistClient(httpClient)
    private val loader = ResourceLoader()

    @Test
    fun `PlaylistClient fetchPlaylist Deserialization from Spotify API response`() = runTest {
        val mockResponseJsonStr = loader.readText("fetchPlaylist_200.json")

        httpClient.mockPostResponse = HttpResponse(
            statusCode = 200,
            headers = emptyMap(),
            body = mockResponseJsonStr
        )

        val res = playlist.fetchPlaylist("spotify:playlist:37i9dQZF1E4yrYiQJfy370")

        assertNotNull(res)
    }

    @Test
    fun `PlaylistClient followPlaylist Deserialization from Spotify API response`() = runTest {
        val mockResponseJsonStr = loader.readText("followPlaylist_200.json")

        httpClient.mockPostResponse = HttpResponse(
            statusCode = 200,
            headers = emptyMap(),
            body = mockResponseJsonStr
        )

        val res = playlist.followPlaylist(listOf("spotify:playlist:37i9dQZF1E4oLwlqvXisyU"))

        assertNotNull(res)
    }

    @Test
    fun `PlaylistClient unfollowPlaylist Deserialization from Spotify API response`() = runTest {
        val mockResponseJsonStr = loader.readText("unfollowPlaylist_200.json")

        httpClient.mockPostResponse = HttpResponse(
            statusCode = 200,
            headers = emptyMap(),
            body = mockResponseJsonStr
        )

        val res = playlist.unfollowPlaylist(listOf("spotify:playlist:37i9dQZF1E4oLwlqvXisyU"))

        assertNotNull(res)
    }

    @Test
    fun `PlaylistClient addTracksToPlaylist Deserialization from Spotify API response`() = runTest {
        val mockResponseJsonStr = loader.readText("addTracksToPlaylist_200.json")

        httpClient.mockPostResponse = HttpResponse(
            statusCode = 200,
            headers = emptyMap(),
            body = mockResponseJsonStr
        )

        val res = playlist.addTracksToPlaylist(
            "spotify:playlist:26ElOYDflVLcKEH4ngO6Kj",
            listOf("spotify:track:43ee3gqWBlPKe2MeGJ2S6I")
        )

        assertNotNull(res)
    }

    @Test
    fun `PlaylistClient removeTracksFromPlaylist Deserialization from Spotify API response`() = runTest {
        val mockResponseJsonStr = loader.readText("removeTracksFromPlaylist_200.json")

        httpClient.mockPostResponse = HttpResponse(
            statusCode = 200,
            headers = emptyMap(),
            body = mockResponseJsonStr
        )

        val res = playlist.removeTracksFromPlaylist(
            "spotify:playlist:26ElOYDflVLcKEH4ngO6Kj",
            listOf("43ca9349729714e5")
        )

        assertNotNull(res)
    }

    @Test
    fun `PlaylistClient createPlaylist Deserialization from Spotify API response`() = runTest {
        val mockResponseJsonStr = loader.readText("createPlaylist_200.json")

        httpClient.mockPostResponse = HttpResponse(
            statusCode = 200,
            headers = emptyMap(),
            body = mockResponseJsonStr
        )

        val res = playlist.createPlaylist("My Test Playlist", "Test description")

        assertNotNull(res)
    }

    @Test
    fun `PlaylistClient updatePlaylist Deserialization from Spotify API response`() = runTest {
        val mockResponseJsonStr = loader.readText("updatePlaylist_200.json")

        httpClient.mockPostResponse = HttpResponse(
            statusCode = 200,
            headers = emptyMap(),
            body = mockResponseJsonStr
        )

        val res = playlist.updatePlaylist("34bc8BvPoIyXEk24bZjizQ", "New Name", "New description")

        assertNotNull(res)
    }

    @Test
    fun `PlaylistClient changePermission Deserialization from Spotify API response`() = runTest {
        val mockResponseJsonStr = loader.readText("changePermission_200.json")

        httpClient.mockPostResponse = HttpResponse(
            statusCode = 200,
            headers = emptyMap(),
            body = mockResponseJsonStr
        )

        val res = playlist.changePermission("zwp96b5ec9w7mjun9duxt6oui", "spotify:playlist:7HJcrk1pwIPOkjfnEWRcF7", true)

        assertNotNull(res)
    }
}