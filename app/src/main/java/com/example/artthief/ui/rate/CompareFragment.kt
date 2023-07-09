package com.example.artthief.ui.rate

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.artthief.R
import com.example.artthief.databinding.FragmentCompareBinding
import com.example.artthief.viewmodels.ArtworksViewModel
import com.squareup.picasso.Picasso

class CompareFragment : Fragment() {

    private var _binding: FragmentCompareBinding? = null
    private val binding
        get() = _binding!!

    private val sharedPreferences by lazy {
        requireActivity().getPreferences(Context.MODE_PRIVATE)
    }
    private val toolbar by lazy { binding.compareFragmentAppBar }

    private val viewModel: ArtworksViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCompareBinding.inflate(
            inflater,
            container,
            false
        )

        setMenuItemOnClickListeners(inflater)

//        viewModel.compareSettingsBooleanArray.obs

        val isCompareSettingIdNumberChecked = isCompareSettingIdNumberChecked()
        val isCompareSettingTitleChecked = isCompareSettingTitleChecked()
        val isCompareSettingArtistChecked = isCompareSettingArtistChecked()
        val isCompareSettingMediaChecked = isCompareSettingMediaChecked()
        val isCompareSettingDimensionsChecked = isCompareSettingDimensionsChecked()
        // TODO: dynamically set visibilities of boxes to GONE & VISIBLE based on settings toggles
        // TODO: use observe live data from view model
        if (isCompareSettingIdNumberChecked || isCompareSettingTitleChecked || isCompareSettingArtistChecked
            || isCompareSettingMediaChecked || isCompareSettingDimensionsChecked) {
            binding.flCompareImage1Description.visibility = View.VISIBLE
            binding.flCompareImage2Description.visibility = View.VISIBLE
        } else {
            binding.flCompareImage1Description.visibility = View.GONE
            binding.flCompareImage2Description.visibility = View.GONE
        }
        // TODO: dynamically set visibilities of settings field to VISIBLE & INVISIBLE ...

        val sectionRating = getCompareSectionRating()
        viewModel.getArtworksByRating(sectionRating).observe(viewLifecycleOwner) {
            Log.i("howdy - section artworks: ", it.toString())
            Picasso
                .get()
                .load(it[0].image_large)
                .into(binding.ivCompareImage1)
            Picasso
                .get()
                .load(it[1].image_large)
                .into(binding.ivCompareImage2)
        }

        return binding.root
    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }

    private fun getCompareSectionRating(): Int {
        return sharedPreferences.getInt("section_rating", 5)
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

    private fun setMenuItemOnClickListeners(inflater: LayoutInflater) {

        toolbar.menu[1].setOnMenuItemClickListener {
            activity
                ?.findNavController(R.id.nav_host_fragment_activity_main)
                ?.navigate(R.id.action_compareToRate)
            true
        }

        toolbar.menu[0].subMenu?.get(0)?.setOnMenuItemClickListener {
            showInstructionsDialog(inflater)
        }
        toolbar.menu[0].subMenu?.get(1)?.setOnMenuItemClickListener {
            showSettingsDialog()
        }
    }

    private fun showInstructionsDialog(inflater: LayoutInflater): Boolean {
        activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                val view: View = inflater.inflate(R.layout.compare_instructions_dialog_title, null)
                setCustomTitle(view)
                setView(R.layout.compare_instructions_dialog_content)
                setPositiveButton(R.string.instructions_ok) { _, _ -> }
            }
            builder.create()
            builder.show()
        }

        return true
    }

    private fun showSettingsDialog(): Boolean {

        activity
            ?.findNavController(R.id.nav_host_fragment_activity_main)
            ?.navigate(R.id.action_compareToCompareSettings)

        return true
    }
}
