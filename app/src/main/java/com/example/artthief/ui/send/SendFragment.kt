package com.example.artthief.ui.send

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.artthief.databinding.FragmentSendBinding
import com.example.artthief.viewmodels.ArtworksViewModel

// TODO: implement send functionality
class SendFragment : Fragment() {

    private val viewModel: ArtworksViewModel by activityViewModels()

    private var _binding: FragmentSendBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSendBinding.inflate(
            inflater,
            container,
            false
        )

        // make title centered
        binding
            .sendFragmentAppBar
            .isTitleCentered = true

        viewModel.artworkListByRatingLive.observe(viewLifecycleOwner) { artworks ->
            val artworksWithoutUnratedDeletedTaken = artworks.filter { artwork ->
                artwork.rating != 0 && !artwork.taken && !artwork.deleted
            }
            val numArtworks = artworksWithoutUnratedDeletedTaken.size
            val buttonText = "Send $numArtworks Artworks"
            binding
                .bSendButton
                .text = buttonText
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
