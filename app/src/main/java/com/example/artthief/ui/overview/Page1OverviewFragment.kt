package com.example.artthief.ui.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.artthief.R

class Page1OverviewFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.overview_page1_fragment, container, false)
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        arguments?.takeIf { it.containsKey(ARG_OBJECT) }?.apply {
//            val textView: TextView = view.findViewById(android.R.id.text1)
//            textView.text = getInt(ARG_OBJECT).toString()
//        }
//    }
}
