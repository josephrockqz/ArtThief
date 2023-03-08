package com.example.artthief.ui.rate

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.artthief.R

class RateFragment : Fragment() {

    private lateinit var rvList: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_rate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rvList = view.findViewById(R.id.art_view)
        rvList.layoutManager = LinearLayoutManager(activity)
        rvList.adapter = RateArtAdapter()
    }
}
