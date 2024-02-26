package com.joerock.artthief.utils

import java.util.*

fun formatListSubmissionDate(): String {
    val calendarInstance = Calendar.getInstance()

    val year = calendarInstance.get(Calendar.YEAR)
    val month = when (calendarInstance.get(Calendar.MONTH)) {
        0 -> "January"
        1 -> "February"
        2 -> "March"
        3 -> "April"
        4 -> "May"
        5 -> "June"
        6 -> "July"
        7 -> "August"
        8 -> "September"
        9 -> "October"
        10 -> "November"
        11 -> "December"
        else -> "January"
    }
    val day = calendarInstance.get(Calendar.DAY_OF_MONTH)
    val hour = calendarInstance.get(Calendar.HOUR_OF_DAY)
    val minute = calendarInstance.get(Calendar.MINUTE)
    val minuteString: String = if (minute < 10) {
        "0$minute"
    } else {
        minute.toString()
    }
    val timeZone = calendarInstance.timeZone.displayName

    return "$month $day, $year at $hour:$minuteString $timeZone"
}
