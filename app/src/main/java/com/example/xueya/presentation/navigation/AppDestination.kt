package com.example.xueya.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * 应用导航目标定义
 */
sealed class AppDestination(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : AppDestination(
        route = "home",
        title = "首页",
        icon = Icons.Default.Home
    )
    
    object AddRecord : AppDestination(
        route = "add_record",
        title = "添加记录",
        icon = Icons.Default.Add
    )
    
    object History : AppDestination(
        route = "history",
        title = "历史记录",
        icon = Icons.Default.List
    )
    
    object Statistics : AppDestination(
        route = "statistics", 
        title = "统计分析",
        icon = Icons.Default.Info
    )
    
    object Settings : AppDestination(
        route = "settings",
        title = "设置",
        icon = Icons.Default.Settings
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
    AppDestination.Settings
)