package com.example.artthief.ui.rate

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.viewpager.widget.ViewPager
import com.example.artthief.R
import com.example.artthief.databinding.FragmentArtworkPageBinding
import com.example.artthief.domain.ArtThiefArtwork
import com.example.artthief.domain.asDatabaseModel
import com.example.artthief.domain.defaultArtThiefArtwork
import com.example.artthief.viewmodels.ArtworksViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.squareup.picasso.Picasso

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
        resources.getDrawable(R.drawable.ic_rate_sharp_24dp)
    }
    private val starUnfilledDrawable: Drawable by lazy {
        resources.getDrawable(R.drawable.ic_rate_sharp_unfilled_24dp)
    }

    private lateinit var star1: ImageView
    private lateinit var star2: ImageView
    private lateinit var star3: ImageView
    private lateinit var star4: ImageView
    private lateinit var star5: ImageView

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

        /**
         * navigate back to rate fragment if artwork fragment is being opened
         * directly from a different tab - not from the rate fragment
         */
        if (artwork.showID == String()) {
            activity
                ?.findNavController(R.id.nav_host_fragment_activity_main)
                ?.popBackStack(R.id.navigation_rate, false)
        } else {
            Picasso
                .get()
                .load(artwork.image_large)
                .into(binding.ivArtworkPageImage)
        }

        val currentViewPagerIndex = parentFragment
            ?.view
            ?.findViewById<ViewPager>(R.id.pager_artwork)
            ?.currentItem
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
            .text = artwork.showID

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

        return binding.root
    }

    private fun handleStarClick(rating: Int) {
        if (rating == artwork.rating) {
            artwork.rating = 0
            setStarDrawables(0)
            updateArtworkRatingDatabase(0)
        } else {
            artwork.rating = rating
            setStarDrawables(rating)
            updateArtworkRatingDatabase(rating)
        }
    }

    private fun setStarDrawables(rating: Int) {
        if (rating > 0) star1.setImageDrawable(starFilledDrawable) else star1.setImageDrawable(starUnfilledDrawable)
        if (rating > 1) star2.setImageDrawable(starFilledDrawable) else star2.setImageDrawable(starUnfilledDrawable)
        if (rating > 2) star3.setImageDrawable(starFilledDrawable) else star3.setImageDrawable(starUnfilledDrawable)
        if (rating > 3) star4.setImageDrawable(starFilledDrawable) else star4.setImageDrawable(starUnfilledDrawable)
        if (rating > 4) star5.setImageDrawable(starFilledDrawable) else star5.setImageDrawable(starUnfilledDrawable)
    }

    private fun updateArtworkRatingDatabase(rating: Int) {
        viewModel.updateArtwork(
            artwork
                .copy(rating = rating)
                .asDatabaseModel()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
