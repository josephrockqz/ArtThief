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
// TODO: Separate Model, Repo, and Network classes into interfaces as well (e.g. ArtworksRepo & ArtworksRepoImpl)
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

    // TODO: rename all it1, it2 etc. usages to be more descriptive
    val artworksByRating: LiveData<List<ArtThiefArtwork>> = Transformations.map(
        database.artworkDao.getArtworks()
    ) { it1 ->
        it1.asDomainModel().sortedByDescending { it2 ->
            it2.rating
        }
    }

    val artworksByShowId: LiveData<List<ArtThiefArtwork>> = Transformations.map(
        database.artworkDao.getArtworks()
    ) { it1 ->
        it1.asDomainModel().sortedBy { it2 ->
            it2.showID.toInt()
        }
    }

    val artworksByArtist: LiveData<List<ArtThiefArtwork>> = Transformations.map(
        database.artworkDao.getArtworks()
    ) { it1 ->
        it1.asDomainModel().sortedBy { it2 ->
            it2.artist
        }
    }

    val ratingSections: LiveData<List<RecyclerViewSection>> = Transformations.map(
        database.artworkDao.getArtworks()
    ) { it1 ->
        // sort artworks by descending rating and update view model
        val artworkListByRating = it1.asDomainModel().sortedByDescending { it2 ->
            it2.rating
        }

        // partition artworks by rating then assign to rv's sections
        val artworkRatingMap = artworkListByRating.groupBy { it3 ->
            it3.rating
        }

        val artworkRatingSections = mutableListOf<RecyclerViewSection>()
        for (i in 5 downTo 0) {
            artworkRatingMap[i]?.let { it4 ->
                artworkRatingSections.add(RecyclerViewSection(i, it4))
            }
        }
        artworkRatingSections
    }

    val highestRatedArtworkUrl: LiveData<String> = Transformations.map(
        database.artworkDao.getArtworks()
    ) { it1 ->
        it1.asDomainModel().sortedByDescending { it2 ->
            it2.rating
        }[0].image_small
    }

    suspend fun refreshArtworks() {
        withContext(Dispatchers.IO) {
            // TODO: get rid of hardcoded passkey
            val artworkList = ArtThiefNetwork.artThiefArtworks.getArtworkList("fb56a1e6-ee06-4911-ad33-c35c298fddbd")
            database.artworkDao.insertAll(artworkList.asDatabaseModel())
        }
    }

    suspend fun updateArtworkRating(artwork: DatabaseArtwork) {
        withContext(Dispatchers.IO) {
            database.artworkDao.insert(artwork)
        }
    }
}
