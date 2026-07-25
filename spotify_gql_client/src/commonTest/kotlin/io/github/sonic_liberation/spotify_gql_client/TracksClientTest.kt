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
import io.github.sonic_liberation.spotify_gql_client.gql.tracks.TracksClient
import io.github.sonic_liberation.spotify_gql_client.loader.ResourceLoader
import io.github.sonic_liberation.spotify_gql_client.mocks.MockHTTPClientAPI
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TracksClientTest {
    private val httpClient = MockHTTPClientAPI()
    private val tracks = TracksClient(httpClient)
    private val loader = ResourceLoader()

    @Test
    fun `TracksClient saveTracks Deserialization from Spotify API response`() = runTest {
        val mockResponseJsonStr = loader.readText("save_200.json")

        httpClient.mockPostResponse = HttpResponse(
            statusCode = 200,
            headers = emptyMap(),
            body = mockResponseJsonStr
        )

        val res = tracks.saveTracks(listOf("spotify:track:11dFghVXANMlKmJXsNCbNl"))

        assertNotNull(res)
        assertNotNull(res.data.addToLibrary)
    }

    @Test
    fun `TracksClient unsaveTracks Deserialization from Spotify API response`() = runTest {
        val mockResponseJsonStr = loader.readText("unsave_200.json")

        httpClient.mockPostResponse = HttpResponse(
            statusCode = 200,
            headers = emptyMap(),
            body = mockResponseJsonStr
        )

        val res = tracks.unsaveTracks(listOf("spotify:track:11dFghVXANMlKmJXsNCbNl"))

        assertNotNull(res)
        assertNotNull(res.data.applyCurations)
    }

    @Test
    fun `TracksClient areTracksSaved Deserialization from Spotify API response`() = runTest {
        val mockResponseJsonStr = loader.readText("isSaved_200.json")

        httpClient.mockPostResponse = HttpResponse(
            statusCode = 200,
            headers = emptyMap(),
            body = mockResponseJsonStr
        )

        val res = tracks.areTracksSaved(
            listOf(
                "spotify:track:5CiPDLxXmUG7Fk5yjlNy9n",
                "spotify:track:0BBLwKdU4vn0HDSi1C8xDZ"
            )
        )

        assertNotNull(res)
        assertEquals(10, res.data.lookup.size)
    }

    @Test
    fun `TracksClient radioPlaylist Deserialization from Spotify API response`() = runTest {
        val mockResponseJsonStr = loader.readText("radio_200.json")

        httpClient.mockGetResponse = HttpResponse(
            statusCode = 200,
            headers = emptyMap(),
            body = mockResponseJsonStr
        )

        val res = tracks.radioPlaylist("0Ji9UonfwC90rbZ4IaQhOb")

        assertNotNull(res)
    }

    @Test
    fun `TracksClient getTrack Deserialization from Spotify API response`() = runTest {
        val mockResponseJsonStr = loader.readText("getTrack_200.json")

        httpClient.mockPostResponse = HttpResponse(
            statusCode = 200,
            headers = emptyMap(),
            body = mockResponseJsonStr
        )

        val res = tracks.getTrack("spotify:track:0Ji9UonfwC90rbZ4IaQhOb")

        assertNotNull(res)
        assertNotNull(res.data.trackUnion)
    }
}