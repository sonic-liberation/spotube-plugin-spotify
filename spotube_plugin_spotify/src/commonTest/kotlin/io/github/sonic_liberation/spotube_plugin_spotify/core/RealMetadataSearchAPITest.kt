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
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.search.MetadataSearchResult
import io.github.sonic_liberation.spotify_gql_client.gql.SpotifyGQLClient
import io.github.sonic_liberation.spotube_plugin_spotify.integration.RealHttpClientAPI
import io.github.sonic_liberation.spotube_plugin_spotify.mocks.MockSystemInfoAPI
import io.github.sonic_liberation.spotube_plugin_spotify.mocks.initMockCredentialsFromCookieResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RealMetadataSearchAPITest {
    val httpClient = RealHttpClientAPI()
    val spotifyGQLClient = SpotifyGQLClient(httpClient, MockSystemInfoAPI())
    val metadataSearchAPI = RealMetadataSearchAPI(spotifyGQLClient)

    init {
        initMockCredentialsFromCookieResult()
    }

    @Test
    fun `search should return correct search results for all types`() = runTest {
        val query = "Shape of You"
        val result = metadataSearchAPI.search(query)

        assertNotNull(result)
        assertTrue(result.isNotEmpty(), "Expected non-empty list of search results")

        val tracks = result.filterIsInstance<MetadataSearchResult.Track>()
        val artists = result.filterIsInstance<MetadataSearchResult.Artist>()
        val albums = result.filterIsInstance<MetadataSearchResult.Album>()
        val playlists = result.filterIsInstance<MetadataSearchResult.Playlist>()

        assertTrue(tracks.isNotEmpty(), "Expected at least one track result")
        assertTrue(artists.isNotEmpty(), "Expected at least one artist result")
        assertTrue(albums.isNotEmpty(), "Expected at least one album result")
        assertTrue(playlists.isNotEmpty(), "Expected at least one playlist result")

        // All the results must contain thumbnails
        result.forEach {
            when (it) {
                is MetadataSearchResult.Track -> {
                    assertTrue(
                        it.data.album?.thumbnails?.isNotEmpty() == true,
                        "Expected at least one thumbnail for track result"
                    )
                }

                is MetadataSearchResult.Artist -> {
                    assertTrue(
                        it.data.thumbnails.isNotEmpty(),
                        "Expected at least one thumbnail for artist result"
                    )
                }

                is MetadataSearchResult.Album -> {
                    assertTrue(
                        it.data.thumbnails.isNotEmpty(),
                        "Expected at least one thumbnail for album result"
                    )
                }

                is MetadataSearchResult.Playlist -> {
                    assertTrue(
                        it.data.thumbnails.isNotEmpty(),
                        "Expected at least one thumbnail for playlist result"
                    )
                }

                is MetadataSearchResult.User -> {
                    assertNotNull(
                        it.data.displayName,
                        "Expected display name for user result"
                    )
                }
            }
        }
    }

    @Test
    fun `search should return correct search results for user type`() = runTest {
        val query = "spotify:user:spotify"
        val result =
            metadataSearchAPI.searchUsers(query, PaginationStrategy.Offset(offset = 0, limit = 20))

        assertNotNull(result)
        assertTrue(result.items.isNotEmpty(), "Expected non-empty list of search results")

        val users = result.items
        assertTrue(users.isNotEmpty(), "Expected at least one user result")

        users.forEach {
            assertNotNull(
                it.data.displayName,
                "Expected display name for user result"
            )
        }
    }

    @Test
    fun `search should return correct search results for playlist type`() = runTest {
        val result = metadataSearchAPI.searchPlaylists(
            "Greatest playlist of all time",
            PaginationStrategy.Offset(offset = 0, limit = 20)
        )

        assertNotNull(result)
        assertTrue(result.items.isNotEmpty(), "Expected non-empty list of search results")

        val playlists = result.items
        assertTrue(playlists.isNotEmpty(), "Expected at least one playlist result")

        playlists.forEach {
            assertTrue(
                it.data.thumbnails.isNotEmpty(),
                "Expected at least one thumbnail for playlist result"
            )
        }
    }

    @Test
    fun `search should return correct search results for album type`() = runTest {
        val result =
            metadataSearchAPI.searchAlbums("Divide", PaginationStrategy.Offset(offset = 0, limit = 20))

        assertNotNull(result)
        assertTrue(result.items.isNotEmpty(), "Expected non-empty list of search results")

        val albums = result.items
        assertTrue(albums.isNotEmpty(), "Expected at least one album result")

        albums.forEach {
            assertTrue(
                it.data.thumbnails.isNotEmpty(),
                "Expected at least one thumbnail for album result"
            )
        }
    }

    @Test
    fun `search should return correct search results for artist type`() = runTest {
        val result =
            metadataSearchAPI.searchArtists(
                "Ed Sheeran",
                PaginationStrategy.Offset(offset = 0, limit = 20)
            )

        assertNotNull(result)
        assertTrue(result.items.isNotEmpty(), "Expected non-empty list of search results")

        val artists = result.items
        assertTrue(artists.isNotEmpty(), "Expected at least one artist result")
    }
}