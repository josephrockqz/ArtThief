package com.example.artthief.ui.overview

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.artthief.databinding.FragmentOverviewBinding

class OverviewFragment : Fragment() {

    private var _binding: FragmentOverviewBinding? = null
    private val binding
        get() = _binding!!

    private lateinit var overviewPagerAdapter: OverviewPagerAdapter
    private lateinit var viewPager: ViewPager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentOverviewBinding.inflate(
            inflater,
            container,
            false
        )

        overviewPagerAdapter = OverviewPagerAdapter(childFragmentManager)
        viewPager = binding.pagerOverview
        viewPager.adapter = overviewPagerAdapter

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
