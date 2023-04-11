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
    val url: String,
    val updated: String,
    val title: String,
    val description: String,
    val thumbnail: String
)

/**
 * Map DatabaseVideos to domain entities
 */
fun List<DatabaseArtwork>.asDomainModel(): List<ArtThiefArtwork> {
    return map {
        ArtThiefArtwork(
            url = it.url,
            title = it.title,
            description = it.description,
            updated = it.updated,
            thumbnail = it.thumbnail
        )
    }
}
