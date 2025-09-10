package com.example.phonedebatehub.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Converts an epoch-millis String (e.g. "1757417701715") to a display string.
 * We keep storage & network as epoch, but present a nice human-readable date in the UI.
 */
object DateFormatter {

    private val isoOut = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    private val dateOnly = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeOnly = SimpleDateFormat("HH:mm", Locale.getDefault())

    fun epochStringToDisplay(epochStr: String?): String? {
        if (epochStr.isNullOrBlank()) return null
        val millis = epochStr.toLongOrNull() ?: return epochStr

        val now = System.currentTimeMillis()
        val diff = now - millis

        // Same-day? show time only. Yesterday? show "Yesterday HH:mm". Otherwise "yyyy-MM-dd HH:mm".
        val date = Date(millis)
        val nowDate = Date(now)

        val sameDay = dateOnly.format(date) == dateOnly.format(nowDate)
        if (sameDay) return timeOnly.format(date)

        val oneDay = TimeUnit.DAYS.toMillis(1)
        val yesterday = Date(now - oneDay)
        val isYesterday = dateOnly.format(date) == dateOnly.format(yesterday)
        if (isYesterday) return "Yesterday ${timeOnly.format(date)}"

        return isoOut.format(date)
    }
}
