package com.joerock.artthief.ui.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.joerock.artthief.databinding.FragmentOverviewPage2Binding
import com.joerock.artthief.ui.overview.OverviewFragment.Companion.BURGLAR_BOTTOM_MARGIN
import com.joerock.artthief.utils.dpToPixels

class Page2OverviewFragment : Fragment() {

    private var _binding: FragmentOverviewPage2Binding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentOverviewPage2Binding.inflate(
            inflater,
            container,
            false
        )

        val burglarBackground = binding.ivBurglarBackgroundP2
        val burglarBackgroundLayoutParams = burglarBackground.layoutParams as ViewGroup.MarginLayoutParams
        burglarBackgroundLayoutParams.setMargins(
            0,
            0,
            0,
            dpToPixels(resources.displayMetrics.density, BURGLAR_BOTTOM_MARGIN)
        )

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
