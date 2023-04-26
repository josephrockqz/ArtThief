package com.example.artthief.ui.rate

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.artthief.domain.ArtThiefArtwork

class ArtworkPagerAdapter(
    fm: FragmentManager
) : FragmentStatePagerAdapter(fm) {

    // TODO: have it so initial index of pager adapter can be non-zero

    /**
     * The artworks that our Adapter will show
     */
    var artworks: List<ArtThiefArtwork> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            // Notify any registered observers that the data set has changed. This will cause every
            // element in our RecyclerView to be invalidated.
            notifyDataSetChanged()
        }

    override fun getCount(): Int = artworks.size

    override fun getItem(i: Int): Fragment = PageArtworkFragment(artworks[i])
}
