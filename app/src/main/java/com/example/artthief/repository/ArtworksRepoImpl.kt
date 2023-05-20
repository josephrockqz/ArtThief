package com.example.artthief.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.artthief.database.ArtworksDatabase
import com.example.artthief.database.DatabaseArtwork
import com.example.artthief.database.asDomainModel
import com.example.artthief.domain.ArtThiefArtwork
import com.example.artthief.network.ArtThiefNetwork
import com.example.artthief.network.asDatabaseModel
import com.example.artthief.ui.rate.data.RecyclerViewSection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for fetching Art Thief artwork from the network and storing them on disk
 */
class ArtworksRepoImpl(private val database: ArtworksDatabase) : ArtworksRepo {

    override val artworks: LiveData<List<ArtThiefArtwork>> = Transformations.map(
        database.artworkDao.getArtworks()
    ) {
        it.asDomainModel()
    }

    override val artworksByRating: LiveData<List<ArtThiefArtwork>> = Transformations.map(
        database.artworkDao.getArtworks()
    ) { list ->
        list.asDomainModel().sortedByDescending { artwork ->
            artwork.rating
        }
    }

    override val artworksByShowId: LiveData<List<ArtThiefArtwork>> = Transformations.map(
        database.artworkDao.getArtworks()
    ) { list ->
        list.asDomainModel().sortedBy { artwork ->
            artwork.showID.toInt()
        }
    }

    override val artworksByArtist: LiveData<List<ArtThiefArtwork>> = Transformations.map(
        database.artworkDao.getArtworks()
    ) { list ->
        list.asDomainModel().sortedBy { artwork ->
            artwork.artist
        }
    }

    override val ratingSections: LiveData<List<RecyclerViewSection>> = Transformations.map(
        database.artworkDao.getArtworks()
    ) { list ->
        // sort artworks by descending rating and update view model
        val artworkListByRating = list.asDomainModel().sortedByDescending { artwork ->
            artwork.rating
        }

        // partition artworks by rating then assign to rv's sections
        val artworkRatingMap = artworkListByRating.groupBy { artwork ->
            artwork.rating
        }

        val artworkRatingSections = mutableListOf<RecyclerViewSection>()
        for (i in 5 downTo 0) {
            artworkRatingMap[i]?.let { list ->
                artworkRatingSections.add(RecyclerViewSection(i, list))
            }
        }
        artworkRatingSections
    }

    override val highestRatedArtwork: LiveData<ArtThiefArtwork> = Transformations.map(
        database.artworkDao.getArtworks()
    ) { list ->
        list.asDomainModel().sortedByDescending { artwork ->
            artwork.rating
        }[0]
    }

    override suspend fun refreshArtworks() {
        withContext(Dispatchers.IO) {
            val artworkList = ArtThiefNetwork.artThiefArtworks.getArtworkList("fb56a1e6-ee06-4911-ad33-c35c298fddbd")
            database.artworkDao.insertAll(artworkList.asDatabaseModel())
        }
    }

    override suspend fun updateArtwork(artwork: DatabaseArtwork) {
        withContext(Dispatchers.IO) {
            database.artworkDao.insert(artwork)
        }
    }
}
