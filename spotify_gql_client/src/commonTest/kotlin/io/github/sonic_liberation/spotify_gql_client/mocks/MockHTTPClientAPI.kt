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

package io.github.sonic_liberation.spotify_gql_client.mocks

import dev.krtirtho.plugin_interfaces.host_apis.HttpClientAPI
import dev.krtirtho.plugin_interfaces.host_apis.HttpResponse

class MockHTTPClientAPI : HttpClientAPI {
    var mockGetResponse = HttpResponse(
        statusCode = 200,
        headers = emptyMap(),
        body = """{"data": {"mock": "response"}}"""
    )

    var mockPostResponse =
        HttpResponse(
            statusCode = 200,
            headers = emptyMap(),
            body = """{"data": {"mock": "response"}}"""
        )

    var mockPutResponse = HttpResponse(
        statusCode = 200,
        headers = emptyMap(),
        body = """{"data": {"mock": "response"}}"""
    )

    var mockDeleteResponse = HttpResponse(
        statusCode = 200,
        headers = emptyMap(),
        body = """{"data": {"mock": "response"}}"""
    )

    var mockOptionsResponse = HttpResponse(
        statusCode = 200,
        headers = emptyMap(),
        body = """{"data": {"mock": "response"}}"""
    )

    var mockHeadResponse = HttpResponse(
        statusCode = 200,
        headers = emptyMap(),
        body = """{"data": {"mock": "response"}}"""
    )

    var mockPatchResponse = HttpResponse(
        statusCode = 200,
        headers = emptyMap(),
        body = """{"data": {"mock": "response"}}"""
    )


    override suspend fun get(
        url: String,
        requestHeaders: Map<String, String>?
    ): HttpResponse = mockGetResponse

    override suspend fun post(
        url: String,
        requestHeaders: Map<String, String>?,
        body: String
    ): HttpResponse = mockPostResponse

    override suspend fun put(
        url: String,
        requestHeaders: Map<String, String>?,
        body: String
    ): HttpResponse = mockPutResponse

    override suspend fun delete(
        url: String,
        requestHeaders: Map<String, String>?
    ): HttpResponse = mockDeleteResponse

    override suspend fun options(
        url: String,
        requestHeaders: Map<String, String>?
    ): HttpResponse = mockOptionsResponse

    override suspend fun head(
        url: String,
        requestHeaders: Map<String, String>?
    ): HttpResponse = mockHeadResponse

    override suspend fun patch(
        url: String,
        requestHeaders: Map<String, String>?,
        body: String
    ): HttpResponse = mockPatchResponse
}