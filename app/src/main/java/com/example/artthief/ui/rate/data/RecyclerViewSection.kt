package com.example.artthief.ui.rate.data

import com.example.artthief.domain.ArtThiefArtwork

data class RecyclerViewSection(
    val label: String,
    val artworks: List<ArtThiefArtwork>
)
