package com.joerock.artthief.ui.rate

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.joerock.artthief.R
import com.joerock.artthief.ar.kotlin.ArActivity
import com.joerock.artthief.databinding.FragmentArtworkBinding
import com.joerock.artthief.ui.rate.adapter.ArtworkPagerAdapter
import android.util.Log
import com.joerock.artthief.viewmodels.ArtworksViewModel
import com.google.ar.core.ArCoreApk
import java.lang.Runtime

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

        overrideOnBackPressed()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        configureToolbar()
    }

    override fun onDestroyView() {
        logMemoryUsage("Before onDestroyView cleanup")
        
        // Clear ViewPager and adapter to prevent memory leaks
        viewPager?.apply {
            // Remove all views and clear any references
            removeAllViews()
            adapter = null
        }
        
        // Clear fragments and their views
        artworkPagerAdapter.clearFragments()
        
        // Clear any large bitmaps from memory
        System.runFinalization()
        System.gc()
        
        _binding = null
        logMemoryUsage("After onDestroyView cleanup")
        super.onDestroyView()
    }

    private fun logMemoryUsage(tag: String) {
        val runtime = Runtime.getRuntime()
        val maxMemory = runtime.maxMemory() / (1024 * 1024)
        val usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
        Log.d("MemoryUsage", "$tag - Used: ${usedMemory}MB / Max: ${maxMemory}MB")
    }

    private fun configurePagerAdapter() {
        logMemoryUsage("Before PagerAdapter init")
        
        // Clear any existing adapter and views
        binding.pagerArtwork.adapter = null
        
        artworkPagerAdapter = ArtworkPagerAdapter(childFragmentManager)
        viewPager = binding.pagerArtwork.apply {
            // Set a ViewTreeObserver to clean up when the view is detached
            addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: View) {}
                override fun onViewDetachedFromWindow(v: View) {
                    // Clean up when view is detached
                    removeAllViews()
                    adapter = null
                }
            })
            
            // Set a page limit to reduce memory usage
            offscreenPageLimit = 1 // Only keep 1 page in memory on each side
            
            // Set the adapter last
            adapter = artworkPagerAdapter
        }
        
        logMemoryUsage("After PagerAdapter init")

        val artworksRvListOrder = when (
            sharedPreferences.getString("rv_list_order", "rating")
        ) {
            "rating" -> viewModel.artworkListByRating
            "show_id" -> viewModel.artworkListByShowId
            else -> viewModel.artworkListByArtist
        }
        artworkImageUrlForAugmented = artworksRvListOrder[viewModel.currentArtworkIndex].image_large
        artworkPagerAdapter.artworks = artworksRvListOrder
        // Set view pager's artwork based on what row (artwork) is pressed
        viewPager.currentItem = viewModel.currentArtworkIndex
        
        // Free up memory by clearing the previous adapter if it exists
        (viewPager.adapter as? ArtworkPagerAdapter)?.clearFragments()

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
            goBack()
        }

        val arAvailability = ArCoreApk.getInstance().checkAvailability(context)
        if (arAvailability.isSupported) {
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

    private fun goBack() {
        activity
            ?.findNavController(R.id.nav_host_fragment_activity_main)
            ?.navigate(R.id.action_artworkToRate)
    }

    private fun overrideOnBackPressed() {
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object: OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    goBack()
                }
            })
    }
}
