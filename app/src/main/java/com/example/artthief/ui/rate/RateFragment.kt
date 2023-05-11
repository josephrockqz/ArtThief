package com.example.artthief.ui.rate

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.GridView
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

// TODO: fix icons not changing on click (not always reproducible)
class RateFragment : Fragment() {

    private val gridView by lazy {
        requireView().findViewById<GridView>(R.id.gv_rateFragment)
    }
    private val recyclerView by lazy {
        requireView().findViewById<RecyclerView>(R.id.rv_rateFragment)
    }
    // TODO: implement sharedPreferences methods (after moving on click listeners here)
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
            artworks?.apply {
                when (sharedPreferences.getString("rv_display_type", "list")) {
                    "list" -> {
                        configureArtworksList(artworks)
                        binding
                            .root
                            .findViewById<RecyclerView>(R.id.rv_rateFragment)
                            .apply {
                                setHasFixedSize(true)
                                isNestedScrollingEnabled = false
                                layoutManager = LinearLayoutManager(context)
                                adapter = when (sharedPreferences.getString("rv_list_order", "rating")) {
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

        when (sharedPreferences.getString("rv_display_type", "list")) {
            "list" -> {
                recyclerView.visibility = View.VISIBLE
                gridView.visibility = View.GONE
                toolbar.title = resources.getString(R.string.title_rate)
                toolbar.menu[0].icon = resources.getDrawable(R.drawable.ic_list_teal_24dp)
                toolbar.menu[1].isVisible = true
                toolbar.menu[2].isVisible = false
            }
            "grid" -> {
                recyclerView.visibility = View.GONE
                gridView.visibility = View.VISIBLE
                toolbar.title = resources.getString(R.string.title_grid_sort)
                toolbar.menu[0].icon = resources.getDrawable(R.drawable.ic_grid_teal_24dp)
                toolbar.menu[1].isVisible = false
                toolbar.menu[2].isVisible = true
            }
        }

        when (sharedPreferences.getString("rv_list_order", "rating")) {
            "rating" -> toolbar.menu[1].icon = resources.getDrawable(R.drawable.ic_rate_outline_teal_24dp)
            "show_id" -> toolbar.menu[1].icon = resources.getDrawable(R.drawable.ic_123_teal_24dp)
            "artist" -> toolbar.menu[1].icon = resources.getDrawable(R.drawable.ic_artist_teal_24dp)
        }

        Log.i("howdy", "on view created")
        when (sharedPreferences.getBoolean("show_deleted_artwork", false)) {
            false -> {
                toolbar.menu[1].subMenu?.get(3)?.title = resources.getString(R.string.mi_show_deleted_art_title)
                toolbar.menu[2].subMenu?.get(7)?.title = resources.getString(R.string.mi_show_deleted_art_title)
            }
            true -> {
                toolbar.menu[1].subMenu?.get(3)?.title = resources.getString(R.string.mi_hide_deleted_art_title)
                toolbar.menu[2].subMenu?.get(7)?.title = resources.getString(R.string.mi_hide_deleted_art_title)
            }
        }

        when (sharedPreferences.getBoolean("show_taken_artwork", false)) {
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

        super.onViewCreated(view, savedInstanceState)
    }

    private fun configureArtworksList(artworks: List<ArtThiefArtwork>) {
        when (sharedPreferences.getString("rv_list_order", "rating")) {
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

        toolbar.menu[3].setOnMenuItemClickListener { refreshRateFragmentFromIcon() }
    }

    private fun displayList(): Boolean {
        val currentDisplayType = sharedPreferences.getString("rv_display_type", "list")
        if (currentDisplayType != "list") {
            with (sharedPreferences.edit()) {
                putString("rv_display_type", "list")
                apply()
            }
            recyclerView.visibility = View.VISIBLE
            gridView.visibility = View.GONE
            toolbar.menu[0].icon = resources.getDrawable(R.drawable.ic_list_teal_24dp)
            toolbar.menu[1].isVisible = true
            toolbar.menu[2].isVisible = false
            toolbar.title = resources.getString(R.string.title_rate)
            refreshRateFragment()
        }
        return true
    }

    private fun displayGrid(): Boolean {
        val currentDisplayType = sharedPreferences.getString("rv_display_type", "list")
        if (currentDisplayType != "grid") {
            with (sharedPreferences.edit()) {
                putString("rv_display_type", "grid")
                apply()
            }
            recyclerView.visibility = View.GONE
            gridView.visibility = View.VISIBLE
            toolbar.menu[0].icon = resources.getDrawable(R.drawable.ic_grid_teal_24dp)
            toolbar.menu[1].isVisible = false
            toolbar.menu[2].isVisible = true
            toolbar.title = resources.getString(R.string.title_grid_sort)
            refreshRateFragment()
        }
        return true
    }

    private fun listByRatingListener(): Boolean {
        val currentListOrder = sharedPreferences.getString("rv_list_order", "rating")
        if (currentListOrder != "rating") {
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
        val currentListOrder = sharedPreferences.getString("rv_list_order", "rating")
        if (currentListOrder != "show_id") {
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
        val currentListOrder = sharedPreferences.getString("rv_list_order", "rating")
        if (currentListOrder != "artist") {
            with (sharedPreferences.edit()) {
                putString("rv_list_order", "artist")
                apply()
            }

            refreshRateFragment()
        }
        return true
    }

    private fun showDeletedArtwork(): Boolean {
        val showDeletedArtworkState = sharedPreferences.getBoolean("show_deleted_artwork", false)
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
        val showTakenArtworkState = sharedPreferences.getBoolean("show_taken_artwork", false)
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
}
