package com.joerock.artthief.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.joerock.artthief.domain.ArtThiefArtwork

/**
 * Database entities are responsible for reading and writing from the database.
 */

/**
 * DatabaseArtwork represents an artwork entity in the database.
 */
@Entity
data class DatabaseArtwork constructor(
    @PrimaryKey
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
    val taken: Boolean,
    val deleted: Boolean,
    val rating: Int,
    val order: Int
)

/**
 * Map DatabaseArtwork to domain entities
 */
fun List<DatabaseArtwork>.asDomainModel(): List<ArtThiefArtwork> {
    return map {
        ArtThiefArtwork(
            artThiefID = it.artThiefID,
            showID = it.showID,
            title = it.title,
            artist = it.artist,
            artistUrl = it.artistUrl,
            media = it.media,
            image_large = it.image_large,
            image_small = it.image_small,
            width = it.width,
            height = it.height,
            taken = it.taken,
            deleted = it.deleted,
            rating = it.rating,
            order = it.order
        )
    }
}
