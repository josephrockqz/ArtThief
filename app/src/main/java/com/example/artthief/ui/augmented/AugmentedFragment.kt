package com.example.artthief.ui.augmented

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.artthief.databinding.FragmentAugmentedBinding

class AugmentedFragment : Fragment() {

    private var _binding: FragmentAugmentedBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val augmentedViewModel =
            ViewModelProvider(this).get(AugmentedViewModel::class.java)

        _binding = FragmentAugmentedBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textAugmented
        augmentedViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
