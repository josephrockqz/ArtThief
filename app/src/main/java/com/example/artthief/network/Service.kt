package com.example.artthief.network

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

/**
 * If more services are needed, split into multiple files
 * Make sure to share retrofit object between services
 */

/**
 * A retrofit service to fetch a Art Thief playlist.
 */
interface ArtThiefService {
    @GET("artThiefArtworks")
    suspend fun getPlaylist(): NetworkArtworkContainer
}

/**
 * Main entry point for network access. Call like `ArtThiefNetwork.artThiefArtworks.getPlaylist()`
 */
object ArtThiefNetwork {

    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://patronsshow.theartleague.org/api/artwork")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val artThiefArtworks = retrofit.create(ArtThiefService::class.java)

}
