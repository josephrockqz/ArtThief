package com.example.artthief.ui.rate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.artthief.R
import com.example.artthief.domain.ArtThiefArtwork
import com.example.artthief.utils.stringifyArtworkDimensions

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // TODO: change color of appbar icons
        // TODO: dynamically set appbar title

        return inflater.inflate(R.layout.fragment_artwork_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // TODO: change color of stars dynamically to reflect artwork's rating
        // TODO: add functionality to handle star clicks: update db rating and color

        view.findViewById<TextView>(R.id.tv_artworkArtist).text = artwork.artist
        view.findViewById<TextView>(R.id.tv_artworkMedia).text = artwork.media
        view.findViewById<TextView>(R.id.tv_artworkShowId).text = artwork.showID
        view.findViewById<TextView>(R.id.tv_artworkDimensions).text =
            stringifyArtworkDimensions(
                artwork.width,
                artwork.height
            )

        super.onViewCreated(view, savedInstanceState)
    }
}
