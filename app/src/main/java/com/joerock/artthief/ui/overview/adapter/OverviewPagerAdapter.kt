package com.joerock.artthief.ui.overview.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.joerock.artthief.ui.overview.Page1OverviewFragment
import com.joerock.artthief.ui.overview.Page2OverviewFragment
import com.joerock.artthief.ui.overview.Page3OverviewFragment
import com.joerock.artthief.ui.overview.Page4OverviewFragment

class OverviewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    companion object {
        const val PAGE_COUNT = 4
        const val PAGE_TITLE = "\u2B24"
    }

    override fun getCount(): Int = PAGE_COUNT

    override fun getItem(i: Int): Fragment {
        return when (i) {
            0 -> Page1OverviewFragment()
            1 -> Page2OverviewFragment()
            2 -> Page3OverviewFragment()
            else -> Page4OverviewFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence = PAGE_TITLE
}
