package com.example.xueya.presentation.components.bloodpressure

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.xueya.domain.usecase.TrendData
import kotlin.math.max
import kotlin.math.min

/**
 * 血压趋势图表
 */
@Composable
fun BloodPressureTrendChart(
    trendData: List<TrendData>,
    modifier: Modifier = Modifier,
    showSystolic: Boolean = true,
    showDiastolic: Boolean = true,
    height: androidx.compose.ui.unit.Dp = 200.dp
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 标题和图例
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "血压趋势",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (showSystolic) {
                        ChartLegendItem(
                            label = "收缩压",
                            color = Color(0xFFF44336)
                        )
                    }
                    
                    if (showDiastolic) {
                        ChartLegendItem(
                            label = "舒张压",
                            color = Color(0xFF2196F3)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (trendData.isNotEmpty()) {
                // 趋势图
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height)
                ) {
                    drawBloodPressureTrend(
                        trendData = trendData,
                        showSystolic = showSystolic,
                        showDiastolic = showDiastolic
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 时间轴标签
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (trendData.size > 1) "${trendData.size}天前" else "今天",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = "今天",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                // 空状态
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "暂无趋势数据",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/**
 * 趋势指示器
 */
@Composable
fun TrendIndicator(
    trend: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val (icon, color, description) = when (trend.lowercase()) {
        "improving", "下降" -> Triple(
            Icons.Default.Check,
            Color(0xFF4CAF50),
            "趋势向好"
        )
        "worsening", "上升" -> Triple(
            Icons.Default.Add,
            Color(0xFFF44336),
            "需要关注"
        )
        else -> Triple(
            Icons.Default.Settings,
            MaterialTheme.colorScheme.onSurfaceVariant,
            "趋势稳定"
        )
    }
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = color
                )
                
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * 分类分布环形图
 */
@Composable
fun CategoryDistributionChart(
    categories: List<CategoryData>,
    totalCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
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
            
            if (categories.isNotEmpty() && totalCount > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 环形图
                    Box(
                        modifier = Modifier.size(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(
                            modifier = Modifier.size(120.dp)
                        ) {
                            drawCategoryDistribution(
                                categories = categories,
                                totalCount = totalCount
                            )
                        }
                        
                        // 中心显示总数
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "$totalCount",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "总记录",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    // 图例
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { category ->
                            CategoryLegendItem(
                                category = category,
                                totalCount = totalCount
                            )
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "暂无分布数据",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * 健康建议卡片
 */
@Composable
fun HealthAdviceCard(
    advice: List<String>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "健康建议",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            advice.forEachIndexed { index, tip ->
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Text(
                        text = "${index + 1}. ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Text(
                        text = tip,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/**
 * 图例项
 */
@Composable
private fun ChartLegendItem(
    label: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, androidx.compose.foundation.shape.CircleShape)
        )
        
        Spacer(modifier = Modifier.width(4.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(
                    Color(android.graphics.Color.parseColor(category.color)),
                    androidx.compose.foundation.shape.CircleShape
                )
        )
        
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

/**
 * 绘制血压趋势线
 */
private fun DrawScope.drawBloodPressureTrend(
    trendData: List<TrendData>,
    showSystolic: Boolean,
    showDiastolic: Boolean
) {
    if (trendData.isEmpty()) return
    
    val width = size.width
    val height = size.height
    val padding = 40f
    
    // 计算数据范围
    val allValues = mutableListOf<Float>()
    if (showSystolic) allValues.addAll(trendData.map { it.avgSystolic.toFloat() })
    if (showDiastolic) allValues.addAll(trendData.map { it.avgDiastolic.toFloat() })
    
    if (allValues.isEmpty()) return
    
    val minValue = allValues.minOrNull() ?: 0f
    val maxValue = allValues.maxOrNull() ?: 0f
    val valueRange = maxValue - minValue
    
    if (valueRange == 0f) return
    
    // 绘制收缩压趋势线
    if (showSystolic) {
        val systolicPath = Path()
        trendData.forEachIndexed { index, data ->
            val x = padding + (index.toFloat() / (trendData.size - 1)) * (width - 2 * padding)
            val y = height - padding - ((data.avgSystolic.toFloat() - minValue) / valueRange) * (height - 2 * padding)
            
            if (index == 0) {
                systolicPath.moveTo(x, y)
            } else {
                systolicPath.lineTo(x, y)
            }
        }
        
        drawPath(
            path = systolicPath,
            color = Color(0xFFF44336),
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        )
    }
    
    // 绘制舒张压趋势线
    if (showDiastolic) {
        val diastolicPath = Path()
        trendData.forEachIndexed { index, data ->
            val x = padding + (index.toFloat() / (trendData.size - 1)) * (width - 2 * padding)
            val y = height - padding - ((data.avgDiastolic.toFloat() - minValue) / valueRange) * (height - 2 * padding)
            
            if (index == 0) {
                diastolicPath.moveTo(x, y)
            } else {
                diastolicPath.lineTo(x, y)
            }
        }
        
        drawPath(
            path = diastolicPath,
            color = Color(0xFF2196F3),
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        )
    }
    
    // 绘制数据点
    trendData.forEachIndexed { index, data ->
        val x = padding + (index.toFloat() / (trendData.size - 1)) * (width - 2 * padding)
        
        if (showSystolic) {
            val systolicY = height - padding - ((data.avgSystolic.toFloat() - minValue) / valueRange) * (height - 2 * padding)
            drawCircle(
                color = Color(0xFFF44336),
                radius = 4.dp.toPx(),
                center = Offset(x, systolicY)
            )
        }
        
        if (showDiastolic) {
            val diastolicY = height - padding - ((data.avgDiastolic.toFloat() - minValue) / valueRange) * (height - 2 * padding)
            drawCircle(
                color = Color(0xFF2196F3),
                radius = 4.dp.toPx(),
                center = Offset(x, diastolicY)
            )
        }
    }
}

/**
 * 绘制分类分布环形图
 */
private fun DrawScope.drawCategoryDistribution(
    categories: List<CategoryData>,
    totalCount: Int
) {
    val center = Offset(size.width / 2, size.height / 2)
    val radius = size.minDimension / 2 * 0.7f
    val strokeWidth = 20.dp.toPx()
    
    var startAngle = -90f
    
    categories.forEach { category ->
        val sweepAngle = (category.count.toFloat() / totalCount) * 360f
        val color = Color(android.graphics.Color.parseColor(category.color))
        
        drawArc(
            color = color,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
            style = Stroke(width = strokeWidth)
        )
        
        startAngle += sweepAngle
    }
}

/**
 * 分类数据
 */
data class CategoryData(
    val name: String,
    val count: Int,
    val color: String
)