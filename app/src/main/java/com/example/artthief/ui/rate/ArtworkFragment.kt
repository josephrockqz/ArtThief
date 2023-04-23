package com.example.artthief.ui.rate

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.viewpager.widget.ViewPager
import com.example.artthief.R
import com.example.artthief.ui.overview.OverviewPagerAdapter
import com.example.artthief.viewmodels.ArtworksViewModel

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

        // back button on click listener
        val navigationIcon = view.findViewById<ViewGroup>(R.id.topAppBarArtwork)[1]
        navigationIcon.setOnClickListener {
            view.findNavController().navigate(R.id.action_artworkToRate)
        }

        // TODO: have on click listener launch augmented activity
        view.findViewById<View>(R.id.mi_augmented).setOnClickListener {
            Log.i("howdy", "augmented item clicked")
        }

        super.onViewCreated(view, savedInstanceState)
    }
}
