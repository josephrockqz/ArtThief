package com.joerock.artthief.ui.send

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.joerock.artthief.R
import com.joerock.artthief.databinding.FragmentSendBinding
import com.joerock.artthief.domain.ArtThiefArtwork
import com.joerock.artthief.network.NetworkArtworkPreferenceList
import com.joerock.artthief.viewmodels.ArtworksViewModel

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

        viewModel.sendArtworkListResponse.observe(viewLifecycleOwner) { responseBody ->
            // TODO: handle response from POST request and display appropriate popup (make sure popup doesn't show up unnecessarily when returning to tab)
            Log.i("responseBody", responseBody.toString())
            when (responseBody.status) {
                "success" -> {
                    val successMessage = "You successfully sent the list for " + responseBody.message + ". If you change your list you can send it again anytime before the drawing."
                    AlertDialog.Builder(requireContext())
                        .setTitle("Success!")
                        .setMessage(successMessage)
                        .setPositiveButton("Okay") { _, _ -> }
                        .show()
                }
                "error" -> {
                    AlertDialog.Builder(requireContext())
                        .setTitle(responseBody.status)
                        .setMessage(responseBody.message)
                        .setPositiveButton("Okay") { _, _ -> }
                        .show()
                }
                else -> {
                    Log.e("SendFragment", "Send POST Request returned a non-success and non-error message")
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
