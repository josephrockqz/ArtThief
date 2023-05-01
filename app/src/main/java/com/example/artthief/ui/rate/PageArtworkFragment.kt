package com.example.artthief.ui.rate

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.viewpager.widget.ViewPager
import com.example.artthief.R
import com.example.artthief.domain.ArtThiefArtwork
import com.example.artthief.domain.asDatabaseModel
import com.example.artthief.viewmodels.ArtworksViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.squareup.picasso.Picasso

// TODO: move default parameter to different file
@SuppressLint("UseCompatLoadingForDrawables")
class PageArtworkFragment(
    private val artwork: ArtThiefArtwork = ArtThiefArtwork(
        artThiefID = 0,
        showID = "",
        title = "",
        artist = "",
        media = "",
        image_large = "",
        image_small = "",
        width = 1.toFloat(),
        height = 1.toFloat(),
        taken = true,
        rating = 0 // 0 stars represents that it's unrated
    )
) : Fragment() {

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
        return inflater.inflate(R.layout.fragment_artwork_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        /**
         * navigate back to rate fragment if artwork fragment is being opened
         * directly from a different tab - not from the rate fragment
         */
        if (artwork.showID == "") {
            activity
                ?.findNavController(R.id.nav_host_fragment_activity_main)
                ?.popBackStack(R.id.navigation_rate, false)
        }

        /**
         * dynamically set artwork's image view source
         */
        val artworkPageImage = view.findViewById<ImageView>(R.id.iv_artworkPageImage)
        Picasso
            .get()
//            .load(artworks[i].image_small)
            .load("https://upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Android_robot.svg/1745px-Android_robot.svg.png")
            .into(artworkPageImage)

        /**
         * set app bar title to the current artwork's title
         */
        // TODO: fix bug where first artwork's title is wrong when swiped to
        val currentViewPagerIndex = parentFragment
            ?.view
            ?.findViewById<ViewPager>(R.id.pager_artwork)
            ?.currentItem
        Log.i("howdy", currentViewPagerIndex.toString())
        viewModel.artworkList.observe(viewLifecycleOwner) { artworks ->
            artworks?.apply {
                val sortedArtworks = artworks.sortedByDescending { it.rating }
                val currentArtworkTitle = sortedArtworks[currentViewPagerIndex!!].title
                parentFragment
                    ?.view
                    ?.findViewById<MaterialToolbar>(R.id.artworkFragmentAppBar)
                    ?.title = currentArtworkTitle
            }
        }

        /**
         * set artwork card information to current artwork's:
         * artist, media, dimensions, show ID
         */
        view.findViewById<TextView>(R.id.tv_artworkArtist).text = artwork.artist
        view.findViewById<TextView>(R.id.tv_artworkMedia).text = artwork.media
        view.findViewById<TextView>(R.id.tv_artworkDimensions).text = artwork.dimensions
        view.findViewById<TextView>(R.id.tv_artworkShowId).text = artwork.showID

        /**
         * Set star click listeners
         */
        star1 = view.findViewById(R.id.iv_artworkPageStar1)
        star1.setOnClickListener { handleStarClick(1) }
        star2 = view.findViewById(R.id.iv_artworkPageStar2)
        star2.setOnClickListener { handleStarClick(2) }
        star3 = view.findViewById(R.id.iv_artworkPageStar3)
        star3.setOnClickListener { handleStarClick(3) }
        star4 = view.findViewById(R.id.iv_artworkPageStar4)
        star4.setOnClickListener { handleStarClick(4) }
        star5 = view.findViewById(R.id.iv_artworkPageStar5)
        star5.setOnClickListener { handleStarClick(5) }
        setStarDrawables(artwork.rating)

        super.onViewCreated(view, savedInstanceState)
    }

    private fun handleStarClick(rating: Int) {
        if (rating == artwork.rating) {
            setStarDrawables(0)
            updateArtworkRatingDatabase(0)
        } else {
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

    // TODO: fix bug where changing artwork rating jumps around
    private fun updateArtworkRatingDatabase(rating: Int) {
        viewModel.updateArtworkRating(
            artwork
                .copy(rating = rating)
                .asDatabaseModel()
        )
    }
}
