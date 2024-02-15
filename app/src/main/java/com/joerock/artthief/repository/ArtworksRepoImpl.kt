package com.joerock.artthief.repository

import android.util.Log
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
        private const val ART_THIEF_PASSCODE = "c10561cf-b9ea-46b3-89ab-5c48af2cccf0"
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
        val comparator = Comparator { artwork1: DatabaseArtwork, artwork2: DatabaseArtwork ->
            val artwork1ShowId = try {
                artwork1.showID.toInt()
            } catch (e: Throwable) {
                5000000
            }
            val artwork2ShowId = try {
                artwork2.showID.toInt()
            } catch (e: Throwable) {
                5000000
            }
            return@Comparator artwork1ShowId - artwork2ShowId
        }
        val sortedArtworkList = list.sortedWith(comparator)
        sortedArtworkList.asDomainModel()
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
        try {
            withContext(Dispatchers.IO) {
                val artworkList = getArtworkList()
                updateArtworkDatabase(artworkList)
            }
        } catch (e: Throwable) {
            Log.e("Problem retrieving artworks", e.toString())
        }
    }

    override suspend fun refreshArtworksAndDeleteOldData() {
        try {
            withContext(Dispatchers.IO) {
                val artworkList = getArtworkList()
                deleteOldData(artworkList)
                updateArtworkDatabase(artworkList)
            }
        } catch (e: Throwable) {
            Log.e("Problem retrieving artworks", e.toString())
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
                    ART_THIEF_PASSCODE,
                    artworkList
                )
        }
    }

    override suspend fun updateArtwork(artwork: DatabaseArtwork) {
        withContext(Dispatchers.IO) {
            database.artworkDao.insert(artwork)
        }
    }

    private fun deleteOldData(artworkList: List<NetworkArtwork>) {
        val artworkIdList = artworkList.map { it.artThiefID }
        database
            .artworkDao
            .deleteArtworksById(artworkIdList)
    }

    private suspend fun getArtworkList(): List<NetworkArtwork> {
        return ArtThiefNetwork
            .artThiefArtworks
            .getArtworkList(ART_THIEF_PASSCODE)

    }

    private fun updateArtworkDatabase(artworkList: List<NetworkArtwork>) {
        artworkList.forEach {
            // for each artwork received from network GET request,
            // retrieve artwork entry from database (if it exists),
            val databaseArtworkEntry: DatabaseArtwork = database.artworkDao.getArtworkById(it.artThiefID)
            if (databaseArtworkEntry == null) {
                database
                    .artworkDao
                    .insert(networkArtworkToDatabaseArtwork(it))
            } else {
                // then see if values differ between network and database,
                if (databaseArtworkEntry.showID != it.showID || databaseArtworkEntry.title != it.title ||
                    databaseArtworkEntry.artist != it.artist || databaseArtworkEntry.media != it.media ||
                    databaseArtworkEntry.image_large != it.image_large ||
                    databaseArtworkEntry.image_small != it.image_small ||
                    databaseArtworkEntry.width != it.width || databaseArtworkEntry.height != it.height ||
                    databaseArtworkEntry.taken != it.taken)
                {
                    // if so, update database entry to reflect new data from network response
                    database
                        .artworkDao
                        .insert(
                            updateArtworkInformation(
                                it,
                                databaseArtworkEntry
                            )
                        )
                }
            }
        }
    }
}
