package com.example.artthief.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.artthief.domain.ArtThiefArtwork

/**
 * Database entities are responsible for reading and writing from the database.
 */

/**
 * DatabaseVideo represents a video entity in the database.
 */
@Entity
data class DatabaseArtwork constructor(
    @PrimaryKey
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
    val rating: Int
)

/**
 * Map DatabaseVideos to domain entities
 */
fun List<DatabaseArtwork>.asDomainModel(): List<ArtThiefArtwork> {
    return map {
        ArtThiefArtwork(
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
            rating = it.rating
        )
    }
}
