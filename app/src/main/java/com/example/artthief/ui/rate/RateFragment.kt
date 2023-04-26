package com.example.artthief.ui.rate

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.artthief.R
import com.example.artthief.databinding.FragmentRateBinding
import com.example.artthief.viewmodels.ArtworksViewModel

class RateFragment : Fragment() {

    private lateinit var viewModelAdapter: ArtworkRatingAdapter

    private val viewModel: ArtworksViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentRateBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_rate,
            container,
            false
        )

        // Set the lifecycleOwner so DataBinding can observe LiveData
        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = viewModel

        // TODO: dynamically assign adapter based on if what toggle is selected
        // TODO: will need to create ArtworkIdAdapter & ArtworkArtistAdapter
        viewModelAdapter = ArtworkRatingAdapter(
            object: ArtworkRatingAdapter.ArtworkClickListener {
                override fun onArtworkClicked(position: Int, view: View) {
                    showArtworkFragment(position, view)
                }
            }
        )

        binding.root.findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = viewModelAdapter
        }

        // Observer for the network error.
        viewModel.eventNetworkError.observe(viewLifecycleOwner, Observer<Boolean> { isNetworkError ->
            if (isNetworkError) onNetworkError()
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.artworkList.observe(viewLifecycleOwner) { artworks ->
            artworks?.apply {
                // TODO: add conditional logic for what type of adapter is in use
                val sortedArtworks = artworks.sortedByDescending { it.stars }
                // TODO: add entries here that represent sections, i.e. stars = -1
                viewModelAdapter.artworks = sortedArtworks
            }
        }
    }

    /**
     * Method for displaying a Toast error message for network errors.
     */
    private fun onNetworkError() {
        if(!viewModel.isNetworkErrorShown.value!!) {
            Toast.makeText(activity, "Network Error", Toast.LENGTH_LONG).show()
            viewModel.onNetworkErrorShown()
        }
    }

    fun showArtworkFragment(position: Int, view: View) {
        viewModel.currentArtwork = position
        view.findNavController().navigate(R.id.action_rateToArtwork)
    }
}
