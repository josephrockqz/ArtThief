package com.joerock.artthief.ui.rate.adapter

import android.annotation.SuppressLint
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.joerock.artthief.R
import com.joerock.artthief.domain.ArtThiefArtwork
import com.joerock.artthief.ui.rate.PageArtworkFragment

class ArtworkPagerAdapter(
    fm: FragmentManager
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragments = SparseArray<PageArtworkFragment>()
    
    var artworks: List<ArtThiefArtwork> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getCount(): Int = artworks.size

    override fun getItem(position: Int): Fragment {
        return PageArtworkFragment(artworks[position]).also {
            fragments.put(position, it)
        }
    }
    
    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }
    
    fun clearFragments() {
        for (i in 0 until fragments.size()) {
            val fragment = fragments.valueAt(i)
            fragment.view?.let { view ->
                // Clear any image views in the fragment
                // Clear the image view using the binding if available
                (fragment.view?.parent as? ViewGroup)?.let { parent ->
                    for (i in 0 until parent.childCount) {
                        val child = parent.getChildAt(i)
                        if (child is ImageView) {
                            child.setImageDrawable(null)
                        }
                    }
                }
            }
        }
        fragments.clear()
        
        // Help garbage collector by running finalization
        System.runFinalization()
        System.gc()
    }
    
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
        // Remove reference to the fragment when it's destroyed
        fragments.remove(position)
    }
}
