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
import io.github.sonic_liberation.spotify_gql_client.gql.SpotifyGQLClient
import io.github.sonic_liberation.spotube_plugin_spotify.integration.RealHttpClientAPI
import io.github.sonic_liberation.spotube_plugin_spotify.mocks.MockSystemInfoAPI
import io.github.sonic_liberation.spotube_plugin_spotify.mocks.initMockCredentialsFromCookieResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RealMetadataTrackAPITest {
    val httpClient = RealHttpClientAPI()
    val spotifyGQLClient = SpotifyGQLClient(httpClient, MockSystemInfoAPI())
    val metadataTrackAPI = RealMetadataTrackAPI(spotifyGQLClient)

    init {
        initMockCredentialsFromCookieResult()
    }

    @Test
    fun `getTrack should return correct track details`() = runTest {
        val trackId = "0TlcczkVTGpinpkGJpT81L" // Drag Path - Twenty One Pilots
        val result = metadataTrackAPI.getTrack(trackId)

        assertEquals("Drag Path", result.title, "Expected track title to be 'Mr. Brightside'")
        assertTrue(result.artists.any { it.name == "Twenty One Pilots" }, "Expected artist to be 'Twenty One Pilots'")
        assertEquals(
            result.album?.thumbnails?.isNotEmpty(),
            true,
            "Expected at least one album thumbnail"
        )
    }

    @Test
    fun `savedTracks should return saved tracks with correct pagination`() = runTest {
        val pagination = PaginationStrategy.Offset(offset = 0, limit = 20)
        val result = metadataTrackAPI.savedTracks(pagination)

        assertTrue(result.items.isNotEmpty(), "Expected non-empty list of saved tracks")
        assertNotNull(
             result.nextPagination, "Expected nextPagination to be null, got ${result.nextPagination}"
        )
        result.items.forEach {
            assertTrue(it.title.isNotEmpty(), "Expected non-empty track title")
            assertTrue(it.artists.isNotEmpty(), "Expected at least one artist")
            assertEquals(
                it.album?.thumbnails?.isNotEmpty(),
                true,
                "Expected at least one album thumbnail"
            )
        }
    }
}