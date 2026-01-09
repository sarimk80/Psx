package com.pizza.psx.presentation.helpers

import androidx.compose.material3.Switch
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.pizza.psx.views.charts.ChartData
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.atan2
import  androidx.compose.ui.unit.IntOffset
import kotlin.random.Random

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



// Find segment at given angle
 fun findSegmentAtAngle(data: List<ChartData>, angle: Float, gapAngle: Float): Int? {
    val total = data.sumOf { it.value.toDouble() }.toFloat()
    var currentAngle = -90f

    data.forEachIndexed { index, item ->
        val sweepAngle = (item.value / total) * 360f - gapAngle

        // Adjust angle for gap
        val segmentStart = currentAngle + gapAngle / 2
        val segmentEnd = segmentStart + sweepAngle

        if (angle >= segmentStart && angle <= segmentEnd) {
            return index
        }

        currentAngle += sweepAngle + gapAngle
    }

    return null
}

// Generate color from string (for consistent colors)
fun getRandomColor(seed: String): Color {
    val hash = seed.hashCode()
    val r = (hash and 0xFF0000) shr 16
    val g = (hash and 0x00FF00) shr 8
    val b = hash and 0x0000FF
    return Color(r, g, b)
}

// Generate distinct colors
fun generateColors(count: Int): List<Color> {
    val hueStep = 360f / count
    return List(count) { index ->
        Color.hsv(hueStep * index, 0.8f, 0.9f)
    }
}

fun generateColorFromSymbol(symbol: String): Color {
    // Create a hash from the symbol for consistent colors
    val hash = symbol.hashCode()

    // Generate hue from hash (0-360)
    val hue = (hash % 360).toFloat()
    if (hue < 0) hue + 360

    // Use bright, saturated colors
    return Color.hsv(
        hue = hue,
        saturation = 0.7f,
        value = 0.9f
    )
}

fun randomColor(alpha:Int=255) = Color(
    Random.nextInt(256),
    Random.nextInt(256),
    Random.nextInt(256),
    alpha = alpha)

val CHART_COLORS = listOf(
    Color(0xFF4CAF50), // Green
    Color(0xFF2196F3), // Blue
    Color(0xFF9C27B0), // Purple
    Color(0xFFFF9800), // Orange
    Color(0xFFF44336), // Red
    Color(0xFF00BCD4), // Cyan
    Color(0xFFE91E63), // Pink
    Color(0xFF8BC34A), // Light Green
    Color(0xFF3F51B5), // Indigo
    Color(0xFFFF5722), // Deep Orange
    Color(0xFF009688), // Teal
    Color(0xFF673AB7), // Deep Purple
)

fun getColorFromIndex(index: Int): Color {
    return CHART_COLORS[index % CHART_COLORS.size]
}

fun stringToIndexString(index: String): String = when(index) {
    "KSE100" -> "kse_100"
    "KMI30" -> "kmi_30"
    "PSXDIV20" -> "psx_div_20"
    "KSE30" -> "kse_30"
    "MII30" -> "mii_30"
    else -> "kse_100" // default case
}