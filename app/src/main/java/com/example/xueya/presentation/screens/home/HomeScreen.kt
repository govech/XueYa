package com.example.xueya.presentation.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.xueya.domain.model.BloodPressureCategory
import com.example.xueya.domain.model.BloodPressureData
import com.example.xueya.domain.usecase.HealthStatus
import com.example.xueya.presentation.components.common.ErrorCard
import com.example.xueya.presentation.components.common.LoadingCard
import com.example.xueya.presentation.screens.home.*

import java.time.format.DateTimeFormatter

/**
 * 首页界面
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
            fontWeight = FontWeight.Bold
        )
        
        Row {
            IconButton(onClick = onSettings) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "设置"
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
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("添加记录")
            }
        }
    }
}