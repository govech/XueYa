package com.example.xueya.presentation.screens.statistics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * 统计分析界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val healthAdvice by viewModel.healthAdvice.collectAsState()
    val trendAnalysis by viewModel.trendAnalysis.collectAsState()
    val isGeneratingAdvice by viewModel.isGeneratingAdvice.collectAsState()
    val isAnalyzingTrend by viewModel.isAnalyzingTrend.collectAsState()
    val adviceError by viewModel.adviceError.collectAsState()
    val trendError by viewModel.trendError.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            StatisticsTopAppBar(
                selectedTimeRange = uiState.selectedTimeRange,
                onTimeRangeClick = viewModel::showTimeRangePicker,
                onRefresh = viewModel::refresh
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 错误提示
            uiState.error?.let { error ->
                ErrorCard(
                    error = error,
                    onDismiss = viewModel::clearError
                )
            }

            // 加载状态
            if (uiState.isLoading) {
                LoadingCard()
            }

            // 主要统计数据
            if (uiState.hasData && !uiState.isLoading) {
                // 概览卡片
                OverviewCard(
                    statistics = uiState.currentStatistics,
                    healthStatus = uiState.healthStatus,
                    timeRange = uiState.selectedTimeRange
                )

                // 血压范围统计
                BloodPressureRangesCard(
                    ranges = uiState.bloodPressureRanges
                )

                // 血压分类分布
                if (uiState.categoryDistribution.isNotEmpty()) {
                    CategoryDistributionCard(
                        categories = uiState.categoryDistribution,
                        totalCount = uiState.recordCount
                    )
                }

                // AI 健康建议
                com.example.xueya.presentation.components.common.HealthAdviceCard(
                    healthAdvice = healthAdvice,
                    isLoading = isGeneratingAdvice,
                    error = adviceError,
                    onRefresh = viewModel::generateHealthAdvice
                )

                // AI 趋势分析
                com.example.xueya.presentation.components.common.TrendAnalysisCard(
                    trendAnalysis = trendAnalysis,
                    isLoading = isAnalyzingTrend,
                    error = trendError,
                    onRefresh = viewModel::analyzeBloodPressureTrend
                )
            }

            // 空状态
            if (!uiState.hasData && !uiState.isLoading) {
                EmptyStatisticsState()
            }

            // 底部间距
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // 时间范围选择器
    if (uiState.showTimeRangePicker) {
        TimeRangePickerDialog(
            selectedTimeRange = uiState.selectedTimeRange,
            customStartDate = uiState.customStartDate,
            customEndDate = uiState.customEndDate,
            onTimeRangeSelected = { timeRange ->
                viewModel.updateTimeRange(timeRange)
                viewModel.hideTimeRangePicker()
            },
            onCustomDateRangeSelected = { startDate, endDate ->
                viewModel.updateCustomDateRange(startDate, endDate)
                viewModel.hideTimeRangePicker()
            },
            onDismiss = viewModel::hideTimeRangePicker
        )
    }
}

/**
 * 顶部应用栏
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatisticsTopAppBar(
    selectedTimeRange: TimeRange,
    onTimeRangeClick: () -> Unit,
    onRefresh: () -> Unit
) {
    TopAppBar(
        title = { Text("统计分析") },
        actions = {
            // 时间范围选择按钮
            OutlinedButton(
                onClick = onTimeRangeClick,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = selectedTimeRange.displayName,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            // 刷新按钮
            IconButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "刷新"
                )
            }
        }
    )
}

/**
 * 错误卡片
 */
@Composable
private fun ErrorCard(
    error: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f)
            )
            
            TextButton(onClick = onDismiss) {
                Text("关闭")
            }
        }
    }
}

/**
 * 加载卡片
 */
@Composable
private fun LoadingCard() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = "正在分析数据...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 空状态
 */
@Composable
private fun EmptyStatisticsState() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "暂无统计数据",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "在选定的时间范围内没有血压记录\n请添加一些记录后再查看统计分析",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}