package com.example.xueya.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.xueya.presentation.screens.home.HomeScreen
import com.example.xueya.presentation.screens.add_record.AddRecordScreen
import com.example.xueya.presentation.screens.history.HistoryScreen
import com.example.xueya.presentation.screens.statistics.StatisticsScreen

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
            SettingsScreenPlaceholder()
        }
    }
}

// 临时占位符界面，后续会替换为实际实现

@Composable
private fun HomeScreenPlaceholder() {
    PlaceholderScreen(
        title = "首页",
        description = "显示最新血压记录和统计概览"
    )
}

@Composable
private fun AddRecordScreenPlaceholder(
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        PlaceholderScreen(
            title = "添加记录",
            description = "输入血压数据"
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(onClick = onNavigateBack) {
            Text("返回")
        }
    }
}

@Composable
private fun HistoryScreenPlaceholder() {
    PlaceholderScreen(
        title = "历史记录",
        description = "查看所有血压记录"
    )
}

@Composable
private fun StatisticsScreenPlaceholder() {
    PlaceholderScreen(
        title = "统计分析",
        description = "查看血压趋势和分析"
    )
}

@Composable
private fun SettingsScreenPlaceholder() {
    PlaceholderScreen(
        title = "设置",
        description = "应用设置和偏好"
    )
}

@Composable
private fun PlaceholderScreen(
    title: String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}