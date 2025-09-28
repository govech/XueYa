package com.example.xueya.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.xueya.R
import com.example.xueya.presentation.components.common.ErrorCard
import com.example.xueya.presentation.components.common.LoadingCard

/**
 * 首页界面
 * 
 * 应用的主界面，展示用户的血压监测概览信息
 * 包括健康状况、最新记录、今日统计、本周概览等核心信息
 * 提供导航到其他功能页面的入口
 * 
 * @param onNavigateToAddRecord 导航到添加记录页面的回调函数
 * @param onNavigateToHistory 导航到历史记录页面的回调函数
 * @param onNavigateToStatistics 导航到统计页面的回调函数
 * @param onNavigateToSettings 导航到设置页面的回调函数
 * @param modifier 修饰符
 * @param viewModel 首页视图模型，通过Hilt注入
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAddRecord: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 顶部操作栏
        item {
            TopActionBar(
                onRefresh = viewModel::refresh,
                onAddRecord = onNavigateToAddRecord,
                onSettings = onNavigateToSettings
            )
        }

        // 错误提示
        uiState.error?.let { error ->
            item {
                ErrorCard(
                    error = error,
                    onDismiss = viewModel::clearError
                )
            }
        }

        // 加载状态
        if (uiState.isLoading) {
            item {
                LoadingCard()
            }
        }

        // 健康状况卡片
        item {
            HealthStatusCard(
                healthStatus = uiState.healthStatus,
                todayMeasurementCount = uiState.todayMeasurementCount,
                weeklyMeasurementCount = uiState.weeklyMeasurementCount
            )
        }

        // 最新记录卡片
        uiState.latestRecord?.let { record ->
            item {
                LatestRecordCard(
                    record = record,
                    onClick = onNavigateToHistory
                )
            }
        }

        // 今日统计卡片
        if (uiState.todayRecords.isNotEmpty()) {
            item {
                TodayStatisticsCard(
                    averageBP = uiState.todayAverageBP,
                    averageHeartRate = uiState.todayAverageHeartRate,
                    measurementCount = uiState.todayMeasurementCount
                )
            }
        }

        // 本周统计概览
        if (uiState.weeklyStatistics.totalCount > 0) {
            item {
                WeeklyOverviewCard(
                    statistics = uiState.weeklyStatistics,
                    onClick = onNavigateToStatistics
                )
            }
        }

        // 快速操作
        item {
            QuickActionsCard(
                onAddRecord = onNavigateToAddRecord,
                onViewHistory = onNavigateToHistory,
                onViewStatistics = onNavigateToStatistics
            )
        }

        // 空状态提示
        if (!uiState.hasData && !uiState.isLoading) {
            item {
                HomeEmptyStateCard(onAddRecord = onNavigateToAddRecord)
            }
        }
    }
}

/**
 * 顶部操作栏组件
 * 
 * 显示页面标题和主要操作按钮
 * 包括设置、刷新和添加记录按钮
 * 
 * @param onRefresh 刷新数据的回调函数
 * @param onAddRecord 导航到添加记录页面的回调函数
 * @param onSettings 导航到设置页面的回调函数
 */
@Composable
private fun TopActionBar(
    onRefresh: () -> Unit,
    onAddRecord: () -> Unit,
    onSettings: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "血压监测",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        
        Row(
            modifier = Modifier.wrapContentWidth(),// ← 新增：按钮区域不抢空间
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onSettings) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(R.string.nav_settings)
                )
            }
            
            IconButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "刷新"
                )
            }

            FilledTonalButton(
                onClick = onAddRecord,
                modifier = Modifier.height(32.dp),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = stringResource(R.string.nav_add_record),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}