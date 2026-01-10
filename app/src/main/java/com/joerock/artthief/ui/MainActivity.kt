package com.joerock.artthief.ui

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
        
        // Enable edge-to-edge display only on Android 14+ (API 34+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }

        with (sharedPreferences.edit()) {
            putString("query_text", String())
            apply()
        }

        viewModel.refreshDataFromRepositoryAndDeleteOldData()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()

        val navView: BottomNavigationView = binding.navView
        
        // Apply window insets only on Android 14+ (API 34+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Apply bottom insets to navigation
            ViewCompat.setOnApplyWindowInsetsListener(navView) { view, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                view.setPadding(
                    view.paddingLeft,
                    view.paddingTop,
                    view.paddingRight,
                    systemBars.bottom
                )
                insets
            }
            
            // Apply top insets to the fragment container
            val fragmentContainer = findViewById<androidx.fragment.app.FragmentContainerView>(R.id.nav_host_fragment_activity_main)
            ViewCompat.setOnApplyWindowInsetsListener(fragmentContainer) { view, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                view.setPadding(
                    view.paddingLeft,
                    systemBars.top,
                    view.paddingRight,
                    view.paddingBottom
                )
                insets
            }
        }

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
