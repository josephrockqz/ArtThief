package com.example.artthief.ui.overview

import android.os.Bundle
import android.util.Log
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

        viewModel.highestRatedArtworkUrl.observe(viewLifecycleOwner) { it1 ->
            Log.i("howdy", it1)
            if (it1 != "") {
                view.findViewById<ImageView>(R.id.iv_highestRatedArtwork)?.let { it2 ->
                    Picasso
                        .get()
                        .load(it1)
                        .into(it2)
                }
            }
        }
    }
}
