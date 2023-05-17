package com.example.artthief.ui.augmented

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.artthief.R
import com.example.artthief.viewmodels.ArtworksViewModel
import com.squareup.picasso.Picasso

class AugmentedFragment : Fragment() {

    private val viewModel: ArtworksViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_augmented, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: rename all it1, it2 etc. usages to be more descriptive
        viewModel.highestRatedArtworkUrl.observe(viewLifecycleOwner) { it1 ->
            if (it1 != "") {
                view.findViewById<ImageView>(R.id.iv_augmentedHighestRatedArtwork)?.let { it2 ->
                    Picasso
                        .get()
                        .load(it1)
                        .into(it2)
                }
            }
        }
    }
}
