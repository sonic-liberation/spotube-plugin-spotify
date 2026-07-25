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
import dev.krtirtho.plugin_interfaces.host_apis.HttpMethod
import io.github.sonic_liberation.spotify_gql_client.services.UserAgents
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

abstract class SpotifyGQLBaseClient(protected val client: HttpClientAPI) {
    companion object {
        const val SPOTIFY_GQL_ENDPOINT = "https://api-partner.spotify.com/pathfinder/v2/query"
        const val SPOTIFY_REST_ENDPOINT = "https://spclient.wg.spotify.com"
        var credentials: CredentialsFromCookieResult? = null
        fun getDefaultHeaders() = mapOf(
            "Content-Type" to "application/json",
            "Accept" to "application/json",
            "Authorization" to "Bearer ${credentials?.accessToken ?: ""}",
            "Cookie" to credentials?.cookies?.joinToString("; ") { "${it.name}=${it.value}" }
                .orEmpty(),
            "User-Agent" to UserAgents.random()
        )

        val json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            classDiscriminator = "__typename"
            serializersModule = SerializersModule {
                polymorphic(ResponseWrapper::class) {
                    subclass(AlbumResponseWrapper::class)
                    subclass(ArtistResponseWrapper::class)
                    subclass(PlaylistResponseWrapper::class)
                    subclass(TrackResponseWrapper::class)
                    subclass(UserResponseWrapper::class)
                    subclass(GenreResponseWrapper::class)
                    subclass(LibraryPseudoPlaylistResponseWrapper::class)
                    subclass(BrowseSectionContainerWrapper::class)
                    subclass(BrowseXlinkResponseWrapper::class)
                    subclass(UnknownTypeResponseWrapper::class)
                }
                polymorphic(ContentItem::class) {
                    subclass(Album::class)
                    subclass(Artist::class)
                    subclass(Playlist::class)
                    subclass(Track::class)
                    subclass(User::class)
                    subclass(Genre::class)
                    subclass(PseudoPlaylist::class)
                    subclass(NotFound::class)
                    subclass(GenericError::class)
                    subclass(UnknownTypeContent::class)
                    subclass(HomeResponsePayload::class)
                    subclass(HomeSectionCollection::class)
                    subclass(HomeSection::class)
                    subclass(LibraryPage::class)
                    subclass(UserLibraryTrackPage::class)
                    subclass(UserLibraryTrackResponse::class)
                    subclass(AlbumOrPrereleasePage::class)
                    subclass(PlaylistItemsPage::class)
                    subclass(ConcertV2::class)
                    subclass(ImageV2::class)
                    subclass(MusicVideosPage::class)
                    subclass(TrackAudioAssociationPage::class)
                    subclass(VisualIdentityImage::class)
                    subclass(AddItemsToPlaylistPayload::class)
                    subclass(RemoveItemsFromPlaylistPayload::class)
                    subclass(RootlistItemMutationResult::class)
                }
                polymorphic(HomeSectionDataUnion::class) {
                    subclass(HomeGenericSectionData::class)
                    subclass(HomeRecentlyPlayedSectionData::class)
                    subclass(HomeRecsItemData::class)
                }
                polymorphic(BrowseSectionDataUnion::class) {
                    subclass(BrowseGenericSectionData::class)
                    subclass(BrowseGridSectionData::class)
                    subclass(BrowseRelatedSectionData::class)
                    subclass(BrowseShortsSectionData::class)
                    subclass(BrowseUnsupportedSectionData::class)
                }
            }
        }
    }

    object ResponseWrapperSerializer : KSerializer<ResponseWrapper> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ResponseWrapper")

        override fun deserialize(decoder: Decoder): ResponseWrapper {
            val jsonDecoder = decoder as? JsonDecoder
                ?: throw IllegalStateException("ResponseWrapper can only be deserialized from JSON")
            val element = jsonDecoder.decodeJsonElement()
            val typename = (element as? JsonObject)?.get("__typename")?.jsonPrimitive?.content
            return try {
                when (typename) {
                    "AlbumResponseWrapper" -> json.decodeFromJsonElement(AlbumResponseWrapper.serializer(), element)
                    "ArtistResponseWrapper" -> json.decodeFromJsonElement(ArtistResponseWrapper.serializer(), element)
                    "PlaylistResponseWrapper" -> json.decodeFromJsonElement(PlaylistResponseWrapper.serializer(), element)
                    "TrackResponseWrapper" -> json.decodeFromJsonElement(TrackResponseWrapper.serializer(), element)
                    "UserResponseWrapper" -> json.decodeFromJsonElement(UserResponseWrapper.serializer(), element)
                    "GenreResponseWrapper" -> json.decodeFromJsonElement(GenreResponseWrapper.serializer(), element)
                    "LibraryPseudoPlaylistResponseWrapper" -> json.decodeFromJsonElement(LibraryPseudoPlaylistResponseWrapper.serializer(), element)
                    "BrowseSectionContainerWrapper" -> json.decodeFromJsonElement(BrowseSectionContainerWrapper.serializer(), element)
                    "BrowseXlinkResponseWrapper" -> json.decodeFromJsonElement(BrowseXlinkResponseWrapper.serializer(), element)
                    else -> {
                        println("Unknown ResponseWrapper type: $typename, treating as UnknownTypeResponseWrapper")
                        UnknownTypeResponseWrapper(__typename = typename ?: "UnknownType")
                    }
                }
            } catch (e: Exception) {
                println("Failed to deserialize ResponseWrapper type: $typename, error: ${e.message}")
                UnknownTypeResponseWrapper(__typename = typename ?: "UnknownType")
            }
        }

        override fun serialize(encoder: Encoder, value: ResponseWrapper) {
            when (value) {
                is AlbumResponseWrapper -> encoder.encodeSerializableValue(AlbumResponseWrapper.serializer(), value)
                is ArtistResponseWrapper -> encoder.encodeSerializableValue(ArtistResponseWrapper.serializer(), value)
                is PlaylistResponseWrapper -> encoder.encodeSerializableValue(PlaylistResponseWrapper.serializer(), value)
                is TrackResponseWrapper -> encoder.encodeSerializableValue(TrackResponseWrapper.serializer(), value)
                is UserResponseWrapper -> encoder.encodeSerializableValue(UserResponseWrapper.serializer(), value)
                is GenreResponseWrapper -> encoder.encodeSerializableValue(GenreResponseWrapper.serializer(), value)
                is LibraryPseudoPlaylistResponseWrapper -> encoder.encodeSerializableValue(LibraryPseudoPlaylistResponseWrapper.serializer(), value)
                is BrowseSectionContainerWrapper -> encoder.encodeSerializableValue(BrowseSectionContainerWrapper.serializer(), value)
                is BrowseXlinkResponseWrapper -> encoder.encodeSerializableValue(BrowseXlinkResponseWrapper.serializer(), value)
                is UnknownTypeResponseWrapper -> encoder.encodeSerializableValue(UnknownTypeResponseWrapper.serializer(), value)
            }
        }
    }

    protected fun restUrl(path: String) = "$SPOTIFY_REST_ENDPOINT$path"

    protected fun mergeHeaders(requestHeaders: Map<String, String>? = null): Map<String, String> =
        getDefaultHeaders() + (requestHeaders ?: emptyMap())

    protected suspend inline fun <reified T> get(
        url: String,
        requestHeaders: Map<String, String>? = null,
    ): T? {
        val res = client.request(HttpMethod.Get, url, mergeHeaders(requestHeaders), null)
        if (res.statusCode >= 400) {
            throw Exception("Spotify GQL request failed with status code ${res.statusCode} and body ${res.body}")
        }

        if (res.body == null || res.body!!.isBlank()) {
            return null
        }

        return json.decodeFromString<T>(res.body!!)
    }

    protected suspend inline fun <reified T, reified B> post(
        url: String,
        body: B,
        requestHeaders: Map<String, String>? = null,
    ): T? {
        val bodyJson = json.encodeToString(body)

        val res = client.request(HttpMethod.Post, url, mergeHeaders(requestHeaders), bodyJson)
        if (res.statusCode >= 400) {
            throw Exception("Spotify GQL request failed with status code ${res.statusCode} and body ${res.body}")
        }

        if (res.body == null || res.body!!.isBlank()) {
            return null
        }

        return json.decodeFromString<T>(res.body!!)
    }

    protected suspend inline fun <reified T, reified B> put(
        url: String,
        body: B,
        requestHeaders: Map<String, String>? = null,
    ): T? {
        val bodyJson = json.encodeToString(body)

        val res = client.request(HttpMethod.Put, url, mergeHeaders(requestHeaders), bodyJson)
        if (res.statusCode >= 400) {
            throw Exception("Spotify GQL request failed with status code ${res.statusCode} and body ${res.body}")
        }

        if (res.body == null || res.body!!.isBlank()) {
            return null
        }

        return json.decodeFromString<T>(res.body!!)
    }


    protected suspend inline fun <reified T, reified B> delete(
        url: String,
        requestHeaders: Map<String, String>? = null,
        body: B,
    ): T? {
        val bodyJson = json.encodeToString(body)
        val res = client.request(HttpMethod.Delete, url, mergeHeaders(requestHeaders), bodyJson)
        if (res.statusCode >= 400) {
            throw Exception("Spotify GQL request failed with status code ${res.statusCode} and body ${res.body}")
        }

        if (res.body == null || res.body!!.isBlank()) {
            return null
        }

        return json.decodeFromString<T>(res.body!!)
    }
}