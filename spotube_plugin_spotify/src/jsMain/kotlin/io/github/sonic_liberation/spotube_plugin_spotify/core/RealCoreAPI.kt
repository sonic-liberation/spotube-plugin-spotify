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

import dev.krtirtho.plugin_interfaces.extras.spotor.JsonContentSerializer
import dev.krtirtho.plugin_interfaces.extras.spotor.SpotrClient
import dev.krtirtho.plugin_interfaces.host_apis.Cookie
import dev.krtirtho.plugin_interfaces.host_apis.HttpClientAPI
import dev.krtirtho.plugin_interfaces.host_apis.PersistedStorageAPI
import dev.krtirtho.plugin_interfaces.host_apis.WebViewAPI
import dev.krtirtho.plugin_interfaces.plugin_apis.core.CoreAPI
import dev.krtirtho.plugin_interfaces.plugin_apis.core.PluginUpdateInfo
import io.github.sonic_liberation.spotify_gql_client.gql.CredentialsFromCookieResult
import io.github.sonic_liberation.spotify_gql_client.gql.SpotifyGQLBaseClient
import io.github.sonic_liberation.spotube_plugin_spotify.services.TOTP
import io.github.sonic_liberation.spotube_plugin_spotify.services.TOTPAlgorithm
import io.github.sonic_liberation.spotube_plugin_spotify.services.TOTPEncoding
import io.github.sonic_liberation.spotube_plugin_spotify.services.TOTPOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import net.swiftzer.semver.SemVer
import kotlin.js.Date
import kotlin.random.Random
import kotlin.time.Clock


private val json = Json { ignoreUnknownKeys = true }

class RealCoreAPI(
    httpClient: HttpClientAPI,
    private val totp: TOTP,
    private val storage: PersistedStorageAPI,
    private val webView: WebViewAPI
) : CoreAPI {
    private val scope = CoroutineScope(Dispatchers.Main)
    private var refreshJob: Job? = null
    private var client = SpotrClient(httpClient) {
        serializer = JsonContentSerializer(json)
    }

    override suspend fun checkPluginUpdates(currentVersion: SemVer): PluginUpdateInfo? {
        return null
    }

    override fun supportMarkdownText(currentVersion: SemVer): String {
        return "Support us please!"
    }

    override val requiresAuthentication = true

    private val loggedInStateFlow = MutableStateFlow(false)
    override val loggedInFlow = loggedInStateFlow.asStateFlow()

    init {
        scope.launch {
            restoreSession()
            scheduleForRefresh()
        }
    }

    private suspend fun restoreSession() {
        val credentialsRaw = storage.getString("credentials") ?: return
        val credentials = json.decodeFromString<CredentialsFromCookieResult>(credentialsRaw)
        SpotifyGQLBaseClient.credentials = credentials
        loggedInStateFlow.value = true
    }

    private suspend fun storeSession(
        credentials: CredentialsFromCookieResult
    ) {
        storage.putString("credentials", json.encodeToString(credentials))
        SpotifyGQLBaseClient.credentials = credentials
    }

    private fun scheduleForRefresh() {
        refreshJob = scope.launch {
            val expiration = SpotifyGQLBaseClient.credentials?.expiration
            val cookies = SpotifyGQLBaseClient.credentials?.cookies

            if (expiration == null || cookies == null) {
                console.log("[RealCoreAPI.scheduleForRefresh] Cannot schedule refresh: missing credentials")
                return@launch
            }

            val delayMillis =
                expiration.minus(Clock.System.now().toEpochMilliseconds()).coerceAtLeast(0)
            console.log("[RealCoreAPI.scheduleForRefresh] Scheduling refresh in ${delayMillis}ms")

            delay(timeMillis = delayMillis)
            storeSession(credentialsFromCookie(cookies))

            scheduleForRefresh()
        }
    }

    private suspend fun generateTimedOnTimePassword(secret: String): String {
        val response = client.get {
            url("https://open.spotify.com/api/server-time")
        }
        val timestampSeconds = response.body<ServerTimeResponse>().serverTime

        val res = this.totp.generate(
            key = secret, options = TOTPOptions(
                digits = 6,
                algorithm = TOTPAlgorithm.SHA1,
                encoding = TOTPEncoding.BASE32,
                period = 30,
                timestamp = timestampSeconds
            )
        )

        return res.otp
    }

    private suspend fun getLatestNuance(cached: Boolean = true): NuanceInfo {
        val timestamp = Clock.System.now().toEpochMilliseconds()
        client.get {
            url("https://gist.githubusercontent.com/raw/22ed9c6ba463899e933427f7de1f0eef/nuances.json")
            if (!cached) {
                parameter("t", timestamp.toString())
            }
        }.body<List<NuanceInfo>>().let { data ->
            return data.maxByOrNull { it.v } ?: throw Exception("No nuances found")
        }
    }

    private fun randomBytesFromMath(length: Int): String {
        val bytes = mutableListOf<String>()

        for (i in 0 until length) {
            bytes.add(Random.nextInt(256).toString())
        }

        return bytes.joinToString("")
    }


    private suspend fun getToken(
        mode: String = "transport",
        totp: String, spDc: String, totpVer: Int
    ): GetTokenResponse {
        val accessTokenUrl =
            "https://open.spotify.com/api/token?reason=$mode&productType=web-player&totp=$totp&totpServer=$totp&totpVer=$totpVer"

        val userAgent =
            "${Date.now()}${Random.nextInt(100) * 1000}${randomBytesFromMath(16)}".split("")
                .joinToString("")

        console.log("Requesting token with URL: $accessTokenUrl")
        console.log("Requesting token with User-Agent: $userAgent")
        console.log("Requesting token with sp_dc: $spDc")

        return client.get {
            url(accessTokenUrl)
            headers {
                append("User-Agent", userAgent)
                append("Cookie", spDc)
            }
        }.let { it ->
            if (it.statusCode != 200) {
                throw Exception("Failed to get token: ${it.statusCode} - ${it.bodyAsText()}")
            }
            val body = it.body<SpotifyTokenResponse>()

            GetTokenResponse(
                body = body, headers = it.headers.map { (key, value) ->
                    key to value.joinToString(";")
                }.toMap()
            )
        }
    }

    private suspend fun credentialsFromCookie(cookies: List<Cookie>): CredentialsFromCookieResult {
        val spDc = cookies.firstOrNull { it.name == "sp_dc" }?.value
            ?: throw Exception("sp_dc cookie not found")


        val nuance = getLatestNuance()

        val totp = generateTimedOnTimePassword(nuance.s)

        val token = runCatching {
            getToken(
                totp = totp,
                spDc = "sp_dc=$spDc;",
                mode = "transport",
                totpVer = nuance.v,
            )
        }
            .getOrElse {
                // We bust cache of nuance
                val nuance = getLatestNuance(cached = false)
                val totp = generateTimedOnTimePassword(nuance.s)
                getToken(
                    totp = totp,
                    spDc = "sp_dc=$spDc;",
                    mode = "transport",
                    totpVer = nuance.v,
                )
            }

        return CredentialsFromCookieResult(
            cookies = cookies,
            accessToken = token.body.accessToken,
            expiration = token.body.accessTokenExpirationTimestampMs
        )
    }


    companion object {
        private val exp = Regex(
            """^https://accounts\.spotify\.com/[^/]+/status($|\?.*)$"""
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun login() {
        webView.webviewCreatedFlow()
            .take(1)
            .flatMapLatest {
                webView.urlChangeFlow()
            }.onEach { url ->
                val safeUrl =
                    if (url.endsWith("/") && url.length > 1) url.substring(
                        0,
                        url.length - 1
                    ) else url

                val hasMatch = exp.matches(safeUrl)

                if (hasMatch) {
                    val cookies = webView.getCookies("https://spotify.com")
                    storeSession(credentialsFromCookie(cookies))
                    webView.exitWebView()
                    loggedInStateFlow.value = true
                    scheduleForRefresh()
                }
            }.launchIn(scope)

        webView.navigateTo("https://accounts.spotify.com/")
    }

    override suspend fun logout() {
        refreshJob?.cancel()
        SpotifyGQLBaseClient.credentials = null
        storage.remove("credentials")
        loggedInStateFlow.value = false
    }
}
