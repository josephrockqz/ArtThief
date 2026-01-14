package com.joerock.artthief.ui.rate

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.viewpager.widget.ViewPager
import com.joerock.artthief.R
import com.joerock.artthief.databinding.FragmentArtworkPageBinding
import com.joerock.artthief.domain.ArtThiefArtwork
import com.joerock.artthief.domain.defaultArtThiefArtwork
import com.joerock.artthief.viewmodels.ArtworksViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.joerock.artthief.utils.getShowIdDisplayValue
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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        /**
         * navigate back to rate fragment if artwork fragment is being opened
         * directly from a different tab - not from the rate fragment
         */
        // Delay the navigation logic to ensure the view is fully created
        view.post {
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

        setupArtistTextView()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
                oldRating
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

    private fun setupArtistTextView() {
        val artistTextView = binding.tvArtworkArtist

        artistTextView.text = artwork.artist
        artistTextView.setTextColor(resources.getColor(R.color.black, null))

        // Artist has a valid URL, make text green and add link icon
        if (!artwork.artistUrl.isNullOrEmpty()) {
            artistTextView.text = artwork.artist + " 🔗"
            artistTextView.setTextColor(resources.getColor(R.color.art_thief_primary, null))
            artistTextView.setOnClickListener {
                 openUrl(artwork.artistUrl)
            }
        } else {
            // No URL, remove click listener
            artistTextView.setOnClickListener(null)
        }
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
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
