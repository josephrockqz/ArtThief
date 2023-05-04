package com.example.artthief.domain

import com.example.artthief.database.DatabaseArtwork
import com.example.artthief.network.NetworkArtwork

/**
 * Domain objects are plain Kotlin data classes that represent the things in our app. These are the
 * objects that should be displayed on screen, or manipulated by the app.
 *
 * @see database for objects that are mapped to the database
 * @see network for objects that parse or prepare network calls
 */
data class ArtThiefArtwork(
    val artThiefID: Int,
    val showID: String,
    val title: String,
    val artist: String,
    val media: String,
    val image_large: String,
    val image_small: String,
    val width: Float,
    val height: Float,
    val taken: Boolean,
    var rating: Int
) {
    /**
     * Dimensions is used for displaying artwork's 2 dimensions
     */
    val dimensions: String
        get() {
            val width = width.toString()
            val height = height.toString()
            return "$width\" by $height\""
        }
}

/**
 * Convert Domain entity to database object
 */
fun ArtThiefArtwork.asDatabaseModel(): DatabaseArtwork {
    return DatabaseArtwork(
        artThiefID = this.artThiefID,
        showID = this.showID,
        title = this.title,
        artist = this.artist,
        media = this.media,
        image_large = this.image_large,
        image_small = this.image_small,
        width = this.width,
        height = this.height,
        taken = this.taken,
        rating = this.rating
    )
}

/**
 * Default ArtThiefArtwork instance for use in pager adapter
 */
val defaultArtThiefArtwork = ArtThiefArtwork(
    artThiefID = 0,
    showID = "",
    title = "",
    artist = "",
    media = "",
    image_large = "",
    image_small = "",
    width = 1.toFloat(),
    height = 1.toFloat(),
    taken = true,
    rating = 0 // 0 stars represents that it's unrated
)
