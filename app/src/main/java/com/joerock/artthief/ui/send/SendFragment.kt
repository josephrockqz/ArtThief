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

    private var buttonHasBeenClicked = false

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
                    buttonHasBeenClicked = true
                    if (artworksNotUnratedDeletedTaken.isNotEmpty()) {
                        sendArtworksPostRequest(artworksNotUnratedDeletedTaken)
                    } else {
                        AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
                            .setTitle(R.string.send_fragment_empty_list_dialog_title)
                            .setMessage(R.string.send_fragment_empty_list_message)
                            .setPositiveButton(R.string.send_fragment_dialog_ok_button_text) { _, _ -> }
                            .show()
                    }
                }
            }
        }

        viewModel.sendArtworkListResponse.observe(viewLifecycleOwner) { responseBody ->
            if (buttonHasBeenClicked) {
                if (responseBody != null) {
                    when (responseBody.status) {
                        "success" -> {
                            AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
                                .setTitle(R.string.send_fragment_success_dialog_title)
                                .setMessage(resources.getString(R.string.send_fragment_success_dialog_message, responseBody.message))
                                .setPositiveButton(R.string.send_fragment_dialog_ok_button_text) { _, _ -> }
                                .show()
                        }
                        "error" -> {
                            AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
                                .setTitle(R.string.send_fragment_error_dialog_title)
                                .setMessage(responseBody.message)
                                .setPositiveButton(R.string.send_fragment_dialog_ok_button_text) { _, _ -> }
                                .show()
                            Log.e("SendFragment", "Send POST Request returned an error")
                        }
                        else -> {
                            Log.e("SendFragment", "Send POST Request returned a non-success and non-error message")
                        }
                    }
                } else {
                    AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
                        .setTitle(R.string.send_fragment_error_dialog_title)
                        .setMessage("Unable to send list")
                        .setPositiveButton(R.string.send_fragment_dialog_ok_button_text) { _, _ -> }
                        .show()
                    Log.e("SendFragment", "Send POST Request failed")
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
