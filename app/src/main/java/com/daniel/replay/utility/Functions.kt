package com.daniel.replay.utility

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun epochTimeToStandardTime(epochTimeString: String): String {
    return try {
        val epochTime = epochTimeString.toLong() * 1000 // Convert seconds to milliseconds
        val date = Date(epochTime)
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        sdf.format(date)
    } catch (e: Exception) {
        // Handle any parsing errors
        "Unable to determine time"
    }
}