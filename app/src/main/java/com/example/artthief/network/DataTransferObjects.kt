package com.example.artthief.network

import com.example.artthief.database.DatabaseArtwork
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * DataTransferObjects are responsible for parsing responses from the server or formatting
 * objects to send to the server.
 */

/**
 * First level of our network result which looks like:

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
   ]

 */

/**
 * Artworks represent an Art Thief item that can be viewed, rated.
 */
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
fun List<NetworkArtwork>.asDatabaseModel(): List<DatabaseArtwork> {
    return map {
        DatabaseArtwork(
            artThiefID = it.artThiefID,
            showID = it.showID,
            title = it.title,
            artist = it.artist,
            media = it.media,
            image_large = it.image_large,
            image_small = it.image_small,
            width = it.width,
            height = it.height,
            taken = it.taken,
            deleted = false, // an artwork is not hidden until the user deleted (hides) it
            rating = 0, // 0 stars represents that it's unrated
            order = 0
        )
    }
}

@JsonClass(generateAdapter = true)
data class NetworkArtworkPreferenceList(
    @Json(name = "artworks")
    val artworks: List<Int>
)
