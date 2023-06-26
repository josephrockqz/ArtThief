package com.example.artthief.ui.rate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.artthief.databinding.FragmentCompareBinding
import com.example.artthief.viewmodels.ArtworksViewModel

class CompareFragment : Fragment() {

    private var _binding: FragmentCompareBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: ArtworksViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCompareBinding.inflate(
            inflater,
            container,
            false
        )

//        toolbar[1].setOnClickListener {
//            activity
//                ?.findNavController(R.id.nav_host_fragment_activity_main)
//                ?.navigate(R.id.action_artworkToRate)
//        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}