package com.example.artthief.utils

// TODO: get rid of this if not used
fun stringifyArtworkDimensions(
    width: Float,
    height: Float
): String {
    val width = width.toString()
    val height = height.toString()
    return "$width\" by $height\""
}
