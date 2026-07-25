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

class RealMetadataPlaylistAPITest {
    val httpClient = RealHttpClientAPI()
    val spotifyGQLClient = SpotifyGQLClient(httpClient, MockSystemInfoAPI())
    val metadataPlaylistAPI = RealMetadataPlaylistAPI(spotifyGQLClient)

    init {
        initMockCredentialsFromCookieResult()
    }

    @Test
    fun `getPlaylist should return a playlist`() = runTest {
        val playlist = metadataPlaylistAPI.getPlaylist(
            "0qMVHggT7vBXaejPgUTbxA" // "The Greatest Showman Full Playlist"
        )

        assertNotNull(playlist)
        assertEquals("0qMVHggT7vBXaejPgUTbxA", playlist.id)
        assertEquals("The Greatest Showman Full Playlist", playlist.title)
        assertTrue(playlist.thumbnails.isNotEmpty(), "Expected at least one thumbnail")
        assertNotNull(playlist.owner, "Expected non-empty owner name")
    }

    @Test
    fun `savedPlaylists should return saved playlists`() = runTest {
        val savedPlaylists = metadataPlaylistAPI.savedPlaylists(
            PaginationStrategy.Offset(offset = 0, limit = 20)
        )

        assertNotNull(savedPlaylists)
        assertTrue(savedPlaylists.items.isNotEmpty(), "Expected non-empty list of items")
        assertTrue(savedPlaylists.totalCount > 0, "Expected total to be greater than 0")
        savedPlaylists.items.forEach {
            assertTrue(it.title.isNotEmpty(), "Expected non-empty playlist title")
            assertNotNull(it.owner, "Expected non-empty playlist owner")
            assertTrue(it.thumbnails.isNotEmpty(), "Expected at least one thumbnail")
        }
    }

    @Test
    fun `getPlaylistTracks should return playlist tracks`() = runTest {
        val playlistTracks = metadataPlaylistAPI.getPlaylistTracks(
            "0qMVHggT7vBXaejPgUTbxA", // "The Greatest Showman Full Playlist"
            PaginationStrategy.Offset(offset = 0, limit = 20) // Has 24 songs
        )

        assertNotNull(playlistTracks)
        assertTrue(playlistTracks.items.isNotEmpty(), "Expected non-empty list of items")
        assertTrue(playlistTracks.totalCount > 0, "Expected total to be greater than 0")
        playlistTracks.items.forEach {
            assertTrue(it.title.isNotEmpty(), "Expected non-empty track title")
            assertTrue(it.artists.isNotEmpty(), "Expected at least one artist")
            assertTrue(it.album?.thumbnails?.isNotEmpty() == true, "Expected at least one album thumbnail")
        }
    }
}