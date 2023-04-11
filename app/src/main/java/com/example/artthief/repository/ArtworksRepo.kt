package com.example.artthief.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.artthief.database.ArtworksDatabase
import com.example.artthief.database.asDomainModel
import com.example.artthief.domain.ArtThiefArtwork
import com.example.artthief.network.ArtThiefNetwork
import com.example.artthief.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for fetching Art Thief artwork from the network and storing them on disk
 */
class VideosRepository(private val database: ArtworksDatabase) {

    val artworks: LiveData<List<ArtThiefArtwork>> = Transformations.map(
        database.artworkDao.getArtworks()
    ) {
        it.asDomainModel()
    }

    suspend fun refreshArtworks() {
        withContext(Dispatchers.IO) {
            val playlist = ArtThiefNetwork.artThiefArtworks.getArtworkList()
            database.artworkDao.insertAll(playlist.asDatabaseModel())
        }
    }

}
