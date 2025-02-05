package com.example.greenstep.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Define your custom color palette
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF66BB6A),  // Light Green
    secondary = Color(0xFF4CAF50), // Green
    tertiary = Color(0xFF388E3C), // Dark Green
    background = Color(0xFF121212), // Dark background for dark mode
    surface = Color(0xFF121212),    // Dark surface for dark mode
    onPrimary = Color.White, // Text on primary button (white)
    onSecondary = Color.White, // Text on secondary button (white)
    onSurface = Color.White, // Text on surfaces in dark mode
    onBackground = Color.White // Text on background in dark mode
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF66BB6A), // Light Green
    secondary = Color(0xFF4CAF50), // Green
    tertiary = Color(0xFF388E3C), // Dark Green
    background = Color(0xFFF5F5DC), // Beige background for light mode
    surface = Color(0xFFF5F5DC),   // Beige surface for light mode
    onPrimary = Color.White, // Text on primary button (white)
    onSecondary = Color.White, // Text on secondary button (white)
    onSurface = Color(0xFF333333), // Dark text on surfaces in light mode
    onBackground = Color(0xFF333333) // Dark text on background in light mode
)

@Composable
fun GreenStepTheme(
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}