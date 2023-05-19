package com.example.artthief.ui.send

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.artthief.databinding.FragmentSendBinding

// TODO: implement send functionality
class SendFragment : Fragment() {

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

        // TODO: change dynamically - do not include unrated, deleted, or taken artworks
        val numArtworks = 56
        val buttonText = "Send $numArtworks Artworks"
        binding
            .bSendButton
            .text = buttonText

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
