package com.example.xueya.presentation.screens.statistics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.xueya.R
import com.example.xueya.domain.usecase.TrendData
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * 趋势分析卡片
 */
@Composable
fun TrendAnalysisCard(
    trendData: List<TrendData>,
    trendAnalysis: TrendAnalysis
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "趋势分析",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 趋势指标
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TrendIndicatorRow(
                    label = "收缩压趋势",
                    trend = trendAnalysis.systolicTrend
                )
                
                TrendIndicatorRow(
                    label = "舒张压趋势",
                    trend = trendAnalysis.diastolicTrend
                )
                
                TrendIndicatorRow(
                    label = "心率趋势",
                    trend = trendAnalysis.heartRateTrend
                )
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                TrendIndicatorRow(
                    label = "整体趋势",
                    trend = trendAnalysis.overallTrend,
                    isOverall = true
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 改善建议
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = trendAnalysis.improvementSuggestion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

/**
 * 趋势指示器行
 */
@Composable
private fun TrendIndicatorRow(
    label: String,
    trend: TrendDirection,
    isOverall: Boolean = false
) {
    val icon = when (trend) {
        TrendDirection.IMPROVING -> Icons.Default.Check  // 使用对勾代表改善
        TrendDirection.STABLE -> Icons.Default.Settings
        TrendDirection.WORSENING -> Icons.Default.Add   // 使用加号代表恶化
    }
    
    val color = Color(android.graphics.Color.parseColor(trend.color))
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (isOverall) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isOverall) FontWeight.Medium else FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(if (isOverall) 24.dp else 20.dp)
            )
            
            Spacer(modifier = Modifier.width(4.dp))
            
            Text(
                text = trend.displayName,
                style = if (isOverall) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium,
                fontWeight = if (isOverall) FontWeight.Medium else FontWeight.Normal,
                color = color
            )
        }
    }
}

/**
 * 健康建议卡片
 */
@Composable
fun HealthRecommendationsCard(
    recommendations: List<String>
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "健康建议",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            recommendations.forEachIndexed { index, recommendation ->
                RecommendationItem(
                    index = index + 1,
                    recommendation = recommendation
                )
                
                if (index < recommendations.size - 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

/**
 * 建议项
 */
@Composable
private fun RecommendationItem(
    index: Int,
    recommendation: String
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 序号
        Surface(
            color = MaterialTheme.colorScheme.primary,
            shape = androidx.compose.foundation.shape.CircleShape,
            modifier = Modifier.size(24.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$index",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = recommendation,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * 时间范围选择器对话框
 */
@Composable
fun TimeRangePickerDialog(
    selectedTimeRange: TimeRange,
    customStartDate: LocalDate?,
    customEndDate: LocalDate?,
    onTimeRangeSelected: (TimeRange) -> Unit,
    onCustomDateRangeSelected: (LocalDate?, LocalDate?) -> Unit,
    onDismiss: () -> Unit
) {
    var tempStartDate by remember { mutableStateOf(customStartDate) }
    var tempEndDate by remember { mutableStateOf(customEndDate) }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.select_time_range),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 预设时间范围
                Column {
                    TimeRange.values().filter { it != TimeRange.CUSTOM }.forEach { timeRange ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedTimeRange == timeRange,
                                onClick = { onTimeRangeSelected(timeRange) }
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Text(
                                text = timeRange.displayName,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    
                    // 自定义范围
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedTimeRange == TimeRange.CUSTOM,
                            onClick = { 
                                if (tempStartDate != null && tempEndDate != null) {
                                    onCustomDateRangeSelected(tempStartDate, tempEndDate)
                                }
                            }
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = stringResource(R.string.custom_range),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    
                    // 自定义日期选择（简化版）
                    if (selectedTimeRange == TimeRange.CUSTOM) {
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.custom_date_range_development),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = stringResource(R.string.custom_date_range_current),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.dialog_cancel))
                    }
                }
            }
        }
    }
}