package com.example.xueya.presentation.screens.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.xueya.domain.usecase.DetailedStatistics
import com.example.xueya.domain.usecase.HealthStatus
import com.example.xueya.domain.usecase.TrendData
import com.example.xueya.presentation.components.common.StatisticItem
import kotlin.math.cos
import kotlin.math.sin

/**
 * 概览卡片
 */
@Composable
fun OverviewCard(
    statistics: DetailedStatistics,
    healthStatus: HealthStatus,
    timeRange: TimeRange
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${timeRange.displayName}概览",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                HealthStatusIndicator(healthStatus = healthStatus)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 主要指标
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticItem(
                    label = "总测量",
                    value = "${statistics.totalCount}次",
                    color = MaterialTheme.colorScheme.primary
                )
                
                StatisticItem(
                    label = "平均血压",
                    value = "${statistics.averageSystolic.toInt()}/${statistics.averageDiastolic.toInt()}",
                    color = MaterialTheme.colorScheme.secondary
                )
                
                StatisticItem(
                    label = "平均心率",
                    value = "${statistics.averageHeartRate.toInt()} bpm",
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
            
            if (statistics.totalCount > 0) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // 正常率和风险率
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatisticItem(
                        label = "正常率",
                        value = "${(statistics.normalPercentage * 100).toInt()}%",
                        color = Color(0xFF4CAF50)
                    )
                    
                    StatisticItem(
                        label = "风险率",
                        value = "${(statistics.riskPercentage * 100).toInt()}%",
                        color = if (statistics.riskPercentage > 0.3f) Color(0xFFF44336) else Color(0xFFFF9800)
                    )
                }
            }
        }
    }
}

/**
 * 健康状况指示器
 */
@Composable
private fun HealthStatusIndicator(healthStatus: HealthStatus) {
    val color = when (healthStatus) {
        HealthStatus.NO_DATA -> MaterialTheme.colorScheme.onSurfaceVariant
        HealthStatus.NORMAL -> Color(0xFF4CAF50)
        HealthStatus.ELEVATED -> Color(0xFFFF9800)
        HealthStatus.MODERATE_RISK -> Color(0xFFFF5722)
        HealthStatus.HIGH_RISK -> Color(0xFFF44336)
        HealthStatus.CRITICAL -> Color(0xFF9C27B0)
    }
    
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(4.dp))
        
        Text(
            text = healthStatus.displayName,
            style = MaterialTheme.typography.bodyMedium,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}



/**
 * 血压范围统计卡片
 */
@Composable
fun BloodPressureRangesCard(
    ranges: BloodPressureRanges
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "血压范围统计",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 收缩压范围
            RangeStatisticRow(
                label = "收缩压",
                min = ranges.systolicMin,
                max = ranges.systolicMax,
                avg = ranges.systolicAvg,
                unit = "mmHg"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 舒张压范围
            RangeStatisticRow(
                label = "舒张压",
                min = ranges.diastolicMin,
                max = ranges.diastolicMax,
                avg = ranges.diastolicAvg,
                unit = "mmHg"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 心率平均值
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "平均心率",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${ranges.heartRateAvg.toInt()} bpm",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * 范围统计行
 */
@Composable
private fun RangeStatisticRow(
    label: String,
    min: Int,
    max: Int,
    avg: Double,
    unit: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "最低: $min $unit",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "平均: ${avg.toInt()} $unit",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "最高: $max $unit",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

/**
 * 分类分布卡片
 */
@Composable
fun CategoryDistributionCard(
    categories: List<CategoryData>,
    totalCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "血压分类分布",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 简单的饼图（使用圆环表示）
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 饼图
                Box(
                    modifier = Modifier.size(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    SimplePieChart(
                        categories = categories,
                        totalCount = totalCount
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // 图例
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    categories.forEach { category ->
                        CategoryLegendItem(
                            category = category,
                            totalCount = totalCount
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}

/**
 * 简单饼图
 */
@Composable
private fun SimplePieChart(
    categories: List<CategoryData>,
    totalCount: Int
) {
    Canvas(
        modifier = Modifier.size(120.dp)
    ) {
        val center = androidx.compose.ui.geometry.Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2 * 0.8f
        
        var startAngle = 0f
        
        categories.forEach { category ->
            val sweepAngle = (category.count.toFloat() / totalCount) * 360f
            val color = Color(android.graphics.Color.parseColor(category.color))
            
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = androidx.compose.ui.geometry.Offset(
                    center.x - radius,
                    center.y - radius
                ),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
            )
            
            startAngle += sweepAngle
        }
    }
}

/**
 * 分类图例项
 */
@Composable
private fun CategoryLegendItem(
    category: CategoryData,
    totalCount: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 颜色指示器
        Surface(
            modifier = Modifier.size(12.dp),
            color = Color(android.graphics.Color.parseColor(category.color)),
            shape = androidx.compose.foundation.shape.CircleShape
        ) {}
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = category.name,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f)
        )
        
        Text(
            text = "${category.count}次 (${((category.count.toFloat() / totalCount) * 100).toInt()}%)",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}