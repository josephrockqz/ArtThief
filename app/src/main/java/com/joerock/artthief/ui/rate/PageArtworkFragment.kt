package com.joerock.artthief.ui.rate

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.google.android.material.appbar.MaterialToolbar
import com.joerock.artthief.R
import com.joerock.artthief.databinding.FragmentArtworkPageBinding
import com.joerock.artthief.domain.ArtThiefArtwork
import com.joerock.artthief.domain.defaultArtThiefArtwork
import com.joerock.artthief.utils.getShowIdDisplayValue
import com.joerock.artthief.viewmodels.ArtworksViewModel
import com.squareup.picasso.Callback
import com.squareup.picasso.MemoryPolicy
import com.joerock.artthief.ui.rate.ArtworkFragment
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.lang.Exception

@SuppressLint("UseCompatLoadingForDrawables")
class PageArtworkFragment(
    private val artwork: ArtThiefArtwork = defaultArtThiefArtwork
) : Fragment() {

    private var _binding: FragmentArtworkPageBinding? = null
    private val binding
        get() = _binding!!

    private val sharedPreferences by lazy {
        requireActivity().getPreferences(Context.MODE_PRIVATE)
    }
    private val viewModel: ArtworksViewModel by activityViewModels()

    private val starFilledDrawable: Drawable by lazy {
        ContextCompat.getDrawable(requireContext(), R.drawable.ic_rate_sharp_24dp)!!
    }
    private val starUnfilledDrawable: Drawable by lazy {
        ContextCompat.getDrawable(requireContext(), R.drawable.ic_rate_sharp_unfilled_24dp)!!
    }

    private lateinit var star1: ImageView
    private lateinit var star2: ImageView
    private lateinit var star3: ImageView
    private lateinit var star4: ImageView
    private lateinit var star5: ImageView
    
    private var currentImageRequest: com.squareup.picasso.RequestCreator? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentArtworkPageBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    private fun logMemoryUsage(tag: String) {
        val runtime = Runtime.getRuntime()
        val maxMemory = runtime.maxMemory() / (1024 * 1024)
        val usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
        Log.d("MemoryUsage", "PageArtwork - $tag - Used: ${usedMemory}MB / Max: ${maxMemory}MB")
    }

    private var imageTarget: Target? = null
    
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        logMemoryUsage("onViewCreated start")
        
        // Clear any previous image to prevent memory leaks
        clearImageResources()
        
        // Delay the navigation logic to ensure the view is fully created
        view.post {
            if (artwork.showID == String()) {
                activity
                    ?.findNavController(R.id.nav_host_fragment_activity_main)
                    ?.popBackStack(R.id.navigation_rate, false)
            } else {
                loadArtworkImage()
            }
        }
                
                // Only load if the view is still attached
                if (isAdded && !isDetached) {
                    currentImageRequest?.into(binding.ivArtworkPageImage)
                }
            }
        }

        // Get the parent fragment's ViewPager safely
        val currentViewPagerIndex = (parentFragment as? ArtworkFragment)?.viewPager?.currentItem ?: 0
        viewModel.currentArtworkIndex = currentViewPagerIndex!!
        val currentArtworkTitle = when (
            sharedPreferences.getString("rv_list_order", "rating")
        ) {
            "rating" -> viewModel.artworkListByRating[currentViewPagerIndex].title
            "show_id" -> viewModel.artworkListByShowId[currentViewPagerIndex].title
            else -> viewModel.artworkListByArtist[currentViewPagerIndex].title
        }
        parentFragment
            ?.view
            ?.findViewById<MaterialToolbar>(R.id.artworkFragmentAppBar)
            ?.title = currentArtworkTitle

        binding
            .tvArtworkArtist
            .text = artwork.artist
        binding
            .tvArtworkMedia
            .text = artwork.media
        binding
            .tvArtworkDimensions
            .text = artwork.dimensions
        binding
            .tvArtworkShowId
            .text = getShowIdDisplayValue(artwork.showID)

        star1 = binding.ivArtworkPageStar1
        star1.setOnClickListener { handleStarClick(1) }
        star2 = binding.ivArtworkPageStar2
        star2.setOnClickListener { handleStarClick(2) }
        star3 = binding.ivArtworkPageStar3
        star3.setOnClickListener { handleStarClick(3) }
        star4 = binding.ivArtworkPageStar4
        star4.setOnClickListener { handleStarClick(4) }
        star5 = binding.ivArtworkPageStar5
        star5.setOnClickListener { handleStarClick(5) }
        setStarDrawables(artwork.rating)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun loadArtworkImage() {
        try {
            // Clear previous resources
            clearImageResources()
            
            // Create a target to handle the loaded image
            imageTarget = object : Target {
                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    logMemoryUsage("Bitmap loaded")
                    bitmap?.let {
                        binding.ivArtworkPageImage.setImageBitmap(it)
                        // Clear the reference to allow GC
                        it.prepareToDraw()
                    }
                }

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                    logMemoryUsage("Bitmap load failed: ${e?.message}")
                    binding.ivArtworkPageImage.setImageResource(R.drawable.ic_outline_circle_24dp)
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    binding.ivArtworkPageImage.setImageResource(R.drawable.ic_loading_24dp)
                }
            }
            
            // Configure Picasso for memory-efficient loading
            Picasso.get()
                .load(artwork.image_large)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .config(Bitmap.Config.RGB_565)
                .fit()
                .centerInside()
                .onlyScaleDown()
                .into(imageTarget as Target)
                
        } catch (e: Exception) {
            logMemoryUsage("Error loading image: ${e.message}")
            binding.ivArtworkPageImage.setImageResource(R.drawable.ic_outline_circle_24dp)
        }
    }
    
    private fun clearImageResources() {
        try {
            // Clear the current image
            binding.ivArtworkPageImage.setImageDrawable(null)
            
            // Cancel any pending requests
            Picasso.get().cancelRequest(binding.ivArtworkPageImage)
            
            // Clear the target to prevent memory leaks
            imageTarget?.let { target ->
                try {
                    // Clear the target reference
                    Picasso.get().cancelRequest(target)
                } catch (e: Exception) {
                    Log.e("PageArtworkFragment", "Error clearing image target", e)
                }
            }
            imageTarget = null
            
            // Clear the current request
            currentImageRequest = null
            
            // Force a GC to clean up any remaining bitmaps
            System.gc()
            System.runFinalization()
            
        } catch (e: Exception) {
            Log.e("PageArtworkFragment", "Error clearing image resources", e)
        }
    }
    
    override fun onDestroyView() {
        logMemoryUsage("onDestroyView start")
        
        // Clear all image resources
        clearImageResources()
        
        // Clear other resources
        _binding = null
        
        logMemoryUsage("onDestroyView end")
        super.onDestroyView()
    }

    private fun handleStarClick(rating: Int) {
        val oldRating = artwork.rating
        if (rating == artwork.rating) {
            artwork.rating = 0
            setStarDrawables(0)
            viewModel.updateArtworkRatings(
                artwork,
                0,
                oldRating
            )
        } else {
            artwork.rating = rating
            setStarDrawables(rating)
            viewModel.updateArtworkRatings(
                artwork,
                rating,
                oldRating`
            )
            updateSectionSortedStatus(rating)
        }
        viewModel.artworksLive.observe(viewLifecycleOwner) { artworks ->
            viewModel.setArtworkList(artworks)
        }
    }

    private fun setStarDrawables(rating: Int) {
        if (rating > 0) star1.setImageDrawable(starFilledDrawable) else star1.setImageDrawable(starUnfilledDrawable)
        if (rating > 1) star2.setImageDrawable(starFilledDrawable) else star2.setImageDrawable(starUnfilledDrawable)
        if (rating > 2) star3.setImageDrawable(starFilledDrawable) else star3.setImageDrawable(starUnfilledDrawable)
        if (rating > 3) star4.setImageDrawable(starFilledDrawable) else star4.setImageDrawable(starUnfilledDrawable)
        if (rating > 4) star5.setImageDrawable(starFilledDrawable) else star5.setImageDrawable(starUnfilledDrawable)
    }

    private fun updateSectionSortedStatus(rating: Int) {
        when (rating) {
            1 -> {
                with (sharedPreferences.edit()) {
                    putBoolean("one_stars_sorted", false)
                    apply()
                }
            }
            2 -> {
                with (sharedPreferences.edit()) {
                    putBoolean("two_stars_sorted", false)
                    apply()
                }
            }
            3 -> {
                with (sharedPreferences.edit()) {
                    putBoolean("three_stars_sorted", false)
                    apply()
                }
            }
            4 -> {
                with (sharedPreferences.edit()) {
                    putBoolean("four_stars_sorted", false)
                    apply()
                }
            }
            5 -> {
                with (sharedPreferences.edit()) {
                    putBoolean("five_stars_sorted", false)
                    apply()
                }
            }
        }
    }
}
