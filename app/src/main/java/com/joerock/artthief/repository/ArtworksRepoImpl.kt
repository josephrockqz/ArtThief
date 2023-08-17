package com.joerock.artthief.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.joerock.artthief.database.ArtworksDatabase
import com.joerock.artthief.database.DatabaseArtwork
import com.joerock.artthief.database.asDomainModel
import com.joerock.artthief.domain.ArtThiefArtwork
import com.joerock.artthief.domain.defaultArtThiefArtwork
import com.joerock.artthief.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

/**
 * Repository for fetching Art Thief artwork from the network and storing them on disk
 */
class ArtworksRepoImpl(private val database: ArtworksDatabase) : ArtworksRepo {

    companion object {
        private const val PASSCODE_GET_REQUEST = "fb56a1e6-ee06-4911-ad33-c35c298fddbd"
        private const val PASSCODE_POST_REQUEST = "c10561cf-b9ea-46b3-89ab-5c48af2cccf0"
    }

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
        val listByRating = filterTakenAndDeletedArtworks.asDomainModel().sortedWith(
            compareByDescending<ArtThiefArtwork> { it.rating }
                .thenBy { it.order }
        )
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
            val artworkList = ArtThiefNetwork
                .artThiefArtworks
                .getArtworkList(PASSCODE_GET_REQUEST)

            artworkList.forEach {
                // for each artwork received from network GET request,
                // retrieve artwork entry from database (if it exists),
                val databaseArtworkEntry: DatabaseArtwork = database.artworkDao.getArtworkById(it.artThiefID)
                if (databaseArtworkEntry == null) {
                    database
                        .artworkDao
                        .insert(networkArtworkToDatabaseArtwork(it))
                } else {
                    // then see if the `taken` status differs between network and database,
                    if (databaseArtworkEntry.taken != it.taken) {
                        // if so, update database entry to reflect new result from network request
                        database
                            .artworkDao
                            .insert(
                                updateArtworkTakenStatus(
                                    it.taken,
                                    databaseArtworkEntry
                                )
                            )
                    }
                }
            }
        }
    }

    override suspend fun sendArtworkList(
        codeName: String,
        artworkList: NetworkArtworkPreferenceList
    ): Response<NetworkArtworkListPostResponse> {
        return withContext(Dispatchers.IO) {
            ArtThiefNetwork
                .artThiefArtworks
                .postArtworkList(
                    codeName,
                    PASSCODE_POST_REQUEST,
                    artworkList
                )
        }
    }

    override suspend fun updateArtwork(artwork: DatabaseArtwork) {
        withContext(Dispatchers.IO) {
            database.artworkDao.insert(artwork)
        }
    }
}