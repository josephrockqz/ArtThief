package com.joerock.artthief.network

import com.joerock.artthief.database.DatabaseArtwork
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
            "artistUrl":"https://example.com/artist/jeanne-garant",
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
    val artistUrl: String?,
    val media: String,
    val image_large: String,
    val image_small: String,
    val width: Float,
    val height: Float,
    val taken: Boolean
)

fun networkArtworkToDatabaseArtwork(networkArtwork: NetworkArtwork): DatabaseArtwork {
    return DatabaseArtwork(
        artThiefID = networkArtwork.artThiefID,
        showID = networkArtwork.showID,
        title = networkArtwork.title,
        artist = networkArtwork.artist,
        artistUrl = networkArtwork.artistUrl,
        media = networkArtwork.media,
        image_large = networkArtwork.image_large,
        image_small = networkArtwork.image_small,
        width = networkArtwork.width,
        height = networkArtwork.height,
        taken = networkArtwork.taken,
        deleted = false, // an artwork is not hidden until the user deleted (hides) it
        rating = 0, // 0 stars represents that it's unrated
        order = 0
    )
}

fun updateArtworkInformation(
    networkArtwork: NetworkArtwork,
    databaseArtwork: DatabaseArtwork
): DatabaseArtwork {
    return DatabaseArtwork(
        artThiefID = databaseArtwork.artThiefID,
        showID = networkArtwork.showID,
        title = networkArtwork.title,
        artist = networkArtwork.artist,
        artistUrl = networkArtwork.artistUrl ?: databaseArtwork.artistUrl,
        media = networkArtwork.media,
        image_large = networkArtwork.image_large,
        image_small = networkArtwork.image_small,
        width = networkArtwork.width,
        height = networkArtwork.height,
        taken = networkArtwork.taken,
        deleted = databaseArtwork.deleted,
        rating = databaseArtwork.rating,
        order = databaseArtwork.order
    )
}

@JsonClass(generateAdapter = true)
data class NetworkArtworkPreferenceList(
    @Json(name = "artworks")
    val artworks: List<Int>
)

@JsonClass(generateAdapter = true)
data class NetworkArtworkListPostResponse(
    @Json(name = "status")
    val status: String,
    @Json(name = "message")
    val message: String,
    @Json(name = "data")
    val data: List<Int>
)
