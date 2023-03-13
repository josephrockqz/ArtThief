package com.example.artthief.ui.rate

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
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
        setHasOptionsMenu(true)

        return inflater.inflate(R.layout.fragment_rate, container, false)
    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        (activity as AppCompatActivity).supportActionBar!!.setShowHideAnimationEnabled(false)
        (activity as AppCompatActivity).supportActionBar!!.show()

        rvList = view.findViewById(R.id.art_view)
        rvList.layoutManager = LinearLayoutManager(activity)
        rvList.adapter = RateArtAdapter()

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.rate_menu, menu)
        super.onCreateOptionsMenu(menu,inflater)
    }
}
