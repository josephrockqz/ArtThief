package com.joerock.artthief.ui.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.joerock.artthief.databinding.FragmentOverviewPage1Binding
import com.joerock.artthief.ui.overview.OverviewFragment.Companion.BURGLAR_BOTTOM_MARGIN
import com.joerock.artthief.utils.dpToPixels

class Page1OverviewFragment : Fragment() {

    private var _binding: FragmentOverviewPage1Binding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentOverviewPage1Binding.inflate(
            inflater,
            container,
            false
        )

        adjustUiBasedOnDeviceOrientation()
        setBackgroundLayoutMarginParams()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun adjustUiBasedOnDeviceOrientation() {
        val orientation = activity?.resources?.configuration?.orientation
        if (orientation == 1) {
            binding.ivBurglar.visibility = View.VISIBLE
            binding.llBurglarBackground.visibility = View.VISIBLE
            binding.ivBurglarBackgroundLandscape.visibility = View.GONE
        } else {
            binding.ivBurglar.visibility = View.GONE
            binding.llBurglarBackground.visibility = View.GONE
            binding.ivBurglarBackgroundLandscape.visibility = View.VISIBLE
        }
    }

    private fun setBackgroundLayoutMarginParams() {
        val burglarBackground = binding.llBurglarBackground
        val burglarBackgroundLayoutParams = burglarBackground.layoutParams as ViewGroup.MarginLayoutParams
        burglarBackgroundLayoutParams.setMargins(
            0,
            0,
            0,
            dpToPixels(resources.displayMetrics.density, BURGLAR_BOTTOM_MARGIN)
        )

        val burglarBackgroundLandscape = binding.ivBurglarBackgroundLandscape
        val burglarBackgroundLandscapeLayoutParams = burglarBackgroundLandscape.layoutParams as ViewGroup.MarginLayoutParams
        burglarBackgroundLandscapeLayoutParams.setMargins(
            0,
            0,
            0,
            dpToPixels(resources.displayMetrics.density, BURGLAR_BOTTOM_MARGIN)
        )

        val burglarImage = binding.ivBurglar
        val burglarLayoutParams = burglarImage.layoutParams as ViewGroup.MarginLayoutParams
        burglarLayoutParams.setMargins(
            0,
            0,
            0,
            dpToPixels(resources.displayMetrics.density, BURGLAR_BOTTOM_MARGIN)
        )
    }
}
