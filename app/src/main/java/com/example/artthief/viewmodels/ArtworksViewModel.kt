package com.example.artthief.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.artthief.database.DatabaseArtwork
import com.example.artthief.database.getDatabase
import com.example.artthief.domain.ArtThiefArtwork
import com.example.artthief.repository.ArtworksRepo
import com.example.artthief.ui.rate.data.ListByOptions
import com.example.artthief.ui.rate.data.ViewByOptions
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
    private val artworksRepo = ArtworksRepo(getDatabase(application))

    /**
     * A list of artworks displayed on the screen.
     */
    val artworkList = artworksRepo.artworks
    private val _artworkList = MutableLiveData<List<ArtThiefArtwork>>()

    /**
     * Enum value determining how to display view of artwork.
     */
    private var _artworkViewBySelection = ViewByOptions.LIST
    val artworkViewBySelection: ViewByOptions
        get() = _artworkViewBySelection

    /**
     * Enum value determining how to display ordering of artworks.
     */
    private var _artworkListBySelection = ListByOptions.RATING
    val artworkListBySelection: ListByOptions
        get() = _artworkListBySelection

    /**
     * Boolean value determining if to display deleted artworks.
     */
    private var _showDeletedArtworks = false
    val showDeletedArtworks: Boolean
        get() = _showDeletedArtworks

    /**
     * Enum value determining if to display taken artworks.
     */
    private var _showTakenArtworks = false
    val showTakenArtworks: Boolean
        get() = _showTakenArtworks

    /**
     * A list of artworks sorted by their ratings.
     */
    private var _artworkListByRating = emptyList<ArtThiefArtwork>()
    val artworkListByRating: List<ArtThiefArtwork>
        get() = _artworkListByRating

    /**
     * Event triggered for network error.
     */
    private var _eventNetworkError = MutableLiveData(false)
    val eventNetworkError: LiveData<Boolean>
        get() = _eventNetworkError

    /**
     * Flag to display the error message. Views should use this to get access
     * to the data.
     */
    private var _isNetworkErrorShown = MutableLiveData(false)
    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown

    /**
     * Integer to represent current artwork selected to display the
     * appropriate page in view pager.
     */
    var currentArtworkIndex = 0

    /**
     * init{} is called immediately when this ViewModel is created.
     */
    init {}

    /**
     * Resets the network error flag.
     */
    fun onNetworkErrorShown() {
        _isNetworkErrorShown.value = true
    }

    /**
     * Refresh data from the repository. Use a coroutine launch to run in a
     * background thread.
     */
    fun refreshDataFromRepository() {
        viewModelScope.launch {
            try {
                artworksRepo.refreshArtworks()
                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false

            } catch (networkError: IOException) {
                // Show a Toast error message and hide the progress bar.
                if(artworkList.value.isNullOrEmpty())
                    _eventNetworkError.value = true
            }
        }
    }

    fun setListBySelection(selection: ListByOptions) {
        _artworkListBySelection = selection
    }

    fun setListBySelection(selection: ViewByOptions) {
        _artworkViewBySelection = selection
    }

    fun setSortedArtworkList(sortedArtworks: List<ArtThiefArtwork>) {
        _artworkListByRating = sortedArtworks
    }

    fun setDeletedArtworksToggle() {
        _showDeletedArtworks = !_showDeletedArtworks
    }

    fun setTakenArtworksToggle() {
        _showTakenArtworks = !_showTakenArtworks
    }

    fun updateArtworkRating(artwork: DatabaseArtwork) {
        viewModelScope.launch {
            artworksRepo.updateArtworkRating(artwork)
        }
    }

    /**
     * Factory for constructing ArtworksViewModel with parameter
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ArtworksViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ArtworksViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct view model")
        }
    }
}
