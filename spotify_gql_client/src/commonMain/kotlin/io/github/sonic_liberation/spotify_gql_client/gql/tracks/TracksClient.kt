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

package io.github.sonic_liberation.spotify_gql_client.gql.tracks

import dev.krtirtho.plugin_interfaces.host_apis.HttpClientAPI
import io.github.sonic_liberation.spotify_gql_client.gql.GQLRequestExtensions
import io.github.sonic_liberation.spotify_gql_client.gql.PersistedQuery
import io.github.sonic_liberation.spotify_gql_client.gql.SpotifyGQLBaseClient

class TracksClient(client: HttpClientAPI): SpotifyGQLBaseClient(client) {

    suspend fun saveTracks(uris: List<String>): TrackMutationResponse? {
        val request = AddToLibraryRequest(
            variables = AddToLibraryVariables(uris = uris),
            operationName = "addToLibrary",
            extensions = GQLRequestExtensions(
                persistedQuery = PersistedQuery(
                    version = 1,
                    sha256Hash = "a3c1ff58e6a36fec5fe1e3a193dc95d9071d96b9ba53c5ba9c1494fb1ee73915",
                ),
            ),
        )

        return post<TrackMutationResponse, AddToLibraryRequest>(SPOTIFY_GQL_ENDPOINT, request)
    }

    suspend fun unsaveTracks(uris: List<String>): TrackMutationResponse? {
        val request = ApplyCurationsRequest(
            variables = ApplyCurationsVariables(
                input = ApplyCurationsInput(
                    curations = listOf(
                        Curation(
                            contextUri = "spotify:collection:tracks",
                            curationType = "UNCURATE",
                        ),
                    ),
                    itemUris = uris,
                ),
            ),
            operationName = "applyCurations",
            extensions = GQLRequestExtensions(
                persistedQuery = PersistedQuery(
                    version = 1,
                    sha256Hash = "05b739a3a73091c213385233b9d3ed8a857c2ca29d2eebadb3d04ed12e288697",
                ),
            ),
        )

        return post<TrackMutationResponse, ApplyCurationsRequest>(SPOTIFY_GQL_ENDPOINT, request)
    }

    suspend fun areTracksSaved(uris: List<String>): TrackCurationResponse? {
        val request = IsCuratedRequest(
            variables = IsCuratedVariables(uris = uris),
            operationName = "isCurated",
            extensions = GQLRequestExtensions(
                persistedQuery = PersistedQuery(
                    version = 1,
                    sha256Hash = "e4ed1f91a2cc5415befedb85acf8671dc1a4bf3ca1a5b945a6386101a22e28a6",
                ),
            ),
        )

        return post<TrackCurationResponse, IsCuratedRequest>(SPOTIFY_GQL_ENDPOINT, request)
    }

    suspend fun radioPlaylist(id: String): TrackRadioPlaylistResponse? {
        val url = restUrl("/inspiredby-mix/v2/seed_to_playlist/spotify:track:$id?response-format=json")

        return get<TrackRadioPlaylistResponse>(url)
    }

    suspend fun getTrack(uri: String): GetTrackResponse? {
        val request = GetTrackRequest(
            variables = GetTrackVariables(uri = uri),
            operationName = "getTrack",
            extensions = GQLRequestExtensions(
                persistedQuery = PersistedQuery(
                    version = 1,
                    sha256Hash = "612585ae06ba435ad26369870deaae23b5c8800a256cd8a57e08eddc25a37294",
                ),
            ),
        )

        return post<GetTrackResponse, GetTrackRequest>(SPOTIFY_GQL_ENDPOINT, request)
    }
}
