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

package io.github.sonic_liberation.spotube_plugin_spotify

import app.cash.zipline.Zipline
import dev.krtirtho.plugin_interfaces.core.runPluginInitialized
import dev.krtirtho.plugin_interfaces.host_apis.CryptoAPI
import dev.krtirtho.plugin_interfaces.host_apis.CryptoAPI_SERVICE_NAME
import io.github.sonic_liberation.spotube_plugin_spotify.core.RealMetadataAlbumAPI
import io.github.sonic_liberation.spotube_plugin_spotify.core.RealMetadataArtistAPI
import io.github.sonic_liberation.spotube_plugin_spotify.core.RealMetadataBrowseAPI
import io.github.sonic_liberation.spotube_plugin_spotify.core.RealMetadataPlaylistAPI
import io.github.sonic_liberation.spotube_plugin_spotify.core.RealMetadataSearchAPI
import io.github.sonic_liberation.spotube_plugin_spotify.core.RealMetadataTrackAPI
import io.github.sonic_liberation.spotube_plugin_spotify.core.RealMetadataUserAPI
import dev.krtirtho.plugin_interfaces.host_apis.HttpClientAPI
import dev.krtirtho.plugin_interfaces.host_apis.HttpClientAPI_SERVICE_NAME
import dev.krtirtho.plugin_interfaces.host_apis.PersistedStorageAPI
import dev.krtirtho.plugin_interfaces.host_apis.PersistedStorageAPI_SERVICE_NAME
import dev.krtirtho.plugin_interfaces.host_apis.SystemInformationAPI
import dev.krtirtho.plugin_interfaces.host_apis.SystemInformationAPI_SERVICE_NAME
import dev.krtirtho.plugin_interfaces.host_apis.WebViewAPI
import dev.krtirtho.plugin_interfaces.host_apis.WebViewAPI_SERVICE_NAME
import dev.krtirtho.plugin_interfaces.plugin_apis.core.CoreAPI
import dev.krtirtho.plugin_interfaces.plugin_apis.core.CoreAPI_SERVICE_NAME
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.album.MetadataAlbumAPI
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.album.MetadataAlbumAPI_SERVICE_NAME
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.artist.MetadataArtistAPI
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.artist.MetadataArtistAPI_SERVICE_NAME
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.browse.MetadataBrowseAPI
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.browse.MetadataBrowseAPI_SERVICE_NAME
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.playlist.MetadataPlaylistAPI
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.playlist.MetadataPlaylistAPI_SERVICE_NAME
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.search.MetadataSearchAPI
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.search.MetadataSearchAPI_SERVICE_NAME
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.track.MetadataTrackAPI
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.track.MetadataTrackAPI_SERVICE_NAME
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.user.MetadataUserAPI
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.user.MetadataUserAPI_SERVICE_NAME
import io.github.sonic_liberation.spotify_gql_client.gql.SpotifyGQLClient
import io.github.sonic_liberation.spotube_plugin_spotify.core.RealCoreAPI
import io.github.sonic_liberation.spotube_plugin_spotify.services.TOTP

private val zipline by lazy { Zipline.get() }

@OptIn(ExperimentalJsExport::class)
@JsExport
fun main() {
    runPluginInitialized {
        val httpClient = zipline.take<HttpClientAPI>(HttpClientAPI_SERVICE_NAME)
        val webview = zipline.take<WebViewAPI>(WebViewAPI_SERVICE_NAME)
        val storage = zipline.take<PersistedStorageAPI>(PersistedStorageAPI_SERVICE_NAME)
        val crypto = zipline.take<CryptoAPI>(CryptoAPI_SERVICE_NAME)
        val systemInfo = zipline.take<SystemInformationAPI>(SystemInformationAPI_SERVICE_NAME)

        val totpGenerator = TOTP(crypto)

        val spotifyGQLClient = SpotifyGQLClient(httpClient, systemInfo)

        zipline.bind<CoreAPI>(
            CoreAPI_SERVICE_NAME,
            instance = RealCoreAPI(
                httpClient = httpClient,
                totp = totpGenerator,
                storage = storage,
                webView = webview
            )
        )
        zipline.bind<MetadataAlbumAPI>(
            MetadataAlbumAPI_SERVICE_NAME,
            RealMetadataAlbumAPI(spotifyGQLClient)
        )
        zipline.bind<MetadataArtistAPI>(
            MetadataArtistAPI_SERVICE_NAME,
            RealMetadataArtistAPI(spotifyGQLClient)
        )
        zipline.bind<MetadataBrowseAPI>(
            MetadataBrowseAPI_SERVICE_NAME,
            RealMetadataBrowseAPI(spotifyGQLClient)
        )
        zipline.bind<MetadataPlaylistAPI>(
            MetadataPlaylistAPI_SERVICE_NAME,
            RealMetadataPlaylistAPI(spotifyGQLClient)
        )
        zipline.bind<MetadataSearchAPI>(
            MetadataSearchAPI_SERVICE_NAME,
            RealMetadataSearchAPI(spotifyGQLClient)
        )
        zipline.bind<MetadataTrackAPI>(
            MetadataTrackAPI_SERVICE_NAME,
            RealMetadataTrackAPI(spotifyGQLClient)
        )
        zipline.bind<MetadataUserAPI>(
            MetadataUserAPI_SERVICE_NAME,
            RealMetadataUserAPI(spotifyGQLClient)
        )
    }
}