package com.example.xueya.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.xueya.presentation.screens.home.HomeScreen
import com.example.xueya.presentation.screens.add_record.AddRecordScreen
import com.example.xueya.presentation.screens.history.HistoryScreen
import com.example.xueya.presentation.screens.statistics.StatisticsScreen
import com.example.xueya.presentation.screens.settings.SettingsScreen

/**
 * 应用导航主机
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.Home.route,
        modifier = modifier
    ) {
        // 首页
        composable(AppDestination.Home.route) {
            HomeScreen(
                onNavigateToAddRecord = {
                    navController.navigate(AppDestination.AddRecord.route)
                },
                onNavigateToHistory = {
                    navController.navigate(AppDestination.History.route)
                },
                onNavigateToStatistics = {
                    navController.navigate(AppDestination.Statistics.route)
                },
                onNavigateToSettings = {
                    navController.navigate(AppDestination.Settings.route)
                }
            )
        }
        
        // 添加记录
        composable(AppDestination.AddRecord.route) {
            AddRecordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRecordSaved = {
                    navController.popBackStack()
                }
            )
        }
        
        // 历史记录
        composable(AppDestination.History.route) {
            HistoryScreen(
                onNavigateToAddRecord = {
                    navController.navigate(AppDestination.AddRecord.route)
                },
                onNavigateToRecordDetail = { recordId ->
                    // 暂时只是占位符，后续可以添加记录详情页面
                }
            )
        }
        
        // 统计分析
        composable(AppDestination.Statistics.route) {
            StatisticsScreen()
        }
        
        // 设置
        composable(AppDestination.Settings.route) {
            SettingsScreen()
        }
    }
}