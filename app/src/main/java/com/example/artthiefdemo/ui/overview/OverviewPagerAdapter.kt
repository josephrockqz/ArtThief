package com.example.artthiefdemo.ui.overview

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class OverviewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getCount(): Int = 2

    override fun getItem(i: Int): Fragment {
        if (i == 0) {
            return Page1OverviewFragment()
        } else if (i == 1) {
            return Page2OverviewFragment()
        }
        // TODO: implement all 4 pages of overview tab
        return OverviewFragment()
    }

    override fun getPageTitle(position: Int): CharSequence {
        if (position == 0) {
            return "uno"
        } else if (position == 1) {
            return "two"
        }
        return "eleventy"
    }
}
