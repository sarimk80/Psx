package com.pizza.psx.ui.theme

import android.os.Build
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pizza.compose.backgroundDark
import com.pizza.compose.backgroundDarkHighContrast
import com.pizza.compose.backgroundDarkMediumContrast
import com.pizza.compose.backgroundLight
import com.pizza.compose.backgroundLightHighContrast
import com.pizza.compose.backgroundLightMediumContrast
import com.pizza.compose.errorContainerDark
import com.pizza.compose.errorContainerDarkHighContrast
import com.pizza.compose.errorContainerDarkMediumContrast
import com.pizza.compose.errorContainerLight
import com.pizza.compose.errorContainerLightHighContrast
import com.pizza.compose.errorContainerLightMediumContrast
import com.pizza.compose.errorDark
import com.pizza.compose.errorDarkHighContrast
import com.pizza.compose.errorDarkMediumContrast
import com.pizza.compose.errorLight
import com.pizza.compose.errorLightHighContrast
import com.pizza.compose.errorLightMediumContrast
import com.pizza.compose.inverseOnSurfaceDark
import com.pizza.compose.inverseOnSurfaceDarkHighContrast
import com.pizza.compose.inverseOnSurfaceDarkMediumContrast
import com.pizza.compose.inverseOnSurfaceLight
import com.pizza.compose.inverseOnSurfaceLightHighContrast
import com.pizza.compose.inverseOnSurfaceLightMediumContrast
import com.pizza.compose.inversePrimaryDark
import com.pizza.compose.inversePrimaryDarkHighContrast
import com.pizza.compose.inversePrimaryDarkMediumContrast
import com.pizza.compose.inversePrimaryLight
import com.pizza.compose.inversePrimaryLightHighContrast
import com.pizza.compose.inversePrimaryLightMediumContrast
import com.pizza.compose.inverseSurfaceDark
import com.pizza.compose.inverseSurfaceDarkHighContrast
import com.pizza.compose.inverseSurfaceDarkMediumContrast
import com.pizza.compose.inverseSurfaceLight
import com.pizza.compose.inverseSurfaceLightHighContrast
import com.pizza.compose.inverseSurfaceLightMediumContrast
import com.pizza.compose.onBackgroundDark
import com.pizza.compose.onBackgroundDarkHighContrast
import com.pizza.compose.onBackgroundDarkMediumContrast
import com.pizza.compose.onBackgroundLight
import com.pizza.compose.onBackgroundLightHighContrast
import com.pizza.compose.onBackgroundLightMediumContrast
import com.pizza.compose.onErrorContainerDark
import com.pizza.compose.onErrorContainerDarkHighContrast
import com.pizza.compose.onErrorContainerDarkMediumContrast
import com.pizza.compose.onErrorContainerLight
import com.pizza.compose.onErrorContainerLightHighContrast
import com.pizza.compose.onErrorContainerLightMediumContrast
import com.pizza.compose.onErrorDark
import com.pizza.compose.onErrorDarkHighContrast
import com.pizza.compose.onErrorDarkMediumContrast
import com.pizza.compose.onErrorLight
import com.pizza.compose.onErrorLightHighContrast
import com.pizza.compose.onErrorLightMediumContrast
import com.pizza.compose.onPrimaryContainerDark
import com.pizza.compose.onPrimaryContainerDarkHighContrast
import com.pizza.compose.onPrimaryContainerDarkMediumContrast
import com.pizza.compose.onPrimaryContainerLightMediumContrast
import com.pizza.compose.onPrimaryLightMediumContrast
import com.pizza.compose.onSecondaryContainerLightMediumContrast
import com.pizza.compose.onSecondaryLightMediumContrast
import com.pizza.compose.onSurfaceLightMediumContrast
import com.pizza.compose.onSurfaceVariantLightMediumContrast
import com.pizza.compose.onTertiaryContainerLightMediumContrast
import com.pizza.compose.onTertiaryLightMediumContrast
import com.pizza.compose.outlineLightMediumContrast
import com.pizza.compose.outlineVariantLightMediumContrast
import com.pizza.compose.primaryContainerLightMediumContrast
import com.pizza.compose.primaryLightMediumContrast
import com.pizza.compose.scrimLightMediumContrast
import com.pizza.compose.secondaryContainerLightMediumContrast
import com.pizza.compose.secondaryLightMediumContrast
import com.pizza.compose.surfaceBrightDark
import com.pizza.compose.surfaceBrightLightMediumContrast
import com.pizza.compose.surfaceContainerDark
import com.pizza.compose.surfaceContainerHighDark
import com.pizza.compose.surfaceContainerHighLightMediumContrast
import com.pizza.compose.surfaceContainerHighestDark
import com.pizza.compose.surfaceContainerHighestLightMediumContrast
import com.pizza.compose.surfaceContainerLightMediumContrast
import com.pizza.compose.surfaceContainerLowDark
import com.pizza.compose.surfaceContainerLowLightMediumContrast
import com.pizza.compose.surfaceContainerLowestDark
import com.pizza.compose.surfaceContainerLowestLightMediumContrast
import com.pizza.compose.surfaceDimDark
import com.pizza.compose.surfaceDimDarkHighContrast
import com.pizza.compose.surfaceDimLightMediumContrast
import com.pizza.compose.surfaceLightMediumContrast
import com.pizza.compose.surfaceVariantLightMediumContrast
import com.pizza.compose.tertiaryContainerLightMediumContrast
import com.pizza.compose.tertiaryLightMediumContrast


import com.pizza.compose.primaryLight
import com.pizza.compose.primaryDark
import com.pizza.compose.primaryContainerDark
import com.pizza.compose.primaryContainerDarkMediumContrast
import com.pizza.compose.onPrimaryLight
import com.pizza.compose.onPrimaryDark
import com.pizza.compose.onPrimaryDarkHighContrast
import com.pizza.compose.onPrimaryDarkMediumContrast
import com.pizza.compose.onSecondaryContainerDarkHighContrast
import com.pizza.compose.onSecondaryContainerDarkMediumContrast
import com.pizza.compose.onSecondaryDarkHighContrast
import com.pizza.compose.onSecondaryDarkMediumContrast
import com.pizza.compose.onSurfaceDarkHighContrast
import com.pizza.compose.onSurfaceDarkMediumContrast
import com.pizza.compose.onSurfaceLightHighContrast
import com.pizza.compose.onSurfaceVariantDarkHighContrast
import com.pizza.compose.onSurfaceVariantDarkMediumContrast
import com.pizza.compose.onSurfaceVariantLightHighContrast
import com.pizza.compose.onTertiaryContainerDarkHighContrast
import com.pizza.compose.onTertiaryContainerDarkMediumContrast
import com.pizza.compose.onTertiaryDarkHighContrast
import com.pizza.compose.onTertiaryDarkMediumContrast
import com.pizza.compose.outlineDarkHighContrast
import com.pizza.compose.outlineDarkMediumContrast
import com.pizza.compose.outlineLightHighContrast
import com.pizza.compose.outlineVariantDarkHighContrast
import com.pizza.compose.outlineVariantDarkMediumContrast
import com.pizza.compose.outlineVariantLightHighContrast
import com.pizza.compose.primaryContainerDarkHighContrast
import com.pizza.compose.primaryDarkHighContrast
import com.pizza.compose.primaryDarkMediumContrast
import com.pizza.compose.scrimDarkHighContrast
import com.pizza.compose.scrimDarkMediumContrast
import com.pizza.compose.scrimLightHighContrast
import com.pizza.compose.secondaryContainerDarkHighContrast
import com.pizza.compose.secondaryContainerDarkMediumContrast
import com.pizza.compose.secondaryDarkHighContrast
import com.pizza.compose.secondaryDarkMediumContrast
import com.pizza.compose.surfaceBrightDarkHighContrast
import com.pizza.compose.surfaceBrightDarkMediumContrast
import com.pizza.compose.surfaceBrightLightHighContrast
import com.pizza.compose.surfaceContainerDarkHighContrast
import com.pizza.compose.surfaceContainerDarkMediumContrast
import com.pizza.compose.surfaceContainerHighDarkHighContrast
import com.pizza.compose.surfaceContainerHighDarkMediumContrast
import com.pizza.compose.surfaceContainerHighLightHighContrast
import com.pizza.compose.surfaceContainerHighestDarkHighContrast
import com.pizza.compose.surfaceContainerHighestDarkMediumContrast
import com.pizza.compose.surfaceContainerHighestLightHighContrast
import com.pizza.compose.surfaceContainerLightHighContrast
import com.pizza.compose.surfaceContainerLowDarkHighContrast
import com.pizza.compose.surfaceContainerLowDarkMediumContrast
import com.pizza.compose.surfaceContainerLowLightHighContrast
import com.pizza.compose.surfaceContainerLowestDarkHighContrast
import com.pizza.compose.surfaceContainerLowestDarkMediumContrast
import com.pizza.compose.surfaceContainerLowestLightHighContrast
import com.pizza.compose.surfaceDarkHighContrast
import com.pizza.compose.surfaceDarkMediumContrast
import com.pizza.compose.surfaceDimDarkMediumContrast
import com.pizza.compose.surfaceDimLightHighContrast
import com.pizza.compose.surfaceLightHighContrast
import com.pizza.compose.surfaceVariantDarkHighContrast
import com.pizza.compose.surfaceVariantDarkMediumContrast
import com.pizza.compose.surfaceVariantLightHighContrast
import com.pizza.compose.tertiaryContainerDarkHighContrast
import com.pizza.compose.tertiaryContainerDarkMediumContrast
import com.pizza.compose.tertiaryDarkHighContrast
import com.pizza.compose.tertiaryDarkMediumContrast
import com.pizza.compose.scrimDark

import com.pizza.compose.primaryContainerLight
import com.pizza.compose.onPrimaryContainerLight
import com.pizza.compose.onPrimaryContainerLightHighContrast
import com.pizza.compose.onPrimaryLightHighContrast
import com.pizza.compose.secondaryLight
import com.pizza.compose.onSecondaryLight
import com.pizza.compose.onSecondaryDark
import com.pizza.compose.secondaryDark
import com.pizza.compose.secondaryContainerLight
import com.pizza.compose.secondaryContainerDark
import com.pizza.compose.onSecondaryContainerDark
import com.pizza.compose.onSecondaryContainerLight
import com.pizza.compose.onSecondaryContainerLightHighContrast
import com.pizza.compose.onSecondaryLightHighContrast
import com.pizza.compose.onSurfaceDark
import com.pizza.compose.onSurfaceLight
import com.pizza.compose.onSurfaceVariantDark
import com.pizza.compose.onSurfaceVariantLight
import com.pizza.compose.onTertiaryContainerDark
import com.pizza.compose.onTertiaryContainerLight
import com.pizza.compose.onTertiaryContainerLightHighContrast
import com.pizza.compose.onTertiaryDark
import com.pizza.compose.onTertiaryLight
import com.pizza.compose.onTertiaryLightHighContrast
import com.pizza.compose.outlineDark
import com.pizza.compose.outlineLight
import com.pizza.compose.outlineVariantDark
import com.pizza.compose.outlineVariantLight
import com.pizza.compose.primaryContainerLightHighContrast
import com.pizza.compose.primaryLightHighContrast
import com.pizza.compose.scrimLight
import com.pizza.compose.secondaryContainerLightHighContrast
import com.pizza.compose.secondaryLightHighContrast
import com.pizza.compose.surfaceBrightLight
import com.pizza.compose.surfaceContainerHighLight
import com.pizza.compose.surfaceContainerHighestLight
import com.pizza.compose.surfaceContainerLight
import com.pizza.compose.surfaceContainerLowLight
import com.pizza.compose.surfaceContainerLowestLight
import com.pizza.compose.surfaceDark
import com.pizza.compose.surfaceDimLight
import com.pizza.compose.surfaceLight
import com.pizza.compose.surfaceVariantDark
import com.pizza.compose.surfaceVariantLight
import com.pizza.compose.tertiaryContainerDark
import com.pizza.compose.tertiaryContainerLight
import com.pizza.compose.tertiaryContainerLightHighContrast
import com.pizza.compose.tertiaryDark
import com.pizza.compose.tertiaryLight
import com.pizza.compose.tertiaryLightHighContrast
import com.pizza.ui.theme.AppTypography
import com.google.accompanist.systemuicontroller.rememberSystemUiController


private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

private val mediumContrastLightColorScheme = lightColorScheme(
    primary = primaryLightMediumContrast,
    onPrimary = onPrimaryLightMediumContrast,
    primaryContainer = primaryContainerLightMediumContrast,
    onPrimaryContainer = onPrimaryContainerLightMediumContrast,
    secondary = secondaryLightMediumContrast,
    onSecondary = onSecondaryLightMediumContrast,
    secondaryContainer = secondaryContainerLightMediumContrast,
    onSecondaryContainer = onSecondaryContainerLightMediumContrast,
    tertiary = tertiaryLightMediumContrast,
    onTertiary = onTertiaryLightMediumContrast,
    tertiaryContainer = tertiaryContainerLightMediumContrast,
    onTertiaryContainer = onTertiaryContainerLightMediumContrast,
    error = errorLightMediumContrast,
    onError = onErrorLightMediumContrast,
    errorContainer = errorContainerLightMediumContrast,
    onErrorContainer = onErrorContainerLightMediumContrast,
    background = backgroundLightMediumContrast,
    onBackground = onBackgroundLightMediumContrast,
    surface = surfaceLightMediumContrast,
    onSurface = onSurfaceLightMediumContrast,
    surfaceVariant = surfaceVariantLightMediumContrast,
    onSurfaceVariant = onSurfaceVariantLightMediumContrast,
    outline = outlineLightMediumContrast,
    outlineVariant = outlineVariantLightMediumContrast,
    scrim = scrimLightMediumContrast,
    inverseSurface = inverseSurfaceLightMediumContrast,
    inverseOnSurface = inverseOnSurfaceLightMediumContrast,
    inversePrimary = inversePrimaryLightMediumContrast,
    surfaceDim = surfaceDimLightMediumContrast,
    surfaceBright = surfaceBrightLightMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestLightMediumContrast,
    surfaceContainerLow = surfaceContainerLowLightMediumContrast,
    surfaceContainer = surfaceContainerLightMediumContrast,
    surfaceContainerHigh = surfaceContainerHighLightMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestLightMediumContrast,
)

private val highContrastLightColorScheme = lightColorScheme(
    primary = primaryLightHighContrast,
    onPrimary = onPrimaryLightHighContrast,
    primaryContainer = primaryContainerLightHighContrast,
    onPrimaryContainer = onPrimaryContainerLightHighContrast,
    secondary = secondaryLightHighContrast,
    onSecondary = onSecondaryLightHighContrast,
    secondaryContainer = secondaryContainerLightHighContrast,
    onSecondaryContainer = onSecondaryContainerLightHighContrast,
    tertiary = tertiaryLightHighContrast,
    onTertiary = onTertiaryLightHighContrast,
    tertiaryContainer = tertiaryContainerLightHighContrast,
    onTertiaryContainer = onTertiaryContainerLightHighContrast,
    error = errorLightHighContrast,
    onError = onErrorLightHighContrast,
    errorContainer = errorContainerLightHighContrast,
    onErrorContainer = onErrorContainerLightHighContrast,
    background = backgroundLightHighContrast,
    onBackground = onBackgroundLightHighContrast,
    surface = surfaceLightHighContrast,
    onSurface = onSurfaceLightHighContrast,
    surfaceVariant = surfaceVariantLightHighContrast,
    onSurfaceVariant = onSurfaceVariantLightHighContrast,
    outline = outlineLightHighContrast,
    outlineVariant = outlineVariantLightHighContrast,
    scrim = scrimLightHighContrast,
    inverseSurface = inverseSurfaceLightHighContrast,
    inverseOnSurface = inverseOnSurfaceLightHighContrast,
    inversePrimary = inversePrimaryLightHighContrast,
    surfaceDim = surfaceDimLightHighContrast,
    surfaceBright = surfaceBrightLightHighContrast,
    surfaceContainerLowest = surfaceContainerLowestLightHighContrast,
    surfaceContainerLow = surfaceContainerLowLightHighContrast,
    surfaceContainer = surfaceContainerLightHighContrast,
    surfaceContainerHigh = surfaceContainerHighLightHighContrast,
    surfaceContainerHighest = surfaceContainerHighestLightHighContrast,
)

private val mediumContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkMediumContrast,
    onPrimary = onPrimaryDarkMediumContrast,
    primaryContainer = primaryContainerDarkMediumContrast,
    onPrimaryContainer = onPrimaryContainerDarkMediumContrast,
    secondary = secondaryDarkMediumContrast,
    onSecondary = onSecondaryDarkMediumContrast,
    secondaryContainer = secondaryContainerDarkMediumContrast,
    onSecondaryContainer = onSecondaryContainerDarkMediumContrast,
    tertiary = tertiaryDarkMediumContrast,
    onTertiary = onTertiaryDarkMediumContrast,
    tertiaryContainer = tertiaryContainerDarkMediumContrast,
    onTertiaryContainer = onTertiaryContainerDarkMediumContrast,
    error = errorDarkMediumContrast,
    onError = onErrorDarkMediumContrast,
    errorContainer = errorContainerDarkMediumContrast,
    onErrorContainer = onErrorContainerDarkMediumContrast,
    background = backgroundDarkMediumContrast,
    onBackground = onBackgroundDarkMediumContrast,
    surface = surfaceDarkMediumContrast,
    onSurface = onSurfaceDarkMediumContrast,
    surfaceVariant = surfaceVariantDarkMediumContrast,
    onSurfaceVariant = onSurfaceVariantDarkMediumContrast,
    outline = outlineDarkMediumContrast,
    outlineVariant = outlineVariantDarkMediumContrast,
    scrim = scrimDarkMediumContrast,
    inverseSurface = inverseSurfaceDarkMediumContrast,
    inverseOnSurface = inverseOnSurfaceDarkMediumContrast,
    inversePrimary = inversePrimaryDarkMediumContrast,
    surfaceDim = surfaceDimDarkMediumContrast,
    surfaceBright = surfaceBrightDarkMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkMediumContrast,
    surfaceContainerLow = surfaceContainerLowDarkMediumContrast,
    surfaceContainer = surfaceContainerDarkMediumContrast,
    surfaceContainerHigh = surfaceContainerHighDarkMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkMediumContrast,
)

private val highContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkHighContrast,
    onPrimary = onPrimaryDarkHighContrast,
    primaryContainer = primaryContainerDarkHighContrast,
    onPrimaryContainer = onPrimaryContainerDarkHighContrast,
    secondary = secondaryDarkHighContrast,
    onSecondary = onSecondaryDarkHighContrast,
    secondaryContainer = secondaryContainerDarkHighContrast,
    onSecondaryContainer = onSecondaryContainerDarkHighContrast,
    tertiary = tertiaryDarkHighContrast,
    onTertiary = onTertiaryDarkHighContrast,
    tertiaryContainer = tertiaryContainerDarkHighContrast,
    onTertiaryContainer = onTertiaryContainerDarkHighContrast,
    error = errorDarkHighContrast,
    onError = onErrorDarkHighContrast,
    errorContainer = errorContainerDarkHighContrast,
    onErrorContainer = onErrorContainerDarkHighContrast,
    background = backgroundDarkHighContrast,
    onBackground = onBackgroundDarkHighContrast,
    surface = surfaceDarkHighContrast,
    onSurface = onSurfaceDarkHighContrast,
    surfaceVariant = surfaceVariantDarkHighContrast,
    onSurfaceVariant = onSurfaceVariantDarkHighContrast,
    outline = outlineDarkHighContrast,
    outlineVariant = outlineVariantDarkHighContrast,
    scrim = scrimDarkHighContrast,
    inverseSurface = inverseSurfaceDarkHighContrast,
    inverseOnSurface = inverseOnSurfaceDarkHighContrast,
    inversePrimary = inversePrimaryDarkHighContrast,
    surfaceDim = surfaceDimDarkHighContrast,
    surfaceBright = surfaceBrightDarkHighContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkHighContrast,
    surfaceContainerLow = surfaceContainerLowDarkHighContrast,
    surfaceContainer = surfaceContainerDarkHighContrast,
    surfaceContainerHigh = surfaceContainerHighDarkHighContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkHighContrast,
)

@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color
)

val unspecified_scheme = ColorFamily(
    Color.Unspecified, Color.Unspecified, Color.Unspecified, Color.Unspecified
)

@Composable
fun PsxTheme(
    darkTheme: Boolean = false,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable() () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> lightScheme
        else -> lightScheme
    }


    val systemUiController = rememberSystemUiController()
    if(darkTheme){
        systemUiController.setSystemBarsColor(
            color = Color.Transparent
        )
    }else{
        systemUiController.setSystemBarsColor(
            color = Color.White
        )
    }


    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content,
        shapes = Shapes
    )
}



val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)