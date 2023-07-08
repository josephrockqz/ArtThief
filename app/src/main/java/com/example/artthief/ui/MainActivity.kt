package com.example.artthief.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.artthief.R
import com.example.artthief.databinding.ActivityMainBinding
import com.example.artthief.viewmodels.ArtworksViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: ArtworksViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    private val sharedPreferences by lazy {
        getPreferences(Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        viewModel.refreshDataFromRepository()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()

        // TODO: change the initialization to take shared preferences into account
        viewModel.setCompareSettingsBooleanArray(mutableListOf(false, false, false, false, false))

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

    fun onCheckChangedIdNumber(v: View) {
        val compareSettingsBooleanArray = viewModel.compareSettingsBooleanArray.value!!
        Log.i("live data list", compareSettingsBooleanArray.toString())
        compareSettingsBooleanArray[0] = (v as SwitchCompat).isChecked
        viewModel.setCompareSettingsBooleanArray(compareSettingsBooleanArray)
//        with (sharedPreferences.edit()) {
//            putBoolean("compare_setting_id_number", (v as SwitchCompat).isChecked)
//            apply()
//        }
    }

//    fun onCheckChangedTitle(v: View) {
//        val compareSettingsBooleanArray = viewModel.compareSettingsBooleanArray
//        compareSettingsBooleanArray[1] = (v as SwitchCompat).isChecked
//        viewModel.setCompareSettingsBooleanArray(compareSettingsBooleanArray)
////        with (sharedPreferences.edit()) {
////            putBoolean("compare_setting_title", (v as SwitchCompat).isChecked)
////            apply()
////        }
//    }
//
//    fun onCheckChangedArtist(v: View) {
//        val compareSettingsBooleanArray = viewModel.compareSettingsBooleanArray
//        compareSettingsBooleanArray[2] = (v as SwitchCompat).isChecked
//        viewModel.setCompareSettingsBooleanArray(compareSettingsBooleanArray)
////        with (sharedPreferences.edit()) {
////            putBoolean("compare_setting_artist", (v as SwitchCompat).isChecked)
////            apply()
////        }
//    }
//
//    fun onCheckChangedMedia(v: View) {
//        val compareSettingsBooleanArray = viewModel.compareSettingsBooleanArray
//        compareSettingsBooleanArray[3] = (v as SwitchCompat).isChecked
//        viewModel.setCompareSettingsBooleanArray(compareSettingsBooleanArray)
////        with (sharedPreferences.edit()) {
////            putBoolean("compare_setting_media", (v as SwitchCompat).isChecked)
////            apply()
////        }
//    }
//
//    fun onCheckChangedDimensions(v: View) {
//        val compareSettingsBooleanArray = viewModel.compareSettingsBooleanArray
//        compareSettingsBooleanArray[4] = (v as SwitchCompat).isChecked
//        viewModel.setCompareSettingsBooleanArray(compareSettingsBooleanArray)
////        with (sharedPreferences.edit()) {
////            putBoolean("compare_setting_dimensions", (v as SwitchCompat).isChecked)
////            apply()
////        }
//    }
}
