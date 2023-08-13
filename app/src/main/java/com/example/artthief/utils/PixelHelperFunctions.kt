package com.example.artthief.utils

internal fun dpToPixels(density: Float, dp: Int): Int {
    return (dp * density + 0.5f).toInt()
}
