package com.example.artthief.ui.rate

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.artthief.R
import com.example.artthief.databinding.FragmentRateBinding
import com.example.artthief.domain.ArtThiefArtwork
import com.example.artthief.domain.asDatabaseModel
import com.example.artthief.ui.rate.adapter.ArtworkAdapter
import com.example.artthief.ui.rate.adapter.ArtworkGridAdapter
import com.example.artthief.ui.rate.adapter.RatingSectionAdapter
import com.example.artthief.ui.rate.data.ArtworkClickListener
import com.example.artthief.viewmodels.ArtworksViewModel
import kotlin.math.roundToInt

// TODO: fix lag whenever RateFragment is loaded when set to `listByRating`
class RateFragment : Fragment() {

    private var _binding: FragmentRateBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: ArtworksViewModel by activityViewModels()

    private val gridView by lazy { binding.gvRateFragment }
    private val recyclerView by lazy { binding.rvRateFragment }
    private val toolbar by lazy { binding.rateFragmentAppBar }
    private val zoomSliderCancelButton by lazy { binding.ivSliderXButton }
    private val zoomSliderContainer by lazy { binding.llZoomSliderContainer }
    private val zoomSliderSeekBar by lazy { binding.sbZoomSlider }
    private val sharedPreferences by lazy {
        requireActivity().getPreferences(Context.MODE_PRIVATE)
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
            viewModel.artworkListByRatingLive.observe(viewLifecycleOwner) { artworks ->
                artworks?.apply {
                    val artworksFilterTakenAndDeleted = filterTakenAndDeletedArtworks(artworks)
                    if (displayTypeState == "grid") {
                        gridView.apply {
                            artworkGridAdapter = ArtworkGridAdapter(
                                artworks = artworksFilterTakenAndDeleted,
                                artworkImageSize = calculateGridViewImageSize(numColumns)
                            )
                            adapter = artworkGridAdapter
                        }
                    }
                    viewModel.setSortedArtworkListByRating(artworksFilterTakenAndDeleted)
                }
            }
            if (displayTypeState != "grid" && rvListOrderState == "rating") {
                // TODO: adjust so that section rating adapter filters taken and deleted artworks
                viewModel.ratingSections.observe(viewLifecycleOwner) { sections ->
                    sections?.apply {
                        recyclerView.apply {
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
            viewModel.artworkListByShowIdLive.observe(viewLifecycleOwner) { artworks ->
                artworks?.apply {
                    val artworksFilterTakenAndDeleted = filterTakenAndDeletedArtworks(artworks)
                    recyclerView.apply {
                        artworkAdapter = ArtworkAdapter(
                            artworkClickListener = object : ArtworkClickListener {
                                override fun onArtworkClicked(
                                    sectionPosition: Int, view: View
                                ) { showArtworkFragment(sectionPosition) }
                            },
                            artworks = artworksFilterTakenAndDeleted,
                            context = context
                        )
                        adapter = artworkAdapter
                    }
                    viewModel.setSortedArtworkListByShowId(artworksFilterTakenAndDeleted)
                }
            }
        } else {
            viewModel.artworkListByArtistLive.observe(viewLifecycleOwner) { artworks ->
                artworks?.apply {
                    val artworksFilterTakenAndDeleted = filterTakenAndDeletedArtworks(artworks)
                    recyclerView.apply {
                        artworkAdapter = ArtworkAdapter(
                            artworkClickListener = object : ArtworkClickListener {
                                override fun onArtworkClicked(
                                    sectionPosition: Int, view: View
                                ) { showArtworkFragment(sectionPosition) }
                            },
                            artworks = artworksFilterTakenAndDeleted,
                            context = context
                        )
                        adapter = artworkAdapter
                    }
                    viewModel.setSortedArtworkListByArtist(artworksFilterTakenAndDeleted)
                }
            }
        }

        val swipeHelper = ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = true

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                onArtworkSwiped(viewHolder, direction)
            }

            override fun onChildDraw(
                canvas: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                onArtworkChildDraw(canvas, viewHolder, dX)

                super.onChildDraw(
                    canvas,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        })
        swipeHelper.attachToRecyclerView(recyclerView)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

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

                zoomSliderContainer.visibility = View.INVISIBLE
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

                zoomSliderContainer.visibility = View.VISIBLE
                val zoomLevel = getZoomLevel()
                gridView.numColumns = zoomLevel + 1
                zoomSliderSeekBar.progress = zoomLevel
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
        zoomSliderSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                val updatedNumColumns = progress + 1
                viewModel.artworkListByRatingLive.observe(viewLifecycleOwner) { artworks ->
                    artworks?.apply {
                        gridView.apply {
                            artworkGridAdapter = ArtworkGridAdapter(
                                artworks = artworks,
                                artworkImageSize = calculateGridViewImageSize(updatedNumColumns)
                            )
                            adapter = artworkGridAdapter
                            numColumns = updatedNumColumns
                        }
                    }
                }
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
        zoomSliderCancelButton.setOnClickListener {
            zoomSliderContainer.visibility = View.INVISIBLE
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
            zoomSliderContainer.visibility = View.INVISIBLE
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
        val isZoomSliderVisible = getZoomSliderVisibility()
        if (isZoomSliderVisible) {
            zoomSliderContainer.visibility = View.INVISIBLE
        } else {
            zoomSliderContainer.bringToFront()
            zoomSliderContainer.visibility = View.VISIBLE
        }
        with (sharedPreferences.edit()) {
            putBoolean("show_zoom_slider", !isZoomSliderVisible)
            apply()
        }
        return true
    }

    private fun refreshRateFragmentFromIcon(): Boolean {
        refreshRateFragment()
        return true
    }

    private fun refreshRateFragment() {
        viewModel.refreshDataFromRepository()
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.rl_rateFragment, RateFragment())
            ?.commit()
    }

    private fun calculateGridViewImageSize(numColumns: Int): Int {
        val screenWidth = resources.displayMetrics.widthPixels
        val artworkImageSideLength = screenWidth / numColumns
        return kotlin.math.floor(artworkImageSideLength.toDouble()).toInt()
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

    private fun onArtworkSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val pos = viewHolder.adapterPosition
        // TODO: only notify removed when necessary - e.g. artwork is deleted and deleted artwork are hidden
        artworkAdapter.notifyItemRemoved(pos)
        if (direction == ItemTouchHelper.LEFT) {
            updateArtworkDeletedDatabase(true, pos)
        } else if (direction == ItemTouchHelper.RIGHT) {
            updateArtworkDeletedDatabase(false, pos)
        }
    }

    private fun onArtworkChildDraw(
        canvas: Canvas,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float
    ) {
        val width = resources.displayMetrics.widthPixels

        when {
            kotlin.math.abs(dX) < width / 3 -> canvas.drawColor(Color.GRAY)
            dX > width / 3 -> canvas.drawColor(Color.GREEN)
            else -> canvas.drawColor(Color.RED)
        }

        val textMargin = resources
            .getDimension(R.dimen.text_margin)
            .roundToInt()

        val deleteIcon = resources.getDrawable(R.drawable.ic_deleted_24dp)
        deleteIcon.bounds = Rect(
            width - textMargin - deleteIcon.intrinsicWidth,
            viewHolder.itemView.top + textMargin + 8,
            width - textMargin,
            viewHolder.itemView.top + deleteIcon.intrinsicHeight + textMargin + 8
        )

        val archiveIcon = resources.getDrawable(R.drawable.ic_archive_24dp)
        archiveIcon.bounds = Rect(
            textMargin,
            viewHolder.itemView.top + textMargin + 8,
            textMargin + archiveIcon.intrinsicWidth,
            viewHolder.itemView.top + archiveIcon.intrinsicHeight + textMargin + 8
        )

        if (dX > 0) archiveIcon.draw(canvas) else deleteIcon.draw(canvas)
    }

    private fun updateArtworkDeletedDatabase(
        deleted: Boolean,
        pos: Int
    ) {
        when (getRvListOrderState()) {
            "rating" -> {
                viewModel.updateArtwork(
                    viewModel
                        .artworkListByRating[pos]
                        .copy(deleted = deleted)
                        .asDatabaseModel()
                )
            }
            "show_id" -> {
                viewModel.updateArtwork(
                    viewModel
                        .artworkListByShowId[pos]
                        .copy(deleted = deleted)
                        .asDatabaseModel()
                )
            }
            "artist" -> {
                viewModel.updateArtwork(
                    viewModel
                        .artworkListByArtist[pos]
                        .copy(deleted = deleted)
                        .asDatabaseModel()
                )
            }
        }
    }

    private fun filterTakenAndDeletedArtworks(artworks: List<ArtThiefArtwork>): List<ArtThiefArtwork> {
        val areTakenArtworksVisible = getShowTakenArtworkState()
        val areDeletedArtworksVisible = getShowDeletedArtworkState()
        return artworks.filter {
            !(!areTakenArtworksVisible && it.taken) && !(!areDeletedArtworksVisible && it.deleted)
        }
    }
}
