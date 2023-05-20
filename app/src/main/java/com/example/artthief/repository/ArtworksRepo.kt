package com.example.artthief.repository

import androidx.lifecycle.LiveData
import com.example.artthief.database.DatabaseArtwork
import com.example.artthief.domain.ArtThiefArtwork
import com.example.artthief.ui.rate.data.RecyclerViewSection

interface ArtworksRepo {

    /**
     * This object is automatically updated when the database is updated
     * The attached fragment/activity is refreshed with new values
     */
    val artworks: LiveData<List<ArtThiefArtwork>>

    val artworksByRating: LiveData<List<ArtThiefArtwork>>

    val artworksByShowId: LiveData<List<ArtThiefArtwork>>

    val artworksByArtist: LiveData<List<ArtThiefArtwork>>

    val ratingSections: LiveData<List<RecyclerViewSection>>

    val highestRatedArtwork: LiveData<ArtThiefArtwork>

    suspend fun refreshArtworks()

    suspend fun updateArtwork(artwork: DatabaseArtwork)
}
