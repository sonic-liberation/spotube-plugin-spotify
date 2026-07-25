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

package io.github.sonic_liberation.spotify_gql_client.integration

import dev.krtirtho.plugin_interfaces.host_apis.HttpClientAPI
import dev.krtirtho.plugin_interfaces.host_apis.HttpResponse
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.takeFrom

class RealHttpClientAPI: HttpClientAPI {
    val httpClient = HttpClient(CIO)

    suspend fun request(
        method: HttpMethod,
        url: String,
        requestHeaders: Map<String, String>?,
        body: String?
    ): HttpResponse {
        val res = httpClient.request {
            this.method = method
            this.url {
                takeFrom(url)
            }
            requestHeaders?.forEach { (key, value) ->
                headers.append(key, value)
            }
            body?.let { setBody(it) }
        }

        return HttpResponse(
            statusCode = res.status.value,
            headers = res.headers.entries().associate { it.key to it.value.joinToString(",") },
            body = res.bodyAsText()
        )
    }

    override suspend fun get(url: String, requestHeaders: Map<String, String>?): HttpResponse {
        return request(HttpMethod.Get, url, requestHeaders, null)
    }

    override suspend fun post(
        url: String,
        requestHeaders: Map<String, String>?,
        body: String
    ): HttpResponse {
        return request(HttpMethod.Post, url, requestHeaders, body)
    }

    override suspend fun put(
        url: String,
        requestHeaders: Map<String, String>?,
        body: String
    ): HttpResponse {
        return request(HttpMethod.Put, url, requestHeaders, body)
    }

    override suspend fun delete(
        url: String,
        requestHeaders: Map<String, String>?
    ): HttpResponse {
        return request(HttpMethod.Delete, url, requestHeaders, null)
    }

    override suspend fun options(
        url: String,
        requestHeaders: Map<String, String>?
    ): HttpResponse {
        return request(HttpMethod.Options, url, requestHeaders, null)
    }

    override suspend fun head(
        url: String,
        requestHeaders: Map<String, String>?
    ): HttpResponse {
        return request(HttpMethod.Head, url, requestHeaders, null)
    }

    override suspend fun patch(
        url: String,
        requestHeaders: Map<String, String>?,
        body: String
    ): HttpResponse {
        return request(HttpMethod.Patch, url, requestHeaders, body)
    }
}