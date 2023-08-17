package com.joerock.artthief.repository

import androidx.lifecycle.LiveData
import com.joerock.artthief.database.DatabaseArtwork
import com.joerock.artthief.domain.ArtThiefArtwork
import com.joerock.artthief.network.NetworkArtworkListPostResponse
import com.joerock.artthief.network.NetworkArtworkPreferenceList
import retrofit2.Response

interface ArtworksRepo {

    val artworks: LiveData<List<ArtThiefArtwork>>

    val artworksByRating: LiveData<List<ArtThiefArtwork>>

    val artworksByShowId: LiveData<List<ArtThiefArtwork>>

    val artworksByArtist: LiveData<List<ArtThiefArtwork>>

    val highestRatedArtwork: LiveData<ArtThiefArtwork>

    fun getArtworksByRating(rating: Int): LiveData<List<ArtThiefArtwork>>

    suspend fun refreshArtworks()

    suspend fun sendArtworkList(
        codeName: String,
        artworkList: NetworkArtworkPreferenceList
    ): Response<NetworkArtworkListPostResponse>

    suspend fun updateArtwork(artwork: DatabaseArtwork)
}
