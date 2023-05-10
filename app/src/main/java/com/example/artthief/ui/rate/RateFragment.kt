package com.example.artthief.ui.rate

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.get
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
import com.example.artthief.ui.rate.adapter.ArtworkAdapter
import com.example.artthief.ui.rate.adapter.RatingSectionAdapter
import com.example.artthief.ui.rate.data.ArtworkClickListener
import com.example.artthief.ui.rate.data.RecyclerViewSection
import com.example.artthief.viewmodels.ArtworksViewModel
import com.google.android.material.appbar.MaterialToolbar

// TODO: fix adapter bugs - ratings not changing (notifyDataSetChanged)
// TODO: fix icon images not changing on click (not always reproducible)
class RateFragment : Fragment() {

    private val sharedPreferences by lazy {
        requireActivity().getPreferences(Context.MODE_PRIVATE)
    }
    private val toolbar by lazy {
        requireView().findViewById<MaterialToolbar>(R.id.rateFragmentAppBar)
    }
    private val viewModel: ArtworksViewModel by activityViewModels()

    private val artworkRatingSections: MutableList<RecyclerViewSection> = mutableListOf()
    private var artworkListByShowId: List<ArtThiefArtwork> = emptyList()
    private var artworkListByArtist: List<ArtThiefArtwork> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Set the lifecycleOwner so DataBinding can observe LiveData
        val binding: FragmentRateBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_rate,
            container,
            false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        /**
         * sort artworks and assign to adapters
         */
        // TODO: fix lag on rate tab (slow every time it loads)
        viewModel.artworkList.observe(viewLifecycleOwner) { artworks ->
            artworks?.apply {

                when (sharedPreferences.getString("rv_display_type", "list")) {
                    "list" -> {
                        configureArtworksList(artworks)
                    }
                    "grid" -> {
                        configureArtworksGrid(artworks)
                    }
                }

                binding
                    .root
                    .findViewById<RecyclerView>(R.id.rv_rateFragment)
                    .apply {
                        layoutManager = LinearLayoutManager(context)
                        // TODO: break out different constructors into their own functions?
                        adapter = when (sharedPreferences.getString("rv_display_type", "list")) {
                            "list" -> when (sharedPreferences.getString("rv_list_order", "rating")) {
                                "show_id" -> ArtworkAdapter(
                                    artworkClickListener = object: ArtworkClickListener {
                                        override fun onArtworkClicked(sectionPosition: Int, view: View) {
                                            showArtworkFragment(sectionPosition)
                                        }
                                    },
                                    artworks = artworkListByShowId
                                )
                                "artist" -> ArtworkAdapter(
                                    artworkClickListener = object: ArtworkClickListener {
                                        override fun onArtworkClicked(sectionPosition: Int, view: View) {
                                            showArtworkFragment(sectionPosition)
                                        }
                                    },
                                    artworks = artworkListByArtist
                                )
                                else -> RatingSectionAdapter(
                                    context = context,
                                    artworkClickListener = object: ArtworkClickListener {
                                        override fun onArtworkClicked(sectionPosition: Int, view: View) {
                                            showArtworkFragment(sectionPosition)
                                        }
                                    },
                                    sections = artworkRatingSections
                                )
                            }
                            // TODO: implement grid artwork adapter
                            else -> RatingSectionAdapter(
                                context = context,
                                artworkClickListener = object: ArtworkClickListener {
                                    override fun onArtworkClicked(sectionPosition: Int, view: View) {
                                        showArtworkFragment(sectionPosition)
                                    }
                                },
                                sections = artworkRatingSections
                            )
                        }
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

        when (sharedPreferences.getString("rv_display_type", "list")) {
            "list" -> toolbar.menu[0].icon = resources.getDrawable(R.drawable.ic_list_teal_24dp)
            "grid" -> toolbar.menu[0].icon = resources.getDrawable(R.drawable.ic_grid_teal_24dp)
        }

        when (sharedPreferences.getString("rv_list_order", "rating")) {
            "rating" -> toolbar.menu[1].icon = resources.getDrawable(R.drawable.ic_rate_outline_teal_24dp)
            "show_id" -> toolbar.menu[1].icon = resources.getDrawable(R.drawable.ic_123_teal_24dp)
            "artist" -> toolbar.menu[1].icon = resources.getDrawable(R.drawable.ic_artist_teal_24dp)
        }

        when (sharedPreferences.getBoolean("show_deleted_artwork", false)) {
            false -> toolbar.menu[1].subMenu?.get(3)?.title = resources.getString(R.string.mi_show_deleted_art_title)
            true -> toolbar.menu[1].subMenu?.get(3)?.title = resources.getString(R.string.mi_hide_deleted_art_title)
        }

        when (sharedPreferences.getBoolean("show_taken_artwork", false)) {
            false -> toolbar.menu[1].subMenu?.get(4)?.title = resources.getString(R.string.mi_show_taken_art_title)
            true -> toolbar.menu[1].subMenu?.get(4)?.title = resources.getString(R.string.mi_hide_taken_art_title)
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun configureArtworksList(artworks: List<ArtThiefArtwork>) {
        when (sharedPreferences.getString("rv_list_order", "rating")) {
            "rating" -> configureArtworksByRating(artworks)
            "show_id" -> configureArtworksByShowId(artworks)
            "artist" -> configureArtworksByArtist(artworks)
        }
    }

    private fun configureArtworksGrid(artworks: List<ArtThiefArtwork>) {

    }

    private fun configureArtworksByRating(artworks: List<ArtThiefArtwork>) {

        // sort artworks by descending rating and update view model
        val sortedArtworks = artworks.sortedByDescending { it.rating }
        viewModel.setSortedArtworkListByRating(sortedArtworks)

        // partition artworks by rating then assign to rv's sections
        val artworkRatingMap = sortedArtworks.groupBy { it.rating }
        for (i in 5 downTo 0) {
            artworkRatingMap[i]?.let {
                artworkRatingSections.add(RecyclerViewSection(i, it))
            }
        }
    }

    private fun configureArtworksByShowId(artworks: List<ArtThiefArtwork>) {
        artworkListByShowId = artworks.sortedBy { it.showID.toInt() }
        viewModel.setSortedArtworkListByShowId(artworkListByShowId)
    }

    private fun configureArtworksByArtist(artworks: List<ArtThiefArtwork>) {
        artworkListByArtist = artworks.sortedBy { it.artist }
        viewModel.setSortedArtworkListByArtist(artworkListByArtist)
    }

    // TODO: get rid of onNetworkError functions? - check to see if ever reached
    private fun onNetworkError() {
        if (!viewModel.isNetworkErrorShown.value!!) {
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
