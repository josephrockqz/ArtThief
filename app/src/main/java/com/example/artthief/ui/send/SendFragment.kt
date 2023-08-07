package com.example.artthief.ui.send

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.artthief.databinding.FragmentSendBinding
import com.example.artthief.domain.ArtThiefArtwork
import com.example.artthief.network.NetworkArtworkPreferenceList
import com.example.artthief.viewmodels.ArtworksViewModel

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

        binding.sendFragmentAppBar.isTitleCentered = true

        viewModel.artworkListByRatingLive.observe(viewLifecycleOwner) { artworks ->
            val artworksNotUnratedDeletedTaken = artworks.filter { artwork ->
                artwork.rating != 0 && !artwork.taken && !artwork.deleted
            }
            val numArtworks = artworksNotUnratedDeletedTaken.size
            val buttonText = "Send $numArtworks Artworks"
            binding.bSendButton.apply {
                text = buttonText
                setOnClickListener {
                    sendArtworksPostRequest(artworksNotUnratedDeletedTaken)
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun sendArtworksPostRequest(artworks: List<ArtThiefArtwork>) {
        val codeName = binding
            .etInputText
            .text
            .toString()
        val artworkIdsInPreferenceOrder = artworks.map {
            it.artThiefID
        }
        viewModel.sendArtworkList(
            codeName,
            NetworkArtworkPreferenceList(
                artworks = artworkIdsInPreferenceOrder
            )
        )
    }
}
