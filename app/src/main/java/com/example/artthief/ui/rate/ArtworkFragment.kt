package com.example.artthief.ui.rate

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.example.artthief.R
import com.example.artthief.ui.rate.adapter.ArtworkPagerAdapter
import com.example.artthief.viewmodels.ArtworksViewModel
import com.google.android.material.appbar.MaterialToolbar

class ArtworkFragment : Fragment() {

    private val sharedPreferences by lazy {
        requireActivity().getPreferences(Context.MODE_PRIVATE)
    }
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

        artworkPagerAdapter.artworks = when (
            sharedPreferences.getString("rv_list_order", "rating")
        ) {
            "rating" -> viewModel.artworkListByRating
            "show_id" -> viewModel.artworkListByShowId
            else -> viewModel.artworkListByArtist
        }
        // Set view pager's artwork based on what row (artwork) is pressed
        viewPager.currentItem = viewModel.currentArtworkIndex

        /**
         * Add on page change listener to set the artwork fragment's app bar
         * title to the current artwork's title after page change
         * NOTE: app bar's title is set initially in [PageArtworkFragment]
         */
        viewPager.addOnPageChangeListener(object: OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                // No-Op
            }

            override fun onPageSelected(position: Int) {
                view
                    .findViewById<MaterialToolbar>(R.id.artworkFragmentAppBar)
                    .title = when (
                        sharedPreferences.getString("rv_list_order", "rating")
                    ) {
                        "rating" -> viewModel.artworkListByRating[position].title
                        "show_id" -> viewModel.artworkListByShowId[position].title
                        else -> viewModel.artworkListByArtist[position].title
                    }
            }

            override fun onPageScrollStateChanged(state: Int) {
                // No-Op
            }
        })

        val toolbar = view.findViewById<MaterialToolbar>(R.id.artworkFragmentAppBar)
        // make title centered
        toolbar.isTitleCentered = true
        // back button on click listener
        toolbar[1].setOnClickListener {
            view.findNavController().popBackStack()
        }

        // TODO: have on click listener launch augmented activity
        view.findViewById<View>(R.id.mi_augmented).setOnClickListener {

        }

        super.onViewCreated(view, savedInstanceState)
    }
}
