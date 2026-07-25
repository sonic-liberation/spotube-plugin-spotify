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

import dev.krtirtho.plugin_interfaces.host_apis.Cookie
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject


@Serializable
data class CredentialsFromCookieResult(
    val cookies: List<Cookie>,
    val accessToken: String,
    val expiration: Long
)

// ─── Color / Image ───────────────────────────────────────────────────────────

@Serializable
data class ColorHex(
    val hex: String,
    @SerialName("isFallback") val isFallback: Boolean = false,
)

@Serializable
data class ExtractedColors(
    val colorDark: ColorHex? = null,
    val colorLight: ColorHex? = null,
    val colorRaw: ColorHex? = null,
)

@Serializable
data class ImageSource(
    val url: String,
    val height: Int? = null,
    val width: Int? = null,
    @SerialName("maxHeight") val maxHeight: Int? = null,
    @SerialName("maxWidth") val maxWidth: Int? = null,
)

@Serializable
data class CoverArt(
    val sources: List<ImageSource> = emptyList(),
    val extractedColors: ExtractedColors? = null,
)

// ─── Shared wrappers ─────────────────────────────────────────────────────────

@Serializable
sealed interface ResponseWrapper {
    val __typename: String
}

@Serializable
sealed interface ContentItem {
    val __typename: String
}

// ─── Album ───────────────────────────────────────────────────────────────────

@Serializable
@SerialName("AlbumResponseWrapper")
data class AlbumResponseWrapper(
    override val __typename: String = "AlbumResponseWrapper",
    val data: Album? = null,
) : ResponseWrapper

@Serializable
@SerialName("Album")
data class Album(
    override val __typename: String = "Album",
    val uri: String = "",
    val name: String = "",
    val type: String? = null,
    val saved: Boolean? = null,
    val playability: Playability? = null,
    val date: AlbumDate? = null,
    val label: String? = null,
    val artists: ArtistList? = null,
    val coverArt: CoverArt? = null,
    val attributes: List<Attribute> = emptyList(),
    val format: String? = null,
    val description: String? = null,
    val images: PlaylistImages? = null,
    val ownerV2: OwnerV2? = null,
    val sharingInfo: SharingInfo? = null,
    val tracks: TrackCount? = null,
    val tracksV2: AlbumTracksV2? = null,
    val copyright: CopyrightList? = null,
    @SerialName("albumType") val albumType: String? = null,
) : ContentItem

@Serializable
data class AlbumDate(
    val isoString: String? = null,
    val year: Int? = null,
    val month: Int? = null,
    val day: Int? = null,
    val precision: String? = null,
)

// ─── Artist ──────────────────────────────────────────────────────────────────

@Serializable
@SerialName("ArtistResponseWrapper")
data class ArtistResponseWrapper(
    override val __typename: String = "ArtistResponseWrapper",
    val data: Artist? = null,
) : ResponseWrapper

@Serializable
@SerialName("Artist")
data class Artist(
    override val __typename: String = "Artist",
    val uri: String = "",
    val id: String? = null,
    val saved: Boolean? = null,
    val profile: ArtistProfile? = null,
    val visuals: ArtistVisuals? = null,
    val stats: ArtistStats? = null,
    val sharingInfo: SharingInfo? = null,
    val discography: ArtistDiscography? = null,
    val relatedContent: ArtistRelatedContent? = null,
) : ContentItem

@Serializable
data class ArtistProfile(
    val name: String = "",
    val biography: ArtistBiography? = null,
)

@Serializable
data class ArtistBiography(
    val text: String,
)

@Serializable
data class ArtistVisuals(
    val avatarImage: CoverArt? = null,
)

@Serializable
data class ArtistStats(
    val followers: Int? = null,
    val monthlyListeners: Int? = null,
)

@Serializable
data class ArtistDiscography(
    val albums: DiscographySection? = null,
    val all: DiscographyPage? = null,
    val compilations: DiscographySection? = null,
    val singles: DiscographySection? = null,
    val latest: ReleaseItem? = null,
    val popularReleasesAlbums: ReleasePage? = null,
    val topTracks: ArtistTopTracks? = null,
)

@Serializable
data class ArtistTopTracks(
    val items: List<ArtistTopTrackItem> = emptyList(),
)

@Serializable
data class ArtistTopTrackItem(
    val track: TopTrack? = null,
)

@Serializable
data class TopTrack(
    val id: String = "",
    val name: String = "",
    val uri: String = "",
    val duration: TrackDuration? = null,
    val discNumber: Int = 0,
    val trackNumber: Int = 0,
    val contentRating: ContentRating? = null,
    val playability: Playability? = null,
    val albumOfTrack: TopTrackAlbum? = null,
    val artists: TopTrackArtists? = null,
    val playcount: String? = null,
)

@Serializable
data class TopTrackAlbum(
    val uri: String = "",
    val name: String? = null,
    val coverArt: CoverArt? = null,
)

@Serializable
data class TopTrackArtists(
    val items: List<TopTrackArtist> = emptyList(),
)

@Serializable
data class TopTrackArtist(
    val uri: String = "",
    val profile: ArtistProfile? = null,
)

@Serializable
data class DiscographySection(
    val totalCount: Int = 0,
)

@Serializable
data class DiscographyPage(
    val items: List<DiscographyReleaseItem> = emptyList(),
    val totalCount: Int = 0,
)

@Serializable
data class DiscographyReleaseItem(
    val releases: ReleasePage? = null,
)

@Serializable
data class ReleasePage(
    val items: List<ReleaseItem> = emptyList(),
    val totalCount: Int? = null,
)

@Serializable
data class ReleaseItem(
    val id: String? = null,
    val name: String? = null,
    val uri: String? = null,
    val type: String? = null,
    val date: AlbumDate? = null,
    val coverArt: CoverArt? = null,
    val playability: Playability? = null,
    val sharingInfo: SharingInfo? = null,
    val tracks: TrackCount? = null,
    val copyright: CopyrightList? = null,
    val label: String? = null,
    val artists: ArtistList? = null,
)

@Serializable
data class ArtistRelatedContent(
    val appearsOn: DiscographyPage? = null,
    val discoveredOn: DiscographyPage? = null,
    val featuredOn: DiscographyPage? = null,
    val featuringV2: FeaturingV2? = null,
    val relatedArtists: RelatedArtistsPage? = null,
)

@Serializable
data class FeaturingV2(
    val items: List<FeaturingV2Item> = emptyList(),
)

@Serializable
data class FeaturingV2Item(
    val data: Playlist? = null,
)

@Serializable
data class RelatedArtistsPage(
    val items: List<RelatedArtistItem> = emptyList(),
)

@Serializable
data class RelatedArtistItem(
    val id: String = "",
    val profile: ArtistProfile? = null,
    val uri: String = "",
    val visuals: ArtistVisuals? = null,
)

@Serializable
data class ArtistList(
    val items: List<ArtistRef> = emptyList(),
)

@Serializable
data class ArtistRef(
    val uri: String = "",
    val profile: ArtistProfile? = null,
)

// ─── Playlist ────────────────────────────────────────────────────────────────

@Serializable
@SerialName("PlaylistResponseWrapper")
data class PlaylistResponseWrapper(
    override val __typename: String = "PlaylistResponseWrapper",
    val data: Playlist? = null,
) : ResponseWrapper

@Serializable
@SerialName("Playlist")
data class Playlist(
    override val __typename: String = "Playlist",
    val uri: String = "",
    val name: String = "",
    val description: String? = null,
    val format: String? = null,
    val images: PlaylistImages? = null,
    val ownerV2: OwnerV2? = null,
    val attributes: List<Attribute> = emptyList(),
    val followers: Long? = null,
    val following: Boolean? = null,
    val basePermission: String? = null,
    val content: PlaylistContent? = null,
    val currentUserCapabilities: CurrentUserCapabilities? = null,
    val revisionId: String? = null,
    val sharingInfo: SharingInfo? = null,
) : ContentItem

@Serializable
data class PlaylistImages(
    val items: List<PlaylistImageItem> = emptyList(),
)

@Serializable
data class PlaylistImageItem(
    val extractedColors: ExtractedColors? = null,
    val sources: List<ImageSource> = emptyList(),
)

@Serializable
data class OwnerV2(
    val data: UserData? = null,
)

@Serializable
data class UserData(
    val uri: String = "",
    val name: String = "",
    val username: String = "",
    val avatar: UserAvatar? = null,
)

@Serializable
data class UserAvatar(
    val sources: List<ImageSource> = emptyList(),
)

@Serializable
data class Attribute(
    val key: String = "",
    val value: String = "",
)

@Serializable
data class PlaylistContent(
    val items: List<PlaylistItem> = emptyList(),
    val pagingInfo: PagingInfo? = null,
    val totalCount: Int = 0,
)

@Serializable
data class PlaylistItem(
    val addedAt: AddedAt? = null,
    val addedBy: AddedBy? = null,
    val attributes: List<Attribute> = emptyList(),
    val itemV2: TrackResponseWrapper? = null,
    val uid: String = "",
)

@Serializable
data class AddedAt(
    val isoString: String,
)

@Serializable
data class AddedBy(
    val uri: String? = null,
    val username: String? = null,
)

@Serializable
data class PagingInfo(
    val limit: Int = 0,
    val offset: Int = 0,
    val nextOffset: Int? = null,
)

@Serializable
data class CurrentUserCapabilities(
    val canAdministratePermissions: Boolean = false,
    val canCancelMembership: Boolean = false,
    val canEditItems: Boolean = false,
    val canView: Boolean = false,
)

@Serializable
data class SharingInfo(
    val shareId: String = "",
    val shareUrl: String = "",
)

// ─── Track ───────────────────────────────────────────────────────────────────

@Serializable
@SerialName("TrackResponseWrapper")
data class TrackResponseWrapper(
    override val __typename: String = "TrackResponseWrapper",
    val data: Track? = null,
) : ResponseWrapper

@Serializable
@SerialName("Track")
data class Track(
    override val __typename: String = "Track",
    val uri: String = "",
    val id: String? = null,
    val name: String = "",
    val duration: TrackDuration? = null,
    val trackDuration: TrackDuration? = null,
    val trackNumber: Int? = null,
    val discNumber: Int? = null,
    val contentRating: ContentRating? = null,
    val playability: Playability? = null,
    val playcount: String? = null,
    val saved: Boolean? = null,
    val mediaType: String? = null,
    val albumOfTrack: AlbumOfTrack? = null,
    val artists: ArtistList? = null,
    val firstArtist: ArtistList? = null,
    val associationsV3: AssociationsV3? = null,
    val sharingInfo: SharingInfo? = null,
    val visualIdentity: VisualIdentity? = null,
) : ContentItem

@Serializable
data class TrackDuration(
    val totalMilliseconds: Long,
)

@Serializable
data class ContentRating(
    val label: String,
)

@Serializable
data class Playability(
    val playable: Boolean,
    val reason: String? = null,
)

@Serializable
data class AlbumOfTrack(
    val uri: String = "",
    val id: String? = null,
    val name: String? = null,
    val type: String? = null,
    val artists: ArtistList? = null,
    val coverArt: CoverArt? = null,
    val date: AlbumDate? = null,
    val playability: Playability? = null,
    val sharingInfo: SharingInfo? = null,
    val tracks: AlbumTracks? = null,
    val copyright: CopyrightList? = null,
    val courtesyLine: String? = null,
)

@Serializable
data class AlbumTracks(
    val items: List<AlbumTrackItem> = emptyList(),
    val totalCount: Int = 0,
)

@Serializable
data class AlbumTrackItem(
    val track: AlbumTrackRef? = null,
)

@Serializable
data class AlbumTrackRef(
    val uri: String = "",
    val trackNumber: Int? = null,
)

@Serializable
data class AlbumTracksV2(
    val items: List<AlbumTrackItemV2> = emptyList(),
    val totalCount: Int = 0,
)

@Serializable
data class AlbumTrackItemV2(
    val uid: String = "",
    val track: TrackV2? = null,
)

@Serializable
data class TrackV2(
    val __typename: String = "Track",
    val uri: String = "",
    val id: String? = null,
    val name: String = "",
    val duration: TrackDuration? = null,
    val trackNumber: Int? = null,
    val discNumber: Int? = null,
    val contentRating: ContentRating? = null,
    val playability: Playability? = null,
    val playcount: String? = null,
    val saved: Boolean? = null,
    val mediaType: String? = null,
    val albumOfTrack: AlbumOfTrack? = null,
    val artists: ArtistList? = null,
    val firstArtist: ArtistList? = null,
    val associationsV3: AssociationsV3? = null,
    val sharingInfo: SharingInfo? = null,
    val visualIdentity: VisualIdentity? = null,
)

@Serializable
data class AssociationsV3(
    val audioAssociations: TrackAudioAssociationPage? = null,
    val videoAssociations: VideoAssociationPage? = null,
)

@Serializable
data class TrackAudioAssociationPage(
    override val __typename: String = "TrackAudioAssociationPage",
    val items: List<JsonObject> = emptyList(),
    val totalCount: Int = 0,
) : ContentItem

@Serializable
data class VideoAssociationPage(
    val totalCount: Int = 0,
)

@Serializable
data class VisualIdentity(
    val squareCoverImage: VisualIdentityImage? = null,
)

@Serializable
data class VisualIdentityImage(
    override val __typename: String = "VisualIdentityImage",
    val extractedColorSet: ExtractedColorSet? = null,
) : ContentItem

@Serializable
data class ExtractedColorSet(
    val encoreBaseSetTextColor: ColorRGBA? = null,
    val highContrast: ColorContrastSet? = null,
    val higherContrast: ColorContrastSet? = null,
    val minContrast: ColorContrastSet? = null,
)

@Serializable
data class ColorRGBA(
    val alpha: Int,
    val red: Int,
    val green: Int,
    val blue: Int,
)

@Serializable
data class ColorContrastSet(
    val backgroundBase: ColorRGBA? = null,
    val backgroundTintedBase: ColorRGBA? = null,
    val textBase: ColorRGBA? = null,
    val textBrightAccent: ColorRGBA? = null,
    val textSubdued: ColorRGBA? = null,
)

// ─── User ────────────────────────────────────────────────────────────────────

@Serializable
@SerialName("UserResponseWrapper")
data class UserResponseWrapper(
    override val __typename: String = "UserResponseWrapper",
    val data: User? = null,
) : ResponseWrapper

@Serializable
@SerialName("User")
data class User(
    override val __typename: String = "User",
    val uri: String = "",
    val displayName: String = "",
    val username: String = "",
    val avatar: UserAvatar? = null,
) : ContentItem

// ─── Genre ───────────────────────────────────────────────────────────────────

@Serializable
@SerialName("GenreResponseWrapper")
data class GenreResponseWrapper(
    override val __typename: String = "GenreResponseWrapper",
    val data: Genre? = null,
) : ResponseWrapper

@Serializable
@SerialName("Genre")
data class Genre(
    override val __typename: String = "Genre",
    val name: String = "",
    val uri: String = "",
) : ContentItem

// ─── PseudoPlaylist ──────────────────────────────────────────────────────────

@Serializable
@SerialName("LibraryPseudoPlaylistResponseWrapper")
data class LibraryPseudoPlaylistResponseWrapper(
    override val __typename: String = "LibraryPseudoPlaylistResponseWrapper",
    val data: PseudoPlaylist? = null,
    @SerialName("_uri") val uri: String? = null,
) : ResponseWrapper

@Serializable
@SerialName("PseudoPlaylist")
data class PseudoPlaylist(
    override val __typename: String = "PseudoPlaylist",
    val uri: String = "",
    val name: String = "",
    val count: Int = 0,
    val image: CoverArt? = null,
) : ContentItem

// ─── Special / Error types ───────────────────────────────────────────────────

@Serializable
data class NotFound(
    override val __typename: String = "NotFound",
) : ContentItem

@Serializable
data class GenericError(
    override val __typename: String = "GenericError",
    val message: String? = null,
) : ContentItem

@Serializable
@SerialName("UnknownType")
data class UnknownTypeContent(
    override val __typename: String = "UnknownType",
) : ContentItem

@Serializable
@SerialName("UnknownType")
data class UnknownTypeResponseWrapper(
    override val __typename: String = "UnknownType",
) : ResponseWrapper

@Serializable
@SerialName("BrowseSectionContainerWrapper")
data class BrowseSectionContainerWrapper(
    override val __typename: String = "BrowseSectionContainerWrapper",
    val data: BrowseSectionContainerContent? = null,
) : ResponseWrapper

@Serializable
@SerialName("BrowseXlinkResponseWrapper")
data class BrowseXlinkResponseWrapper(
    override val __typename: String = "BrowseXlinkResponseWrapper",
    val data: BrowseClientFeature? = null,
) : ResponseWrapper

@Serializable
data class BrowseSectionContainerContent(
    val __typename: String = "BrowseSectionContainer",
    val data: BrowseSectionContainerData? = null,
)

@Serializable
data class BrowseSectionContainerData(
    val cardRepresentation: CardRepresentation? = null,
)

@Serializable
data class CardRepresentation(
    val artwork: CoverArt? = null,
    val backgroundColor: BrowseColor? = null,
    val title: Label? = null,
)

@Serializable
data class BrowseColor(
    val hex: String? = null,
)

@Serializable
data class BrowseClientFeature(
    val __typename: String = "BrowseClientFeature",
    val artwork: CoverArt? = null,
    val backgroundColor: BrowseColor? = null,
    val featureUri: String? = null,
    val iconOverlay: CoverArt? = null,
    val title: Label? = null,
)

@Serializable
data class BrowseSectionContainer(
    val __typename: String = "BrowseSectionContainer",
    val header: BrowseHeader? = null,
    val sections: BrowseSectionList? = null,
)

@Serializable
data class BrowseHeader(
    val backgroundImage: JsonObject? = null,
    val color: BrowseColor? = null,
    val subtitle: Label? = null,
    val title: Label? = null,
)

@Serializable
data class BrowseSectionList(
    val items: List<BrowseSection> = emptyList(),
    val totalCount: Int = 0,
    val pagingInfo: PagingInfo? = null,
)

@Serializable
data class BrowseSection(
    val __typename: String = "BrowseSection",
    val uri: String? = null,
    val data: BrowseSectionDataUnion? = null,
    val sectionItems: BrowseSectionItems? = null,
    val targetLocation: String? = null,
)

@Serializable
data class BrowseSectionItems(
    val items: List<BrowseSectionItem> = emptyList(),
    val totalCount: Int = 0,
)

@Serializable
data class BrowseSectionItem(
    val uri: String? = null,
    @Serializable(with = SpotifyGQLBaseClient.ResponseWrapperSerializer::class)
    val content: ResponseWrapper? = null,
)

@Serializable
sealed interface BrowseSectionDataUnion {
    val __typename: String
}

@Serializable
@SerialName("BrowseGenericSectionData")
data class BrowseGenericSectionData(
    override val __typename: String = "BrowseGenericSectionData",
    val title: Label? = null,
    val subtitle: Label? = null,
) : BrowseSectionDataUnion

@Serializable
@SerialName("BrowseGridSectionData")
data class BrowseGridSectionData(
    override val __typename: String = "BrowseGridSectionData",
    val title: Label? = null,
    val subtitle: Label? = null,
) : BrowseSectionDataUnion

@Serializable
@SerialName("BrowseRelatedSectionData")
data class BrowseRelatedSectionData(
    override val __typename: String = "BrowseRelatedSectionData",
    val title: Label? = null,
    val subtitle: Label? = null,
) : BrowseSectionDataUnion

@Serializable
@SerialName("BrowseShortsSectionData")
data class BrowseShortsSectionData(
    override val __typename: String = "BrowseShortsSectionData",
) : BrowseSectionDataUnion

@Serializable
@SerialName("BrowseUnsupportedSectionData")
data class BrowseUnsupportedSectionData(
    override val __typename: String = "BrowseUnsupportedSectionData",
) : BrowseSectionDataUnion

// ─── Home / Browse ───────────────────────────────────────────────────────────

@Serializable
data class HomeResponsePayload(
    override val __typename: String = "HomeResponsePayload",
    val greeting: Greeting? = null,
    val sectionContainer: SectionContainer? = null,
) : ContentItem

@Serializable
data class Greeting(
    val transformedLabel: String? = null,
    val translatedBaseText: String? = null,
)

@Serializable
data class SectionContainer(
    val sections: HomeSectionList? = null,
)

@Serializable
data class HomeSectionList(
    val items: List<HomeSectionItem> = emptyList(),
    val totalCount: Int = 0,
    val pagingInfo: PagingInfo? = null,
)

@Serializable
data class HomeSectionItem(
    val uri: String? = null,
    val data: HomeSectionDataUnion? = null,
    val sectionItems: SectionItems? = null,
    @Serializable(with = SpotifyGQLBaseClient.ResponseWrapperSerializer::class)
    val content: ResponseWrapper? = null,
)

@Serializable
sealed interface HomeSectionDataUnion {
    val __typename: String
}

@Serializable
@SerialName("HomeGenericSectionData")
data class HomeGenericSectionData(
    override val __typename: String = "HomeGenericSectionData",
    val title: Label? = null,
    val subtitle: Label? = null,
    val headerEntity: UnknownTypeContent? = null,
) : HomeSectionDataUnion

@Serializable
@SerialName("HomeShortsSectionData")
data class HomeShortsSectionData(
    override val __typename: String = "HomeShortsSectionData",
) : HomeSectionDataUnion

@Serializable
@SerialName("HomeRecentlyPlayedSectionData")
data class HomeRecentlyPlayedSectionData(
    override val __typename: String = "HomeRecentlyPlayedSectionData",
    val title: Label? = null,
    val subtitle: Label? = null,
    val headerEntity: UnknownTypeContent? = null,
) : HomeSectionDataUnion

@Serializable
data class Label(
    val transformedLabel: String? = null,
    val translatedBaseText: String? = null,
)

@Serializable
data class SectionItems(
    val items: List<SectionItemContent> = emptyList(),
    val totalCount: Int = 0,
    val pagingInfo: PagingInfo? = null,
)

@Serializable
data class SectionItemContent(
    val uri: String? = null,
    val data: JsonObject? = null,
    @Serializable(with = SpotifyGQLBaseClient.ResponseWrapperSerializer::class)
    val content: ResponseWrapper? = null,
)

@Serializable
data class HomeRecsItemData(
    override val __typename: String = "HomeRecsItemData",
) : HomeSectionDataUnion

@Serializable
data class HomeSectionCollection(
    override val __typename: String = "HomeSectionCollection",
    val sections: List<HomeSection> = emptyList(),
) : ContentItem

@Serializable
data class HomeSection(
    override val __typename: String = "HomeSection",
    val uri: String? = null,
    val data: HomeSectionDataUnion? = null,
    val sectionItems: SectionItems? = null,
) : ContentItem

// ─── Library ─────────────────────────────────────────────────────────────────

@Serializable
data class LibraryPage(
    override val __typename: String = "LibraryPage",
    val items: List<LibraryItem> = emptyList(),
    val totalCount: Int = 0,
    val pagingInfo: PagingInfo? = null,
    val availableFilters: List<LibraryFilter> = emptyList(),
    val availableSortOrders: List<LibrarySortOrder> = emptyList(),
    val breadcrumbs: List<JsonObject> = emptyList(),
    val selectedFilters: List<LibraryFilter> = emptyList(),
    val selectedSortOrder: LibrarySortOrder? = null,
) : ContentItem

@Serializable
data class LibraryItem(
    val addedAt: AddedAt? = null,
    val depth: Int = 0,
    val pinned: Boolean = false,
    val pinnable: Boolean = false,
    val playedAt: JsonObject? = null,
    @Serializable(with = SpotifyGQLBaseClient.ResponseWrapperSerializer::class)
    val item: ResponseWrapper? = null,
)

@Serializable
data class LibraryFilter(
    val id: String = "",
    val name: String = "",
)

@Serializable
data class LibrarySortOrder(
    val id: String = "",
    val name: String = "",
)

@Serializable
@SerialName("UserLibraryTrackPage")
data class UserLibraryTrackPage(
    override val __typename: String = "UserLibraryTrackPage",
    val items: List<UserLibraryTrackResponse> = emptyList(),
    val totalCount: Int = 0,
    val pagingInfo: PagingInfo? = null,
) : ContentItem

@Serializable
@SerialName("UserLibraryTrackResponse")
data class UserLibraryTrackResponse(
    override val __typename: String = "UserLibraryTrackResponse",
    val addedAt: AddedAt? = null,
    val track: LibraryTrackWrapper? = null,
) : ContentItem

@Serializable
data class LibraryTrackWrapper(
    val data: Track? = null,
    @SerialName("_uri") val uri: String? = null,
)

// ─── Search ──────────────────────────────────────────────────────────────────

@Serializable
data class AlbumOrPrereleasePage(
    override val __typename: String = "AlbumOrPrereleasePage",
    val items: List<AlbumResponseWrapper> = emptyList(),
    val totalCount: Int = 0,
) : ContentItem

@Serializable
data class PlaylistItemsPage(
    override val __typename: String = "PlaylistItemsPage",
    val items: List<PlaylistItem> = emptyList(),
    val pagingInfo: PagingInfo? = null,
    val totalCount: Int = 0,
) : ContentItem

// ─── Concert ─────────────────────────────────────────────────────────────────

@Serializable
data class ConcertV2(
    override val __typename: String = "ConcertV2",
    val uri: String = "",
    val title: String = "",
    val festival: Boolean = false,
    val location: ConcertLocation? = null,
    val startDateIsoString: String? = null,
) : ContentItem

@Serializable
data class ConcertLocation(
    val city: String? = null,
    val name: String? = null,
)

// ─── ImageV2 ─────────────────────────────────────────────────────────────────

@Serializable
data class ImageV2(
    override val __typename: String = "ImageV2",
    val sources: List<ImageSourceV2> = emptyList(),
) : ContentItem

@Serializable
data class ImageSourceV2(
    val url: String = "",
    @SerialName("maxHeight") val maxHeight: Int? = null,
    @SerialName("maxWidth") val maxWidth: Int? = null,
)

// ─── Music Videos ────────────────────────────────────────────────────────────

@Serializable
data class MusicVideosPage(
    override val __typename: String = "MusicVideosPage",
    val items: List<JsonObject> = emptyList(),
    val totalCount: Int = 0,
) : ContentItem

// ─── Mutation payloads ───────────────────────────────────────────────────────

@Serializable
data class AddItemsToPlaylistPayload(
    override val __typename: String = "AddItemsToPlaylistPayload",
) : ContentItem

@Serializable
data class RemoveItemsFromPlaylistPayload(
    override val __typename: String = "RemoveItemsFromPlaylistPayload",
) : ContentItem

@Serializable
data class RootlistItemMutationResult(
    override val __typename: String = "RootlistItemMutationResult",
) : ContentItem

// ─── Misc helpers ────────────────────────────────────────────────────────────

@Serializable
data class TrackCount(
    val totalCount: Int = 0,
)

@Serializable
data class CopyrightList(
    val items: List<CopyrightItem> = emptyList(),
    val totalCount: Int = 0,
)

@Serializable
data class CopyrightItem(
    val text: String = "",
    val type: String = "",
)
