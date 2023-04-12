package com.example.artthief.network

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * If more services are needed, split into multiple files
 * Make sure to share retrofit object between services
 */

/**
 * A retrofit service to fetch a Art Thief playlist.
 */
interface ArtThiefService {
//    @GET("artThiefArtworks")
//    suspend fun getArtworkList(): NetworkArtworkContainer

    @GET("artwork")
    suspend fun getArtworkList(@Query("passcode") passcode: String): List<NetworkArtwork>
}

/**
 * Main entry point for network access. Call like `ArtThiefNetwork.artThiefArtworks.getPlaylist()`
 */
object ArtThiefNetwork {

    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://patronsshow.theartleague.org/api/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val artThiefArtworks = retrofit.create(ArtThiefService::class.java)
}
