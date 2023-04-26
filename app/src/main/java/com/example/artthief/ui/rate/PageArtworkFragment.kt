package com.example.artthief.ui.rate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.artthief.R
import com.example.artthief.domain.ArtThiefArtwork

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
        view.findViewById<TextView>(R.id.tv_artworkTitle).text = artwork.title

        super.onViewCreated(view, savedInstanceState)
    }
}
