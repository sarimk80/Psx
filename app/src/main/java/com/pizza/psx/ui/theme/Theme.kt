package com.pizza.psx.ui.theme

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
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pizza.ui.theme.AppTypography
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pizza.compose.BackgroundDark
import com.pizza.compose.BackgroundLight
import com.pizza.compose.ErrorContainerDark
import com.pizza.compose.ErrorContainerLight
import com.pizza.compose.ErrorDark
import com.pizza.compose.ErrorLight
import com.pizza.compose.InverseOnSurfaceDark
import com.pizza.compose.InverseOnSurfaceLight
import com.pizza.compose.InversePrimaryDark
import com.pizza.compose.InversePrimaryLight
import com.pizza.compose.InverseSurfaceDark
import com.pizza.compose.InverseSurfaceLight
import com.pizza.compose.OnBackgroundDark
import com.pizza.compose.OnBackgroundLight
import com.pizza.compose.OnErrorContainerDark
import com.pizza.compose.OnErrorContainerLight
import com.pizza.compose.OnErrorDark
import com.pizza.compose.OnErrorLight
import com.pizza.compose.OnPrimaryContainerDark
import com.pizza.compose.OnPrimaryContainerLight
import com.pizza.compose.OnPrimaryDark
import com.pizza.compose.OnPrimaryFixed
import com.pizza.compose.OnPrimaryFixedVariant
import com.pizza.compose.OnPrimaryLight
import com.pizza.compose.OnSecondaryContainerDark
import com.pizza.compose.OnSecondaryContainerLight
import com.pizza.compose.OnSecondaryDark
import com.pizza.compose.OnSecondaryFixed
import com.pizza.compose.OnSecondaryFixedVariant
import com.pizza.compose.OnSecondaryLight
import com.pizza.compose.OnSurfaceDark
import com.pizza.compose.OnSurfaceLight
import com.pizza.compose.OnSurfaceVariantDark
import com.pizza.compose.OnSurfaceVariantLight
import com.pizza.compose.OnTertiaryContainerDark
import com.pizza.compose.OnTertiaryContainerLight
import com.pizza.compose.OnTertiaryDark
import com.pizza.compose.OnTertiaryFixed
import com.pizza.compose.OnTertiaryFixedVariant
import com.pizza.compose.OnTertiaryLight
import com.pizza.compose.OutlineDark
import com.pizza.compose.OutlineLight
import com.pizza.compose.OutlineVariantDark
import com.pizza.compose.OutlineVariantLight
import com.pizza.compose.PrimaryContainerDark
import com.pizza.compose.PrimaryContainerLight
import com.pizza.compose.PrimaryDark
import com.pizza.compose.PrimaryFixed
import com.pizza.compose.PrimaryFixedDim
import com.pizza.compose.PrimaryLight
import com.pizza.compose.ScrimDark
import com.pizza.compose.ScrimLight
import com.pizza.compose.SecondaryContainerDark
import com.pizza.compose.SecondaryContainerLight
import com.pizza.compose.SecondaryDark
import com.pizza.compose.SecondaryFixed
import com.pizza.compose.SecondaryFixedDim
import com.pizza.compose.SecondaryLight
import com.pizza.compose.SurfaceBrightDark
import com.pizza.compose.SurfaceBrightLight
import com.pizza.compose.SurfaceContainerDark
import com.pizza.compose.SurfaceContainerHighDark
import com.pizza.compose.SurfaceContainerHighLight
import com.pizza.compose.SurfaceContainerHighestDark
import com.pizza.compose.SurfaceContainerHighestLight
import com.pizza.compose.SurfaceContainerLight
import com.pizza.compose.SurfaceContainerLowDark
import com.pizza.compose.SurfaceContainerLowLight
import com.pizza.compose.SurfaceContainerLowestDark
import com.pizza.compose.SurfaceContainerLowestLight
import com.pizza.compose.SurfaceDark
import com.pizza.compose.SurfaceDimDark
import com.pizza.compose.SurfaceDimLight
import com.pizza.compose.SurfaceLight
import com.pizza.compose.SurfaceTintDark
import com.pizza.compose.SurfaceTintLight
import com.pizza.compose.SurfaceVariantDark
import com.pizza.compose.SurfaceVariantLight
import com.pizza.compose.TertiaryContainerDark
import com.pizza.compose.TertiaryContainerLight
import com.pizza.compose.TertiaryDark
import com.pizza.compose.TertiaryFixed
import com.pizza.compose.TertiaryFixedDim
import com.pizza.compose.TertiaryLight


private val lightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerLight,
    inversePrimary = InversePrimaryLight,
    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    secondaryContainer = SecondaryContainerLight,
    onSecondaryContainer = OnSecondaryContainerLight,
    tertiary = TertiaryLight,
    onTertiary = OnTertiaryLight,
    tertiaryContainer = TertiaryContainerLight,
    onTertiaryContainer = OnTertiaryContainerLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    surfaceTint = SurfaceTintLight,
    inverseSurface = InverseSurfaceLight,
    inverseOnSurface = InverseOnSurfaceLight,
    error = ErrorLight,
    onError = OnErrorLight,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,
    outline = OutlineLight,
    outlineVariant = OutlineVariantLight,
    scrim = ScrimLight,
    surfaceBright = SurfaceBrightLight,
    surfaceContainer = SurfaceContainerLight,
    surfaceContainerHigh = SurfaceContainerHighLight,
    surfaceContainerHighest = SurfaceContainerHighestLight,
    surfaceContainerLow = SurfaceContainerLowLight,
    surfaceContainerLowest = SurfaceContainerLowestLight,
    surfaceDim = SurfaceDimLight,
    primaryFixed = PrimaryFixed,
    primaryFixedDim = PrimaryFixedDim,
    onPrimaryFixed = OnPrimaryFixed,
    onPrimaryFixedVariant = OnPrimaryFixedVariant,
    secondaryFixed = SecondaryFixed,
    secondaryFixedDim = SecondaryFixedDim,
    onSecondaryFixed = OnSecondaryFixed,
    onSecondaryFixedVariant = OnSecondaryFixedVariant,
    tertiaryFixed = TertiaryFixed,
    tertiaryFixedDim = TertiaryFixedDim,
    onTertiaryFixed = OnTertiaryFixed,
    onTertiaryFixedVariant = OnTertiaryFixedVariant,
)

private val darkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    inversePrimary = InversePrimaryDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,
    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    tertiaryContainer = TertiaryContainerDark,
    onTertiaryContainer = OnTertiaryContainerDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    surfaceTint = SurfaceTintDark,
    inverseSurface = InverseSurfaceDark,
    inverseOnSurface = InverseOnSurfaceDark,
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,
    outline = OutlineDark,
    outlineVariant = OutlineVariantDark,
    scrim = ScrimDark,
    surfaceBright = SurfaceBrightDark,
    surfaceContainer = SurfaceContainerDark,
    surfaceContainerHigh = SurfaceContainerHighDark,
    surfaceContainerHighest = SurfaceContainerHighestDark,
    surfaceContainerLow = SurfaceContainerLowDark,
    surfaceContainerLowest = SurfaceContainerLowestDark,
    surfaceDim = SurfaceDimDark,
    primaryFixed = PrimaryFixed,
    primaryFixedDim = PrimaryFixedDim,
    onPrimaryFixed = OnPrimaryFixed,
    onPrimaryFixedVariant = OnPrimaryFixedVariant,
    secondaryFixed = SecondaryFixed,
    secondaryFixedDim = SecondaryFixedDim,
    onSecondaryFixed = OnSecondaryFixed,
    onSecondaryFixedVariant = OnSecondaryFixedVariant,
    tertiaryFixed = TertiaryFixed,
    tertiaryFixedDim = TertiaryFixedDim,
    onTertiaryFixed = OnTertiaryFixed,
    onTertiaryFixedVariant = OnTertiaryFixedVariant,
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
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable() () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> darkColorScheme
        else -> lightColorScheme
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