package com.example.artthief.ui.overview

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.artthief.R

class Page3OverviewFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_overview_page3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupHyperlink()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupHyperlink() {
        val linkTextView = requireView().findViewById<TextView>(R.id.tv_instructions_link)
        linkTextView.movementMethod = LinkMovementMethod.getInstance()
    }
}
