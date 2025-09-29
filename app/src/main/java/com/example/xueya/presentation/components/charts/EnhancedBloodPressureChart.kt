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
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.graphics.Paint
import android.graphics.Typeface
import com.example.xueya.domain.analysis.BloodPressureAnomalyDetector
import com.example.xueya.domain.analysis.TrendAnalyzer
import com.example.xueya.domain.model.BloodPressureData
import com.example.xueya.ui.theme.BloodPressureChartColors
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
    val chartColors = BloodPressureChartColors.current()
    
    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val leftRightPadding = 60.dp.toPx() // 左右padding为Y轴标签留出空间
        val topPadding = 20.dp.toPx() // 顶部padding
        val bottomPadding = 70.dp.toPx() // 底部padding为X轴标签和标题留出空间
        
        val chartWidth = canvasWidth - leftRightPadding * 2
        val chartHeight = canvasHeight - topPadding - bottomPadding
        
        // 绘制网格线
        drawGridLines(
            chartColors = chartColors,
            left = leftRightPadding,
            top = topPadding,
            width = chartWidth,
            height = chartHeight
        )
        
        // 绘制坐标轴标签
        drawAxisLabels(
            chartColors = chartColors,
            data = data,
            left = leftRightPadding,
            top = topPadding,
            width = chartWidth,
            height = chartHeight
        )
        
        // 绘制参考线
        drawReferenceLines(
            chartColors = chartColors,
            left = leftRightPadding,
            top = topPadding,
            width = chartWidth,
            height = chartHeight
        )
        
        // 绘制数据点
        drawDataPoints(
            chartColors = chartColors,
            data = data,
            anomalies = anomalies,
            left = leftRightPadding,
            top = topPadding,
            width = chartWidth,
            height = chartHeight,
            onDataPointClick = onDataPointClick
        )
        
        // 绘制趋势线
        trendResult?.let { trend ->
            drawTrendLine(
                chartColors = chartColors,
                trend = trend,
                data = data,
                left = leftRightPadding,
                top = topPadding,
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
    chartColors: com.example.xueya.ui.theme.BloodPressureChartColorScheme,
    left: Float,
    top: Float,
    width: Float,
    height: Float
) {
    // 主网格线 - 使用主题颜色
    val majorGridColor = chartColors.majorGridLine
    val majorStrokeWidth = 1.dp.toPx()
    
    // 次网格线 - 使用主题颜色
    val minorGridColor = chartColors.minorGridLine
    val minorStrokeWidth = 0.5.dp.toPx()
    
    // 水平网格线 (血压值)
    val pressureValues = listOf(80, 100, 120, 140, 160, 180, 200)
    pressureValues.forEachIndexed { index, pressure ->
        val y = top + height - (height * index / (pressureValues.size - 1))
        
        // 主要刻度线 (每40mmHg一条)
        val isMajorLine = pressure % 40 == 0
        val color = if (isMajorLine) majorGridColor else minorGridColor
        val strokeWidth = if (isMajorLine) majorStrokeWidth else minorStrokeWidth
        
        drawLine(
            color = color,
            start = Offset(left, y),
            end = Offset(left + width, y),
            strokeWidth = strokeWidth
        )
    }
    
    // 垂直网格线 (时间) - 更加智能的分布
    val verticalGridCount = 6 // 减少垂直网格线数量
    for (i in 0..verticalGridCount) {
        val x = left + (width * i / verticalGridCount)
        
        // 边界线使用主网格线样式
        val isBorderLine = i == 0 || i == verticalGridCount
        val color = if (isBorderLine) majorGridColor else minorGridColor
        val strokeWidth = if (isBorderLine) majorStrokeWidth else minorStrokeWidth
        
        drawLine(
            color = color,
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
    chartColors: com.example.xueya.ui.theme.BloodPressureChartColorScheme,
    left: Float,
    top: Float,
    width: Float,
    height: Float
) {
    val maxPressure = 200.0
    val minPressure = 80.0
    
    // 血压分类参考线数据 - 使用主题颜色
    val referenceLines = listOf(
        Triple(120.0, chartColors.normalReference, "正常血压上限"),
        Triple(140.0, chartColors.elevatedReference, "高血压1期"),
        Triple(160.0, chartColors.stage1Reference, "高血压2期"),
        Triple(180.0, chartColors.stage2Reference, "高血压危象")
    )
    
    referenceLines.forEach { (pressure, color, _) ->
        val y = top + height - ((pressure - minPressure) / (maxPressure - minPressure) * height).toFloat()
        
        // 绘制参考线
    drawLine(
            color = color.copy(alpha = 0.6f),
            start = Offset(left, y),
            end = Offset(left + width, y),
            strokeWidth = 2.dp.toPx(),
            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                floatArrayOf(10f, 5f), 0f
            )
        )
        
        // 绘制参考线标签 (右侧)
        val textPaint = Paint().apply {
            this.color = color.copy(alpha = 0.8f).toArgb()
            textSize = 9.dp.toPx()
            typeface = Typeface.DEFAULT
            isAntiAlias = true
        }
        
        drawContext.canvas.nativeCanvas.drawText(
            "${pressure.toInt()}",
            left + width + 5.dp.toPx(),
            y + 3.dp.toPx(),
            textPaint
        )
    }
    
    // 添加理想血压区域背景
    val idealTop = top + height - ((120.0 - minPressure) / (maxPressure - minPressure) * height).toFloat()
    val idealBottom = top + height - ((90.0 - minPressure) / (maxPressure - minPressure) * height).toFloat()
    
    drawRect(
        color = chartColors.idealZoneBackground,
        topLeft = Offset(left, idealTop),
        size = androidx.compose.ui.geometry.Size(width, idealBottom - idealTop)
    )
}

/**
 * 绘制数据点
 */
private fun DrawScope.drawDataPoints(
    chartColors: com.example.xueya.ui.theme.BloodPressureChartColorScheme,
    data: List<BloodPressureData>,
    anomalies: List<BloodPressureAnomalyDetector.AnomalyPoint>,
    left: Float,
    top: Float,
    width: Float,
    height: Float,
    @Suppress("UNUSED_PARAMETER") onDataPointClick: (BloodPressureData) -> Unit // 预留给未来的交互功能
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
                BloodPressureAnomalyDetector.Severity.HIGH -> chartColors.highAnomalyPoint to 4.dp.toPx()
                BloodPressureAnomalyDetector.Severity.MEDIUM -> chartColors.mediumAnomalyPoint to 3.dp.toPx()
                BloodPressureAnomalyDetector.Severity.LOW -> chartColors.lowAnomalyPoint to 2.dp.toPx()
            }
        } else {
            chartColors.normalDataPoint to 2.dp.toPx()
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
    chartColors: com.example.xueya.ui.theme.BloodPressureChartColorScheme,
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
        TrendAnalyzer.TrendDirection.INCREASING -> chartColors.stage1Reference
        TrendAnalyzer.TrendDirection.DECREASING -> chartColors.normalReference
        TrendAnalyzer.TrendDirection.STABLE -> chartColors.axisLabelText
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
 * 图表说明 - 增强版
 */
@Composable
private fun ChartLegend(
    anomalyCount: Int,
    trendDirection: TrendAnalyzer.TrendDirection?
) {
    val chartColors = BloodPressureChartColors.current()
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // 第一行：数据点说明
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 正常数据点
            LegendItem(
                color = chartColors.normalDataPoint,
                label = "正常数据",
                shape = "circle"
            )
            
            // 异常数据点
            if (anomalyCount > 0) {
                LegendItem(
                    color = chartColors.highAnomalyPoint,
                    label = "异常数据 ($anomalyCount 个)",
                    shape = "circle"
                )
            }
            
            // 趋势线
            trendDirection?.let { direction ->
                val (text, color) = when (direction) {
                    TrendAnalyzer.TrendDirection.INCREASING -> "↗ 上升趋势" to chartColors.stage1Reference
                    TrendAnalyzer.TrendDirection.DECREASING -> "↘ 下降趋势" to chartColors.normalReference
                    TrendAnalyzer.TrendDirection.STABLE -> "→ 稳定趋势" to chartColors.axisLabelText
                }
                
                LegendItem(
                color = color,
                    label = text,
                    shape = "line"
                )
            }
        }
        
        // 第二行：参考线说明
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendItem(
                color = chartColors.normalReference,
                label = "正常(120)",
                shape = "dash"
            )
            LegendItem(
                color = chartColors.elevatedReference,
                label = "偏高(140)",
                shape = "dash"
            )
            LegendItem(
                color = chartColors.stage1Reference,
                label = "1期(160)",
                shape = "dash"
            )
            LegendItem(
                color = chartColors.stage2Reference,
                label = "2期(180)",
                shape = "dash"
            )
        }
    }
}

/**
 * 图例项组件
 */
@Composable
private fun LegendItem(
    color: Color,
    label: String,
    shape: String, // "circle", "line", "dash"
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        when (shape) {
            "circle" -> {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(color, androidx.compose.foundation.shape.CircleShape)
                )
            }
            "line" -> {
                Box(
                    modifier = Modifier
                        .width(12.dp)
                        .height(2.dp)
                        .background(color)
                )
            }
            "dash" -> {
                Canvas(
                    modifier = Modifier
                        .width(12.dp)
                        .height(2.dp)
                ) {
                    drawLine(
                        color = color,
                        start = Offset(0f, size.height / 2),
                        end = Offset(size.width, size.height / 2),
                        strokeWidth = 2.dp.toPx(),
                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                            floatArrayOf(4f, 2f), 0f
                        )
                    )
                }
            }
        }
        
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
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

/**
 * 绘制坐标轴标签
 */
private fun DrawScope.drawAxisLabels(
    chartColors: com.example.xueya.ui.theme.BloodPressureChartColorScheme,
    data: List<BloodPressureData>,
    left: Float,
    top: Float,
    width: Float,
    height: Float
) {
    val textPaint = Paint().apply {
        color = chartColors.axisLabelText.toArgb()
        textSize = 12.dp.toPx()
        typeface = Typeface.DEFAULT
        isAntiAlias = true
    }
    
    // Y轴标签 (血压值)
    val pressureValues = listOf(80, 100, 120, 140, 160, 180, 200)
    pressureValues.forEachIndexed { index, pressure ->
        val y = top + height - (height * index / (pressureValues.size - 1))
        
        // 绘制血压数值标签
        drawContext.canvas.nativeCanvas.drawText(
            "${pressure}",
            left - 30.dp.toPx(),
            y + 4.dp.toPx(), // 稍微向下偏移以居中对齐
            textPaint
        )
        
        // 绘制单位标签 (只在最下方显示一次)
        if (index == 0) {
            drawContext.canvas.nativeCanvas.drawText(
                "mmHg",
                left - 30.dp.toPx(),
                y + 20.dp.toPx(),
                textPaint.apply { textSize = 10.dp.toPx() }
            )
        }
    }
    
    // X轴标签 (时间) - 智能显示格式
    if (data.isNotEmpty()) {
        val firstTime = data.first().measureTime
        val lastTime = data.last().measureTime
        val timeSpan = java.time.Duration.between(firstTime, lastTime)
        
        // 根据时间跨度选择合适的格式
        val (timeFormatter, maxLabels) = when {
            timeSpan.toDays() <= 1 -> DateTimeFormatter.ofPattern("HH:mm") to 6 // 小时:分钟
            timeSpan.toDays() <= 7 -> DateTimeFormatter.ofPattern("MM/dd") to 7 // 月/日
            timeSpan.toDays() <= 30 -> DateTimeFormatter.ofPattern("MM/dd") to 5 // 月/日
            else -> DateTimeFormatter.ofPattern("MM/dd") to 4 // 月/日
        }
        
        val step = maxOf(1, data.size / maxLabels)
        
        data.forEachIndexed { index, bp ->
            if (index % step == 0 || index == data.size - 1) {
                val x = left + (index.toFloat() / (data.size - 1).coerceAtLeast(1)) * width
                val timeText = bp.measureTime.format(timeFormatter)
                
                // 计算文本宽度以更好地居中
                val textWidth = textPaint.measureText(timeText)
                
                drawContext.canvas.nativeCanvas.drawText(
                    timeText,
                    x - textWidth / 2, // 真正的居中对齐
                    top + height + 25.dp.toPx(),
                    textPaint
                )
            }
        }
        
        // 添加时间轴标题
        val axisTitle = when {
            timeSpan.toDays() <= 1 -> "时间"
            timeSpan.toDays() <= 30 -> "日期"
            else -> "日期"
        }
        
        drawContext.canvas.nativeCanvas.drawText(
            axisTitle,
            left + width / 2 - textPaint.measureText(axisTitle) / 2,
            top + height + 45.dp.toPx(),
            textPaint.apply { 
                textSize = 10.dp.toPx()
                color = chartColors.axisLabelText.toArgb()
            }
        )
    }
}
