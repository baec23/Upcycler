package com.baec23.upcycler.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColorScheme(
    primary = Green80,
    onPrimary = Green20,
    primaryContainer = Green30,
    onPrimaryContainer = Green90,
    inversePrimary = Green50,
    secondary = Orange80,
    onSecondary = Orange20,
    secondaryContainer = Orange30,
    onSecondaryContainer = Orange90,
    error = Red80,
    onError = Red20,
    errorContainer = Red30,
    onErrorContainer = Red90,
    background = Gray10,
    onBackground = Gray90,
    surface = Gray30,
    onSurface = Gray80,
    inverseSurface = Gray90,
    inverseOnSurface = Gray10,
    surfaceVariant = Gray20,
    onSurfaceVariant = Gray80,
    outline = Gray80

)

private val LightColorPalette = lightColorScheme(
    primary = Green50,
    onPrimary = Color.White,
    primaryContainer = Green90,
    onPrimaryContainer = Green10,
    inversePrimary = Green80,
    secondary = Green30,
    onSecondary = Color.White,
    secondaryContainer = Orange90,
    onSecondaryContainer = Orange10,
    error = Red50,
    onError = Color.White,
    errorContainer = Red90,
    onErrorContainer = Red10,
    background = Color.White,
    onBackground = Gray10,
    surface = Gray90,
    onSurface = Gray30,
    inverseSurface = Gray20,
    inverseOnSurface = Gray90,
    surfaceVariant = Gray90,
    onSurfaceVariant = Gray30,
    outline = Gray50
)

@Composable
fun UpcyclerTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {

//    val useDynamicColors = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val colors = when {
//        useDynamicColors && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
//        useDynamicColors && !darkTheme -> dynamicLightColorScheme(LocalContext.current)
        darkTheme -> DarkColorPalette
        else -> LightColorPalette
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}