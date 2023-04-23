package com.example.artthief.ui.rate

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.artthief.R
import com.example.artthief.viewmodels.ArtworksViewModel

class ArtworkFragment() : Fragment() {

    private val viewModel: ArtworksViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.i("howdy", "made ittttt")
//        Log.i("howdy", artThiefId.toString())
        return inflater.inflate(R.layout.fragment_artwork, container, false)
    }
}
