package com.example.artthief.ui.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.artthief.databinding.FragmentOverviewPage4Binding
import com.example.artthief.viewmodels.ArtworksViewModel
import com.squareup.picasso.Picasso

// TODO: display artwork image on top of easel correctly, decrease height of easel
class Page4OverviewFragment : Fragment() {

    private var _binding: FragmentOverviewPage4Binding? = null
    private val binding
        get() = _binding!!

    private val viewModel: ArtworksViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentOverviewPage4Binding.inflate(
            inflater,
            container,
            false
        )

        viewModel.highestRatedArtworkUrl.observe(viewLifecycleOwner) {
            if (it.image_large != String()) {
                Picasso
                    .get()
                    .load(it.image_large)
                    .into(binding.ivHighestRatedArtwork)
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
