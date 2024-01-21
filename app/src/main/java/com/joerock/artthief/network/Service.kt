package com.joerock.artthief.network

import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

/**
 * A retrofit service to fetch a Art Thief playlist.
 */
interface ArtThiefService {
    @GET("artwork")
    suspend fun getArtworkList(@Query("passcode") passcode: String): NetworkArtworkObject

    @POST("user/{codeName}/selections")
    suspend fun postArtworkList(
        @Path("codeName") codeName: String,
        @Query("passcode") passcode: String,
        @Body artworkList: NetworkArtworkPreferenceList
    ): Response<NetworkArtworkListPostResponse>
}

/**
 * Main entry point for network access
 */
object ArtThiefNetwork {

    private val httpClient = OkHttpClient.Builder().addInterceptor { chain ->
        val originalRequest = chain.request()
        val request = originalRequest.newBuilder()
            .header("Content-Type", "application/json")
            .method(originalRequest.method(), originalRequest.body())
            .build()

        chain.proceed(request)
    }.build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://patronsshow.theartleague.org/api/")
        .addConverterFactory(MoshiConverterFactory.create())
        .client(httpClient)
        .build()

    val artThiefArtworks: ArtThiefService = retrofit.create(ArtThiefService::class.java)
}
