package com.example.artthief.network

import com.example.artthief.database.DatabaseArtwork
import com.example.artthief.domain.ArtThiefArtwork
import com.squareup.moshi.JsonClass

/**
 * DataTransferObjects are responsible for parsing responses from the server or formatting
 * objects to send to the server. You should convert these to domain objects before using them.
 */

/**
 * ArtworkHolder holds a list of Artwork.
 *
 * This is to parse first level of our network result which looks like
 *
 * {
 *   "artworks": []
 * }
 */
@JsonClass(generateAdapter = true)
data class NetworkArtworkContainer(val artworks: List<NetworkArtwork>)

/**
 * Artworks represent an Art Thief item that can be viewed, rated.
 */
@JsonClass(generateAdapter = true)
data class NetworkArtwork(
    val title: String,
    val description: String,
    val url: String,
    val updated: String,
    val thumbnail: String,
    val closedCaptions: String?
)

/**
 * Convert Network results to database objects
 */
fun NetworkArtworkContainer.asDomainModel(): List<ArtThiefArtwork> {
    return artworks.map {
        ArtThiefArtwork(
            url = it.url,
            title = it.title,
            description = it.description,
            updated = it.updated,
            thumbnail = it.thumbnail
        )
    }
}

/**
 * Convert Network results to database objects
 */
fun NetworkArtworkContainer.asDatabaseModel(): List<DatabaseArtwork> {
    return artworks.map {
        DatabaseArtwork(
            title = it.title,
            description = it.description,
            url = it.url,
            updated = it.updated,
            thumbnail = it.thumbnail)
    }
}
