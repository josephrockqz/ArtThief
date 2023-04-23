package com.example.artthief.ui.rate

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ArtworkPagerAdapter(
    fm: FragmentManager,
    private val size: Int
) : FragmentPagerAdapter(fm) {

    override fun getCount(): Int = size

    override fun getItem(i: Int): Fragment = PageArtworkFragment(i)
}
