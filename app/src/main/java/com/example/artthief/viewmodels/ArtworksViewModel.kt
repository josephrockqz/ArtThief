package com.example.artthief.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.artthief.database.DatabaseArtwork
import com.example.artthief.database.getDatabase
import com.example.artthief.domain.ArtThiefArtwork
import com.example.artthief.repository.ArtworksRepoImpl
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * ArtworksViewModel designed to store and manage UI-related data in a lifecycle conscious way. This
 * allows data to survive configuration changes such as screen rotations. In addition, background
 * work such as fetching network results can continue through configuration changes and deliver
 * results after the new Fragment or Activity is available
 *
 * @param application The application that this view model is attached to, it's safe to hold a
 * reference to applications across rotation since Application is never recreated during actiivty
 * or fragment lifecycle events
 */
class ArtworksViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * The data source this ViewModel will fetch results from
     */
    private val artworksRepo = ArtworksRepoImpl(getDatabase(application))

    /**
     * Lists of artwork with different ways of sorting
     */
    val artworkListByRatingLive = artworksRepo.artworksByRating
    val artworkListByShowIdLive = artworksRepo.artworksByShowId
    val artworkListByArtistLive = artworksRepo.artworksByArtist

    val highestRatedArtworkUrl = artworksRepo.highestRatedArtwork

    /**
     * Integer to represent current artwork selected to display the
     * appropriate page in view pager.
     */
    var currentArtworkIndex = 0

    /**
     * A list of artworks sorted by their ratings.
     */
    private var _artworkListByRating = emptyList<ArtThiefArtwork>()
    val artworkListByRating: List<ArtThiefArtwork>
        get() = _artworkListByRating

    /**
     * A list of artworks sorted by their ratings filter by grid specs
     */
    private var _artworkListByRatingGrid = emptyList<ArtThiefArtwork>()
    val artworkListByRatingGrid: List<ArtThiefArtwork>
        get() = _artworkListByRatingGrid

    /**
     * A list of artworks sorted by their show IDs.
     */
    private var _artworkListByShowId = emptyList<ArtThiefArtwork>()
    val artworkListByShowId: List<ArtThiefArtwork>
        get() = _artworkListByShowId

    /**
     * A list of artworks sorted by their show IDs.
     */
    private var _artworkListByArtist = emptyList<ArtThiefArtwork>()
    val artworkListByArtist: List<ArtThiefArtwork>
        get() = _artworkListByArtist

    fun getArtworksByRating(rating: Int): LiveData<List<ArtThiefArtwork>> {
        return artworksRepo.getArtworksByRating(rating)
    }

    /**
     * Refresh data from the repository. Use a coroutine launch to run in a
     * background thread.
     */
    fun refreshDataFromRepository() {
        viewModelScope.launch {
            try {
                artworksRepo.refreshArtworks()

            } catch (_: IOException) { }
        }
    }

    fun setSortedArtworkListByRating(sortedArtworks: List<ArtThiefArtwork>) {
        _artworkListByRating = sortedArtworks
    }

    fun setSortedArtworkListByRatingGrid(sortedArtworks: List<ArtThiefArtwork>) {
        _artworkListByRatingGrid = sortedArtworks
    }

    fun setSortedArtworkListByShowId(sortedArtworks: List<ArtThiefArtwork>) {
        _artworkListByShowId = sortedArtworks
    }

    fun setSortedArtworkListByArtist(sortedArtworks: List<ArtThiefArtwork>) {
        _artworkListByArtist = sortedArtworks
    }

    fun updateArtwork(artwork: DatabaseArtwork) {
        viewModelScope.launch {
            artworksRepo.updateArtwork(artwork)
        }
    }
}
