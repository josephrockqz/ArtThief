package com.joerock.artthief.ui.rate.data

import com.joerock.artthief.domain.ArtThiefArtwork

data class RecyclerViewSection(
    val rating: Int,
    val artworks: List<ArtThiefArtwork>
)
