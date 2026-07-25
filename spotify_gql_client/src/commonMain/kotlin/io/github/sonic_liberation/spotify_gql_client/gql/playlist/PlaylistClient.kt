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

package io.github.sonic_liberation.spotify_gql_client.gql.playlist

import dev.krtirtho.plugin_interfaces.host_apis.HttpClientAPI
import io.github.sonic_liberation.spotify_gql_client.gql.GQLRequestExtensions
import io.github.sonic_liberation.spotify_gql_client.gql.PersistedQuery
import io.github.sonic_liberation.spotify_gql_client.gql.SpotifyGQLBaseClient
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class PlaylistClient(client: HttpClientAPI): SpotifyGQLBaseClient(client) {

    suspend fun fetchPlaylist(
        uri: String,
        offset: Int = 0,
        limit: Int = 25,
        enableWatchFeedEntrypoint: Boolean = true,
    ): PlaylistV2Response? {
        val request = FetchPlaylistRequest(
            variables = FetchPlaylistVariables(
                uri = uri,
                offset = offset,
                limit = limit,
                enableWatchFeedEntrypoint = enableWatchFeedEntrypoint,
            ),
            operationName = "fetchPlaylist",
            extensions = GQLRequestExtensions(
                persistedQuery = PersistedQuery(
                    version = 1,
                    sha256Hash = "cd2275433b29f7316176e7b5b5e098ae7744724e1a52d63549c76636b3257749",
                ),
            ),
        )

        return post<PlaylistV2Response, FetchPlaylistRequest>(SPOTIFY_GQL_ENDPOINT, request)
    }

    suspend fun followPlaylist(uris: List<String>): RootlistMutationResponse? {
        val request = FollowPlaylistRequest(
            variables = FollowPlaylistVariables(uris = uris),
            operationName = "addItemsToRootlist",
            extensions = GQLRequestExtensions(
                persistedQuery = PersistedQuery(
                    version = 1,
                    sha256Hash = "bd9c5cae1ee80ebca05d7ed12fd394216f49a20ce72d9dc762868df0f14522ea",
                ),
            ),
        )

        return post<RootlistMutationResponse, FollowPlaylistRequest>(SPOTIFY_GQL_ENDPOINT, request)
    }

    suspend fun unfollowPlaylist(uris: List<String>): RootlistMutationResponse? {
        val request = UnfollowPlaylistRequest(
            variables = UnfollowPlaylistVariables(uris = uris),
            operationName = "removeItemsFromRootlist",
            extensions = GQLRequestExtensions(
                persistedQuery = PersistedQuery(
                    version = 1,
                    sha256Hash = "3422f1866532820a1d8d0560f88542dbdbd35d0377ee5ecd49593590f5f1e86b",
                ),
            ),
        )

        return post<RootlistMutationResponse, UnfollowPlaylistRequest>(SPOTIFY_GQL_ENDPOINT, request)
    }

    suspend fun createPlaylist(
        name: String,
        description: String? = null,
    ): CreatePlaylistResponse? {
        val values = buildJsonObject {
            put("name", name)
            description?.let { put("description", it) }
        }

        val body = CreatePlaylistBody(
            ops = listOf(
                CreatePlaylistOp(
                    kind = "UPDATE_LIST_ATTRIBUTES",
                    updateListAttributes = UpdateListAttributes(
                        newAttributes = NewAttributes(values = values),
                    ),
                ),
            ),
        )

        val url = restUrl("/playlist/v2/playlist?format=json")
        return post<CreatePlaylistResponse, CreatePlaylistBody>(url, body)
    }

    suspend fun updatePlaylist(
        id: String,
        name: String? = null,
        description: String? = null,
    ): UpdatePlaylistResponse? {
        val values = buildJsonObject {
            name?.let { put("name", it) }
            description?.let { put("description", it) }
        }

        val body = UpdatePlaylistBody(
            deltas = listOf(
                UpdatePlaylistDelta(
                    ops = listOf(
                        UpdatePlaylistOp(
                            kind = "UPDATE_LIST_ATTRIBUTES",
                            updateListAttributes = UpdateListAttributes(
                                newAttributes = NewAttributes(values = values),
                            ),
                        ),
                    ),
                    info = UpdateInfo(
                        source = SourceInfo(client = "WEBPLAYER"),
                    ),
                ),
            ),
        )

        val url = restUrl("/playlist/v2/playlist/$id/changes?format=json")
        return post<UpdatePlaylistResponse, UpdatePlaylistBody>(url, body)
    }

    suspend fun changePermission(
        userId: String,
        playlistUri: String,
        isPublic: Boolean,
    ): ChangePermissionResponse? {
        val values = buildJsonObject {
            put("public", JsonPrimitive(isPublic))
        }

        val body = ChangePermissionBody(
            deltas = listOf(
                ChangePermissionDelta(
                    ops = listOf(
                        ChangePermissionOp(
                            kind = "UPDATE_ITEM_ATTRIBUTES",
                            updateItemAttributes = UpdateItemAttributes(
                                newAttributes = NewAttributes(values = values),
                                item = ItemRef(uri = playlistUri),
                            ),
                        ),
                    ),
                    info = UpdateInfo(
                        source = SourceInfo(client = "WEBPLAYER"),
                    ),
                ),
            ),
        )

        val url = restUrl("/playlist/v2/user/$userId/rootlist/changes?format=json")
        return post<ChangePermissionResponse, ChangePermissionBody>(url, body)
    }

    suspend fun addTracksToPlaylist(
        playlistUri: String,
        trackUris: List<String>,
    ): AddToPlaylistResponse? {
        val request = AddToPlaylistRequest(
            variables = AddToPlaylistVariables(
                playlistItemUris = trackUris,
                playlistUri = playlistUri,
                newPosition = NewPosition(
                    moveType = "BOTTOM_OF_PLAYLIST",
                    fromUid = null,
                ),
            ),
            operationName = "addToPlaylist",
            extensions = GQLRequestExtensions(
                persistedQuery = PersistedQuery(
                    version = 1,
                    sha256Hash = "47b2a1234b17748d332dd0431534f22450e9ecbb3d5ddcdacbd83368636a0990",
                ),
            ),
        )

        return post<AddToPlaylistResponse, AddToPlaylistRequest>(SPOTIFY_GQL_ENDPOINT, request)
    }

    suspend fun removeTracksFromPlaylist(
        playlistUri: String,
        uids: List<String>,
    ): RemoveFromPlaylistResponse? {
        val request = RemoveFromPlaylistRequest(
            variables = RemoveFromPlaylistVariables(
                playlistUri = playlistUri,
                uids = uids,
            ),
            operationName = "removeFromPlaylist",
            extensions = GQLRequestExtensions(
                persistedQuery = PersistedQuery(
                    version = 1,
                    sha256Hash = "47b2a1234b17748d332dd0431534f22450e9ecbb3d5ddcdacbd83368636a0990",
                ),
            ),
        )

        return post<RemoveFromPlaylistResponse, RemoveFromPlaylistRequest>(SPOTIFY_GQL_ENDPOINT, request)
    }
}