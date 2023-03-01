package com.example.artthiefdemo.ui.rate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.artthiefdemo.databinding.FragmentRateBinding

class RateFragment : Fragment() {

    private var _binding: FragmentRateBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rateViewModel =
            ViewModelProvider(this).get(RateViewModel::class.java)

        _binding = FragmentRateBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textRate
        rateViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}