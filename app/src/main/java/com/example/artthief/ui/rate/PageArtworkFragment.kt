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
    private val artwork: ArtThiefArtwork
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_artwork_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // TODO: dynamically set appbar title, picture, rating, title, etc.
        view.findViewById<TextView>(R.id.tv_artworkArtist).text = artwork.artist
        view.findViewById<TextView>(R.id.tv_artworkMedia).text = artwork.media
        view.findViewById<TextView>(R.id.tv_artworkShowId).text = artwork.showID

//        val width = artwork.width.toString()
//        val height = artwork.height.toString()
//        val dimensions = "$width\" by $height\""
//        view.findViewById<TextView>(R.id.tv_artworkDimensions).text = dimensions
        view.findViewById<TextView>(R.id.tv_artworkDimensions).text =
            stringifyArtworkDimensions(
                artwork.width,
                artwork.height
            )

        super.onViewCreated(view, savedInstanceState)
    }
}
