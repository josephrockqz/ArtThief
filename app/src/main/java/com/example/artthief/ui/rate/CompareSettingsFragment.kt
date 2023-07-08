package com.example.artthief.ui.rate

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.artthief.R
import com.example.artthief.databinding.FragmentCompareSettingsBinding
import com.example.artthief.viewmodels.ArtworksViewModel

class CompareSettingsFragment : Fragment() {

    private var _binding: FragmentCompareSettingsBinding? = null
    private val binding
        get() = _binding!!

    private val sharedPreferences by lazy {
        requireActivity().getPreferences(Context.MODE_PRIVATE)
    }
    private val toolbar by lazy { binding.toolbarCompareSettingsTop }

    private val viewModel: ArtworksViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCompareSettingsBinding.inflate(
            inflater,
            container,
            false
        )

        toolbar[0].setOnClickListener {
            activity
                ?.findNavController(R.id.nav_host_fragment_activity_main)
                ?.navigate(R.id.action_compareSettingsToCompare)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}