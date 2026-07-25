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

import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.album.MetadataAlbumType
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.common.PaginationStrategy
import io.github.sonic_liberation.spotify_gql_client.gql.SpotifyGQLClient
import io.github.sonic_liberation.spotube_plugin_spotify.integration.RealHttpClientAPI
import io.github.sonic_liberation.spotube_plugin_spotify.mocks.MockSystemInfoAPI
import io.github.sonic_liberation.spotube_plugin_spotify.mocks.initMockCredentialsFromCookieResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RealMetadataAlbumAPITest {
    val httpClient = RealHttpClientAPI()
    val spotifyGQLClient = SpotifyGQLClient(httpClient, MockSystemInfoAPI())
    val metadataAlbumAPI = RealMetadataAlbumAPI(spotifyGQLClient)

    init {
        initMockCredentialsFromCookieResult()
    }

    @Test
    fun `getAlbumTracks should return correct pagination result`() = runTest {
        val albumId =
            "4aawyAB9vmqN3uQ7FjRGTy" // "Global Warming" by Pitbull, which has more than 18 tracks
        val pagination = PaginationStrategy.Offset(offset = 0, limit = 20)

        val result = metadataAlbumAPI.getAlbumTracks(albumId, pagination)

        assertEquals(18, result.items.size, "Expected at least 18 tracks, got ${result.items.size}")
        assertEquals(
            null, result.nextPagination, "Expected nextPagination to be null, got ${result.nextPagination}"
        )
        result.items.forEach {
            assertTrue(it.title.isNotEmpty(), "Expected non-empty track title")
            assertTrue(it.artists.isNotEmpty(), "Expected at least one artist")
            assertTrue(it.album?.thumbnails?.isNotEmpty() == true, "Expected at least one album thumbnail")
        }
    }

    @Test
    fun `getAlbum should return correct album details`() = runTest {
        val albumId = "4aawyAB9vmqN3uQ7FjRGTy" // "Global Warming" by Pitbull

        val album = metadataAlbumAPI.getAlbum(albumId)

        assertEquals("Global Warming", album.title, "Expected album title to be 'Global Warming'")
        assertEquals(
            MetadataAlbumType.Album, album.albumType, "Expected album type to be 'Album'"
        )
        assertTrue(
            album.artists.any { it.name == "Pitbull" },
            "Expected at least one artist to be 'Pitbull'"
        )
    }

    @Test
    fun `savedAlbums should return paginated saved albums`() = runTest {
        val pagination = PaginationStrategy.Offset(offset = 0, limit = 20)

        val result = metadataAlbumAPI.savedAlbums(pagination)

        assertTrue(
            result.items.isNotEmpty(), "Expected at least one saved album, got ${result.items.size}"
        )
        assertTrue(
            result.totalCount >= result.items.size,
            "Expected totalCount to be greater than or equal to items size"
        )
    }
}