package com.example.artthief.ui.overview

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.example.artthief.R
import com.google.android.material.tabs.TabLayout

class Page1OverviewFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_overview_page1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // set burglar vector's relative layout bottom margin
        val burglarLayout = requireView().findViewById<RelativeLayout>(R.id.rl_burglar)
        val burglarLayoutParams = burglarLayout.layoutParams as ViewGroup.MarginLayoutParams
        burglarLayoutParams.setMargins(0, 0, 0, -160)
        burglarLayout.layoutParams = burglarLayoutParams

        super.onViewCreated(view, savedInstanceState)
    }
}
