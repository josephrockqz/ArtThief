package com.example.artthief.ui.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.artthief.databinding.FragmentOverviewPage1Binding

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

        // set burglar vector's relative layout bottom margin
        val burglarLayout = binding.rlBurglar
        val burglarLayoutParams = burglarLayout.layoutParams as ViewGroup.MarginLayoutParams
        burglarLayoutParams.setMargins(
            0,
            0,
            0,
            -160
        )
        burglarLayout.layoutParams = burglarLayoutParams

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
