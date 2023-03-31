package com.example.artthief.ui.send

import android.os.Bundle
import android.view.*
import android.widget.Button
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val numArtworks = 56
        val buttonText = "Send $numArtworks Artworks"
        val button = getView()?.findViewById<Button>(R.id.b_send_button)
        button?.text = buttonText
    }
}
