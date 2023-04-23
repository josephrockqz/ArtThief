package com.example.artthief.ui.rate

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.artthief.ui.overview.*

class ArtworkPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getCount(): Int = 4

    override fun getItem(i: Int): Fragment {
        return when (i) {
            0 -> Page1OverviewFragment()
            1 -> Page2OverviewFragment()
            2 -> Page3OverviewFragment()
            else -> Page4OverviewFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence {
        return "Howdy"
    }
}
