package com.example.artthief.ui.rate.data

import com.example.artthief.domain.ArtThiefArtwork

data class RecyclerViewSection(
    val rating: Int,
    val artworks: List<ArtThiefArtwork>
)
