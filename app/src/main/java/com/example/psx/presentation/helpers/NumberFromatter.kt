package com.example.psx.presentation.helpers

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

fun number_format(amount:Double):String{
    val numberFormat = DecimalFormat("#,###.00")
    return numberFormat.format(amount)
}

fun formatTimestamp(ts: Long): String {
    val date = Date(ts)
    val sdf = SimpleDateFormat("HH:mm\ndd MMM", Locale.getDefault())
    return sdf.format(date)
}


 fun formatShortDate(timestamp: Long): String {
    val date = Date(timestamp)
    val formatter = SimpleDateFormat("MMM dd", Locale.getDefault())
    return formatter.format(date)
}

 fun formatVolume(volume: Double): String {
    return when {
        volume >= 1_000_000 -> "%.1fM".format(volume / 1_000_000)
        volume >= 1_000 -> "%.1fK".format(volume / 1_000)
        else -> "%.0f".format(volume)
    }
}

 fun formatCurrency(value: Double): String {
    return when {
        abs(value) >= 1_000_000_000 -> "%.2fB".format(value / 1_000_000_000.0)
        abs(value) >= 1_000_000 -> "%.2fM".format(value / 1_000_000.0)
        abs(value) >= 1_000 -> "%.2fK".format(value / 1_000.0)
        else -> "%.2f".format(value)
    }
}