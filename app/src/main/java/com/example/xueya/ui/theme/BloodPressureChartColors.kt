package com.example.xueya.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme

/**
 * 血压图表专用颜色配置
 * 
 * 提供血压监测图表的统一颜色方案，确保与应用主题一致
 * 支持浅色和深色模式
 */
@Stable
object BloodPressureChartColors {
    
    /**
     * 获取当前主题下的血压图表颜色配置
     */
    @Composable
    fun current(): BloodPressureChartColorScheme {
        val isDark = isSystemInDarkTheme()
        
        return BloodPressureChartColorScheme(
            // 正常数据点颜色
            normalDataPoint = MaterialTheme.colorScheme.primary,
            
            // 血压线条颜色 - 深色模式下稍微调亮
            systolicLine = if (isDark) BloodPressureHigh1.copy(alpha = 0.9f) else BloodPressureHigh1,
            diastolicLine = MaterialTheme.colorScheme.tertiary,
            
            // 异常点颜色 - 深色模式下保持可见性
            lowAnomalyPoint = if (isDark) BloodPressureElevated.copy(alpha = 0.9f) else BloodPressureElevated,
            mediumAnomalyPoint = if (isDark) BloodPressureHigh1.copy(alpha = 0.9f) else BloodPressureHigh1,
            highAnomalyPoint = if (isDark) BloodPressureCrisis.copy(alpha = 0.9f) else BloodPressureCrisis,
            
            // 参考线颜色 - 深色模式下增强对比度
            normalReference = if (isDark) BloodPressureNormal.copy(alpha = 0.8f) else BloodPressureNormal,
            elevatedReference = if (isDark) BloodPressureElevated.copy(alpha = 0.8f) else BloodPressureElevated,
            stage1Reference = if (isDark) BloodPressureHigh1.copy(alpha = 0.8f) else BloodPressureHigh1,
            stage2Reference = if (isDark) BloodPressureHigh2.copy(alpha = 0.8f) else BloodPressureHigh2,
            crisisReference = if (isDark) BloodPressureCrisis.copy(alpha = 0.8f) else BloodPressureCrisis,
            
            // 网格线颜色 - 深色模式下调整透明度
            majorGridLine = MaterialTheme.colorScheme.onSurface.copy(alpha = if (isDark) 0.4f else 0.3f),
            minorGridLine = MaterialTheme.colorScheme.onSurface.copy(alpha = if (isDark) 0.2f else 0.1f),
            
            // 文本颜色
            axisLabelText = MaterialTheme.colorScheme.onSurfaceVariant,
            referenceLabelText = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            
            // 背景颜色 - 深色模式下调整透明度
            idealZoneBackground = BloodPressureNormal.copy(alpha = if (isDark) 0.15f else 0.1f),
            chartBackground = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

/**
 * 血压图表颜色方案数据类
 */
@Stable
data class BloodPressureChartColorScheme(
    // 数据点颜色
    val normalDataPoint: Color,
    
    // 线条颜色
    val systolicLine: Color,
    val diastolicLine: Color,
    
    // 异常点颜色
    val lowAnomalyPoint: Color,
    val mediumAnomalyPoint: Color,
    val highAnomalyPoint: Color,
    
    // 参考线颜色
    val normalReference: Color,
    val elevatedReference: Color,
    val stage1Reference: Color,
    val stage2Reference: Color,
    val crisisReference: Color,
    
    // 网格线颜色
    val majorGridLine: Color,
    val minorGridLine: Color,
    
    // 文本颜色
    val axisLabelText: Color,
    val referenceLabelText: Color,
    
    // 背景颜色
    val idealZoneBackground: Color,
    val chartBackground: Color
) {
    
    /**
     * 根据血压值获取对应的参考线颜色
     */
    fun getReferenceColor(pressure: Double): Color {
        return when {
            pressure >= 180 -> crisisReference
            pressure >= 160 -> stage2Reference
            pressure >= 140 -> stage1Reference
            pressure >= 120 -> elevatedReference
            else -> normalReference
        }
    }
    
    /**
     * 根据异常严重程度获取颜色
     */
    fun getAnomalyColor(severity: String): Color {
        return when (severity.lowercase()) {
            "high" -> highAnomalyPoint
            "medium" -> mediumAnomalyPoint
            "low" -> lowAnomalyPoint
            else -> normalDataPoint
        }
    }
    
    /**
     * 获取血压分类的颜色和标签
     */
    fun getBloodPressureCategory(systolic: Int, diastolic: Int): Pair<Color, String> {
        return when {
            systolic >= 180 || diastolic >= 110 -> crisisReference to "高血压危象"
            systolic >= 160 || diastolic >= 100 -> stage2Reference to "高血压2期"
            systolic >= 140 || diastolic >= 90 -> stage1Reference to "高血压1期"
            systolic >= 120 || diastolic >= 80 -> elevatedReference to "血压偏高"
            else -> normalReference to "血压正常"
        }
    }
}

/**
 * 血压图表渐变色配置
 */
object BloodPressureGradients {
    
    @Composable
    fun normalGradient() = androidx.compose.ui.graphics.Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        )
    )
    
    @Composable
    fun warningGradient() = androidx.compose.ui.graphics.Brush.verticalGradient(
        colors = listOf(
            BloodPressureElevated.copy(alpha = 0.3f),
            BloodPressureElevated.copy(alpha = 0.1f)
        )
    )
    
    @Composable
    fun dangerGradient() = androidx.compose.ui.graphics.Brush.verticalGradient(
        colors = listOf(
            BloodPressureHigh2.copy(alpha = 0.3f),
            BloodPressureHigh2.copy(alpha = 0.1f)
        )
    )
}
