package com.example.artthief.ui.send

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.artthief.databinding.FragmentSendBinding

class SendFragment : Fragment() {

    private var _binding: FragmentSendBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val rateViewModel =
            ViewModelProvider(this).get(SendViewModel::class.java)

        _binding = FragmentSendBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textSend
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