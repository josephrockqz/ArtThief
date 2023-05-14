package com.example.artthief.ui.rate

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.GridView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.artthief.R
import com.example.artthief.databinding.FragmentRateBinding
import com.example.artthief.domain.ArtThiefArtwork
import com.example.artthief.ui.rate.adapter.ArtworkAdapter
import com.example.artthief.ui.rate.adapter.ArtworkGridAdapter
import com.example.artthief.ui.rate.adapter.RatingSectionAdapter
import com.example.artthief.ui.rate.data.ArtworkClickListener
import com.example.artthief.ui.rate.data.RecyclerViewSection
import com.example.artthief.viewmodels.ArtworksViewModel
import com.google.android.material.appbar.MaterialToolbar

class RateFragment : Fragment() {

    private val gridView by lazy {
        requireView().findViewById<GridView>(R.id.gv_rateFragment)
    }
    private val recyclerView by lazy {
        requireView().findViewById<RecyclerView>(R.id.rv_rateFragment)
    }
    private val sharedPreferences by lazy {
        requireActivity().getPreferences(Context.MODE_PRIVATE)
    }
    private val toolbar by lazy {
        requireView().findViewById<MaterialToolbar>(R.id.rateFragmentAppBar)
    }

    private val viewModel: ArtworksViewModel by activityViewModels()

    private val artworkRatingSections: MutableList<RecyclerViewSection> = mutableListOf()
    private var artworkListByRating: List<ArtThiefArtwork> = emptyList()
    private var artworkListByShowId: List<ArtThiefArtwork> = emptyList()
    private var artworkListByArtist: List<ArtThiefArtwork> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Set the lifecycleOwner so DataBinding can observe LiveData
        val binding: FragmentRateBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_rate,
            container,
            false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.artworkList.observe(viewLifecycleOwner) { artworks ->
            // TODO: depending on taken/deleted artworks options selected, filter out here
            artworks?.apply {
                when (getDisplayTypeState()) {
                    "list" -> {
                        configureArtworksList(artworks)
                        binding
                            .root
                            .findViewById<RecyclerView>(R.id.rv_rateFragment)
                            .apply {
                                setHasFixedSize(true)
                                isNestedScrollingEnabled = false
                                layoutManager = LinearLayoutManager(context)
                                adapter = when (getRvListOrderState()) {
                                    "show_id" -> ArtworkAdapter(
                                        artworkClickListener = object : ArtworkClickListener {
                                            override fun onArtworkClicked(
                                                sectionPosition: Int, view: View
                                            ) { showArtworkFragment(sectionPosition) }
                                        },
                                        artworks = artworkListByShowId
                                    )
                                    "artist" -> ArtworkAdapter(
                                        artworkClickListener = object : ArtworkClickListener {
                                            override fun onArtworkClicked(
                                                sectionPosition: Int, view: View
                                            ) { showArtworkFragment(sectionPosition) }
                                        },
                                        artworks = artworkListByArtist
                                    )
                                    else -> RatingSectionAdapter(
                                        context = context,
                                        artworkClickListener = object : ArtworkClickListener {
                                            override fun onArtworkClicked(
                                                sectionPosition: Int, view: View
                                            ) { showArtworkFragment(sectionPosition) }
                                        },
                                        sections = artworkRatingSections
                                    )
                                }
                            }
                    }
                    "grid" -> {
                        configureArtworksGrid(artworks)
                        binding
                            .root
                            .findViewById<GridView>(R.id.gv_rateFragment)
                            .apply {
                                adapter = ArtworkGridAdapter(
                                    artworks = artworkListByRating,
                                    context = context
                                )
                            }
                    }
                }
            }
        }

        // Observer for the network error.
        viewModel
            .eventNetworkError
            .observe(viewLifecycleOwner, Observer<Boolean> { isNetworkError ->
                if (isNetworkError) onNetworkError()
            })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val zoomSlider = requireView().findViewById<LinearLayout>(R.id.ll_zoomSliderContainer)

        when (getDisplayTypeState()) {
            "list" -> {
                recyclerView.visibility = View.VISIBLE
                gridView.visibility = View.GONE

                toolbar.title = resources.getString(R.string.title_rate)
                toolbar.menu[0].icon = resources.getDrawable(R.drawable.ic_list_teal_24dp)
                toolbar.menu[1].isVisible = true
                toolbar.menu[2].isVisible = false
                toolbar.menu[3].isVisible = false
                toolbar.menu[5].isVisible = true

                zoomSlider.visibility = View.INVISIBLE
            }
            "grid" -> {
                recyclerView.visibility = View.GONE
                gridView.visibility = View.VISIBLE

                toolbar.title = resources.getString(R.string.title_grid_sort)
                toolbar.menu[0].icon = resources.getDrawable(R.drawable.ic_grid_teal_24dp)
                toolbar.menu[1].isVisible = false
                toolbar.menu[2].isVisible = true
                toolbar.menu[3].isVisible = true
                toolbar.menu[5].isVisible = false

                val zoomSliderVisibilityState = getZoomSliderVisibility()
                Log.i("onViewCreated", zoomSliderVisibilityState.toString())
                if (zoomSliderVisibilityState) {
                    zoomSlider.bringToFront()
                    zoomSlider.visibility = View.VISIBLE
                } else zoomSlider.visibility = View.INVISIBLE
                val zoomLevel = getZoomLevel()
                gridView.numColumns = zoomLevel + 1
                requireView().findViewById<SeekBar>(R.id.sb_zoomSlider).progress = zoomLevel
            }
        }

        when (getRvListOrderState()) {
            "rating" -> toolbar.menu[1].icon = resources.getDrawable(R.drawable.ic_rate_outline_teal_24dp)
            "show_id" -> toolbar.menu[1].icon = resources.getDrawable(R.drawable.ic_123_teal_24dp)
            "artist" -> toolbar.menu[1].icon = resources.getDrawable(R.drawable.ic_artist_teal_24dp)
        }

        when (getShowDeletedArtworkState()) {
            false -> {
                toolbar.menu[1].subMenu?.get(3)?.title = resources.getString(R.string.mi_show_deleted_art_title)
                toolbar.menu[2].subMenu?.get(7)?.title = resources.getString(R.string.mi_show_deleted_art_title)
            }
            true -> {
                toolbar.menu[1].subMenu?.get(3)?.title = resources.getString(R.string.mi_hide_deleted_art_title)
                toolbar.menu[2].subMenu?.get(7)?.title = resources.getString(R.string.mi_hide_deleted_art_title)
            }
        }

        when (getShowTakenArtworkState()) {
            false -> {
                toolbar.menu[1].subMenu?.get(4)?.title = resources.getString(R.string.mi_show_taken_art_title)
                toolbar.menu[2].subMenu?.get(8)?.title = resources.getString(R.string.mi_show_taken_art_title)
            }
            true -> {
                toolbar.menu[1].subMenu?.get(4)?.title = resources.getString(R.string.mi_hide_taken_art_title)
                toolbar.menu[2].subMenu?.get(8)?.title = resources.getString(R.string.mi_hide_taken_art_title)
            }
        }

        setMenuItemOnClickListeners()
        setZoomSliderChangeListener()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun configureArtworksList(artworks: List<ArtThiefArtwork>) {
        when (getRvListOrderState()) {
            "rating" -> configureArtworksByRating(artworks)
            "show_id" -> configureArtworksByShowId(artworks)
            "artist" -> configureArtworksByArtist(artworks)
        }
    }

    private fun configureArtworksGrid(artworks: List<ArtThiefArtwork>) {
        artworkListByRating = artworks.sortedByDescending { it.rating }
        viewModel.setSortedArtworkListByRating(artworkListByRating)
    }

    // TODO: fix lag on rate tab (slow every time it loads)
    private fun configureArtworksByRating(artworks: List<ArtThiefArtwork>) {

        // sort artworks by descending rating and update view model
        artworkListByRating = artworks.sortedByDescending { it.rating }
        viewModel.setSortedArtworkListByRating(artworkListByRating)

        // partition artworks by rating then assign to rv's sections
        val artworkRatingMap = artworkListByRating.groupBy { it.rating }
        for (i in 5 downTo 0) {
            artworkRatingMap[i]?.let {
                artworkRatingSections.add(RecyclerViewSection(i, it))
            }
        }
    }

    private fun configureArtworksByShowId(artworks: List<ArtThiefArtwork>) {
        artworkListByShowId = artworks.sortedBy { it.showID.toInt() }
        viewModel.setSortedArtworkListByShowId(artworkListByShowId)
    }

    private fun configureArtworksByArtist(artworks: List<ArtThiefArtwork>) {
        artworkListByArtist = artworks.sortedBy { it.artist }
        viewModel.setSortedArtworkListByArtist(artworkListByArtist)
    }

    // TODO: get rid of onNetworkError functions? - check to see if ever reached
    private fun onNetworkError() {
        if (!viewModel.isNetworkErrorShown.value!!) {
            Toast
                .makeText(activity, "Network Error", Toast.LENGTH_LONG)
                .show()
            viewModel.onNetworkErrorShown()
        }
    }

    fun showArtworkFragment(position: Int) {
        viewModel.currentArtworkIndex = position
        activity
            ?.findNavController(R.id.nav_host_fragment_activity_main)
            ?.navigate(R.id.action_rateToArtwork)
    }

    private fun setMenuItemOnClickListeners() {

        toolbar.menu[0].subMenu?.get(0)?.setOnMenuItemClickListener { displayList() }
        toolbar.menu[0].subMenu?.get(1)?.setOnMenuItemClickListener { displayGrid() }

        toolbar.menu[1].subMenu?.get(0)?.setOnMenuItemClickListener { listByRatingListener() }
        toolbar.menu[1].subMenu?.get(1)?.setOnMenuItemClickListener { listByShowIdListener() }
        toolbar.menu[1].subMenu?.get(2)?.setOnMenuItemClickListener { listByArtistListener() }
        toolbar.menu[1].subMenu?.get(3)?.setOnMenuItemClickListener { showDeletedArtwork() }
        toolbar.menu[1].subMenu?.get(4)?.setOnMenuItemClickListener { showTakenArtwork() }

        toolbar.menu[2].subMenu?.get(7)?.setOnMenuItemClickListener { showDeletedArtwork() }
        toolbar.menu[2].subMenu?.get(8)?.setOnMenuItemClickListener { showTakenArtwork() }

        toolbar.menu[3].setOnMenuItemClickListener { toggleGridZoomSlider() }

        toolbar.menu[4].setOnMenuItemClickListener { refreshRateFragmentFromIcon() }
    }

    private fun setZoomSliderChangeListener() {
        requireView().findViewById<SeekBar>(R.id.sb_zoomSlider).setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                gridView.numColumns = progress + 1
                with (sharedPreferences.edit()) {
                    putInt("zoom_level", progress)
                    apply()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // No-Op
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // No-Op
            }
        })
    }

    private fun displayList(): Boolean {
        if (getDisplayTypeState() != "list") {
            with (sharedPreferences.edit()) {
                putString("display_type", "list")
                apply()
            }
            recyclerView.visibility = View.VISIBLE
            gridView.visibility = View.GONE
            toolbar.menu[0].icon = resources.getDrawable(R.drawable.ic_list_teal_24dp)
            toolbar.menu[1].isVisible = true
            toolbar.menu[2].isVisible = false
            toolbar.menu[3].isVisible = false
            toolbar.menu[5].isVisible = true
            toolbar.title = resources.getString(R.string.title_rate)
            refreshRateFragment()
        }
        return true
    }

    private fun displayGrid(): Boolean {
        if (getDisplayTypeState() != "grid") {
            with (sharedPreferences.edit()) {
                putString("display_type", "grid")
                apply()
            }
            recyclerView.visibility = View.GONE
            gridView.visibility = View.VISIBLE
            toolbar.menu[0].icon = resources.getDrawable(R.drawable.ic_grid_teal_24dp)
            toolbar.menu[1].isVisible = false
            toolbar.menu[2].isVisible = true
            toolbar.menu[3].isVisible = true
            toolbar.menu[5].isVisible = false
            toolbar.title = resources.getString(R.string.title_grid_sort)
            refreshRateFragment()
        }
        return true
    }

    private fun listByRatingListener(): Boolean {
        if (getRvListOrderState() != "rating") {
            with (sharedPreferences.edit()) {
                putString("rv_list_order", "rating")
                apply()
            }
            toolbar.menu[1].icon = resources.getDrawable(R.drawable.ic_rate_outline_teal_24dp)
            refreshRateFragment()
        }
        return true
    }

    private fun listByShowIdListener(): Boolean {
        if (getRvListOrderState() != "show_id") {
            with (sharedPreferences.edit()) {
                putString("rv_list_order", "show_id")
                apply()
            }
            toolbar.menu[1].icon = resources.getDrawable(R.drawable.ic_123_teal_24dp)
            refreshRateFragment()
        }
        return true
    }

    private fun listByArtistListener(): Boolean {
        if (getRvListOrderState() != "artist") {
            with (sharedPreferences.edit()) {
                putString("rv_list_order", "artist")
                apply()
            }
            toolbar.menu[1].icon = resources.getDrawable(R.drawable.ic_artist_teal_24dp)
            refreshRateFragment()
        }
        return true
    }

    private fun showDeletedArtwork(): Boolean {
        val showDeletedArtworkState = getShowDeletedArtworkState()
        if (showDeletedArtworkState) {
            toolbar.menu[1].subMenu?.get(3)?.title = resources.getString(R.string.mi_show_deleted_art_title)
            toolbar.menu[2].subMenu?.get(7)?.title = resources.getString(R.string.mi_show_deleted_art_title)
        } else {
            toolbar.menu[1].subMenu?.get(3)?.title = resources.getString(R.string.mi_hide_deleted_art_title)
            toolbar.menu[2].subMenu?.get(7)?.title = resources.getString(R.string.mi_hide_deleted_art_title)
        }
        with (sharedPreferences.edit()) {
            putBoolean("show_deleted_artwork", !showDeletedArtworkState)
            apply()
        }
        refreshRateFragment()
        return true
    }

    private fun showTakenArtwork(): Boolean {
        val showTakenArtworkState = getShowTakenArtworkState()
        if (showTakenArtworkState) {
            toolbar.menu[1].subMenu?.get(4)?.title = resources.getString(R.string.mi_show_taken_art_title)
            toolbar.menu[2].subMenu?.get(8)?.title = resources.getString(R.string.mi_show_taken_art_title)
        } else {
            toolbar.menu[1].subMenu?.get(4)?.title = resources.getString(R.string.mi_hide_taken_art_title)
            toolbar.menu[2].subMenu?.get(8)?.title = resources.getString(R.string.mi_hide_taken_art_title)
        }
        with (sharedPreferences.edit()) {
            putBoolean("show_taken_artwork", !showTakenArtworkState)
            apply()
        }
        refreshRateFragment()
        return true
    }

    // TODO: fix bug where toggle doesn't work temporarily
    private fun toggleGridZoomSlider(): Boolean {
        val zoomSliderVisibilityState = getZoomSliderVisibility()
        Log.i("toggleGridZoomSlider", zoomSliderVisibilityState.toString())
        if (zoomSliderVisibilityState) {
            requireView().findViewById<LinearLayout>(R.id.ll_zoomSliderContainer).visibility = View.INVISIBLE
        } else {
            requireView().findViewById<LinearLayout>(R.id.ll_zoomSliderContainer).bringToFront()
            requireView().findViewById<LinearLayout>(R.id.ll_zoomSliderContainer).visibility = View.VISIBLE
        }
        with (sharedPreferences.edit()) {
            putBoolean("show_zoom_slider", !zoomSliderVisibilityState)
            apply()
        }
        return true
    }

    private fun refreshRateFragmentFromIcon(): Boolean {
        refreshRateFragment()
        return true
    }

    private fun refreshRateFragment() {
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.rl_rateFragment, RateFragment())
            ?.commit()
    }

    // TODO: implement search bar functionality

    private fun getDisplayTypeState(): String {
        return sharedPreferences.getString("display_type", "list")!!
    }

    private fun getRvListOrderState(): String {
        return sharedPreferences.getString("rv_list_order", "rating")!!
    }

    private fun getShowDeletedArtworkState(): Boolean {
        return sharedPreferences.getBoolean("show_deleted_artwork", false)
    }

    private fun getShowTakenArtworkState(): Boolean {
        return sharedPreferences.getBoolean("show_taken_artwork", false)
    }

    private fun getZoomLevel(): Int {
        return sharedPreferences.getInt("zoom_level", 2)
    }

    private fun getZoomSliderVisibility(): Boolean {
        return sharedPreferences.getBoolean("show_zoom_slider", false)
    }
}
