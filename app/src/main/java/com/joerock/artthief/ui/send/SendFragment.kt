package com.joerock.artthief.ui.send

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.joerock.artthief.R
import com.joerock.artthief.databinding.FragmentSendBinding
import com.joerock.artthief.domain.ArtThiefArtwork
import com.joerock.artthief.network.NetworkArtworkListPostResponse
import com.joerock.artthief.network.NetworkArtworkPreferenceList
import com.joerock.artthief.utils.formatListSubmissionDate
import com.joerock.artthief.viewmodels.ArtworksViewModel

class SendFragment : Fragment() {

    private val viewModel: ArtworksViewModel by activityViewModels()

    private var _binding: FragmentSendBinding? = null
    private val binding
        get() = _binding!!

    private val sharedPreferences by lazy {
        requireActivity().getPreferences(Context.MODE_PRIVATE)
    }

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

        val lastListSubmissionText = sharedPreferences.getString("last_list_submission_text", "")!!
        if (lastListSubmissionText != String()) {
            binding.tvLastListSubmissionText.text = lastListSubmissionText
        }

        binding.etInputText.setText(sharedPreferences.getString("code_name", ""))
        binding.etInputText.doOnTextChanged { text, _, _, _ ->
            with (sharedPreferences.edit()) {
                putString("code_name", text.toString())
                apply()
            }
        }

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

                            setMostRecentListSubmissionMetadata(responseBody)
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

    private fun setMostRecentListSubmissionMetadata(responseBody: NetworkArtworkListPostResponse) {
        val date = formatListSubmissionDate()
        val lastListSubmissionText = "Last sent for ${responseBody.message} on $date"
        with (sharedPreferences.edit()) {
            putString("last_list_submission_text", lastListSubmissionText)
            apply()
        }
        binding.tvLastListSubmissionText.text = lastListSubmissionText
    }
}
