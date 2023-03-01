package com.example.artthiefdemo.ui.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.example.artthiefdemo.R
import com.example.artthiefdemo.databinding.FragmentOverviewBinding

//class OverviewFragment : Fragment() {
//
//    private var _binding: FragmentOverviewBinding? = null
//
//    // This property is only valid between onCreateView and
//    // onDestroyView.
//    private val binding get() = _binding!!
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        val overviewViewModel =
//            ViewModelProvider(this).get(OverviewViewModel::class.java)
//
//        _binding = FragmentOverviewBinding.inflate(inflater, container, false)
//        val root: View = binding.root
//
//        val textView: TextView = binding.textOverview
//        overviewViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
//        return root
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}

class OverviewFragment : Fragment() {

    private lateinit var overviewPagerAdapter: OverviewPagerAdapter
    private lateinit var viewPager: ViewPager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_overview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        overviewPagerAdapter = OverviewPagerAdapter(childFragmentManager)
        viewPager = view.findViewById(R.id.pager_overview)
        viewPager.adapter = overviewPagerAdapter
    }

}
