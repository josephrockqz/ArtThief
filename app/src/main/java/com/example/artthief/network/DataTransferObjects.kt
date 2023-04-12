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
 * This is to parse first level of our network result which looks like:
 * [
       {
            "artThiefID":2012345,
            "showID":"259",
            "title":"Math Class",
            "artist":"Jeanne Garant",
            "media":"Oil",
            "image_large":"https:\/\/artthief.zurka.com\/images\/Large\/12345L-22.jpg",
            "image_small":"https:\/\/artthief.zurka.com\/images\/Small\/12345S-22.jpg",
            "width":38,
            "height":38,
            "taken":true
        }
        ...
 * ]
 */
//@JsonClass(generateAdapter = true)
//data class NetworkArtworkContainer(val artworks: List<NetworkArtwork>)

/**
 * Artworks represent an Art Thief item that can be viewed, rated.
 */
//@JsonClass(generateAdapter = true)
//data class NetworkArtwork(
//    val title: String,
//    val description: String,
//    val url: String,
//    val updated: String,
//    val thumbnail: String,
//    val closedCaptions: String?
//)

@JsonClass(generateAdapter = true)
data class NetworkArtwork(
    val artThiefID: Int,
    val showID: String,
    val title: String,
    val artist: String,
    val media: String,
    val image_large: String,
    val image_small: String,
    val width: Float,
    val height: Float,
    val taken: Boolean
)

/**
 * Convert Network results to database objects
 */
//fun NetworkArtworkContainer.asDomainModel(): List<ArtThiefArtwork> {
//    return artworks.map {
//        ArtThiefArtwork(
//            url = it.url,
//            title = it.title,
//            description = it.description,
//            updated = it.updated,
//            thumbnail = it.thumbnail
//        )
//    }
//}

fun List<NetworkArtwork>.asDomainModel(): List<ArtThiefArtwork> {
    return map {
        ArtThiefArtwork(
            artThiefID = it.artThiefID,
            showID = it.showID,
            title = it.title,
            artist = it.title,
            media = it.media,
            image_large = it.image_large,
            image_small = it.image_small,
            width = it.width,
            height = it.height,
            taken = it.taken
        )
    }
}

/**
 * Convert Network results to database objects
 */
//fun NetworkArtworkContainer.asDatabaseModel(): List<DatabaseArtwork> {
//    return artworks.map {
//        DatabaseArtwork(
//            title = it.title,
//            description = it.description,
//            url = it.url,
//            updated = it.updated,
//            thumbnail = it.thumbnail
//        )
//    }
//}

fun List<NetworkArtwork>.asDatabaseModel(): List<DatabaseArtwork> {
    return map {
        DatabaseArtwork(
            artThiefID = it.artThiefID,
            showID = it.showID,
            title = it.title,
            artist = it.title,
            media = it.media,
            image_large = it.image_large,
            image_small = it.image_small,
            width = it.width,
            height = it.height,
            taken = it.taken
        )
    }
}
