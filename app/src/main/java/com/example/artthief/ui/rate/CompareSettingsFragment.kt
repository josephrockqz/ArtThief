package com.example.artthief.ui.rate

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.artthief.R
import com.example.artthief.databinding.FragmentCompareSettingsBinding
import com.example.artthief.viewmodels.ArtworksViewModel

class CompareSettingsFragment : Fragment() {

    private var _binding: FragmentCompareSettingsBinding? = null
    private val binding
        get() = _binding!!

    private val sharedPreferences by lazy {
        requireActivity().getPreferences(Context.MODE_PRIVATE)
    }
    private val toolbar by lazy { binding.toolbarCompareSettingsTop }

    private val viewModel: ArtworksViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCompareSettingsBinding.inflate(
            inflater,
            container,
            false
        )

        configureToggleFunctionality()
        setMenuItemOnClickListener()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun configureToggleFunctionality() {
        binding.scIdNumber.isChecked = isCompareSettingIdNumberChecked()
        binding.scTitle.isChecked = isCompareSettingTitleChecked()
        binding.scArtist.isChecked = isCompareSettingArtistChecked()
        binding.scMedia.isChecked = isCompareSettingMediaChecked()
        binding.scSizeInInches.isChecked = isCompareSettingDimensionsChecked()

        binding.scIdNumber.setOnClickListener {
            with (sharedPreferences.edit()) {
                putBoolean("compare_setting_id_number", (it as SwitchCompat).isChecked)
                apply()
            }
        }
        binding.scTitle.setOnClickListener {
            with (sharedPreferences.edit()) {
                putBoolean("compare_setting_title", (it as SwitchCompat).isChecked)
                apply()
            }
        }
        binding.scArtist.setOnClickListener {
            with (sharedPreferences.edit()) {
                putBoolean("compare_setting_artist", (it as SwitchCompat).isChecked)
                apply()
            }
        }
        binding.scMedia.setOnClickListener {
            with (sharedPreferences.edit()) {
                putBoolean("compare_setting_media", (it as SwitchCompat).isChecked)
                apply()
            }
        }
        binding.scSizeInInches.setOnClickListener {
            with (sharedPreferences.edit()) {
                putBoolean("compare_setting_dimensions", (it as SwitchCompat).isChecked)
                apply()
            }
        }
    }

    private fun setMenuItemOnClickListener() {
        toolbar[0].setOnClickListener {
            activity
                ?.findNavController(R.id.nav_host_fragment_activity_main)
                ?.navigate(R.id.action_compareSettingsToCompare)
        }
    }

    private fun isCompareSettingIdNumberChecked(): Boolean {
        return sharedPreferences.getBoolean("compare_setting_id_number", false)
    }

    private fun isCompareSettingTitleChecked(): Boolean {
        return sharedPreferences.getBoolean("compare_setting_title", false)
    }

    private fun isCompareSettingArtistChecked(): Boolean {
        return sharedPreferences.getBoolean("compare_setting_artist", false)
    }

    private fun isCompareSettingMediaChecked(): Boolean {
        return sharedPreferences.getBoolean("compare_setting_media", false)
    }

    private fun isCompareSettingDimensionsChecked(): Boolean {
        return sharedPreferences.getBoolean("compare_setting_dimensions", false)
    }
}