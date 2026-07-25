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

import io.github.sonic_liberation.spotify_gql_client.gql.GQLRequestExtensions
import kotlinx.serialization.Serializable

@Serializable
data class TrackCurationStatus(
    val isCurated: Boolean,
)

@Serializable
data class TrackCurationResponseWrapper(
    val data: TrackCurationStatus,
)

@Serializable
data class LookupData(
    val lookup: List<TrackCurationResponseWrapper>,
)

@Serializable
data class TrackCurationResponse(
    val data: LookupData,
)

@Serializable
data class TrackRadioPlaylistResponse(
    val total: Int,
    val mediaItems: List<TrackRadioPlaylistItem>,
)

@Serializable
data class TrackRadioPlaylistItem(
    val uri: String,
)

@Serializable
data class AddToLibraryVariables(
    val uris: List<String>,
)

@Serializable
data class AddToLibraryRequest(
    val variables: AddToLibraryVariables,
    val operationName: String,
    val extensions: GQLRequestExtensions,
)

@Serializable
data class Curation(
    val contextUri: String,
    val curationType: String,
)

@Serializable
data class ApplyCurationsInput(
    val curations: List<Curation>,
    val itemUris: List<String>,
)

@Serializable
data class ApplyCurationsVariables(
    val input: ApplyCurationsInput,
)

@Serializable
data class ApplyCurationsRequest(
    val variables: ApplyCurationsVariables,
    val operationName: String,
    val extensions: GQLRequestExtensions,
)

@Serializable
data class IsCuratedVariables(
    val uris: List<String>,
)

@Serializable
data class IsCuratedRequest(
    val variables: IsCuratedVariables,
    val operationName: String,
    val extensions: GQLRequestExtensions,
)


@Serializable
data class TrackMutationResponse(
    val data: MutationData,
) {
    @Serializable
    data class MutationResult(
        val typename: String? = null,
    )

    @Serializable
    data class MutationData(
        val addToLibrary: MutationResult? = null,
        val applyCurations: List<MutationResult>? = null,
    )

}

@Serializable
data class GetTrackRequest(
    val variables: GetTrackVariables,
    val operationName: String = "getTrack",
    val extensions: GQLRequestExtensions,
)

@Serializable
data class GetTrackVariables(
    val uri: String,
)

@Serializable
data class GetTrackResponse(
    val data: GetTrackData,
)

@Serializable
data class GetTrackData(
    val trackUnion: TrackUnion,
)

@Serializable
data class TrackUnion(
    val id: String,
    val name: String,
    val uri: String,
    val duration: TrackDuration,
    val trackNumber: Int? = null,
    val discNumber: Int? = null,
    val contentRating: TrackContentRating,
    val playability: TrackPlayability,
    val playcount: String? = null,
    val albumOfTrack: AlbumOfTrackUnion,
    val firstArtist: FirstArtistList? = null,
)

@Serializable
data class TrackDuration(
    val totalMilliseconds: Long,
)

@Serializable
data class TrackContentRating(
    val label: String,
)

@Serializable
data class TrackPlayability(
    val playable: Boolean,
    val reason: String? = null,
)

@Serializable
data class FirstArtistList(
    val items: List<FirstArtistItem>,
)

@Serializable
data class FirstArtistItem(
    val uri: String? = null,
    val profile: FirstArtistProfile? = null,
)

@Serializable
data class FirstArtistProfile(
    val name: String? = null,
)

@Serializable
data class AlbumOfTrackUnion(
    val id: String,
    val name: String,
    val uri: String,
    val type: String,
    val date: AlbumDate? = null,
    val coverArt: AlbumCoverArt? = null,
    val tracks: AlbumTracks? = null,
)

@Serializable
data class AlbumDate(
    val isoString: String? = null,
    val precision: String? = null,
    val year: Int? = null,
)

@Serializable
data class AlbumCoverArt(
    val sources: List<AlbumImageSource>,
)

@Serializable
data class AlbumImageSource(
    val url: String,
    val width: Int? = null,
    val height: Int? = null,
)

@Serializable
data class AlbumTracks(
    val totalCount: Int,
)
