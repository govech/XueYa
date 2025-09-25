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

/**
 * 浅色主题颜色方案
 * 
 * 定义应用浅色主题的颜色配置，基于自定义的健康主题颜色
 */
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

/**
 * 深色主题颜色方案
 * 
 * 定义应用深色主题的颜色配置，基于自定义的健康主题颜色
 */
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

/**
 * 应用主题组合函数
 * 
 * 提供应用级别的主题配置，支持深色模式和动态颜色
 * 根据系统设置和参数决定使用浅色主题还是深色主题
 * 
 * @param darkTheme 是否使用深色主题，默认根据系统设置决定
 * @param dynamicColor 是否使用动态颜色，默认为false，健康应用更倾向于一致的品牌颜色
 * @param content 内容组合函数
 */
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