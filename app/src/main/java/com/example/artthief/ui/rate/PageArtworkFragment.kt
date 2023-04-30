package com.example.artthief.ui.rate

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
import com.example.artthief.utils.stringifyArtworkDimensions
import com.example.artthief.viewmodels.ArtworksViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.squareup.picasso.Picasso

// TODO: move default parameter to different file
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
        stars = 0 // 0 stars represents that it's unrated
    )
) : Fragment() {

    private val viewModel: ArtworksViewModel by activityViewModels()

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

        // TODO: change color of stars dynamically to reflect artwork's rating
        // TODO: add functionality to handle star clicks: update db rating and color

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
        val viewPager = parentFragment
            ?.view
            ?.findViewById<ViewPager>(R.id.pager_artwork)
        val currentViewPagerIndex = viewPager?.currentItem
        val currentArtworkTitle = currentViewPagerIndex?.let {
            viewModel.artworkList.value?.get(it)?.title
        }
        parentFragment
            ?.view
            ?.findViewById<MaterialToolbar>(R.id.artworkFragmentAppBar)
            ?.title = currentArtworkTitle

        /**
         * set artwork card information to current artwork's:
         * artist, media, dimensions, show ID
         */
        view.findViewById<TextView>(R.id.tv_artworkArtist).text = artwork.artist
        view.findViewById<TextView>(R.id.tv_artworkMedia).text = artwork.media
        view.findViewById<TextView>(R.id.tv_artworkDimensions).text =
            stringifyArtworkDimensions(
                artwork.width,
                artwork.height
            )
        view.findViewById<TextView>(R.id.tv_artworkShowId).text = artwork.showID

        super.onViewCreated(view, savedInstanceState)
    }
}
