package com.joerock.artthief.ui.rate.data

import android.view.View

interface ArtworkClickListener {
    fun onArtworkClicked(sectionPosition: Int, view: View)
}
