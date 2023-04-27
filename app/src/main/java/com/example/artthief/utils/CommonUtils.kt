package com.example.artthief.utils

fun stringifyArtworkDimensions(
    width: Float,
    height: Float
): String {
    val width = width.toString()
    val height = height.toString()
    return "$width\" by $height\""
}
