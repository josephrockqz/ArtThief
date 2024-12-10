package com.joerock.artthief.ui.rate

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.joerock.artthief.R
import com.joerock.artthief.databinding.FragmentCompareBinding
import com.joerock.artthief.domain.ArtThiefArtwork
import com.joerock.artthief.domain.asDatabaseModel
import com.joerock.artthief.utils.getShowIdDisplayValue
import com.joerock.artthief.viewmodels.ArtworksViewModel
import com.squareup.picasso.Picasso

class CompareFragment : Fragment() {

    private var _binding: FragmentCompareBinding? = null
    private val binding
        get() = _binding!!

    private val sharedPreferences by lazy {
        requireActivity().getPreferences(Context.MODE_PRIVATE)
    }
    private val toolbar by lazy { binding.compareFragmentAppBar }
    private val viewModel: ArtworksViewModel by activityViewModels()

    private var sectionRating: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompareBinding.inflate(
            inflater,
            container,
            false
        )

        viewModel.isComparisonFinalized = false

        viewModel.artworkSectionCompareOrdering.clear()
        viewModel.artworksToCompare.clear()
        viewModel.preferenceGraph.clear()
        viewModel.currentWinningArtwork = null

        setMenuItemOnClickListeners(inflater)
        configureImageDescriptionUIBasedOnSettings()
        overrideOnBackPressed()

        sectionRating = getCompareSectionRating()
        viewModel.getArtworksByRating(sectionRating).observe(viewLifecycleOwner) { artworks ->
            for (i in artworks.indices) {
                viewModel.artworkSectionCompareOrdering += artworks[i].copy()
            }

            viewModel.artworksToCompare = artworks.toMutableList()
            startComparison()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        viewModel.getArtworksByRating(sectionRating).removeObservers(viewLifecycleOwner)

        _binding = null
    }

    private fun startComparison() {
        if (viewModel.artworksToCompare.isEmpty()) return

        viewModel.currentWinningArtwork = viewModel.artworksToCompare[0]

        compareWithNextArtwork()
    }

    private fun compareWithNextArtwork() {
        // Find the next artwork to compare
        val nextArtwork = viewModel.artworksToCompare.find { artwork ->
            artwork.artThiefID != viewModel.currentWinningArtwork!!.artThiefID &&
            !hasPreference(viewModel.currentWinningArtwork!!.artThiefID, artwork.artThiefID) &&
            !hasPreference(artwork.artThiefID, viewModel.currentWinningArtwork!!.artThiefID)
        }
        if (nextArtwork != null) {
            loadArtworkDataUI(viewModel.currentWinningArtwork!!, nextArtwork)
    
            binding.flCompareArtwork1.setOnClickListener {
                handleComparisonResult(viewModel.currentWinningArtwork!!, nextArtwork)
            }
            binding.flCompareArtwork2.setOnClickListener {
                handleComparisonResult(nextArtwork, viewModel.currentWinningArtwork!!)
            }
        } else {
            finalizeComparison()
        }
    }

    private fun handleComparisonResult(winningArtwork: ArtThiefArtwork, losingArtwork: ArtThiefArtwork) {

        updatePreferenceGraph(winningArtwork.artThiefID, losingArtwork.artThiefID)

        val currentArtworkNeedsMoreComparisons = viewModel.artworksToCompare.any { artwork ->
            artwork.artThiefID != viewModel.currentWinningArtwork!!.artThiefID &&
            !hasPreference(viewModel.currentWinningArtwork!!.artThiefID, artwork.artThiefID)
        }

        if (!currentArtworkNeedsMoreComparisons) {
            // Find a new artwork that needs more comparisons
            viewModel.currentWinningArtwork = viewModel.artworksToCompare.find { artwork ->
                viewModel.artworksToCompare.any { other ->
                    artwork.artThiefID != other.artThiefID && 
                    !hasPreference(artwork.artThiefID, other.artThiefID) &&
                    !hasPreference(other.artThiefID, artwork.artThiefID)
                }
            }
    
            if (viewModel.currentWinningArtwork != null) {
                compareWithNextArtwork()
            } else {
                finalizeComparison()
            }
        } else {
            // Current winning artwork still needs more comparisons
            viewModel.currentWinningArtwork = winningArtwork
            compareWithNextArtwork()
        }
    }

    private fun updatePreferenceGraph(winnerId: Int, loserId: Int) {
        viewModel.preferenceGraph.putIfAbsent(winnerId, mutableSetOf())
        viewModel.preferenceGraph[winnerId]!!.add(loserId)
    }

    private fun hasPreference(artworkId1: Int, artworkId2: Int): Boolean {
        // Perform a DFS to check if there's a path from artworkId1 to artworkId2
        return dfs(artworkId1, artworkId2, mutableSetOf())
    }

    private fun dfs(currentId: Int, targetId: Int, visited: MutableSet<Int>): Boolean {
        if (currentId == targetId) return true
        visited.add(currentId)

        viewModel.preferenceGraph[currentId]?.forEach { neighborId ->
            if (!visited.contains(neighborId) && dfs(neighborId, targetId, visited)) {
                return true
            }
        }
        return false
    }

    private fun createOrderedArtworkList(): List<ArtThiefArtwork> {
        val inDegree = mutableMapOf<Int, Int>()
        viewModel.artworksToCompare.forEach { artwork ->
            inDegree[artwork.artThiefID] = 0
        }
        
        viewModel.preferenceGraph.forEach { (_, losers) ->
            losers.forEach { loserId ->
                inDegree[loserId] = (inDegree[loserId] ?: 0) + 1
            }
        }
        
        val queue = ArrayDeque<Int>()
        inDegree.forEach { (id, degree) ->
            if (degree == 0) queue.add(id)
        }
        
        val orderedIds = mutableListOf<Int>()
        
        while (queue.isNotEmpty()) {
            val winnerId = queue.removeFirst()
            orderedIds.add(winnerId)
            
            viewModel.preferenceGraph[winnerId]?.forEach { loserId ->
                inDegree[loserId] = inDegree[loserId]!! - 1
                if (inDegree[loserId] == 0) {
                    queue.add(loserId)
                }
            }
        }
        
        // Map ordered IDs back to ArtThiefArtwork objects
        val idToArtwork = viewModel.artworksToCompare.associateBy { it.artThiefID }
        return orderedIds.map { id -> idToArtwork[id]!! }
    }

    private fun finalizeComparison() {
        if (viewModel.isComparisonFinalized) return
        
        // Create ordered list based on preference graph
        val orderedArtworks = createOrderedArtworkList()
        viewModel.artworkSectionCompareOrdering = orderedArtworks.toMutableList()

        viewModel.getArtworksByRating(sectionRating).removeObservers(viewLifecycleOwner)
        
        updateArtworkOrderingDatabase()
        markSectionAsCompareCompleted()
        
        view?.post {
            showCompareDoneDialog(layoutInflater)
        }

        viewModel.isComparisonFinalized = true
    }

    private fun configureImageDescriptionUIBasedOnSettings() {
        val isCompareSettingIdNumberChecked = isCompareSettingIdNumberChecked()
        val isCompareSettingTitleChecked = isCompareSettingTitleChecked()
        val isCompareSettingArtistChecked = isCompareSettingArtistChecked()
        val isCompareSettingMediaChecked = isCompareSettingMediaChecked()
        val isCompareSettingDimensionsChecked = isCompareSettingDimensionsChecked()
        if (isCompareSettingIdNumberChecked || isCompareSettingTitleChecked ||
            isCompareSettingArtistChecked || isCompareSettingMediaChecked ||
            isCompareSettingDimensionsChecked) {

            binding.flCompareImage1Description.visibility = View.VISIBLE
            binding.flCompareImage2Description.visibility = View.VISIBLE

            if (isCompareSettingIdNumberChecked) {
                binding.tvArtworkShowId1.visibility = View.VISIBLE
                binding.tvArtworkShowId2.visibility = View.VISIBLE
            } else {
                binding.tvArtworkShowId1.visibility = View.GONE
                binding.tvArtworkShowId2.visibility = View.INVISIBLE
            }
            if (isCompareSettingTitleChecked) {
                binding.tvArtworkTitle1.visibility = View.VISIBLE
                binding.tvArtworkTitle2.visibility = View.VISIBLE
            } else {
                binding.tvArtworkTitle1.visibility = View.GONE
                binding.tvArtworkTitle2.visibility = View.GONE
            }
            if (isCompareSettingArtistChecked) {
                binding.tvArtworkArtist1.visibility = View.VISIBLE
                binding.tvArtworkArtist2.visibility = View.VISIBLE
            } else {
                binding.tvArtworkArtist1.visibility = View.GONE
                binding.tvArtworkArtist2.visibility = View.GONE
            }
            if (isCompareSettingMediaChecked) {
                binding.tvArtworkMedia1.visibility = View.VISIBLE
                binding.tvArtworkMedia2.visibility = View.VISIBLE
            } else {
                binding.tvArtworkMedia1.visibility = View.GONE
                binding.tvArtworkMedia2.visibility = View.GONE
            }
            if (isCompareSettingDimensionsChecked) {
                binding.tvArtworkDimensions1.visibility = View.VISIBLE
                binding.tvArtworkDimensions2.visibility = View.VISIBLE
            } else {
                binding.tvArtworkDimensions1.visibility = View.GONE
                binding.tvArtworkDimensions2.visibility = View.GONE
            }
        } else {
            binding.flCompareImage1Description.visibility = View.GONE
            binding.flCompareImage2Description.visibility = View.GONE
        }
    }

    private fun goBack() {
        activity
            ?.findNavController(R.id.nav_host_fragment_activity_main)
            ?.navigate(R.id.action_compareToRate)
    }

    private fun loadArtworkDataUI(artwork1: ArtThiefArtwork, artwork2: ArtThiefArtwork) {
        Picasso
            .get()
            .load(artwork1.image_large)
            .into(binding.ivCompareImage1)
        Picasso
            .get()
            .load(artwork2.image_large)
            .into(binding.ivCompareImage2)

        binding.tvArtworkShowId1.text = getShowIdDisplayValue(artwork1.showID)
        binding.tvArtworkTitle1.text = artwork1.title
        binding.tvArtworkArtist1.text = artwork1.artist
        binding.tvArtworkMedia1.text = artwork1.media
        binding.tvArtworkDimensions1.text = artwork1.dimensions

        binding.tvArtworkShowId2.text = getShowIdDisplayValue(artwork2.showID)
        binding.tvArtworkTitle2.text = artwork2.title
        binding.tvArtworkArtist2.text = artwork2.artist
        binding.tvArtworkMedia2.text = artwork2.media
        binding.tvArtworkDimensions2.text = artwork2.dimensions
    }

    private fun markSectionAsCompareCompleted() {
        when (sectionRating) {
            1 -> {
                with (sharedPreferences.edit()) {
                    putBoolean("one_stars_sorted", true)
                    apply()
                }
            }
            2 -> {
                with (sharedPreferences.edit()) {
                    putBoolean("two_stars_sorted", true)
                    apply()
                }
            }
            3 -> {
                with (sharedPreferences.edit()) {
                    putBoolean("three_stars_sorted", true)
                    apply()
                }
            }
            4 -> {
                with (sharedPreferences.edit()) {
                    putBoolean("four_stars_sorted", true)
                    apply()
                }
            }
            5 -> {
                with (sharedPreferences.edit()) {
                    putBoolean("five_stars_sorted", true)
                    apply()
                }
            }
        }
    }

    private fun overrideOnBackPressed() {
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object: OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    goBack()
                }
            })
    }

    private fun setMenuItemOnClickListeners(inflater: LayoutInflater) {

        toolbar.menu[1].setOnMenuItemClickListener {
            goBack()
            true
        }

        toolbar.menu[0].subMenu?.get(0)?.setOnMenuItemClickListener {
            showInstructionsDialog(inflater)
        }
        toolbar.menu[0].subMenu?.get(1)?.setOnMenuItemClickListener {
            showSettingsFragment()
        }
    }

    private fun showCompareDoneDialog(inflater: LayoutInflater) {
        activity?.let {
            val builder = AlertDialog.Builder(it, R.style.AlertDialogTheme)
            builder.apply {
                val view: View = inflater.inflate(R.layout.compare_finished_dialog_title, null)
                setCustomTitle(view)
                setView(R.layout.compare_finished_dialog_content)
                setPositiveButton(R.string.instructions_ok) { _, _ ->
                    goBack()
                }
                setCancelable(false)
            }
            builder.create()
            builder.show()
        }
    }

    private fun showInstructionsDialog(inflater: LayoutInflater): Boolean {
        activity?.let {
            val builder = AlertDialog.Builder(it, R.style.AlertDialogTheme)
            builder.apply {
                val view: View = inflater.inflate(R.layout.compare_instructions_dialog_title, null)
                setCustomTitle(view)
                setView(R.layout.compare_instructions_dialog_content)
                setPositiveButton(R.string.instructions_ok) { _, _ -> }
            }
            builder.create()
            builder.show()
        }

        return true
    }

    private fun showSettingsFragment(): Boolean {

        activity
            ?.findNavController(R.id.nav_host_fragment_activity_main)
            ?.navigate(R.id.action_compareToCompareSettings)

        return true
    }

    private fun updateArtworkOrderingDatabase() {
        viewModel.getArtworksByRating(sectionRating).removeObservers(viewLifecycleOwner)

        viewModel.artworkSectionCompareOrdering.forEachIndexed { index, artwork ->
            viewModel.updateArtwork(
                artwork
                    .copy(order = index)
                    .asDatabaseModel()
            )
        }
    }

    private fun getCompareSectionRating(): Int {
        return sharedPreferences.getInt("section_rating", 5)
    }

    private fun isCompareSettingIdNumberChecked(): Boolean {
        return sharedPreferences.getBoolean("compare_setting_id_number", false)
    }

    private fun isCompareSettingTitleChecked(): Boolean {
        return sharedPreferences.getBoolean("compare_setting_title", false)
    }

    private fun isCompareSettingArtistChecked(): Boolean {
        return sharedPreferences.getBoolean("compare_setting_artist", false)
    }

    private fun isCompareSettingMediaChecked(): Boolean {
        return sharedPreferences.getBoolean("compare_setting_media", false)
    }

    private fun isCompareSettingDimensionsChecked(): Boolean {
        return sharedPreferences.getBoolean("compare_setting_dimensions", false)
    }
}
