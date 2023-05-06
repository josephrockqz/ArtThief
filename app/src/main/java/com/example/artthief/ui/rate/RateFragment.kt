package com.example.artthief.ui.rate

import android.os.Bundle
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
import com.example.artthief.domain.ArtThiefArtwork
import com.example.artthief.ui.rate.adapter.RatingSectionAdapter
import com.example.artthief.ui.rate.data.RecyclerViewSection
import com.example.artthief.viewmodels.ArtworksViewModel
import com.google.android.material.appbar.MaterialToolbar

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
        // TODO: delete ArtworkRatingAdapter use when ready
        viewModelAdapter = ArtworkRatingAdapter(
            object: ArtworkRatingAdapter.ArtworkClickListener {
                override fun onArtworkClicked(position: Int, view: View) {
                    showArtworkFragment(position)
                }
            }
        )

        /**
         * sort artworks and assign to adapters
         */
        viewModel.artworkList.observe(viewLifecycleOwner) { artworks ->
            artworks?.apply {
                // TODO: add conditional logic for what type of adapter is in use
                val sortedArtworks = artworks.sortedByDescending { it.rating }
                viewModel.setSortedArtworkList(sortedArtworks)
                viewModelAdapter.artworks = sortedArtworks

                /**
                 * Configure artwork rating adapter with sections
                 */
                val sections = mutableListOf<RecyclerViewSection>()
                val fiveStarArtworks = mutableListOf<ArtThiefArtwork>()
                val fourStarArtworks = mutableListOf<ArtThiefArtwork>()
                val threeStarArtworks = mutableListOf<ArtThiefArtwork>()
                val twoStarArtworks = mutableListOf<ArtThiefArtwork>()
                val oneStarArtworks = mutableListOf<ArtThiefArtwork>()
                val unratedArtworks = mutableListOf<ArtThiefArtwork>()

                sortedArtworks.forEach {
                    when (it.rating) {
                        5 -> fiveStarArtworks.add(it)
                        4 -> fourStarArtworks.add(it)
                        3 -> threeStarArtworks.add(it)
                        2 -> twoStarArtworks.add(it)
                        1 -> oneStarArtworks.add(it)
                        0 -> unratedArtworks.add(it)
                    }
                }

                if (fiveStarArtworks.isNotEmpty()) {
                    sections.add(
                        RecyclerViewSection(
                            "5 stars",
                            fiveStarArtworks
                        )
                    )
                }
                if (fourStarArtworks.isNotEmpty()) {
                    sections.add(
                        RecyclerViewSection(
                            "4 stars",
                            fourStarArtworks
                        )
                    )
                }
                if (threeStarArtworks.isNotEmpty()) {
                    sections.add(
                        RecyclerViewSection(
                            "3 stars",
                            threeStarArtworks
                        )
                    )
                }
                if (twoStarArtworks.isNotEmpty()) {
                    sections.add(
                        RecyclerViewSection(
                            "2 stars",
                            twoStarArtworks
                        )
                    )
                }
                if (oneStarArtworks.isNotEmpty()) {
                    sections.add(
                        RecyclerViewSection(
                            "1 stars",
                            oneStarArtworks
                        )
                    )
                }
                if (unratedArtworks.isNotEmpty()) {
                    sections.add(
                        RecyclerViewSection(
                            "Unrated",
                            unratedArtworks
                        )
                    )
                }

                binding
                    .root
                    .findViewById<RecyclerView>(R.id.rv_rateFragment)
                    .apply {
                        hasFixedSize() // TODO: change to false?
                        layoutManager = LinearLayoutManager(context)
//                        adapter = viewModelAdapter
                        val ratingSectionAdapter = RatingSectionAdapter(
                            context = context,
                            sections = sections
                        )
                        adapter = ratingSectionAdapter
                    }
            }
        }

        // Observer for the network error.
        viewModel
            .eventNetworkError
            .observe(viewLifecycleOwner, Observer<Boolean> { isNetworkError ->
                if (isNetworkError) onNetworkError()
            })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // make title centered
        val toolbar = view.findViewById<MaterialToolbar>(R.id.rateFragmentAppBar)
        toolbar.isTitleCentered = true
    }

    /**
     * Method for displaying a Toast error message for network errors.
     */
    private fun onNetworkError() {
        if(!viewModel.isNetworkErrorShown.value!!) {
            Toast
                .makeText(activity, "Network Error", Toast.LENGTH_LONG)
                .show()
            viewModel.onNetworkErrorShown()
        }
    }

    fun showArtworkFragment(position: Int) {
        viewModel.currentArtworkIndex = position
        activity
            ?.findNavController(R.id.nav_host_fragment_activity_main)
            ?.navigate(R.id.action_rateToArtwork)
    }
}
