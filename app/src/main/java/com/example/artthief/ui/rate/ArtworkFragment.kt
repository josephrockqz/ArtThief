package com.example.artthief.ui.rate

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.artthief.R
import com.example.artthief.viewmodels.ArtworksViewModel

class ArtworkFragment : Fragment() {

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val navigationIcon = view.findViewById<ViewGroup>(R.id.topAppBarArtwork)[1]
        navigationIcon.setOnClickListener {
            view.findNavController().navigate(R.id.action_artworkToRate)
        }

        // TODO: have on click listener launch augmented activity
        view.findViewById<View>(R.id.mi_augmented).setOnClickListener {
            Log.i("howdy", "augmented item clicked")
        }

        super.onViewCreated(view, savedInstanceState)
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        Log.i("howdy", "on options item selected")
//        when (item.itemId) {
//            R.id.mi_augmented -> Log.i("howdy", "augmented menu item")
//        }
//
//        return super.onOptionsItemSelected(item)
//    }
}
