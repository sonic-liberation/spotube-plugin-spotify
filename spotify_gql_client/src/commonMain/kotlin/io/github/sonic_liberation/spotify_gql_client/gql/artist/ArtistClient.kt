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

package io.github.sonic_liberation.spotify_gql_client.gql.artist

import dev.krtirtho.plugin_interfaces.host_apis.HttpClientAPI
import io.github.sonic_liberation.spotify_gql_client.gql.GQLRequestExtensions
import io.github.sonic_liberation.spotify_gql_client.gql.PersistedQuery
import io.github.sonic_liberation.spotify_gql_client.gql.SpotifyGQLBaseClient


class ArtistClient(client: HttpClientAPI) : SpotifyGQLBaseClient(client) {
    suspend fun getArtist(
        uri: String,
        locale: String = "",
        preReleaseV2: Boolean = false,
    ): ArtistResponse? {
        val request = GetArtistRequest(
            variables = GetArtistVariables(uri = uri, locale = locale, preReleaseV2 = preReleaseV2),
            extensions = GQLRequestExtensions(
                persistedQuery = PersistedQuery(
                    version = 1,
                    sha256Hash = "7f86ff63e38c24973a2842b672abe44c910c1973978dc8a4a0cb648edef34527",
                ),
            ),
        )
        return post<ArtistResponse, GetArtistRequest>(SPOTIFY_GQL_ENDPOINT, request)
    }

    suspend fun follow(uris: List<String>): MutationResponse? {
        val request = FollowArtistRequest(
            variables = FollowArtistVariables(uris = uris),
            extensions = GQLRequestExtensions(
                persistedQuery = PersistedQuery(
                    version = 1,
                    sha256Hash = "a3c1ff58e6a36fec5fe1e3a193dc95d9071d96b9ba53c5ba9c1494fb1ee73915",
                ),
            ),
        )
        return post<MutationResponse, FollowArtistRequest>(SPOTIFY_GQL_ENDPOINT, request)
    }

    suspend fun unfollow(uris: List<String>): MutationResponse? {
        val request = UnfollowArtistRequest(
            variables = UnfollowArtistVariables(uris = uris),
            extensions = GQLRequestExtensions(
                persistedQuery = PersistedQuery(
                    version = 1,
                    sha256Hash = "a3c1ff58e6a36fec5fe1e3a193dc95d9071d96b9ba53c5ba9c1494fb1ee73915",
                ),
            ),
        )
        return post<MutationResponse, UnfollowArtistRequest>(SPOTIFY_GQL_ENDPOINT, request)
    }

    suspend fun discographyOverview(uri: String): ArtistResponse? {
        val request = DiscographyOverviewRequest(
            variables = DiscographyOverviewVariables(uri = uri),
            extensions = GQLRequestExtensions(
                persistedQuery = PersistedQuery(
                    version = 1,
                    sha256Hash = "5e07d323febb57b4a56a42abbf781490e58764aa45feb6e3dc0591564fc56599",
                ),
            ),
        )
        return post<ArtistResponse, DiscographyOverviewRequest>(SPOTIFY_GQL_ENDPOINT, request)
    }

    suspend fun discographyAll(
        uri: String,
        offset: Int = 0,
        limit: Int = 20,
        order: String = "DATE_DESC",
    ): ArtistResponse? {
        val request = DiscographyAllRequest(
            variables = DiscographyAllVariables(uri = uri, offset = offset, limit = limit, order = order),
            extensions = GQLRequestExtensions(
                persistedQuery = PersistedQuery(
                    version = 1,
                    sha256Hash = "5e07d323febb57b4a56a42abbf781490e58764aa45feb6e3dc0591564fc56599",
                ),
            ),
        )
        return post<ArtistResponse, DiscographyAllRequest>(SPOTIFY_GQL_ENDPOINT, request)
    }

    suspend fun appearsOnPlaylists(uri: String): ArtistResponse? {
        val request = AppearsOnPlaylistsRequest(
            variables = AppearsOnPlaylistsVariables(uri = uri),
            extensions = GQLRequestExtensions(
                persistedQuery = PersistedQuery(
                    version = 1,
                    sha256Hash = "9a4bb7a20d6720fe52d7b47bc001cfa91940ddf5e7113761460b4a288d18a4c1",
                ),
            ),
        )
        return post<ArtistResponse, AppearsOnPlaylistsRequest>(SPOTIFY_GQL_ENDPOINT, request)
    }

    suspend fun discoveredOnPlaylists(uri: String): ArtistResponse? {
        val request = DiscoveredOnPlaylistsRequest(
            variables = DiscoveredOnPlaylistsVariables(uri = uri),
            extensions = GQLRequestExtensions(
                persistedQuery = PersistedQuery(
                    version = 1,
                    sha256Hash = "71c2392e4cecf6b48b9ad1311ae08838cbdabcfd189c6bf0c66c2430b8dcfdb1",
                ),
            ),
        )
        return post<ArtistResponse, DiscoveredOnPlaylistsRequest>(SPOTIFY_GQL_ENDPOINT, request)
    }

    suspend fun featuredPlaylists(uri: String): ArtistResponse? {
        val request = FeaturedPlaylistsRequest(
            variables = FeaturedPlaylistsVariables(uri = uri),
            extensions = GQLRequestExtensions(
                persistedQuery = PersistedQuery(
                    version = 1,
                    sha256Hash = "20842d6d9d2d28ef945984b68cb927bb33edd00eab84a8da1667def21f1f2c54",
                ),
            ),
        )
        return post<ArtistResponse, FeaturedPlaylistsRequest>(SPOTIFY_GQL_ENDPOINT, request)
    }

    suspend fun relatedArtists(uri: String): ArtistResponse? {
        val request = RelatedArtistsRequest(
            variables = RelatedArtistsVariables(uri = uri),
            extensions = GQLRequestExtensions(
                persistedQuery = PersistedQuery(
                    version = 1,
                    sha256Hash = "3d031d6cb22a2aa7c8d203d49b49df731f58b1e2799cc38d9876d58771aa66f3",
                ),
            ),
        )
        return post<ArtistResponse, RelatedArtistsRequest>(SPOTIFY_GQL_ENDPOINT, request)
    }
}
