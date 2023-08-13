package com.joerock.artthief.ui.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.joerock.artthief.databinding.FragmentOverviewPage4Binding
import com.joerock.artthief.ui.overview.OverviewFragment.Companion.BURGLAR_BOTTOM_MARGIN
import com.joerock.artthief.utils.dpToPixels
import com.joerock.artthief.viewmodels.ArtworksViewModel
import com.squareup.picasso.Picasso

class Page4OverviewFragment : Fragment() {

    companion object {
        const val HIGHEST_RATED_ARTWORK_BOTTOM_MARGIN = -142
    }

    private var _binding: FragmentOverviewPage4Binding? = null
    private val binding
        get() = _binding!!

    private val viewModel: ArtworksViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentOverviewPage4Binding.inflate(
            inflater,
            container,
            false
        )

        setLayoutMarginParams()

        val orientation = activity?.resources?.configuration?.orientation
        if (orientation == 1) {
            binding.easel.visibility = View.VISIBLE
            binding.ivHighestRatedArtwork.visibility = View.VISIBLE
            viewModel.highestRatedArtworkUrl.observe(viewLifecycleOwner) {
                if (it.image_large != String()) {
                    Picasso
                        .get()
                        .load(it.image_large)
                        .into(binding.ivHighestRatedArtwork)
                }
            }
        } else {
            binding.easel.visibility = View.GONE
            binding.ivHighestRatedArtwork.visibility = View.GONE
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setLayoutMarginParams() {
        val highestRatedArtwork = binding.ivHighestRatedArtwork
        val highestRatedArtworkLayoutParams = highestRatedArtwork.layoutParams as ViewGroup.MarginLayoutParams
        highestRatedArtworkLayoutParams.setMargins(
            0,
            0,
            0,
            dpToPixels(resources.displayMetrics.density, HIGHEST_RATED_ARTWORK_BOTTOM_MARGIN)
        )

        val burglarBackground = binding.ivBurglarBackgroundP4
        val burglarBackgroundLayoutParams = burglarBackground.layoutParams as ViewGroup.MarginLayoutParams
        burglarBackgroundLayoutParams.setMargins(
            0,
            0,
            0,
            dpToPixels(resources.displayMetrics.density, BURGLAR_BOTTOM_MARGIN)
        )
    }
}
