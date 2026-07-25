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

class RealMetadataBrowseAPITest {
    val httpClient = RealHttpClientAPI()
    val spotifyGQLClient = SpotifyGQLClient(httpClient, MockSystemInfoAPI())
    val metadataBrowseApi = RealMetadataBrowseAPI(spotifyGQLClient)

    init {
        initMockCredentialsFromCookieResult()
    }

    @Test
    fun `genres should return list including home-genre and browse genres`() = runTest {
        val genres = metadataBrowseApi.genres()

        assertNotNull(genres)
        assertTrue(genres.isNotEmpty(), "Expected non-empty list of genres")
        assertEquals(genres.first().id, "home-genre", "Expected first genre to be 'home-genre'")
        assertEquals(genres.first().name, "All", "Expected first genre name to be 'All'")
        assertTrue(genres.size > 1, "Expected more than just 'home-genre'")
        assertTrue(genres.drop(1).all { it.id.startsWith("spotify:") }, "Expected browse genres to have spotify uri as id")
        assertTrue(genres.drop(1).all { it.name.isNotBlank() }, "Expected all genres to have non-blank names")
    }

    @Test
    fun `list with home-genre should return home feed sections`() = runTest {
        val list = metadataBrowseApi.list(
            genreId = "home-genre",
            pagination = PaginationStrategy.Offset(offset = 0, limit = 20)
        )

        assertNotNull(list)
        assertTrue(list.items.isNotEmpty(), "Expected non-empty list of items")
        assertTrue(list.totalCount > 0, "Expected total to be greater than 0")
    }

    @Test
    fun `list with normal genre should return browse page sections`() = runTest {
        val list = metadataBrowseApi.list(
            genreId = "spotify:page:0JQ5DAqbMKFSi39LMRT0Cy",
            pagination = PaginationStrategy.Offset(offset = 0, limit = 10)
        )

        assertNotNull(list)
        assertTrue(list.items.isNotEmpty(), "Expected non-empty list of sections")
        assertTrue(list.totalCount > 0, "Expected total to be greater than 0")
        assertTrue(list.items.all { it.title.isNotBlank() }, "Expected all sections to have titles")
    }

    @Test
    fun `sublist with home-genre should return section items`() = runTest {
        val sublist = metadataBrowseApi.sublist(
            genreId = "home-genre",
            sectionId = "spotify:section:0JQ5DAnM3wGh0gz1MXnu3R",
            pagination = PaginationStrategy.Offset(offset = 0, limit = 20)
        )

        assertNotNull(sublist)
        assertTrue(sublist.items.isNotEmpty(), "Expected non-empty list of items")
        assertTrue(sublist.totalCount > 0, "Expected total to be greater than 0")
    }

    @Test
    fun `sublist with normal genre should return section items`() = runTest {
        val sublist = metadataBrowseApi.sublist(
            genreId = "spotify:page:0JQ5DAqbMKFSi39LMRT0Cy",
            sectionId = "spotify:section:0JQ5IMCbQBLkEvckqOqavM",
            pagination = PaginationStrategy.Offset(offset = 0, limit = 10)
        )

        assertNotNull(sublist)
        assertTrue(sublist.items.isNotEmpty(), "Expected non-empty list of items")
        assertTrue(sublist.totalCount > 0, "Expected total to be greater than 0")
    }

    @Test
    fun `featured should return correct featured items of playlist format`() = runTest {
        val featuredItems = metadataBrowseApi.featured()

        assertNotNull(featuredItems)
        assertTrue(featuredItems.isNotEmpty(), "Expected non-empty list of items")
    }
}