package com.example.xueya.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.xueya.R

/**
 * 应用导航目标定义
 */
sealed class AppDestination(
    val route: String,
    val titleResId: Int,
    val icon: ImageVector
) {
    object Home : AppDestination(
        route = "home",
        titleResId = R.string.nav_home,
        icon = Icons.Default.Home
    )
    
    object AddRecord : AppDestination(
        route = "add_record",
        titleResId = R.string.nav_add_record,
        icon = Icons.Default.Add
    )
    
    object History : AppDestination(
        route = "history",
        titleResId = R.string.nav_history,
        icon = Icons.Default.List
    )
    
    object Statistics : AppDestination(
        route = "statistics", 
        titleResId = R.string.nav_statistics,
        icon = Icons.Default.Info
    )
    
    object Settings : AppDestination(
        route = "settings",
        titleResId = R.string.nav_settings,
        icon = Icons.Default.Settings
    )
    
    object AiTest : AppDestination(
        route = "ai_test",
        titleResId = R.string.ai_function_test,
        icon = Icons.Default.Build
    )
}

/**
 * 底部导航栏目标
 */
val bottomNavDestinations = listOf(
    AppDestination.Home,
    AppDestination.History,
    AppDestination.Statistics,
    AppDestination.Settings
)

/**
 * 所有导航目标
 */
val allDestinations = listOf(
    AppDestination.Home,
    AppDestination.AddRecord,
    AppDestination.History,
    AppDestination.Statistics,
    AppDestination.Settings,
    AppDestination.AiTest
)