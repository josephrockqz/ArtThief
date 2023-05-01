package com.example.artthief.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.artthief.database.DatabaseArtwork
import com.example.artthief.database.getDatabase
import com.example.artthief.domain.ArtThiefArtwork
import com.example.artthief.repository.ArtworksRepo
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
     * A list of artworks displayed on the screen
     */
    // TODO: centralize artwork list sorted by rating
    val artworkList = artworksRepo.artworks

    /**
     * A list of artworks that can be shown on the screen. This is private to avoid exposing a
     * way to set this value to observers.
     */
    private val _artworkList = MutableLiveData<List<ArtThiefArtwork>>()

    /**
     * Event triggered for network error. This is private to avoid exposing a
     * way to set this value to observers.
     */
    private var _eventNetworkError = MutableLiveData(false)

    /**
     * Event triggered for network error. Views should use this to get access
     * to the data.
     */
    val eventNetworkError: LiveData<Boolean>
        get() = _eventNetworkError

    /**
     * Flag to display the error message. This is private to avoid exposing a
     * way to set this value to observers.
     */
    private var _isNetworkErrorShown = MutableLiveData(false)

    /**
     * Flag to display the error message. Views should use this to get access
     * to the data.
     */
    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown

    /**
     * Integer to represent current artwork selected to display the
     * appropriate page in view pager.
     */
    // TODO: get rid of this field if never actually used
    var currentArtworkIndex = 0

    /**
     * init{} is called immediately when this ViewModel is created.
     */
    init {
        refreshDataFromRepository()
    }

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
    private fun refreshDataFromRepository() {
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

    fun getSortedArtworkList(): List<ArtThiefArtwork> {
        return artworkList.value?.sortedByDescending { it.rating }!!
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