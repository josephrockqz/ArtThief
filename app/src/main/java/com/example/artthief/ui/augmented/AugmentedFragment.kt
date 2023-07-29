package com.example.artthief.ui.augmented

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.artthief.R
import com.example.artthief.ar.kotlin.ArActivity
import com.example.artthief.databinding.FragmentAugmentedBinding
import com.example.artthief.viewmodels.ArtworksViewModel
import com.google.ar.core.ArCoreApk
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

        val arAvailability = ArCoreApk.getInstance().checkAvailability(context)
        if (arAvailability.isSupported) {
            binding.tvAugmentedText.text = activity?.getString(R.string.augmented_supported_visualize_text)

            binding.flAugmentedLaunch.visibility = View.VISIBLE
            binding.flAugmentedLaunch.isEnabled = true

            viewModel.highestRatedArtworkUrl.observe(viewLifecycleOwner) { artwork ->
                binding.bAugmentedLaunch.setOnClickListener {
                    val intent = Intent(activity, ArActivity::class.java)
                    intent.putExtra("artwork_image_url", artwork.image_large)
                    activity?.startActivity(intent)
                }
            }
        } else {
            binding.tvAugmentedText.text = activity?.getString(R.string.augmented_unsupported_visualize_text)

            binding.flAugmentedLaunch.visibility = View.GONE
            binding.flAugmentedLaunch.isEnabled = false
        }

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
