package com.example.xueya.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*

/**
 * 应用主导航组合
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            AppBottomNavigation(
                currentDestination = currentDestination,
                onNavigateToDestination = { destination ->
                    navController.navigate(destination.route) {
                        // 避免重复导航到同一目标
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // 避免多个副本堆叠
                        launchSingleTop = true
                        // 恢复状态
                        restoreState = true
                    }
                }
            )
        },
        floatingActionButton = {
            // 在首页和历史记录页面显示添加按钮
            if (currentDestination?.route in listOf(
                AppDestination.Home.route,
                AppDestination.History.route
            )) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(AppDestination.AddRecord.route)
                    }
                ) {
                    Icon(
                        imageVector = AppDestination.AddRecord.icon,
                        contentDescription = AppDestination.AddRecord.title
                    )
                }
            }
        }
    ) { paddingValues ->
        AppNavHost(
            navController = navController,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

/**
 * 应用底部导航栏
 */
@Composable
private fun AppBottomNavigation(
    currentDestination: NavDestination?,
    onNavigateToDestination: (AppDestination) -> Unit
) {
    NavigationBar {
        bottomNavDestinations.forEach { destination ->
            val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)
            
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigateToDestination(destination) },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.title
                    )
                },
                label = {
                    Text(destination.title)
                }
            )
        }
    }
}

/**
 * 检查当前目标是否在层级中
 */
private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: AppDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.route, true) ?: false
    } ?: false