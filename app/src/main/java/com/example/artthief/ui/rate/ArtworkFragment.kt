package com.example.artthief.ui.rate

import android.content.Context
import android.content.Intent
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
import com.example.artthief.ar.kotlin.ArActivity
import com.example.artthief.databinding.FragmentArtworkBinding
import com.example.artthief.ui.rate.adapter.ArtworkPagerAdapter
import com.example.artthief.viewmodels.ArtworksViewModel
import com.google.ar.core.ArCoreApk

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

    private var artworkImageUrlForAugmented = ""

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

        configurePagerAdapter()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        configureToolbar()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun configurePagerAdapter() {

        artworkPagerAdapter = ArtworkPagerAdapter(childFragmentManager)
        viewPager = binding.pagerArtwork
        viewPager.adapter = artworkPagerAdapter

        val artworksRvListOrder = when (
            sharedPreferences.getString("rv_list_order", "rating")
        ) {
            "rating" -> viewModel.artworkListByRating
            "show_id" -> viewModel.artworkListByShowId
            else -> viewModel.artworkListByArtist
        }
//        artworkImageUrlForAugmented = artworksRvListOrder[viewModel.currentArtworkIndex].image_large
        artworkPagerAdapter.artworks = artworksRvListOrder
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
                    val artworkPageSelected = when (
                        sharedPreferences.getString("rv_list_order", "rating")
                    ) {
                        "rating" -> viewModel.artworkListByRating[position]
                        "show_id" -> viewModel.artworkListByShowId[position]
                        else -> viewModel.artworkListByArtist[position]
                    }
                    binding
                        .artworkFragmentAppBar
                        .title = artworkPageSelected.title
                    artworkImageUrlForAugmented = artworkPageSelected.image_large
                }
                override fun onPageScrollStateChanged(state: Int) {
                    // No-Op
                }
            }
        )
    }

    private fun configureToolbar() {

        val toolbar = binding.artworkFragmentAppBar
        toolbar.isTitleCentered = true
        toolbar[1].setOnClickListener {
            activity
                ?.findNavController(R.id.nav_host_fragment_activity_main)
                ?.navigate(R.id.action_artworkToRate)
        }

        val availability = ArCoreApk.getInstance().checkAvailability(context)
        if (availability.isSupported) {
            toolbar[2].visibility = View.VISIBLE
            toolbar[2].isEnabled = true

            toolbar.menu[0].setOnMenuItemClickListener {
                val intent = Intent(activity, ArActivity::class.java)
                intent.putExtra("artwork_image_url", artworkImageUrlForAugmented)
                activity?.startActivity(intent)
                true
            }
        } else {
            toolbar[2].visibility = View.INVISIBLE
            toolbar[2].isEnabled = false
        }
    }
}
