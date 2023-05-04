package com.example.artthief.ui.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.artthief.R
import com.example.artthief.viewmodels.ArtworksViewModel

class Page4OverviewFragment : Fragment() {

    // TODO: use view model to retrieve highest rated artwork
    // TODO: display highest rated artwork image in this fragment on easel
    private val viewModel: ArtworksViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_overview_page4, container, false)
    }
}
