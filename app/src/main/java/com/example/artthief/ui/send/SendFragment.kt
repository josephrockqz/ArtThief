package com.example.artthief.ui.send

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.artthief.R

class SendFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_send, container, false)
    }
}
