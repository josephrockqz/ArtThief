package com.example.artthief.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.artthief.database.ArtworksDatabase
import com.example.artthief.database.DatabaseArtwork
import com.example.artthief.database.asDomainModel
import com.example.artthief.domain.ArtThiefArtwork
import com.example.artthief.network.ArtThiefNetwork
import com.example.artthief.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for fetching Art Thief artwork from the network and storing them on disk
 */
class ArtworksRepo(private val database: ArtworksDatabase) {

    /**
     * This object is automatically updated when the database is updated
     * The attached fragment/activity is refreshed with new values
     */
    val artworks: LiveData<List<ArtThiefArtwork>> = Transformations.map(
        database.artworkDao.getArtworks()
    ) {
        it.asDomainModel()
    }

    suspend fun refreshArtworks() {
        withContext(Dispatchers.IO) {
            val artworkList = ArtThiefNetwork.artThiefArtworks.getArtworkList("fb56a1e6-ee06-4911-ad33-c35c298fddbd")
            database.artworkDao.insertAll(artworkList.asDatabaseModel())
        }
    }

    fun updateArtworkRating(artwork: DatabaseArtwork) {
        database.artworkDao.insert(artwork)
    }
}
