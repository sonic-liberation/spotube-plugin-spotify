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

package io.github.sonic_liberation.spotify_gql_client.gql

import dev.krtirtho.plugin_interfaces.host_apis.HttpClientAPI
import dev.krtirtho.plugin_interfaces.host_apis.SystemInformationAPI
import io.github.sonic_liberation.spotify_gql_client.gql.album.AlbumClient
import io.github.sonic_liberation.spotify_gql_client.gql.artist.ArtistClient
import io.github.sonic_liberation.spotify_gql_client.gql.browse.BrowseClient
import io.github.sonic_liberation.spotify_gql_client.gql.library.LibraryClient
import io.github.sonic_liberation.spotify_gql_client.gql.playlist.PlaylistClient
import io.github.sonic_liberation.spotify_gql_client.gql.search.SearchClient
import io.github.sonic_liberation.spotify_gql_client.gql.tracks.TracksClient
import io.github.sonic_liberation.spotify_gql_client.gql.user.UserClient

class SpotifyGQLClient(client: HttpClientAPI, systemInfo: SystemInformationAPI) {
    val user = UserClient(client)
    val playlist = PlaylistClient(client)
    val artist = ArtistClient(client)
    val album = AlbumClient(client)
    val library = LibraryClient(client)
    val search = SearchClient(client)
    val browse = BrowseClient(client, systemInfo)
    val tracks = TracksClient(client)
}