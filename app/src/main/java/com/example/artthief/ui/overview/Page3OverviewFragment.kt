package com.example.artthief.ui.overview

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.artthief.databinding.FragmentOverviewPage3Binding

class Page3OverviewFragment : Fragment() {

    private var _binding: FragmentOverviewPage3Binding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentOverviewPage3Binding.inflate(
            inflater,
            container,
            false
        )

        setupHyperlink()

        val burglarBackground = binding.ivBurglarBackgroundP3
        val burglarBackgroundLayoutParams = burglarBackground.layoutParams as ViewGroup.MarginLayoutParams
        burglarBackgroundLayoutParams.setMargins(
            0,
            0,
            0,
            OverviewFragment.burglarBottomMargin
        )

        return binding.root
    }

    private fun setupHyperlink() {
        val linkTextView = binding.tvInstructionsLink
        linkTextView.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
