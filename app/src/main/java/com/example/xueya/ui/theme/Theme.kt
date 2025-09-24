package com.example.xueya.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val HealthLightColorScheme = lightColorScheme(
    primary = HealthPrimary,
    onPrimary = HealthOnPrimary,
    primaryContainer = HealthPrimaryContainer,
    onPrimaryContainer = HealthOnPrimaryContainer,
    secondary = HealthSecondary,
    onSecondary = HealthOnSecondary,
    secondaryContainer = HealthSecondaryContainer,
    onSecondaryContainer = HealthOnSecondaryContainer,
    tertiary = HealthTertiary,
    onTertiary = HealthOnTertiary,
    tertiaryContainer = HealthTertiaryContainer,
    onTertiaryContainer = HealthOnTertiaryContainer,
    error = HealthError,
    onError = HealthOnError,
    errorContainer = HealthErrorContainer,
    onErrorContainer = HealthOnErrorContainer,
    background = HealthSurface,
    onBackground = HealthOnSurface,
    surface = HealthSurface,
    onSurface = HealthOnSurface,
    surfaceVariant = HealthSurfaceVariant,
    onSurfaceVariant = HealthOnSurfaceVariant,
)

private val HealthDarkColorScheme = darkColorScheme(
    primary = HealthPrimaryDark,
    onPrimary = HealthOnPrimaryDark,
    primaryContainer = HealthPrimaryContainerDark,
    onPrimaryContainer = HealthOnPrimaryContainerDark,
    secondary = HealthSecondaryDark,
    onSecondary = HealthOnSecondaryDark,
    secondaryContainer = HealthSecondaryContainerDark,
    onSecondaryContainer = HealthOnSecondaryContainerDark,
    tertiary = HealthTertiaryDark,
    onTertiary = HealthOnTertiaryDark,
    tertiaryContainer = HealthTertiaryContainerDark,
    onTertiaryContainer = HealthOnTertiaryContainerDark,
    error = HealthError,
    onError = HealthOnError,
    errorContainer = HealthErrorContainer,
    onErrorContainer = HealthOnErrorContainer,
    background = HealthSurfaceDark,
    onBackground = HealthOnSurfaceDark,
    surface = HealthSurfaceDark,
    onSurface = HealthOnSurfaceDark,
    surfaceVariant = HealthSurfaceVariantDark,
    onSurfaceVariant = HealthOnSurfaceVariantDark,
)

@Composable
fun XueYaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    // For health app, we prefer consistent branding colors
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> HealthDarkColorScheme
        else -> HealthLightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}