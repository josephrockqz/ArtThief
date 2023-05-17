package com.example.artthief.ui.augmented

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.artthief.databinding.FragmentAugmentedBinding
import com.example.artthief.viewmodels.ArtworksViewModel
import com.squareup.picasso.Picasso

class AugmentedFragment : Fragment() {

    private var _binding: FragmentAugmentedBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: ArtworksViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAugmentedBinding.inflate(
            inflater,
            container,
            false
        )

        viewModel.highestRatedArtworkUrl.observe(viewLifecycleOwner) { it1 ->
            Picasso
                .get()
                .load(it1)
                .into(binding.ivAugmentedHighestRatedArtwork)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
