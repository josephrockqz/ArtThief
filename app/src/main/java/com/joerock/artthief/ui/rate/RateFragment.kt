package com.joerock.artthief.ui.rate

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.joerock.artthief.R
import com.joerock.artthief.databinding.FragmentRateBinding
import com.joerock.artthief.domain.ArtThiefArtwork
import com.joerock.artthief.domain.asDatabaseModel
import com.joerock.artthief.ui.rate.adapter.ArtworkAdapter
import com.joerock.artthief.ui.rate.adapter.ArtworkGridAdapter
import com.joerock.artthief.ui.rate.adapter.RatingSectionAdapter
import com.joerock.artthief.ui.rate.data.ArtworkClickListener
import com.joerock.artthief.ui.rate.data.CompareClickListener
import com.joerock.artthief.ui.rate.data.RecyclerViewSection
import com.joerock.artthief.ui.rate.data.SwipeUpdateArtworkDeleted
import com.joerock.artthief.viewmodels.ArtworksViewModel
import java.util.*
import kotlin.math.roundToInt

class RateFragment : Fragment() {

    private var _binding: FragmentRateBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: ArtworksViewModel by activityViewModels()

    private val gridView by lazy { binding.rvGridRateFragment }
    private val recyclerView by lazy { binding.rvRateFragment }
    private val toolbar by lazy { binding.rateFragmentAppBar }
    private val sharedPreferences by lazy {
        requireActivity().getPreferences(Context.MODE_PRIVATE)
    }

    private lateinit var artworkAdapter: ArtworkAdapter
    private lateinit var ratingSectionAdapter: RatingSectionAdapter
    private lateinit var artworkGridAdapter: ArtworkGridAdapter
    private lateinit var artworksListGridView: List<ArtThiefArtwork>
    private lateinit var artworkClickListener: ArtworkClickListener
    private lateinit var compareClickListener: CompareClickListener
    private lateinit var swipeHelper: ItemTouchHelper

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

        setArtworkList()
        configureDisplays()
        configureArtworkLoadingListener()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        configureListAndGridView()
        configureToolbar()
        configureZoomSlider()
        configureTakenAndDeletedArtworks()
        setMenuItemOnClickListeners()
        setZoomSliderChangeListener()
        setZoomSliderCancelButtonListener()

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding.sbZoomSlider.visibility = View.GONE
        binding.llZoomSliderContainer.visibility = View.GONE
        binding.ivSliderXButton.visibility = View.GONE
        _binding = null
    }

    fun showArtworkFragment(position: Int) {
        viewModel.currentArtworkIndex = position
        activity
            ?.findNavController(R.id.nav_host_fragment_activity_main)
            ?.navigate(R.id.action_rateToArtwork)
    }

    fun showCompareFragment(sectionRating: Int) {
        with (sharedPreferences.edit()) {
            putInt("section_rating", sectionRating)
            apply()
        }
        activity
            ?.findNavController(R.id.nav_host_fragment_activity_main)
            ?.navigate(R.id.action_rateToCompare)
    }

    private fun setArtworkList() {
        viewModel.artworksLive.observe(viewLifecycleOwner) { artworks ->
            viewModel.setArtworkList(artworks)
        }
    }

    private fun configureDisplays() {

        val displayTypeState = getDisplayTypeState()
        val rvListOrderState = getRvListOrderState()

        artworkClickListener = object : ArtworkClickListener {
            override fun onArtworkClicked(sectionPosition: Int, view: View) {
                with (sharedPreferences.edit()) {
                    putString("query_text", String())
                    apply()
                }
                showArtworkFragment(sectionPosition)
            }
        }
        compareClickListener = object : CompareClickListener {
            override fun onCompareClicked(sectionRating: Int) {
                showCompareFragment(sectionRating)
            }
        }
        swipeHelper = configureSwipeHelper()
        val swipeUpdateArtworkDeleted = object: SwipeUpdateArtworkDeleted {
            override fun updateArtworkDeleted(pos: Int, direction: Int) {
                updateArtworkDeletedState(pos, direction)
            }
        }

        if (displayTypeState == "grid" || (rvListOrderState != "show_id" && rvListOrderState != "artist")) {
            viewModel.artworkListByRatingLive.observe(viewLifecycleOwner) { artworks ->
                val artworksFilterTakenAndDeleted = filterTakenAndDeletedArtworks(artworks)
                artworksListGridView = filterGridView(artworksFilterTakenAndDeleted)
                if (displayTypeState == "grid") {
                    gridView.apply {
                        artworkGridAdapter = ArtworkGridAdapter(
                            artworks = artworksListGridView,
                            artworkImageSize = calculateGridViewImageSize(getZoomLevel())
                        )
                        adapter = artworkGridAdapter
                        layoutManager = GridLayoutManager(context, getZoomLevel())
                    }
                    val itemTouchHelper = configureDragHelper(artworksListGridView)
                    itemTouchHelper.attachToRecyclerView(gridView)
                    viewModel.setSortedArtworkListByRating(artworksListGridView)
                }
                viewModel.setSortedArtworkListByRating(artworksFilterTakenAndDeleted)
            }
            if (displayTypeState != "grid" && rvListOrderState == "rating") {
                viewModel.artworkListByRatingLive.observe(viewLifecycleOwner) { artworks ->
                    val artworksFilterTakenAndDeleted = filterTakenAndDeletedArtworks(artworks)
                    val artworksFilterSearchBar = filterQueryText(artworksFilterTakenAndDeleted)
                    // partition artworks by rating then assign to rv's sections
                    val artworkRatingMap = artworksFilterSearchBar.groupBy { artwork ->
                        artwork.rating
                    }
                    val artworkRatingSections = mutableListOf<RecyclerViewSection>()
                    for (i in 5 downTo 0) {
                        artworkRatingMap[i]?.let { list ->
                            artworkRatingSections.add(RecyclerViewSection(i, list))
                        }
                    }

                    recyclerView.apply {
                        ratingSectionAdapter = RatingSectionAdapter(
                            artworkClickListener = artworkClickListener,
                            compareClickListener = compareClickListener,
                            context = context,
                            resources = resources,
                            sections = artworkRatingSections,
                            sharedPreferences = sharedPreferences,
                            swipeUpdateArtworkDeleted = swipeUpdateArtworkDeleted
                        )
                        adapter = ratingSectionAdapter
                    }
                    viewModel.setSortedArtworkListByRating(artworksFilterSearchBar)
                }
            }
        } else if (rvListOrderState == "show_id") {
            viewModel.artworkListByShowIdLive.observe(viewLifecycleOwner) { artworks ->
                val artworksFilterTakenAndDeleted = filterTakenAndDeletedArtworks(artworks)
                val artworksFilterSearchBar = filterQueryText(artworksFilterTakenAndDeleted)
                recyclerView.apply {
                    artworkAdapter = ArtworkAdapter(
                        artworkClickListener = artworkClickListener,
                        artworks = artworksFilterSearchBar,
                        context = context
                    )
                    adapter = artworkAdapter
                }
                viewModel.setSortedArtworkListByShowId(artworksFilterSearchBar)
            }
        } else {
            viewModel.artworkListByArtistLive.observe(viewLifecycleOwner) { artworks ->
                val artworksFilterTakenAndDeleted = filterTakenAndDeletedArtworks(artworks)
                val artworksFilterSearchBar = filterQueryText(artworksFilterTakenAndDeleted)
                recyclerView.apply {
                    artworkAdapter = ArtworkAdapter(
                        artworkClickListener = artworkClickListener,
                        artworks = artworksFilterSearchBar,
                        context = context
                    )
                    adapter = artworkAdapter
                }
                viewModel.setSortedArtworkListByArtist(artworksFilterSearchBar)
            }
        }

        if (displayTypeState != "grid" && rvListOrderState != "rating") {
            swipeHelper.attachToRecyclerView(recyclerView)
        }
    }

    private fun configureArtworkLoadingListener() {
        val animatedVectorDrawable: AnimatedVectorDrawable = resources.getDrawable(R.drawable.ic_loading_animated) as AnimatedVectorDrawable
        binding.rateFragmentAppBar.menu.findItem(R.id.mi_loading).icon = animatedVectorDrawable

        viewModel.isDataLoading.observe(viewLifecycleOwner) { isDataLoading ->
            if (isDataLoading == true) {
                binding.rateFragmentAppBar.menu.findItem(R.id.mi_loading).isVisible = true
                binding.rateFragmentAppBar.menu.findItem(R.id.mi_refresh).isVisible = false
                animatedVectorDrawable.start()
            } else {
                binding.rateFragmentAppBar.menu.findItem(R.id.mi_loading).isVisible = false
                binding.rateFragmentAppBar.menu.findItem(R.id.mi_refresh).isVisible = true
                animatedVectorDrawable.stop()
            }
        }
    }

    private fun configureListAndGridView() {
        when (getDisplayTypeState()) {
            "list" -> {
                recyclerView.visibility = View.VISIBLE
                gridView.visibility = View.INVISIBLE
                updateListFilterChecks()
            }
            "grid" -> {
                recyclerView.visibility = View.INVISIBLE
                gridView.visibility = View.VISIBLE
                updateGridFilterChecks()
            }
        }
    }

    private fun configureToolbar() {
        when (getDisplayTypeState()) {
            "list" -> {
                toolbar.title = resources.getString(R.string.title_rate)
                toolbar.menu[0].icon = resources.getDrawable(R.drawable.ic_list_teal_24dp)
                toolbar.menu[0].subMenu?.get(0)?.isChecked = true
                toolbar.menu[1].isVisible = true
                toolbar.menu[2].isVisible = false
                toolbar.menu[3].isVisible = false
                toolbar.menu[5].isVisible = true
            }
            "grid" -> {
                toolbar.title = resources.getString(R.string.title_grid_sort)
                toolbar.menu[0].icon = resources.getDrawable(R.drawable.ic_grid_teal_24dp)
                toolbar.menu[0].subMenu?.get(1)?.isChecked = true
                toolbar.menu[1].isVisible = false
                toolbar.menu[2].isVisible = true
                toolbar.menu[3].isVisible = true
                toolbar.menu[5].isVisible = false
            }
        }

        when (getRvListOrderState()) {
            "rating" -> toolbar.menu[1].icon = resources.getDrawable(R.drawable.ic_rate_outline_teal_24dp)
            "show_id" -> toolbar.menu[1].icon = resources.getDrawable(R.drawable.ic_123_teal_24dp)
            "artist" -> toolbar.menu[1].icon = resources.getDrawable(R.drawable.ic_artist_teal_24dp)
        }
    }

    private fun configureZoomSlider() {
        when (getDisplayTypeState()) {
            "list" -> {
                toggleGridZoomSlider(false)
            }
            "grid" -> {
                toggleGridZoomSlider(getZoomSliderVisibility())
                binding.sbZoomSlider.progress = getZoomLevel() - 1
            }
        }
    }

    private fun configureTakenAndDeletedArtworks() {

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
    }

    private fun setMenuItemOnClickListeners() {

        toolbar.menu[0].subMenu?.get(0)?.setOnMenuItemClickListener { displayRecyclerView() }
        toolbar.menu[0].subMenu?.get(1)?.setOnMenuItemClickListener { displayGridView() }

        toolbar.menu[1].subMenu?.get(0)?.setOnMenuItemClickListener { listByRatingListener() }
        toolbar.menu[1].subMenu?.get(1)?.setOnMenuItemClickListener { listByShowIdListener() }
        toolbar.menu[1].subMenu?.get(2)?.setOnMenuItemClickListener { listByArtistListener() }
        toolbar.menu[1].subMenu?.get(3)?.setOnMenuItemClickListener { showDeletedArtwork() }
        toolbar.menu[1].subMenu?.get(4)?.setOnMenuItemClickListener { showTakenArtwork() }

        // 0: show all; 1: 5 stars; ... 5: 1 stars; 6: unrated
        toolbar.menu[2].subMenu?.get(0)?.setOnMenuItemClickListener { displayGridAfterFilter(0) }
        toolbar.menu[2].subMenu?.get(1)?.setOnMenuItemClickListener { displayGridAfterFilter(1) }
        toolbar.menu[2].subMenu?.get(2)?.setOnMenuItemClickListener { displayGridAfterFilter(2) }
        toolbar.menu[2].subMenu?.get(3)?.setOnMenuItemClickListener { displayGridAfterFilter(3) }
        toolbar.menu[2].subMenu?.get(4)?.setOnMenuItemClickListener { displayGridAfterFilter(4) }
        toolbar.menu[2].subMenu?.get(5)?.setOnMenuItemClickListener { displayGridAfterFilter(5) }
        toolbar.menu[2].subMenu?.get(6)?.setOnMenuItemClickListener { displayGridAfterFilter(6) }
        toolbar.menu[2].subMenu?.get(7)?.setOnMenuItemClickListener { showDeletedArtwork() }
        toolbar.menu[2].subMenu?.get(8)?.setOnMenuItemClickListener { showTakenArtwork() }

        toolbar.menu[3].setOnMenuItemClickListener {
            val currentZoomSliderVisibility = getZoomSliderVisibility()
            toggleGridZoomSlider(!currentZoomSliderVisibility)
        }

        toolbar.menu[4].setOnMenuItemClickListener { refreshRateFragmentFromIcon() }

        configureSearchBarQueryTextListener()
    }

    private fun setZoomSliderChangeListener() {
        binding.sbZoomSlider.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                val updatedNumColumns = progress + 1
                with (sharedPreferences.edit()) {
                    putInt("zoom_level", updatedNumColumns)
                    apply()
                }
                refreshRateFragment()
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
        binding.ivSliderXButton.setOnClickListener {
            toggleGridZoomSlider(false)
        }
    }

    private fun displayRecyclerView(): Boolean {
        if (getDisplayTypeState() != "list") {
            with (sharedPreferences.edit()) {
                putString("display_type", "list")
                apply()
            }
            recyclerView.visibility = View.VISIBLE
            gridView.visibility = View.INVISIBLE
            toggleGridZoomSlider(false)
            toolbar.menu[0].icon = resources.getDrawable(R.drawable.ic_list_teal_24dp)
            toolbar.menu[0].subMenu?.get(0)?.isChecked = true
            toolbar.menu[0].subMenu?.get(1)?.isChecked = false
            updateListFilterChecks()
            toolbar.menu[1].isVisible = true
            toolbar.menu[2].isVisible = false
            toolbar.menu[3].isVisible = false
            toolbar.menu[5].isVisible = true
            toolbar.title = resources.getString(R.string.title_rate)
            refreshRateFragment()
        }
        return true
    }

    private fun displayGridView(): Boolean {
        if (getDisplayTypeState() != "grid") {
            with (sharedPreferences.edit()) {
                putString("display_type", "grid")
                apply()
            }
            recyclerView.visibility = View.INVISIBLE
            gridView.visibility = View.VISIBLE
            toolbar.menu[0].icon = resources.getDrawable(R.drawable.ic_grid_teal_24dp)
            toolbar.menu[0].subMenu?.get(0)?.isChecked = false
            toolbar.menu[0].subMenu?.get(1)?.isChecked = true
            updateGridFilterChecks()
            toolbar.menu[1].isVisible = false
            toolbar.menu[2].isVisible = true
            toolbar.menu[3].isVisible = true
            toolbar.menu[5].isVisible = false
            toolbar.title = resources.getString(R.string.title_grid_sort)
            refreshRateFragment()
        }
        return true
    }

    private fun displayGridAfterFilter(filter: Int): Boolean {
        when (filter) {
            0 -> toolbar.menu[2].icon = resources.getDrawable(R.drawable.ic_filter_teal_24dp)
            1 -> toolbar.menu[2].icon = resources.getDrawable(R.drawable.ic_five_teal_24dp)
            2 -> toolbar.menu[2].icon = resources.getDrawable(R.drawable.ic_four_teal_24dp)
            3 -> toolbar.menu[2].icon = resources.getDrawable(R.drawable.ic_three_teal_24dp)
            4 -> toolbar.menu[2].icon = resources.getDrawable(R.drawable.ic_two_teal_24dp)
            5 -> toolbar.menu[2].icon = resources.getDrawable(R.drawable.ic_one_teal_24dp)
            6 -> toolbar.menu[2].icon = resources.getDrawable(R.drawable.ic_outline_circle_teal_24dp)
        }
        with (sharedPreferences.edit()) {
            putInt("gv_filter", filter)
            apply()
        }
        updateGridFilterChecks(filter)
        refreshRateFragment()
        return true
    }

    private fun listByRatingListener(): Boolean {
        if (getRvListOrderState() != "rating") {
            with (sharedPreferences.edit()) {
                putString("rv_list_order", "rating")
                apply()
            }
            toolbar.menu[1].icon = resources.getDrawable(R.drawable.ic_rate_outline_teal_24dp)
            toolbar.menu[1].subMenu?.get(0)?.isChecked = true
            toolbar.menu[1].subMenu?.get(1)?.isChecked = false
            toolbar.menu[1].subMenu?.get(2)?.isChecked = false
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
            toolbar.menu[1].subMenu?.get(0)?.isChecked = false
            toolbar.menu[1].subMenu?.get(1)?.isChecked = true
            toolbar.menu[1].subMenu?.get(2)?.isChecked = false
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
            toolbar.menu[1].subMenu?.get(0)?.isChecked = false
            toolbar.menu[1].subMenu?.get(1)?.isChecked = false
            toolbar.menu[1].subMenu?.get(2)?.isChecked = true
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

    private fun toggleGridZoomSlider(enable: Boolean): Boolean {
        if (enable) {
            binding.llZoomSliderContainer.bringToFront()
            binding.llZoomSliderContainer.visibility = View.VISIBLE
        } else {
            binding.llZoomSliderContainer.visibility = View.GONE
        }
        with (sharedPreferences.edit()) {
            putBoolean("show_zoom_slider", enable)
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
        activity
            ?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.rl_rateFragment, RateFragment())
            ?.commit()
    }

    private fun calculateGridViewImageSize(numColumns: Int): Int {
        val screenWidth = resources.displayMetrics.widthPixels
        val artworkImageSideLength = screenWidth / numColumns
        return kotlin.math.floor(artworkImageSideLength.toDouble()).toInt()
    }

    private fun getDisplayTypeState(): String {
        return sharedPreferences.getString("display_type", "list")!!
    }

    private fun getRvListOrderState(): String {
        return sharedPreferences.getString("rv_list_order", "rating")!!
    }

    private fun getGridViewDisplayFilter(): Int {
        // 0 represents "Show All", 1 represents "5 Stars"
        return sharedPreferences.getInt("gv_filter", 0)
    }

    private fun getShowDeletedArtworkState(): Boolean {
        return sharedPreferences.getBoolean("show_deleted_artwork", false)
    }

    private fun getShowTakenArtworkState(): Boolean {
        return sharedPreferences.getBoolean("show_taken_artwork", false)
    }

    private fun getSearchBarQueryText(): String {
        return sharedPreferences.getString("query_text", String())!!
    }

    private fun getZoomLevel(): Int {
        return sharedPreferences.getInt("zoom_level", 2)
    }

    private fun getZoomSliderVisibility(): Boolean {
        return sharedPreferences.getBoolean("show_zoom_slider", false)
    }

    private fun updateArtworkDeletedDatabase(deleted: Boolean, pos: Int) {
        when (getRvListOrderState()) {
            "rating" -> viewModel.updateArtwork(
                            viewModel
                                .artworkListByRating[pos]
                                .copy(deleted = deleted)
                                .asDatabaseModel()
                        )
            "show_id" -> viewModel.updateArtwork(
                            viewModel
                                .artworkListByShowId[pos]
                                .copy(deleted = deleted)
                                .asDatabaseModel()
                        )
            "artist" -> viewModel.updateArtwork(
                            viewModel
                                .artworkListByArtist[pos]
                                .copy(deleted = deleted)
                                .asDatabaseModel()
                        )
        }
    }

    private fun filterTakenAndDeletedArtworks(artworks: List<ArtThiefArtwork>): List<ArtThiefArtwork> {
        val areTakenArtworksVisible = getShowTakenArtworkState()
        val areDeletedArtworksVisible = getShowDeletedArtworkState()
        return artworks.filter {
            !(!areTakenArtworksVisible && it.taken) && !(!areDeletedArtworksVisible && it.deleted)
        }
    }

    private fun filterGridView(artworks: List<ArtThiefArtwork>): List<ArtThiefArtwork> {
        return when (getGridViewDisplayFilter()) {
            1 -> artworks.filter { it.rating == 5 }
            2 -> artworks.filter { it.rating == 4 }
            3 -> artworks.filter { it.rating == 3 }
            4 -> artworks.filter { it.rating == 2 }
            5 -> artworks.filter { it.rating == 1 }
            6 -> artworks.filter { it.rating == 0 }
            else -> artworks
        }
    }

    private fun filterQueryText(artworks: List<ArtThiefArtwork>): List<ArtThiefArtwork> {
        return when (val queryText = getSearchBarQueryText()) {
            String() -> artworks
            else -> artworks.filter {
                it.title.lowercase(Locale.ROOT).contains(queryText) ||
                    it.artist.lowercase(Locale.ROOT).contains(queryText) ||
                        it.showID.lowercase(Locale.ROOT).contains(queryText) ||
                            it.media.lowercase(Locale.ROOT).contains(queryText)
            }
        }
    }

    private fun configureSearchBarQueryTextListener() {
        val search = toolbar.menu.findItem(R.id.mi_search)
        val searchView = search.actionView as SearchView
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    with (sharedPreferences.edit()) {
                        putString("query_text", newText.lowercase(Locale.ROOT))
                        apply()
                    }
                    refreshRateFragment()
                }
                return true
            }
        })
    }

    private fun configureDragHelper(artworksFilterGridView: List<ArtThiefArtwork>): ItemTouchHelper {
        return ItemTouchHelper(object : ItemTouchHelper.Callback() {
            var dragFrom = -1
            var dragTo = -1

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                return makeFlag(
                    ItemTouchHelper.ACTION_STATE_DRAG,
                    ItemTouchHelper.DOWN or ItemTouchHelper.UP or ItemTouchHelper.START or ItemTouchHelper.END
                )
            }
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition

                if (dragFrom == -1) {
                    dragFrom =  fromPosition
                }
                dragTo = toPosition
                viewModel.updateSelectedGridArtwork(artworksFilterGridView[dragFrom])

                if (viewHolder.adapterPosition < target.adapterPosition) {
                    for (i in viewHolder.adapterPosition until target.adapterPosition) {
                        Collections.swap(artworksFilterGridView, i, i + 1)
                    }
                } else {
                    for (i in viewHolder.adapterPosition downTo target.adapterPosition + 1) {
                        Collections.swap(artworksFilterGridView, i, i - 1)
                    }
                }
                artworkGridAdapter.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
                return true
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)

                viewModel.artworkSelectedGridReadyToBeUpdated = true

                if (dragFrom != -1 && dragTo != -1 && dragFrom != dragTo) {
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
                    val selectedArtworkOldRating = viewModel.artworkSelectedGrid.rating
                    viewModel.updateArtworkRatingsDragAndDrop(
                        artworksListGridView = artworksListGridView,
                        dragFrom = dragFrom,
                        dragTo = dragTo,
                        selectedArtworkNewRating = selectedArtworkNewRating,
                        selectedArtworkOldRating = selectedArtworkOldRating
                    )
                    if (selectedArtworkNewRating != selectedArtworkOldRating && selectedArtworkNewRating != 0) {
                        updateSectionSortedStatus(selectedArtworkNewRating)
                    }
                }

                dragFrom = -1
                dragTo = -1

                refreshRateFragment()
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // No-Op
            }
            override fun isLongPressDragEnabled(): Boolean = true
            override fun isItemViewSwipeEnabled(): Boolean = false
        })
    }

    private fun configureSwipeHelper(): ItemTouchHelper {
        return ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = true

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.adapterPosition
                artworkAdapter.notifyItemRemoved(pos)
                updateArtworkDeletedState(pos, direction)
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
    }

    private fun updateArtworkDeletedState(pos: Int, direction: Int) {
        if (direction == ItemTouchHelper.LEFT) {
            updateArtworkDeletedDatabase(true, pos)
        } else if (direction == ItemTouchHelper.RIGHT) {
            updateArtworkDeletedDatabase(false, pos)
        }
    }

    private fun updateListFilterChecks() {
        when (getRvListOrderState()) {
            "rating" -> {
                toolbar.menu[1].subMenu?.get(0)?.isChecked = true
                toolbar.menu[1].subMenu?.get(1)?.isChecked = false
                toolbar.menu[1].subMenu?.get(2)?.isChecked = false
            }
            "show_id" -> {
                toolbar.menu[1].subMenu?.get(0)?.isChecked = false
                toolbar.menu[1].subMenu?.get(1)?.isChecked = true
                toolbar.menu[1].subMenu?.get(2)?.isChecked = false
            }
            "artist" -> {
                toolbar.menu[1].subMenu?.get(0)?.isChecked = false
                toolbar.menu[1].subMenu?.get(1)?.isChecked = false
                toolbar.menu[1].subMenu?.get(2)?.isChecked = true
            }
        }
    }

    private fun updateGridFilterChecks(filter: Int = getGridViewDisplayFilter()) {
        for (i in 0..6) {
            toolbar.menu[2].subMenu?.get(i)?.isChecked = i == filter
        }
    }

    private fun updateSectionSortedStatus(rating: Int) {
        when (rating) {
            1 -> {
                with (sharedPreferences.edit()) {
                    putBoolean("one_stars_sorted", false)
                    apply()
                }
            }
            2 -> {
                with (sharedPreferences.edit()) {
                    putBoolean("two_stars_sorted", false)
                    apply()
                }
            }
            3 -> {
                with (sharedPreferences.edit()) {
                    putBoolean("three_stars_sorted", false)
                    apply()
                }
            }
            4 -> {
                with (sharedPreferences.edit()) {
                    putBoolean("four_stars_sorted", false)
                    apply()
                }
            }
            5 -> {
                with (sharedPreferences.edit()) {
                    putBoolean("five_stars_sorted", false)
                    apply()
                }
            }
        }
    }
}
