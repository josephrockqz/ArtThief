package com.example.artthief.ui.send

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.artthief.databinding.FragmentSendBinding
import com.example.artthief.domain.ArtThiefArtwork
import com.example.artthief.network.NetworkListData
import com.example.artthief.viewmodels.ArtworksViewModel

class SendFragment : Fragment() {

    companion object {
        private const val BASE_URL = "https://api.example.com"
    }

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

    // TODO: SEND - finish implementing
    private fun sendArtworksPostRequest(artworks: List<ArtThiefArtwork>) {
        val codeName = binding
            .etInputText
            .text
            .toString()
        Log.i("code name", codeName)
        val listData = NetworkListData(
            artworks = listOf(),
            codeName = codeName
        )
        viewModel.sendArtworkList(listData)
    }
}
