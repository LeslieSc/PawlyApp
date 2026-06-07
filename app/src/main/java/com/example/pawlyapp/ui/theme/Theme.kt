package com.example.pawlyapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = PawlyBrown,
    secondary = PawlySoftBeige,
    tertiary = PawlyDarkBrown,
    background = PawlyCream,
    surface = PawlyWhite,
    onPrimary = PawlyWhite,
    onSecondary = PawlyDarkBrown,
    onTertiary = PawlyWhite,
    onBackground = PawlyDarkBrown,
    onSurface = PawlyDarkBrown
)

@Composable
fun PawlyAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}