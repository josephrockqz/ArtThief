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
import com.example.artthief.databinding.FragmentArtworkBinding
import com.example.artthief.ui.rate.adapter.ArtworkPagerAdapter
import com.example.artthief.viewmodels.ArtworksViewModel

class ArtworkFragment : Fragment() {

    private var _binding: FragmentArtworkBinding? = null
    private val binding
        get() = _binding!!

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

        _binding = FragmentArtworkBinding.inflate(
            inflater,
            container,
            false
        )

        // configure pager adapter
        artworkPagerAdapter = ArtworkPagerAdapter(childFragmentManager)
        viewPager = binding.pagerArtwork
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
        viewPager.addOnPageChangeListener(
            object: OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    // No-Op
                }
                override fun onPageSelected(position: Int) {
                    binding
                        .artworkFragmentAppBar
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
            }
        )

        val toolbar = binding.artworkFragmentAppBar
        toolbar.isTitleCentered = true
        toolbar[1].setOnClickListener {
            activity
                ?.findNavController(R.id.nav_host_fragment_activity_main)
                ?.navigate(R.id.action_artworkToRate)
        }

        // TODO: have on click listener launch augmented activity
        toolbar.menu[0].setOnMenuItemClickListener {
            true
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
