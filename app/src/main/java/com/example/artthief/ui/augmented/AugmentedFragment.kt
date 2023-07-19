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

        // TODO: change text on wall when someone is unable to use AR mode
        // TODO: change button to install when someone is unable to use AR mode
        // TODO: have on click listener launch augmented activity (when AR is available on device)

        viewModel.highestRatedArtworkUrl.observe(viewLifecycleOwner) {
            if (it.image_small != String()) {
                Picasso
                    .get()
                    .load(it.image_small)
                    .into(binding.ivAugmentedHighestRatedArtwork)
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
