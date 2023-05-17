package com.example.artthief.ui.rate

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.artthief.R
import com.example.artthief.databinding.FragmentRateBinding
import com.example.artthief.ui.rate.adapter.ArtworkAdapter
import com.example.artthief.ui.rate.adapter.ArtworkGridAdapter
import com.example.artthief.ui.rate.adapter.RatingSectionAdapter
import com.example.artthief.ui.rate.data.ArtworkClickListener
import com.example.artthief.viewmodels.ArtworksViewModel
import com.google.android.material.appbar.MaterialToolbar

// TODO: fix bug where returning to RateFragment from [ArtworkFragment] breaks everything - could be memory leaks due to gettingDrawables
// TODO: fix lag whenever RateFragment is loaded when set to `listByRating`
class RateFragment : Fragment() {

    private var _binding: FragmentRateBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: ArtworksViewModel by activityViewModels()

    private val gridView by lazy {
        binding
            .root
            .findViewById<GridView>(R.id.gv_rateFragment)
    }
    private val recyclerView by lazy {
        binding
            .root
            .findViewById<RecyclerView>(R.id.rv_rateFragment)
    }
    private val sharedPreferences by lazy {
        requireActivity().getPreferences(Context.MODE_PRIVATE)
    }
    private val toolbar by lazy {
        binding
            .root
            .findViewById<MaterialToolbar>(R.id.rateFragmentAppBar)
    }

    private lateinit var artworkAdapter: ArtworkAdapter
    private lateinit var ratingSectionAdapter: RatingSectionAdapter
    private lateinit var artworkGridAdapter: ArtworkGridAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_rate,
            container,
            false
        )
        binding.lifecycleOwner = viewLifecycleOwner

        val displayTypeState = getDisplayTypeState()
        val rvListOrderState = getRvListOrderState()

        if (displayTypeState == "grid" || (rvListOrderState != "show_id" && rvListOrderState != "artist")) {
            Log.i("howdy", "conditional 1")
            viewModel.artworkListByRatingLive.observe(viewLifecycleOwner) { artworks ->
                artworks?.apply {
                    if (displayTypeState == "grid") {
                        Log.i("howdy", "conditional 2")
                        binding
                            .root
                            .findViewById<GridView>(R.id.gv_rateFragment)
                            .apply {
                                artworkGridAdapter = ArtworkGridAdapter(
                                    context = context,
                                    artworks = artworks
                                )
                                adapter = artworkGridAdapter
                            }
                    }
                    viewModel.setSortedArtworkListByRating(artworks)
                }
            }
            if (displayTypeState != "grid" && rvListOrderState == "rating") {
                Log.i("howdy", "conditional 3")
                viewModel.ratingSections.observe(viewLifecycleOwner) { sections ->
                    sections?.apply {
                        binding
                            .root
                            .findViewById<RecyclerView>(R.id.rv_rateFragment)
                            .apply {
                                ratingSectionAdapter = RatingSectionAdapter(
                                    artworkClickListener = object : ArtworkClickListener {
                                        override fun onArtworkClicked(
                                            sectionPosition: Int, view: View
                                        ) { showArtworkFragment(sectionPosition) }
                                    },
                                    context = context,
                                    sections = sections
                                )
                                adapter = ratingSectionAdapter
                            }
                    }
                }
            }
        } else if (rvListOrderState == "show_id") {
            Log.i("howdy", "conditional 4")
            viewModel.artworkListByShowIdLive.observe(viewLifecycleOwner) { artworks ->
                artworks?.apply {
                    binding
                        .root
                        .findViewById<RecyclerView>(R.id.rv_rateFragment)
                        .apply {
                            artworkAdapter = ArtworkAdapter(
                                artworkClickListener = object : ArtworkClickListener {
                                    override fun onArtworkClicked(
                                        sectionPosition: Int, view: View
                                    ) { showArtworkFragment(sectionPosition) }
                                },
                                artworks = artworks
                            )
                            adapter = artworkAdapter
                        }
                    viewModel.setSortedArtworkListByShowId(artworks)
                }
            }
        } else {
            Log.i("howdy", "conditional 5")
            viewModel.artworkListByArtistLive.observe(viewLifecycleOwner) { artworks ->
                artworks?.apply {
                    binding
                        .root
                        .findViewById<RecyclerView>(R.id.rv_rateFragment)
                        .apply {
                            artworkAdapter = ArtworkAdapter(
                                artworkClickListener = object : ArtworkClickListener {
                                    override fun onArtworkClicked(
                                        sectionPosition: Int, view: View
                                    ) { showArtworkFragment(sectionPosition) }
                                },
                                artworks = artworks
                            )
                            adapter = artworkAdapter
                        }
                    viewModel.setSortedArtworkListByArtist(artworks)
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val zoomSlider = binding.root.findViewById<LinearLayout>(R.id.ll_zoomSliderContainer)

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
                if (zoomSliderVisibilityState) {
                    zoomSlider.bringToFront()
                    zoomSlider.visibility = View.VISIBLE
                } else zoomSlider.visibility = View.INVISIBLE
                val zoomLevel = getZoomLevel()
                gridView.numColumns = zoomLevel + 1
                binding.root.findViewById<SeekBar>(R.id.sb_zoomSlider).progress = zoomLevel
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
        setZoomSliderCancelButtonListener()

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i("howdy", "reate fragment on destroy")
        _binding = null
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
        binding
            .root
            .findViewById<SeekBar>(R.id.sb_zoomSlider)
            .setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
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

    private fun setZoomSliderCancelButtonListener() {
        binding
            .root
            .findViewById<ImageView>(R.id.iv_sliderXButton)
            .setOnClickListener {
                binding
                    .root
                    .findViewById<LinearLayout>(R.id.ll_zoomSliderContainer)
                    .visibility = View.INVISIBLE
                with (sharedPreferences.edit()) {
                    putBoolean("show_zoom_slider", false)
                    apply()
                }
            }
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
        if (zoomSliderVisibilityState) {
            binding
                .root
                .findViewById<LinearLayout>(R.id.ll_zoomSliderContainer)
                .visibility = View.INVISIBLE
        } else {
            binding
                .root
                .findViewById<LinearLayout>(R.id.ll_zoomSliderContainer)
                .bringToFront()
            binding
                .root
                .findViewById<LinearLayout>(R.id.ll_zoomSliderContainer)
                .visibility = View.VISIBLE
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
