package com.example.artthief.ui

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.artthief.R
import com.example.artthief.databinding.ActivityMainBinding
import com.example.artthief.ui.rate.RateFragment
import com.example.artthief.viewmodels.ArtworksViewModel
import com.google.android.material.appbar.MaterialToolbar

class MainActivity : AppCompatActivity() {

    private val sharedPreferences by lazy {
        getPreferences(Context.MODE_PRIVATE)
    }
    private val toolbar by lazy {
        findViewById<MaterialToolbar>(R.id.rateFragmentAppBar)
    }
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

    fun displayList(item: MenuItem) {
        val currentDisplayType = sharedPreferences.getString("rv_display_type", "list")
        if (currentDisplayType != "list") {
            with (sharedPreferences.edit()) {
                putString("rv_display_type", "list")
                apply()
            }
            toolbar.menu[0].icon = resources.getDrawable(R.drawable.ic_list_teal_24dp)
            // TODO: changing to grid view should change the icon from filter to star
            refreshRateFragment()
        }
    }

    fun displayGrid(item: MenuItem) {
        val currentDisplayType = sharedPreferences.getString("rv_display_type", "list")
        if (currentDisplayType != "grid") {
            with (sharedPreferences.edit()) {
                putString("rv_display_type", "grid")
                apply()
            }
            toolbar.menu[0].icon = resources.getDrawable(R.drawable.ic_grid_teal_24dp)
            // TODO: changing to grid view should change the icon from star to filter
            refreshRateFragment()
        }
    }

    fun listByRatingListener(item: MenuItem) {
        val currentListOrder = sharedPreferences.getString("rv_list_order", "rating")
        if (currentListOrder != "rating") {
            with (sharedPreferences.edit()) {
                putString("rv_list_order", "rating")
                apply()
            }
            toolbar.menu[1].icon = resources.getDrawable(R.drawable.ic_rate_outline_teal_24dp)
            refreshRateFragment()
        }
    }

    fun listByShowIdListener(item: MenuItem) {
        val currentListOrder = sharedPreferences.getString("rv_list_order", "rating")
        if (currentListOrder != "show_id") {
            with (sharedPreferences.edit()) {
                putString("rv_list_order", "show_id")
                apply()
            }
            toolbar.menu[1].icon = resources.getDrawable(R.drawable.ic_123_teal_24dp)
            refreshRateFragment()
        }
    }

    fun listByArtistListener(item: MenuItem) {
        val currentListOrder = sharedPreferences.getString("rv_list_order", "rating")
        if (currentListOrder != "artist") {
            with (sharedPreferences.edit()) {
                putString("rv_list_order", "artist")
                apply()
            }
            toolbar.menu[1].icon = resources.getDrawable(R.drawable.ic_artist_teal_24dp)
            refreshRateFragment()
        }
    }

    fun showDeletedArtwork(item: MenuItem) {
        val showDeletedArtworkState = sharedPreferences.getBoolean("show_deleted_artwork", false)
        if (showDeletedArtworkState) {
            item.title = resources.getString(R.string.mi_show_deleted_art_title)
        } else {
            item.title = resources.getString(R.string.mi_hide_deleted_art_title)
        }
        with (sharedPreferences.edit()) {
            putBoolean("show_deleted_artwork", !showDeletedArtworkState)
            apply()
        }
        refreshRateFragment()
    }

    fun showTakenArtwork(item: MenuItem) {
        val showTakenArtworkState = sharedPreferences.getBoolean("show_taken_artwork", false)
        if (showTakenArtworkState) {
            item.title = resources.getString(R.string.mi_show_taken_art_title)
        } else {
            item.title = resources.getString(R.string.mi_hide_taken_art_title)
        }
        with (sharedPreferences.edit()) {
            putBoolean("show_taken_artwork", !showTakenArtworkState)
            apply()
        }
        refreshRateFragment()
    }

    fun refreshRateFragment(item: MenuItem) {
        refreshRateFragment()
    }

    private fun refreshRateFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.rl_rateFragment, RateFragment())
            .commit()
    }
}
