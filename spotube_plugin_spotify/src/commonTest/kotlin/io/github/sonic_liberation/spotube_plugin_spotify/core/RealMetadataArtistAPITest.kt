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
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RealMetadataArtistAPITest {
    val httpClient = RealHttpClientAPI()
    val spotifyGQLClient = SpotifyGQLClient(httpClient, MockSystemInfoAPI())
    val metadataArtistApi = RealMetadataArtistAPI(spotifyGQLClient)

    init {
        initMockCredentialsFromCookieResult()
    }

    @Test
    fun `getArtistTopTracks should return correct top tracks for an artist`() = runTest {
        val artistId = "1uNFoZAHBGtllmzznpCI3s" // Justin Bieber
        val result = metadataArtistApi.getArtistTop10Tracks(artistId)

        assertEquals(10, result.size, "Expected 10 top tracks, got ${result.size}")
        assertTrue(
            result.all { it.artists.any { artist -> artist.id == artistId } },
            "Expected all top tracks to be by the specified artist"
        )
    }

    @Test
    fun `getArtistAlbums should return correct albums for an artist`() = runTest {
        val artistId = "1uNFoZAHBGtllmzznpCI3s"
        val pagination = PaginationStrategy.Offset(offset = 0, limit = 20)

        val result = metadataArtistApi.getArtistAlbums(artistId, pagination)

        assertTrue(
            result.items.isNotEmpty(),
            "Expected at least one album, got ${result.items.size}"
        )
    }

    @Test
    fun `getArtist should return correct artist details`() = runTest {
        val artistId = "1uNFoZAHBGtllmzznpCI3s"
        val result = metadataArtistApi.getArtist(artistId)

        assertNotNull(result, "Expected artist details to not be null")
        assertEquals(artistId, result.id, "Expected artist ID to match")
        assertEquals("Justin Bieber", result.name, "Expected artist name to match")
        assertTrue(result.thumbnails.isNotEmpty(), "Expected at least one thumbnail")
    }

    @Test
    fun `artistOverview should return complete overview with non-null pagination`() = runTest {
        val artistId = "1uNFoZAHBGtllmzznpCI3s"

        val result = metadataArtistApi.artistOverview(artistId)

        assertNotNull(result.artist, "Expected artist details to not be null")
        assertEquals(artistId, result.artist.id, "Expected artist ID to match")
        assertTrue(result.top10Tracks.isNotEmpty(), "Expected at least one top track")
        assertEquals(10, result.top10Tracks.size, "Expected exactly 10 top tracks")
        assertTrue(result.albums.items.isNotEmpty(), "Expected at least one album")
        assertNotNull(result.albums.nextPagination, "Expected albums nextPagination to not be null")
        assertTrue(result.relatedArtists.items.isNotEmpty(), "Expected at least one related artist")
        assertNotNull(result.relatedArtists.nextPagination, "Expected relatedArtists nextPagination to not be null")
        assertTrue(result.featuredPlaylists.items.isNotEmpty(), "Expected at least one featured playlist")
        assertNotNull(result.featuredPlaylists.nextPagination, "Expected featuredPlaylists nextPagination to not be null")
    }

    @Test
    fun `relatedArtists should return paginated related artists`() = runTest {
        val artistId = "1uNFoZAHBGtllmzznpCI3s"
        val pagination = PaginationStrategy.Offset(offset = 0, limit = 5)

        val result = metadataArtistApi.relatedArtists(artistId, pagination)

        assertTrue(result.items.isNotEmpty(), "Expected at least one related artist")
        assertTrue(result.items.size <= 5, "Expected at most 5 items due to limit")
        result.items.forEach {
            assertTrue(it.name.isNotEmpty(), "Expected non-empty artist name")
            assertTrue(it.id.isNotEmpty(), "Expected non-empty artist id")
        }
    }

    @Test
    fun `featuredPlaylists should return paginated featured playlists`() = runTest {
        val artistId = "1uNFoZAHBGtllmzznpCI3s"
        val pagination = PaginationStrategy.Offset(offset = 0, limit = 5)

        val result = metadataArtistApi.featuredPlaylists(artistId, pagination)

        assertTrue(result.items.isNotEmpty(), "Expected at least one featured playlist")
        assertTrue(result.items.size <= 5, "Expected at most 5 items due to limit")
        result.items.forEach {
            assertTrue(it.title.isNotEmpty(), "Expected non-empty playlist title")
        }
    }

    @Test
    fun `savedArtists should return correct saved artists for a user`() = runTest {
        val pagination = PaginationStrategy.Offset(offset = 0, limit = 20)

        val result = metadataArtistApi.savedArtists(pagination)

        assertTrue(
            result.items.isNotEmpty(),
            "Expected at least one saved artist, got ${result.items.size}"
        )
        assertTrue(
            result.totalCount >= result.items.size,
            "Expected totalCount to be greater than or equal to items size"
        )
        result.items.forEach {
            assertTrue(it.name.isNotEmpty(), "Expected non-empty artist name")
            assertTrue(it.thumbnails.isNotEmpty(), "Expected at least one thumbnail")
        }
    }
}
