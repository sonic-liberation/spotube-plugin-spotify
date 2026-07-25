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

import io.github.sonic_liberation.spotify_gql_client.gql.GQLRequestExtensions
import io.github.sonic_liberation.spotify_gql_client.gql.Playlist
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class PlaylistImageSource(
    val height: Int?,
    val url: String,
    val width: Int?,
)

@Serializable
data class Attribute(
    val key: String,
    val value: String,
)

@Serializable
data class ArtistItem(
    val profile: ArtistProfile,
    val uri: String,
)

@Serializable
data class ArtistProfile(
    val name: String,
)

@Serializable
data class Artists(
    val items: List<ArtistItem>,
)

@Serializable
data class CoverArt(
    val sources: List<PlaylistImageSource>,
)

@Serializable
data class AlbumOfTrack(
    val artists: Artists,
    val coverArt: CoverArt,
    val name: String,
    val uri: String,
)

@Serializable
data class ContentRating(
    val label: String,
)

@Serializable
data class TrackDuration(
    val totalMilliseconds: Long,
)

@Serializable
data class Playability(
    val playable: Boolean,
    val reason: String,
)

@Serializable
data class Track(
    val albumOfTrack: AlbumOfTrack,
    val artists: Artists,
    val contentRating: ContentRating,
    val discNumber: Int,
    val trackDuration: TrackDuration,
    val mediaType: String,
    val name: String,
    val playability: Playability,
    val playcount: String,
    val trackNumber: Int,
    val uri: String,
)

@Serializable
data class TrackResponseWrapper(
    val data: Track,
)

@Serializable
data class AddedAt(
    val isoString: String,
)

@Serializable
data class PlaylistItem(
    val addedAt: AddedAt,
    val addedBy: AddedBy?,
    val attributes: List<Attribute>,
    val itemV2: TrackResponseWrapper,
    val uid: String,
)

@Serializable
data class AddedBy(
    val uri: String?,
    val username: String?,
)


@Serializable
data class PlaylistV2Response(
    val data: PlaylistV2Data,
) {
    @Serializable
    data class PlaylistV2Data(
        val playlistV2: Playlist,
    )
}

@Serializable
data class RootlistItemMutationResult(
    val typename: String? = null,
)

@Serializable
data class RootlistMutationData(
    val addItemsToRootlist: RootlistItemMutationResult? = null,
    val removeItemsFromRootlist: RootlistItemMutationResult? = null,
)

@Serializable
data class RootlistMutationResponse(
    val data: RootlistMutationData,
)

@Serializable
data class FetchPlaylistVariables(
    val uri: String,
    val offset: Int,
    val limit: Int,
    val enableWatchFeedEntrypoint: Boolean = true,
)

@Serializable
data class FollowPlaylistVariables(
    val uris: List<String>,
)

@Serializable
data class UnfollowPlaylistVariables(
    val uris: List<String>,
)

@Serializable
data class FetchPlaylistRequest(
    val variables: FetchPlaylistVariables,
    val operationName: String,
    val extensions: GQLRequestExtensions,
)

@Serializable
data class FollowPlaylistRequest(
    val variables: FollowPlaylistVariables,
    val operationName: String,
    val extensions: GQLRequestExtensions,
)

@Serializable
data class UnfollowPlaylistRequest(
    val variables: UnfollowPlaylistVariables,
    val operationName: String,
    val extensions: GQLRequestExtensions,
)

// Create playlist REST models
@Serializable
data class NewAttributes(
    val values: JsonObject,
)

@Serializable
data class UpdateListAttributes(
    val newAttributes: NewAttributes,
)

@Serializable
data class CreatePlaylistOp(
    val kind: String,
    val updateListAttributes: UpdateListAttributes,
)

@Serializable
data class CreatePlaylistBody(
    val ops: List<CreatePlaylistOp>,
)

@Serializable
data class CreatePlaylistResponse(
    val uri: String,
    val revision: String,
)

// Update playlist REST models
@Serializable
data class UpdatePlaylistOp(
    val kind: String,
    val updateListAttributes: UpdateListAttributes,
)

@Serializable
data class SourceInfo(
    val client: String,
)

@Serializable
data class UpdateInfo(
    val source: SourceInfo,
)

@Serializable
data class UpdatePlaylistDelta(
    val ops: List<UpdatePlaylistOp>,
    val info: UpdateInfo,
)

@Serializable
data class UpdatePlaylistBody(
    val deltas: List<UpdatePlaylistDelta>,
)

@Serializable
data class SyncResult(
    val fromRevision: String,
    val toRevision: String,
)

@Serializable
data class UpdatePlaylistResponse(
    val revision: String,
    val syncResult: SyncResult? = null,
    val resultingRevisions: List<String>? = null,
    val multipleHeads: Boolean? = null,
    val changesRequireResync: Boolean? = null,
)

// Change permission REST models
@Serializable
data class ItemRef(
    val uri: String,
)

@Serializable
data class UpdateItemAttributes(
    val newAttributes: NewAttributes,
    val item: ItemRef,
)

@Serializable
data class ChangePermissionOp(
    val kind: String,
    val updateItemAttributes: UpdateItemAttributes,
)

@Serializable
data class ChangePermissionDelta(
    val ops: List<ChangePermissionOp>,
    val info: UpdateInfo,
)

@Serializable
data class ChangePermissionBody(
    val deltas: List<ChangePermissionDelta>,
)

@Serializable
data class ChangePermissionResponse(
    val revision: String,
    val syncResult: SyncResult? = null,
    val resultingRevisions: List<String>? = null,
    val multipleHeads: Boolean? = null,
    val changesRequireResync: Boolean? = null,
)

// Add tracks to playlist GQL models
@Serializable
data class NewPosition(
    val moveType: String,
    val fromUid: String?,
)

@Serializable
data class AddToPlaylistVariables(
    val playlistItemUris: List<String>,
    val playlistUri: String,
    val newPosition: NewPosition,
)

@Serializable
data class AddToPlaylistRequest(
    val variables: AddToPlaylistVariables,
    val operationName: String,
    val extensions: GQLRequestExtensions,
)

@Serializable
data class AddToPlaylistPayload(
    val typename: String? = null,
)

@Serializable
data class AddToPlaylistData(
    val addItemsToPlaylist: AddToPlaylistPayload,
)

@Serializable
data class AddToPlaylistResponse(
    val data: AddToPlaylistData,
)

// Remove tracks from playlist GQL models
@Serializable
data class RemoveFromPlaylistVariables(
    val playlistUri: String,
    val uids: List<String>,
)

@Serializable
data class RemoveFromPlaylistRequest(
    val variables: RemoveFromPlaylistVariables,
    val operationName: String,
    val extensions: GQLRequestExtensions,
)

@Serializable
data class RemoveFromPlaylistPayload(
    val typename: String? = null,
)

@Serializable
data class RemoveFromPlaylistData(
    val removeItemsFromPlaylist: RemoveFromPlaylistPayload,
)

@Serializable
data class RemoveFromPlaylistResponse(
    val data: RemoveFromPlaylistData,
)
