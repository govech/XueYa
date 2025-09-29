package com.example.xueya.presentation.components.charts

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.xueya.domain.analysis.BloodPressureStatistics
import com.example.xueya.domain.analysis.TrendAnalyzer

/**
 * 血压统计信息卡片
 * 显示关键统计指标、异常数量、趋势方向等
 */
@Composable
fun BloodPressureStatsCard(
    statistics: BloodPressureStatistics,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 标题
            Text(
                text = "统计概览",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            // 统计指标行
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // 平均血压
                StatItem(
                    title = "平均血压",
                    value = "${statistics.averageSystolic.toInt()}/${statistics.averageDiastolic.toInt()}",
                    color = Color.Blue,
                    modifier = Modifier.weight(1f)
                )
                
                // 异常次数
                StatItem(
                    title = "异常次数",
                    value = "${statistics.anomalyCount}",
                    color = if (statistics.anomalyCount > 0) Color.Red else Color.Green,
                    modifier = Modifier.weight(1f)
                )
                
                // 趋势方向
                StatItem(
                    title = "趋势",
                    value = getTrendDisplayText(statistics.trendResult?.direction),
                    color = getTrendColor(statistics.trendResult?.direction),
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 血压范围统计
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // 最高血压
                StatItem(
                    title = "最高血压",
                    value = "${statistics.maxSystolic.toInt()}/${statistics.maxDiastolic.toInt()}",
                    color = Color.Red,
                    modifier = Modifier.weight(1f)
                )
                
                // 最低血压
                StatItem(
                    title = "最低血压",
                    value = "${statistics.minSystolic.toInt()}/${statistics.minDiastolic.toInt()}",
                    color = Color.Green,
                    modifier = Modifier.weight(1f)
                )
                
                // 记录总数
                StatItem(
                    title = "记录总数",
                    value = "${statistics.recordCount}",
                    color = Color.Gray,
                    modifier = Modifier.weight(1f)
                )
            }
            
            // 健康状态描述
            if (statistics.recordCount > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = statistics.getHealthStatusDescription(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

/**
 * 统计项组件
 */
@Composable
private fun StatItem(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

/**
 * 获取趋势显示文本
 */
private fun getTrendDisplayText(direction: TrendAnalyzer.TrendDirection?): String {
    return when (direction) {
        TrendAnalyzer.TrendDirection.INCREASING -> "↗ 上升"
        TrendAnalyzer.TrendDirection.DECREASING -> "↘ 下降"
        TrendAnalyzer.TrendDirection.STABLE -> "→ 稳定"
        null -> "未知"
    }
}

/**
 * 获取趋势颜色
 */
private fun getTrendColor(direction: TrendAnalyzer.TrendDirection?): Color {
    return when (direction) {
        TrendAnalyzer.TrendDirection.INCREASING -> Color.Red
        TrendAnalyzer.TrendDirection.DECREASING -> Color.Green
        TrendAnalyzer.TrendDirection.STABLE -> Color.Gray
        null -> Color.Gray
    }
}
