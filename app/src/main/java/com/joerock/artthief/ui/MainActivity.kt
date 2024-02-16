package com.joerock.artthief.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.joerock.artthief.R
import com.joerock.artthief.databinding.ActivityMainBinding
import com.joerock.artthief.viewmodels.ArtworksViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: ArtworksViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding
    private val sharedPreferences by lazy {
        getPreferences(Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        with (sharedPreferences.edit()) {
            putString("query_text", String())
            apply()
        }

        viewModel.refreshDataFromRepositoryAndDeleteOldData()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

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
}
