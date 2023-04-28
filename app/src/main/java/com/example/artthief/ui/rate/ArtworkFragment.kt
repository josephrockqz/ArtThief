package com.example.artthief.ui.rate

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.viewpager.widget.ViewPager
import com.example.artthief.R
import com.example.artthief.viewmodels.ArtworksViewModel
import com.google.android.material.appbar.MaterialToolbar

class ArtworkFragment : Fragment() {

    private val viewModel: ArtworksViewModel by activityViewModels()

    private lateinit var artworkPagerAdapter: ArtworkPagerAdapter
    private lateinit var viewPager: ViewPager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_artwork, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // configure pager adapter
        artworkPagerAdapter = ArtworkPagerAdapter(childFragmentManager)
        viewPager = view.findViewById(R.id.pager_artwork)
        viewPager.adapter = artworkPagerAdapter

        // sort artworks so that pages are in correct order
        viewModel.artworkList.observe(viewLifecycleOwner) { artworks ->
            artworks?.apply {
                // TODO: add conditional logic for what type of adapter is in use
                val sortedArtworks = artworks.sortedByDescending { it.stars }
                // TODO: add entries here that represent sections, i.e. stars = -1
                artworkPagerAdapter.artworks = sortedArtworks
                // Set view pager's artwork based on what row (artwork) is pressed
                viewPager.currentItem = viewModel.currentArtwork
            }
        }


        val toolbar = view.findViewById<MaterialToolbar>(R.id.artworkFragmentAppBar)
        // make title centered
        toolbar.isTitleCentered = true
        // back button on click listener
        toolbar[1].setOnClickListener {
            view.findNavController().navigate(R.id.action_artworkToRate)
        }

        // TODO: have on click listener launch augmented activity
        view.findViewById<View>(R.id.mi_augmented).setOnClickListener {
            Log.i("howdy", "augmented item clicked")
        }

        super.onViewCreated(view, savedInstanceState)
    }
}
