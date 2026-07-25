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
import io.github.sonic_liberation.spotify_gql_client.gql.CredentialsFromCookieResult
import io.github.sonic_liberation.spotify_gql_client.gql.SpotifyGQLBaseClient
import io.github.sonic_liberation.spotify_gql_client.gql.browse.BrowseClient
import io.github.sonic_liberation.spotify_gql_client.integration.RealHttpClientAPI
import io.github.sonic_liberation.spotify_gql_client.loader.ResourceLoader
import io.github.sonic_liberation.spotify_gql_client.mocks.MockHTTPClientAPI
import io.github.sonic_liberation.spotify_gql_client.mocks.MockSystemInfoAPI
import io.github.sonic_liberation.spotify_gql_client.mocks.getSpTFromMockCredentials
import io.github.sonic_liberation.spotify_gql_client.mocks.initMockCredentialsFromCookieResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class BrowseClientTest {
    class UnitTests {

        private val httpClient = MockHTTPClientAPI()
        private val browse = BrowseClient(httpClient, MockSystemInfoAPI())
        private val loader = ResourceLoader()

        @Test
        fun `BrowseClient home Deserialization from Spotify API response`() = runTest {
            val mockResponseJsonStr = loader.readText("home_200.json")

            httpClient.mockPostResponse = HttpResponse(
                statusCode = 200,
                headers = emptyMap(),
                body = mockResponseJsonStr
            )

            val res = browse.home(
                spT = "SPT",
            )

            assertEquals(true, res != null)
        }

        @Test
        fun `BrowseClient section playlist Deserialization from Spotify API response`() = runTest {
            val mockResponseJsonStr = loader.readText("section_playlist_200.json")

            httpClient.mockPostResponse = HttpResponse(
                statusCode = 200,
                headers = emptyMap(),
                body = mockResponseJsonStr
            )

            val res = browse.section(
                "spotify:section:dummy",
                "SPT"
            )

            assertEquals(true, res != null)
        }

        @Test
        fun `BrowseClient section album Deserialization from Spotify API response`() = runTest {
            val mockResponseJsonStr = loader.readText("section_album_200.json")

            httpClient.mockPostResponse = HttpResponse(
                statusCode = 200,
                headers = emptyMap(),
                body = mockResponseJsonStr
            )

            val res = browse.section(
                "spotify:section:dummy",
                "SPT"
            )

            assertEquals(true, res != null)
        }


        @Test
        fun `BrowseResponse releases Deserialization from Spotify API response`() = runTest {
            val mockResponseJsonStr = loader.readText("releases_200.json")

            httpClient.mockPostResponse = HttpResponse(
                statusCode = 200,
                headers = emptyMap(),
                body = mockResponseJsonStr
            )

            val res = browse.releases()

            assertEquals(true, res != null)
            assertEquals(true, res?.data?.whatsNewFeedItems?.items?.isNotEmpty() == true)
        }
    }

    class IntegrationTests {
        private val httpClient = RealHttpClientAPI()
        private val browse = BrowseClient(httpClient, MockSystemInfoAPI())

        init {
            initMockCredentialsFromCookieResult()
        }

        @Test
        fun `BrowseClient home Deserialization from Spotify API response`() = runTest {
            val res = browse.home(
                spT = getSpTFromMockCredentials(),
            )
            assertEquals(true, res != null)
        }

        @Test
        fun `BrowseClient section playlist Deserialization from Spotify API response`() = runTest {
            val res = browse.section(
                "spotify:section:0JQ5DAuChZYPe9iDhh2mJz",
                getSpTFromMockCredentials()
            )
            assertEquals(true, res != null)
        }

        @Test
        fun `BrowseClient section album Deserialization from Spotify API response`() = runTest {
            val res = browse.section(
                "spotify:section:0JQ5DAnM3wGh0gz1MXnu3B",
                getSpTFromMockCredentials()
            )
            assertEquals(true, res != null)
        }

        @Test
        fun `BrowseResponse releases Deserialization from Spotify API response`() = runTest {
            val res = browse.releases()

            assertEquals(true, res != null)
            assertEquals(true, res?.data?.whatsNewFeedItems?.items?.isNotEmpty() == true)
        }

    }
}
