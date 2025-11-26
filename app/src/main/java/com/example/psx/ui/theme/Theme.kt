package com.example.psx.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.psx.ui.theme.FinancialColors.NeutralGray

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = PrimaryBlue.copy(alpha = 0.1f),
    onPrimaryContainer = PrimaryBlue,

    secondary = NeutralGray,
    onSecondary = Color.White,
    secondaryContainer = NeutralGray.copy(alpha = 0.1f),
    onSecondaryContainer = NeutralGray,

    tertiary = AccentGreen,
    onTertiary = Color.White,

    background = LightGray,
    onBackground = Color(0xFF1F2937),

    surface = CardSurface,
    onSurface = Color(0xFF1F2937),

    surfaceVariant = Color(0xFFF3F4F6),
    onSurfaceVariant = NeutralGrayColor,

    outline = BorderGray,
    outlineVariant = BorderGray.copy(alpha = 0.5f),

    error = AccentRed,
    onError = Color.White,

    // Custom colors for financial indicators
    inversePrimary = AccentGreen,
    inverseSurface = AccentRed
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimaryBlue,
    onPrimary = Color.Black,
    primaryContainer = DarkPrimaryBlue.copy(alpha = 0.2f),
    onPrimaryContainer = DarkPrimaryBlue,

    secondary = DarkTextSecondary,
    onSecondary = Color.Black,
    secondaryContainer = DarkTextSecondary.copy(alpha = 0.2f),
    onSecondaryContainer = DarkTextSecondary,

    tertiary = AccentGreen,
    onTertiary = Color.Black,

    background = Color(0xFF111827),
    onBackground = DarkTextPrimary,

    surface = DarkSurface,
    onSurface = DarkTextPrimary,

    surfaceVariant = DarkCard,
    onSurfaceVariant = DarkTextSecondary,

    outline = Color(0xFF4B5563),
    outlineVariant = Color(0xFF374151),

    error = AccentRed,
    onError = Color.Black,

    inversePrimary = AccentGreen,
    inverseSurface = AccentRed
)

// Extended color palette for financial-specific use cases
object FinancialColors {
    // Price movement colors
    val PositiveGreen = AccentGreen
    val NegativeRed = AccentRed
    val NeutralGray = NeutralGrayColor

    // Chart colors
    val ChartLine = PrimaryBlue
    val ChartArea = PrimaryBlue.copy(alpha = 0.1f)
    val ChartGrid = BorderGray

    // Status colors
    val Success = Color(0xFF10B981)
    val Warning = Color(0xFFF59E0B)
    val Info = PrimaryBlue

    // Market-specific colors
    val PreMarket = Color(0xFF8B5CF6)    // Purple for pre-market
    val AfterHours = Color(0xFFF97316)   // Orange for after-hours
    val VolumeSurge = Color(0xFFEC4899)  // Pink for volume surges

    // Background gradients
    val GradientStart = PrimaryBlue
    val GradientEnd = Color(0xFF004499)
}

// Custom color sets for different market sentiments
object MarketSentimentColors {
    val Bullish = Color(0xFF00A86B)      // Strong green for bullish
    val Bearish = Color(0xFFE53E3E)      // Strong red for bearish
    val Neutral = Color(0xFF6B7280)      // Gray for neutral
    val Volatile = Color(0xFFF59E0B)     // Orange for volatile
}

@Composable
fun PsxTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        androidx.compose.ui.platform.LocalView.current.context?.let { context ->
            val window = (context as? Activity)?.window
            window?.let {
                it.statusBarColor = colorScheme.primary.toArgb()
                WindowCompat.getInsetsController(it, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)