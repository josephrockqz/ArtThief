package com.example.artthiefdemo.ui.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.artthiefdemo.R

class OverviewFragment : Fragment() {

    private lateinit var overviewPagerAdapter: OverviewPagerAdapter
    private lateinit var viewPager: ViewPager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_overview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        overviewPagerAdapter = OverviewPagerAdapter(childFragmentManager)
        viewPager = view.findViewById(R.id.pager_overview)
        viewPager.adapter = overviewPagerAdapter
    }

}
