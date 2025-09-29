package com.example.xueya.presentation.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.xueya.domain.analysis.BloodPressureAnomalyDetector
import com.example.xueya.domain.analysis.TrendAnalyzer
import com.example.xueya.domain.model.BloodPressureData
import java.time.format.DateTimeFormatter

/**
 * 增强版血压图表组件
 * 支持异常点标注、趋势线显示、交互功能
 */
@Composable
fun EnhancedBloodPressureChart(
    data: List<BloodPressureData>,
    anomalies: List<BloodPressureAnomalyDetector.AnomalyPoint>,
    trendResult: TrendAnalyzer.TrendResult?,
    modifier: Modifier = Modifier,
    onDataPointClick: (BloodPressureData) -> Unit = {},
    height: androidx.compose.ui.unit.Dp = 300.dp
) {
    if (data.isEmpty()) {
        EmptyChartState(modifier = modifier.height(height))
        return
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // 图表标题
            Text(
                text = "血压趋势图",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // 图表说明
            ChartLegend(
                anomalyCount = anomalies.size,
                trendDirection = trendResult?.direction
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 图表画布
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                ChartCanvas(
                    data = data,
                    anomalies = anomalies,
                    trendResult = trendResult,
                    onDataPointClick = onDataPointClick,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

/**
 * 图表画布
 */
@Composable
private fun ChartCanvas(
    data: List<BloodPressureData>,
    anomalies: List<BloodPressureAnomalyDetector.AnomalyPoint>,
    trendResult: TrendAnalyzer.TrendResult?,
    onDataPointClick: (BloodPressureData) -> Unit,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val padding = 40.dp.toPx()
        
        val chartWidth = canvasWidth - padding * 2
        val chartHeight = canvasHeight - padding * 2
        
        // 绘制网格线
        drawGridLines(
            left = padding,
            top = padding,
            width = chartWidth,
            height = chartHeight
        )
        
        // 绘制参考线
        drawReferenceLines(
            left = padding,
            top = padding,
            width = chartWidth,
            height = chartHeight
        )
        
        // 绘制数据点
        drawDataPoints(
            data = data,
            anomalies = anomalies,
            left = padding,
            top = padding,
            width = chartWidth,
            height = chartHeight,
            onDataPointClick = onDataPointClick
        )
        
        // 绘制趋势线
        trendResult?.let { trend ->
            drawTrendLine(
                trend = trend,
                data = data,
                left = padding,
                top = padding,
                width = chartWidth,
                height = chartHeight
            )
        }
    }
}

/**
 * 绘制网格线
 */
private fun DrawScope.drawGridLines(
    left: Float,
    top: Float,
    width: Float,
    height: Float
) {
    val gridColor = Color.Gray.copy(alpha = 0.3f)
    val strokeWidth = 1.dp.toPx()
    
    // 水平网格线 (血压值)
    for (i in 0..6) {
        val y = top + (height * i / 6)
        val pressure = 80 + (6 - i) * 20 // 80-200 mmHg
        drawLine(
            color = gridColor,
            start = Offset(left, y),
            end = Offset(left + width, y),
            strokeWidth = strokeWidth
        )
    }
    
    // 垂直网格线 (时间)
    for (i in 0..7) {
        val x = left + (width * i / 7)
        drawLine(
            color = gridColor,
            start = Offset(x, top),
            end = Offset(x, top + height),
            strokeWidth = strokeWidth
        )
    }
}

/**
 * 绘制参考线
 */
private fun DrawScope.drawReferenceLines(
    left: Float,
    top: Float,
    width: Float,
    height: Float
) {
    val normalSystolic = 120.0
    val normalDiastolic = 80.0
    val maxPressure = 200.0
    val minPressure = 80.0
    
    // 正常血压参考线
    val normalY = top + height - ((normalSystolic - minPressure) / (maxPressure - minPressure) * height).toFloat()
    drawLine(
        color = Color.Green.copy(alpha = 0.7f),
        start = Offset(left, normalY),
        end = Offset(left + width, normalY),
        strokeWidth = 2.dp.toPx()
    )
}

/**
 * 绘制数据点
 */
private fun DrawScope.drawDataPoints(
    data: List<BloodPressureData>,
    anomalies: List<BloodPressureAnomalyDetector.AnomalyPoint>,
    left: Float,
    top: Float,
    width: Float,
    height: Float,
    onDataPointClick: (BloodPressureData) -> Unit
) {
    val maxPressure = 200.0
    val minPressure = 80.0
    val anomalyMap = anomalies.associateBy { it.data }
    
    data.forEachIndexed { index, bp ->
        val x = (left + (width * index / (data.size - 1).coerceAtLeast(1))).toFloat()
        val y = (top + height - ((bp.systolic.toFloat() - minPressure) / (maxPressure - minPressure) * height)).toFloat()
        
        val isAnomaly = anomalyMap.containsKey(bp)
        val anomaly = anomalyMap[bp]
        
        val (color, radius) = if (isAnomaly && anomaly != null) {
            when (anomaly.severity) {
                BloodPressureAnomalyDetector.Severity.HIGH -> Color.Red to 8.dp.toPx()
                BloodPressureAnomalyDetector.Severity.MEDIUM -> Color(0xFFFF8C00) to 6.dp.toPx()
                BloodPressureAnomalyDetector.Severity.LOW -> Color.Yellow to 4.dp.toPx()
            }
        } else {
            Color.Blue to 4.dp.toPx()
        }
        
        // 绘制外圈（异常点）
        if (isAnomaly) {
            drawCircle(
                color = color,
                radius = radius,
                center = Offset(x, y)
            )
            // 绘制内圈
            drawCircle(
                color = Color.White,
                radius = radius * 0.6f,
                center = Offset(x, y)
            )
        } else {
            // 正常点
            drawCircle(
                color = color,
                radius = radius,
                center = Offset(x, y)
            )
        }
    }
}

/**
 * 绘制趋势线
 */
private fun DrawScope.drawTrendLine(
    trend: TrendAnalyzer.TrendResult,
    data: List<BloodPressureData>,
    left: Float,
    top: Float,
    width: Float,
    height: Float
) {
    val maxPressure = 200.0
    val minPressure = 80.0
    
    val color = when (trend.direction) {
        TrendAnalyzer.TrendDirection.INCREASING -> Color.Red
        TrendAnalyzer.TrendDirection.DECREASING -> Color.Green
        TrendAnalyzer.TrendDirection.STABLE -> Color.Gray
    }
    
    val startX = left
    val endX = left + width
    val startY = top + height - ((trend.intercept - minPressure) / (maxPressure - minPressure) * height).toFloat()
    val endY = top + height - (((trend.slope * (data.size - 1) + trend.intercept) - minPressure) / (maxPressure - minPressure) * height).toFloat()
    
    drawLine(
        color = color,
        start = Offset(startX, startY),
        end = Offset(endX, endY),
        strokeWidth = 3.dp.toPx()
    )
}

/**
 * 图表说明
 */
@Composable
private fun ChartLegend(
    anomalyCount: Int,
    trendDirection: TrendAnalyzer.TrendDirection?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "蓝点: 正常数据 | 异常点: $anomalyCount 个",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        trendDirection?.let { direction ->
            val (text, color) = when (direction) {
                TrendAnalyzer.TrendDirection.INCREASING -> "↗ 上升" to Color.Red
                TrendAnalyzer.TrendDirection.DECREASING -> "↘ 下降" to Color.Green
                TrendAnalyzer.TrendDirection.STABLE -> "→ 稳定" to Color.Gray
            }
            
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = color,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * 空图表状态
 */
@Composable
private fun EmptyChartState(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "暂无数据",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
