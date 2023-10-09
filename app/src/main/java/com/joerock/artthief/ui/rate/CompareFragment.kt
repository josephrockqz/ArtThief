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
    private lateinit var topArtwork: ArtThiefArtwork
    private lateinit var bottomArtwork: ArtThiefArtwork
    private lateinit var winningArtwork: ArtThiefArtwork
    private var winningArtworkOnTopBool: Boolean = true

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

        viewModel.artworkSectionCompareMapping.clear()
        viewModel.artworkSectionCompareOrdering.clear()
        viewModel.artworkSectionCompletedComparisonsCounter = 0
        viewModel.artworkSectionCompareTotalNumComparisonsForCompletion = 0

        setMenuItemOnClickListeners(inflater)
        configureImageDescriptionUIBasedOnSettings()
        overrideOnBackPressed()

        sectionRating = getCompareSectionRating()
        viewModel.getArtworksByRating(sectionRating).observe(viewLifecycleOwner) {
            for (i in it.indices) {
                viewModel.artworkSectionCompareTotalNumComparisonsForCompletion += i
                viewModel.artworkSectionCompareMapping[it[i].artThiefID] = mutableListOf()
                viewModel.artworkSectionCompareOrdering += it[i].copy()
            }

            val nextArtworks = getNextCompareArtworks()
            topArtwork = nextArtworks[1]
            bottomArtwork = nextArtworks[0]
            loadArtworkDataUI(topArtwork, bottomArtwork)

            binding.flCompareArtwork1.setOnClickListener {
                winningArtwork = topArtwork
                winningArtworkOnTopBool = true
                viewModel.artworkSectionCompareMapping[topArtwork.artThiefID]!! += bottomArtwork.artThiefID
                viewModel.artworkSectionCompareMapping[bottomArtwork.artThiefID]!! += topArtwork.artThiefID
                if (topArtwork.order > bottomArtwork.order) {
                    val indexOfLosingArtwork = viewModel.artworkSectionCompareOrdering.indexOf(bottomArtwork)
                    viewModel.artworkSectionCompareOrdering.remove(topArtwork)
                    viewModel.artworkSectionCompareOrdering.add(indexOfLosingArtwork, topArtwork)
                }
                checkIfSectionCompareCompleted(inflater)
            }
            binding.flCompareArtwork2.setOnClickListener {
                winningArtwork = bottomArtwork
                winningArtworkOnTopBool = false
                viewModel.artworkSectionCompareMapping[topArtwork.artThiefID]!! += bottomArtwork.artThiefID
                viewModel.artworkSectionCompareMapping[bottomArtwork.artThiefID]!! += topArtwork.artThiefID
                if (bottomArtwork.order > topArtwork.order) {
                    val indexOfLosingArtwork = viewModel.artworkSectionCompareOrdering.indexOf(topArtwork)
                    viewModel.artworkSectionCompareOrdering.remove(bottomArtwork)
                    viewModel.artworkSectionCompareOrdering.add(indexOfLosingArtwork, bottomArtwork)
                }
                checkIfSectionCompareCompleted(inflater)
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    private fun checkIfSectionCompareCompleted(inflater: LayoutInflater) {
        viewModel.artworkSectionCompletedComparisonsCounter += 1
        if (viewModel.artworkSectionCompletedComparisonsCounter >= viewModel.artworkSectionCompareTotalNumComparisonsForCompletion) {
            showCompareDoneDialog(inflater)
            markSectionAsCompareCompleted()
            updateArtworkOrderingDatabase()
        } else {
            val nextArtworks = getNextCompareArtworks(winningArtwork)
            topArtwork = nextArtworks[1]
            bottomArtwork = nextArtworks[0]
            loadArtworkDataUI(topArtwork, bottomArtwork)
        }
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

    private fun getNextCompareArtworks(
        winningArtwork: ArtThiefArtwork? = null
    ): MutableList<ArtThiefArtwork> {
        val nextArtworks = mutableListOf<ArtThiefArtwork>()

        if (winningArtwork != null) {
            val winningArtworkPreviousComparisonAmount =
                viewModel
                    .artworkSectionCompareMapping[winningArtwork.artThiefID]!!
                    .size
            if (winningArtworkPreviousComparisonAmount < viewModel.artworkSectionCompareOrdering.size - 1) {
                nextArtworks += winningArtwork.copy()
            }
        }

        var parsingIndex = viewModel.artworkSectionCompareOrdering.size - 1
        while (nextArtworks.size < 2 && parsingIndex >= 0) {
            val artwork = viewModel.artworkSectionCompareOrdering[parsingIndex]
            val artworkPreviousComparisonAmount =
                viewModel
                    .artworkSectionCompareMapping[artwork.artThiefID]!!
                    .size
            if (nextArtworks.size == 0 && artworkPreviousComparisonAmount < viewModel.artworkSectionCompareOrdering.size - 1) {
                nextArtworks += artwork.copy()
            } else if (nextArtworks.size == 1 &&
                artworkPreviousComparisonAmount < viewModel.artworkSectionCompareOrdering.size - 1 &&
                    !viewModel.artworkSectionCompareMapping[artwork.artThiefID]!!.contains(nextArtworks[0].artThiefID) &&
                        nextArtworks[0].artThiefID != artwork.artThiefID
            ) {
                if (winningArtworkOnTopBool) {
                    nextArtworks.add(0, artwork.copy())
                } else {
                    nextArtworks += artwork.copy()
                }
            }
            parsingIndex -= 1
        }
        return nextArtworks
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

        binding.tvArtworkShowId1.text = artwork1.showID
        binding.tvArtworkTitle1.text = artwork1.title
        binding.tvArtworkArtist1.text = artwork1.artist
        binding.tvArtworkMedia1.text = artwork1.media
        binding.tvArtworkDimensions1.text = artwork1.dimensions

        binding.tvArtworkShowId2.text = artwork2.showID
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
            val builder = AlertDialog.Builder(it)
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
            val builder = AlertDialog.Builder(it)
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
        for (i in viewModel.artworkSectionCompareOrdering.indices) {
            viewModel.updateArtwork(
                viewModel
                    .artworkSectionCompareOrdering[i]
                    .copy(order = i)
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
