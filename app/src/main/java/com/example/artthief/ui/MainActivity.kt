package com.example.artthief.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.get
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.artthief.R
import com.example.artthief.databinding.ActivityMainBinding
import com.example.artthief.ui.rate.data.ListByOptions
import com.example.artthief.ui.rate.data.ViewByOptions
import com.example.artthief.viewmodels.ArtworksViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: ArtworksViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.refreshDataFromRepository()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_overview,
                R.id.navigation_rate,
                R.id.navigation_augmented,
                R.id.navigation_send
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    // TODO: implement these with shared preferences instead of view model
    fun displayList(item: MenuItem) {
        if (viewModel.artworkViewBySelection != ViewByOptions.LIST) {
            viewModel.setListBySelection(ViewByOptions.LIST)
            val toolbar = findViewById<Toolbar>(R.id.rateFragmentAppBar)
            toolbar.menu[0].icon = resources.getDrawable(R.drawable.ic_list_teal_24dp)
        }
    }

    fun displayGrid(item: MenuItem) {
        if (viewModel.artworkViewBySelection != ViewByOptions.GRID) {
            viewModel.setListBySelection(ViewByOptions.GRID)
            val toolbar = findViewById<Toolbar>(R.id.rateFragmentAppBar)
            toolbar.menu[0].icon = resources.getDrawable(R.drawable.ic_grid_teal_24dp)
        }
    }

    fun listByRatingListener(item: MenuItem) {
        if (viewModel.artworkListBySelection != ListByOptions.RATING) {
            viewModel.setListBySelection(ListByOptions.RATING)
            val toolbar = findViewById<Toolbar>(R.id.rateFragmentAppBar)
            toolbar.menu[1].icon = resources.getDrawable(R.drawable.ic_rate_outline_teal_24dp)
        }
    }

    fun listByShowIdListener(item: MenuItem) {
        if (viewModel.artworkListBySelection != ListByOptions.SHOW_ID) {
            viewModel.setListBySelection(ListByOptions.SHOW_ID)
            val toolbar = findViewById<Toolbar>(R.id.rateFragmentAppBar)
            toolbar.menu[1].icon = resources.getDrawable(R.drawable.ic_123_teal_24dp)
        }
    }

    fun listByArtistListener(item: MenuItem) {
        if (viewModel.artworkListBySelection != ListByOptions.ARTIST) {
            viewModel.setListBySelection(ListByOptions.ARTIST)
            val toolbar = findViewById<Toolbar>(R.id.rateFragmentAppBar)
            toolbar.menu[1].icon = resources.getDrawable(R.drawable.ic_artist_teal_24dp)
        }
    }

    fun showDeletedArtwork(item: MenuItem) {
        viewModel.setDeletedArtworksToggle()
        if (item.title == resources.getString(R.string.mi_show_deleted_art_title)) {
            item.title = resources.getString(R.string.mi_hide_deleted_art_title)
        } else {
            item.title = resources.getString(R.string.mi_show_deleted_art_title)
        }
    }

    fun showTakenArtwork(item: MenuItem) {
        viewModel.setTakenArtworksToggle()
        if (item.title == resources.getString(R.string.mi_show_taken_art_title)) {
            item.title = resources.getString(R.string.mi_hide_taken_art_title)
        } else {
            item.title = resources.getString(R.string.mi_show_taken_art_title)
        }
    }
}
