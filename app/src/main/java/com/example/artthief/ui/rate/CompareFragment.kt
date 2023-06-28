package com.example.artthief.ui.rate

import android.os.Bundle
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

        viewModel.highestRatedArtworkUrl.observe(viewLifecycleOwner) {
            if (it.image_large != String()) {
                Picasso
                    .get()
                    .load(it.image_large)
                    .into(binding.ivCompareImage1)
                Picasso
                    .get()
                    .load(it.image_large)
                    .into(binding.ivCompareImage2)
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
            showSettingsDialog(inflater)
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

    private fun showSettingsDialog(inflater: LayoutInflater): Boolean {
        activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                val view: View = inflater.inflate(R.layout.compare_settings_dialog_title, null)
                setCustomTitle(view)
                setView(R.layout.compare_settings_dialog_content)
                setPositiveButton(R.string.instructions_ok) { _, _ -> }
            }
            builder.create()
            builder.show()
        }

        return true
    }
}
