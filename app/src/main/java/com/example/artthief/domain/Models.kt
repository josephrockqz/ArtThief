package com.example.artthief.domain

/**
 * Domain objects are plain Kotlin data classes that represent the things in our app. These are the
 * objects that should be displayed on screen, or manipulated by the app.
 *
 * @see database for objects that are mapped to the database
 * @see network for objects that parse or prepare network calls
 */

/**
 * Videos represent a devbyte that can be played.
 */
data class ArtThiefArtwork(
    val title: String,
    val description: String,
    val url: String,
    val updated: String,
    val thumbnail: String
) {
    // TODO - any calculated fields for an artwork go here
}
