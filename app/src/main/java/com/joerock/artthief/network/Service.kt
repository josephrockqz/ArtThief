package com.joerock.artthief.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

/**
 * If more services are needed, split into multiple files
 * Make sure to share retrofit object between services
 */

/**
 * A retrofit service to fetch a Art Thief playlist.
 */
interface ArtThiefService {
    @GET("artwork")
    suspend fun getArtworkList(@Query("passcode") passcode: String): List<NetworkArtwork>

    @POST("user/{codeName}/selections")
    suspend fun postArtworkList(
        @Path("codeName") codeName: String,
        @Query("passcode") passcode: String,
        @Body artworkList: NetworkArtworkPreferenceList
    ): Response<NetworkArtworkListPostResponse>
}

/**
 * Main entry point for network access. Call like `ArtThiefNetwork.artThiefArtworks.getPlaylist()`
 */
object ArtThiefNetwork {

    private val httpClient = OkHttpClient.Builder().addInterceptor(object: Interceptor {
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val originalRequest = chain.request()
            val request = originalRequest.newBuilder()
                .header("Content-Type", "application/json")
                .method(originalRequest.method(), originalRequest.body())
                .build()

            return chain.proceed(request)
        }
    }).build()

    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://patronsshow.theartleague.org/api/")
        .addConverterFactory(MoshiConverterFactory.create())
        .client(httpClient)
        .build()

    val artThiefArtworks = retrofit.create(ArtThiefService::class.java)
}
