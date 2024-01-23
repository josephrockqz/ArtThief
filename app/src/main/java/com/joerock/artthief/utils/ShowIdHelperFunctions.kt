package com.joerock.artthief.utils

import androidx.core.text.isDigitsOnly

internal fun getShowIdDisplayValue(showId: String): String {
    return if (showId.isDigitsOnly()) {
        showId
    } else {
        ""
    }
}
