package com.example.artthief.ui.rate

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.SearchView
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
        super.onViewCreated(view, savedInstanceState)

//        val searchView = getView()?.findViewById<SearchView>(R.id.mi_search)
//        searchView?.color

        rvList = view.findViewById(R.id.art_view)
        rvList.layoutManager = LinearLayoutManager(activity)
        rvList.adapter = RateArtAdapter()
    }
}
