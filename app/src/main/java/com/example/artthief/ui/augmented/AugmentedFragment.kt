package com.example.artthief.ui.augmented

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.artthief.R

class AugmentedFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val currentDestination = activity?.findNavController(R.id.nav_host_fragment_activity_main)?.currentDestination
        val backQueue = activity?.findNavController(R.id.nav_host_fragment_activity_main)?.backQueue
        Log.i("flavor - current destination", currentDestination.toString())
        Log.i("flavor - back queue", backQueue.toString())

        return inflater.inflate(R.layout.fragment_augmented, container, false)
    }
}
