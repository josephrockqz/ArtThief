package com.example.artthief.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.artthief.database.ArtworksDatabase
import com.example.artthief.database.DatabaseArtwork
import com.example.artthief.database.asDomainModel
import com.example.artthief.domain.ArtThiefArtwork
import com.example.artthief.domain.defaultArtThiefArtwork
import com.example.artthief.network.ArtThiefNetwork
import com.example.artthief.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for fetching Art Thief artwork from the network and storing them on disk
 */
class ArtworksRepoImpl(private val database: ArtworksDatabase) : ArtworksRepo {

    override val artworks: LiveData<List<ArtThiefArtwork>> = Transformations.map(
        database.artworkDao.getArtworks()
    ) { list ->
        list.asDomainModel()
    }

    override val artworksByRating: LiveData<List<ArtThiefArtwork>> = Transformations.map(
        database.artworkDao.getArtworks()
    ) { list ->
        list.asDomainModel().sortedWith(
            compareByDescending<ArtThiefArtwork> { it.rating }
                .thenBy { it.order }
        )
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
        list.asDomainModel().sortedWith(
            compareBy<ArtThiefArtwork> { it.artist }
                .thenBy { it.title }
        )
    }

    override val highestRatedArtwork: LiveData<ArtThiefArtwork> = Transformations.map(
        database.artworkDao.getArtworks()
    ) { list ->
        val filterTakenAndDeletedArtworks = list.filter {
            !it.taken && !it.deleted
        }
        val listByRating = filterTakenAndDeletedArtworks.asDomainModel().sortedByDescending { artwork ->
            artwork.rating
        }
        if (listByRating.isNotEmpty()) listByRating[0]
        else defaultArtThiefArtwork
    }

    override fun getArtworksByRating(rating: Int): LiveData<List<ArtThiefArtwork>> = Transformations.map(
        database.artworkDao.getArtworks()
    ) { list ->
        val filterArtworksWithRating = list.filter {
            it.rating == rating
        }
        filterArtworksWithRating.asDomainModel().sortedBy { artwork ->
            artwork.order
        }
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
