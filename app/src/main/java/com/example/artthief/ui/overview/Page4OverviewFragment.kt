package com.example.artthief.ui.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.artthief.R
import com.example.artthief.viewmodels.ArtworksViewModel
import com.squareup.picasso.Picasso

class Page4OverviewFragment : Fragment() {

    // TODO: display artwork image on top of easel correctly
    private val viewModel: ArtworksViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_overview_page4, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageUrlHighestRatedArtwork: String = viewModel.getHighestRatedArtworkImageUrl()

        if (imageUrlHighestRatedArtwork != "") {
            view.findViewById<ImageView>(R.id.iv_highestRatedArtwork)?.let {
                Picasso
                    .get()
//            .load(imageUrlHighestRatedArtwork[0])
                    .load("https://upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Android_robot.svg/1745px-Android_robot.svg.png")
                    .into(it)
            }
        }
    }
}
