package com.example.artthief.ui.rate

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.artthief.R
import com.example.artthief.databinding.FragmentCompareBinding
import com.example.artthief.domain.ArtThiefArtwork
import com.example.artthief.viewmodels.ArtworksViewModel
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

        // TODO: dynamically decide whether to reset or continue compare process based on savedInstanceState or something
        viewModel.artworkSectionCompareMapping.clear()
        viewModel.artworkSectionCompareOrdering.clear()

        setMenuItemOnClickListeners(inflater)
        configureImageDescriptionUIBasedOnSettings()

        val sectionRating = getCompareSectionRating()
        viewModel.getArtworksByRating(sectionRating).observe(viewLifecycleOwner) {
            Log.i("howdy - section artworks: ", it.toString())

            var totalNumberOfComparisons = 0
            for (i in it.indices) {
                totalNumberOfComparisons += i
                viewModel.artworkSectionCompareMapping[it[i].artThiefID] = mutableListOf()
                viewModel.artworkSectionCompareOrdering += it[i]
            }

            Log.i("mapping", viewModel.artworkSectionCompareMapping.toString())
            Log.i("ordering list", viewModel.artworkSectionCompareOrdering.toString())

            val nextArtworks = getNextCompareArtworks(it)
            // TODO: make sure artworks are displayed on top or bottom appropriately (accounting for previous winner's location) - can build this into get function
            loadArtworkDataUI(nextArtworks[1], nextArtworks[0])

            binding.flCompareArtwork1.setOnClickListener {
                // TODO: update mapping with new completed comparison
//                viewModel.artworkSectionCompareMapping[artwork1.artThiefID] += artwork2.artThiefID
                // TODO: retrieve next 2 artworks to be displayed, update UI
                // TODO: update ordering of artworks
                // TODO: increment running count of completed comparisons
                // TODO: check to see if comparisons are done - if so, alert, mark section as sorted, update artwork database with ordering
            }
            binding.flCompareArtwork2.setOnClickListener {

            }
        }

        return binding.root
    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
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

    // TODO: add extra ArtThiefArtwork? parameter and dynamically pass in the previous winner
    private fun getNextCompareArtworks(artworkSectionList: List<ArtThiefArtwork>): MutableList<ArtThiefArtwork> {
        val nextArtworks = mutableListOf<ArtThiefArtwork>()

        var parsingIndex = artworkSectionList.size - 1
        while (nextArtworks.size < 2 && parsingIndex >= 0) {
            val artwork = artworkSectionList[parsingIndex]
            val artworkPreviousComparisonAmount = viewModel
                .artworkSectionCompareMapping[artwork.artThiefID]!!
                .size
            if (nextArtworks.size == 0 && artworkPreviousComparisonAmount < artworkSectionList.size - 1) {
                nextArtworks += artwork
            } else if (nextArtworks.size == 1 &&
                artworkPreviousComparisonAmount < artworkSectionList.size - 1 &&
                !viewModel.artworkSectionCompareMapping[artwork.artThiefID]!!.contains(nextArtworks[0].artThiefID)
            ) {
                nextArtworks += artwork
            }
            parsingIndex -= 1
        }
        return nextArtworks
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

    private fun setMenuItemOnClickListeners(inflater: LayoutInflater) {

        toolbar.menu[1].setOnMenuItemClickListener {
            activity
                ?.findNavController(R.id.nav_host_fragment_activity_main)
                ?.navigate(R.id.action_compareToRate)
            true
        }

        toolbar.menu[0].subMenu?.get(0)?.setOnMenuItemClickListener {
            showInstructionsDialog(inflater)
        }
        toolbar.menu[0].subMenu?.get(1)?.setOnMenuItemClickListener {
            showSettingsDialog()
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

    private fun showSettingsDialog(): Boolean {

        activity
            ?.findNavController(R.id.nav_host_fragment_activity_main)
            ?.navigate(R.id.action_compareToCompareSettings)

        return true
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
