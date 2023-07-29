package com.example.artthief.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.artthief.database.DatabaseArtwork
import com.example.artthief.database.getDatabase
import com.example.artthief.domain.ArtThiefArtwork
import com.example.artthief.domain.asDatabaseModel
import com.example.artthief.domain.defaultArtThiefArtwork
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

    private val artworksRepo = ArtworksRepoImpl(getDatabase(application))

    val artworksLive = artworksRepo.artworks

    val artworkListByRatingLive = artworksRepo.artworksByRating
    val artworkListByShowIdLive = artworksRepo.artworksByShowId
    val artworkListByArtistLive = artworksRepo.artworksByArtist

    val highestRatedArtworkUrl = artworksRepo.highestRatedArtwork

    /**
     * Integer to represent current artwork selected to display the
     * appropriate page in view pager.
     */
    var currentArtworkIndex = 0

    private var _artworkList = emptyList<ArtThiefArtwork>()

    private var _artworkListByRating = emptyList<ArtThiefArtwork>()
    val artworkListByRating: List<ArtThiefArtwork>
        get() = _artworkListByRating

    private var _artworkListByShowId = emptyList<ArtThiefArtwork>()
    val artworkListByShowId: List<ArtThiefArtwork>
        get() = _artworkListByShowId

    private var _artworkListByArtist = emptyList<ArtThiefArtwork>()
    val artworkListByArtist: List<ArtThiefArtwork>
        get() = _artworkListByArtist

    val artworkSectionCompareOrdering = mutableListOf<ArtThiefArtwork>()
    val artworkSectionCompareMapping = mutableMapOf<Int, MutableList<Int>>()
    var artworkSectionCompletedComparisonsCounter = 0
    var artworkSectionCompareTotalNumComparisonsForCompletion = 0

    private lateinit var _artworkSelectedGrid: ArtThiefArtwork
    val artworkSelectedGrid: ArtThiefArtwork
        get() = _artworkSelectedGrid

    fun getArtworksByRating(rating: Int): LiveData<List<ArtThiefArtwork>> {
        return artworksRepo.getArtworksByRating(rating)
    }

    fun refreshDataFromRepository() {
        viewModelScope.launch {
            try {
                artworksRepo.refreshArtworks()
            } catch (_: IOException) { }
        }
    }

    fun setArtworkList(artworks: List<ArtThiefArtwork>) {
        _artworkList = artworks
    }

    fun setSortedArtworkListByRating(sortedArtworks: List<ArtThiefArtwork>) {
        _artworkListByRating = sortedArtworks
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

    fun updateArtworkRatings(
        artwork: ArtThiefArtwork,
        newRating: Int,
        oldRating: Int
    ) {
        val newRatingSectionArtworks: MutableList<ArtThiefArtwork> = mutableListOf()
        val oldRatingSectionArtworks: MutableList<ArtThiefArtwork> = mutableListOf()
        _artworkList.forEach {
            if (it.rating == newRating) {
                newRatingSectionArtworks += it.copy()
            } else if (it.rating == oldRating) {
                oldRatingSectionArtworks += it.copy()
            }
            newRatingSectionArtworks.sortedBy { newSectionArtwork ->
                newSectionArtwork.order
            }
            oldRatingSectionArtworks.sortedBy { oldSectionArtwork ->
                oldSectionArtwork.order
            }
        }

        if (oldRating == 0) {
            updateArtwork(
                artwork
                    .copy(rating = newRating, order = newRatingSectionArtworks.size)
                    .asDatabaseModel()
            )
        }
        // If new rating is higher than old rating, place artwork at end of order
        else if (newRating > oldRating) {
            oldRatingSectionArtworks.forEach {
                if (it.order > artwork.order) {
                    updateArtwork(
                        it
                            .copy(order = it.order - 1)
                            .asDatabaseModel()
                    )
                }
            }
            updateArtwork(
                artwork
                    .copy(rating = newRating, order = newRatingSectionArtworks.size)
                    .asDatabaseModel()
            )
        }
        // If new rating is lower than old rating, place artwork at beginning of order
        else {
            oldRatingSectionArtworks.forEach {
                if (it.order > artwork.order) {
                    updateArtwork(
                        it
                            .copy(order = it.order - 1)
                            .asDatabaseModel()
                    )
                }
            }
            newRatingSectionArtworks.forEach {
                updateArtwork(
                    it
                        .copy(order = it.order + 1)
                        .asDatabaseModel()
                )
            }
            updateArtwork(
                artwork
                    .copy(rating = newRating, order = 0)
                    .asDatabaseModel()
            )
        }
    }

    // TODO: D&D - dynamically update artwork rating based on dragging and dropping
    // TODO: D&D - make sure to accommodate for artwork ordering logic when changing rating
    fun updateArtworkRatingsDragAndDrop(
        artworksListGridView: List<ArtThiefArtwork>,
        dragFrom: Int,
        dragTo: Int,
    ) {
        Log.i("drag from", dragFrom.toString())
        Log.i("drag to", dragTo.toString())
        Log.i("selected artwork", _artworkSelectedGrid.toString())
        Log.i("grid artworks", artworksListGridView.toString())

        val selectedArtworkNewRating = if (dragTo == 0) {
            artworksListGridView[1].rating
        } else if (dragTo == artworksListGridView.size - 1) {
            artworksListGridView[artworksListGridView.size - 2].rating
        } else {
            if (dragFrom > dragTo) {
                artworksListGridView[dragTo + 1].rating
            } else {
                artworksListGridView[dragTo - 1].rating
            }
        }

        val selectedArtworkOldRating = _artworkSelectedGrid.rating
        if (selectedArtworkOldRating == selectedArtworkNewRating) {
            val artworksInSection = mutableListOf<ArtThiefArtwork>()
            for (i in artworksListGridView.indices) {
                if (artworksListGridView[i].rating == selectedArtworkOldRating) {
                    artworksInSection.add(artworksListGridView[i].copy())
                }
            }
            for (i in artworksInSection.indices) {
                updateArtwork(
                    artworksInSection[i]
                        .copy(order = i)
                        .asDatabaseModel()
                )
            }
        } else {
            // TODO: update orders of artworks in old rating section
            val selectedArtworkOldOrder = _artworkSelectedGrid.order
            
            // TODO: update moving artwork rating and order
            // TODO: update orders of artworks in new rating section
        }
    }

    fun updateSelectedGridArtwork(artwork: ArtThiefArtwork) {
        _artworkSelectedGrid = artwork.copy()
    }
}
