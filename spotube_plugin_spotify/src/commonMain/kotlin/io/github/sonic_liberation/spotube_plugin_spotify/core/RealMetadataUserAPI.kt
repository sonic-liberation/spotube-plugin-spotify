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

import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.common.Thumbnail
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.user.MetadataUser
import dev.krtirtho.plugin_interfaces.plugin_apis.metadata.user.MetadataUserAPI
import io.github.sonic_liberation.spotify_gql_client.gql.SpotifyGQLClient

class RealMetadataUserAPI(val spotifyGQLClient: SpotifyGQLClient) : MetadataUserAPI {
    override suspend fun getUser(id: String): MetadataUser? {
        val response = spotifyGQLClient.user.profileAttributes() ?: return null
        val profile = response.data.me.profile
        return MetadataUser(
            id = profile.username,
            username = profile.username,
            displayName = profile.name,
            thumbnails = profile.avatar?.sources?.map {
                Thumbnail(
                    it.url,
                    it.width ?: 10,
                    it.height ?: 10
                )
            } ?: emptyList(),
            externalUri = profile.uri
        )
    }

}