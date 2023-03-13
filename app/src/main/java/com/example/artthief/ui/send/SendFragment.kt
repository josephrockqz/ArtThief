package com.example.artthief.ui.send

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.artthief.R
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
        setHasOptionsMenu(true)

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

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        (activity as AppCompatActivity).supportActionBar!!.setShowHideAnimationEnabled(false)
        (activity as AppCompatActivity).supportActionBar!!.show()

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.send_menu, menu)
        super.onCreateOptionsMenu(menu,inflater)
    }
}