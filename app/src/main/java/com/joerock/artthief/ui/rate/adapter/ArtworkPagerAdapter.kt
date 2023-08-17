package com.joerock.artthief.ui.rate.adapter

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.joerock.artthief.domain.ArtThiefArtwork
import com.joerock.artthief.ui.rate.PageArtworkFragment

class ArtworkPagerAdapter(
    fm: FragmentManager
) : FragmentStatePagerAdapter(fm) {

    var artworks: List<ArtThiefArtwork> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            // This will cause every element in our RecyclerView to be invalidated.
            notifyDataSetChanged()
        }

    override fun getCount(): Int = artworks.size

    override fun getItem(i: Int): Fragment = PageArtworkFragment(artworks[i])
}
