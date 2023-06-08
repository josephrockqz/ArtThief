package com.example.artthief.ui.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.artthief.databinding.FragmentOverviewPage2Binding
import com.example.artthief.ui.overview.OverviewFragment.Companion.burglarBottomMargin

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
            burglarBottomMargin
        )

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
