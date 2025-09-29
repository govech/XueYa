package com.example.xueya.domain.analysis

import com.example.xueya.domain.model.BloodPressureData
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.math.pow

/**
 * 血压趋势分析器
 * 使用线性回归分析血压数据的变化趋势
 */
class TrendAnalyzer {
    
    /**
     * 趋势方向
     */
    enum class TrendDirection {
        INCREASING, // 上升趋势
        DECREASING, // 下降趋势
        STABLE      // 稳定趋势
    }
    
    /**
     * 趋势分析结果
     */
    data class TrendResult(
        val direction: TrendDirection,
        val slope: Double,        // 斜率
        val intercept: Double,    // 截距
        val rSquared: Double,     // R²值（拟合优度）
        val confidence: Double,   // 置信度 (0-1)
        val description: String   // 趋势描述
    )
    
    /**
     * 分析血压数据趋势
     * @param data 血压数据列表
     * @param threshold 趋势判断阈值，默认0.5
     * @return 趋势分析结果
     */
    fun analyzeTrend(
        data: List<BloodPressureData>,
        threshold: Double = 0.5
    ): TrendResult {
        if (data.size < 2) {
            return TrendResult(
                direction = TrendDirection.STABLE,
                slope = 0.0,
                intercept = 0.0,
                rSquared = 0.0,
                confidence = 0.0,
                description = "数据不足，无法分析趋势"
            )
        }
        
        // 计算平均血压作为趋势分析指标
        val avgBloodPressure = data.map { (it.systolic + it.diastolic) / 2.0 }
        val timePoints = data.mapIndexed { index, _ -> index.toDouble() }
        
        val regression = calculateLinearRegression(timePoints, avgBloodPressure)
        
        val direction = when {
            regression.slope > threshold -> TrendDirection.INCREASING
            regression.slope < -threshold -> TrendDirection.DECREASING
            else -> TrendDirection.STABLE
        }
        
        val confidence = calculateConfidence(regression.rSquared, data.size)
        
        val description = when (direction) {
            TrendDirection.INCREASING -> "血压呈上升趋势，需要关注"
            TrendDirection.DECREASING -> "血压呈下降趋势，情况良好"
            TrendDirection.STABLE -> "血压保持稳定"
        }
        
        return TrendResult(
            direction = direction,
            slope = regression.slope,
            intercept = regression.intercept,
            rSquared = regression.rSquared,
            confidence = confidence,
            description = description
        )
    }
    
    /**
     * 计算线性回归
     */
    private fun calculateLinearRegression(x: List<Double>, y: List<Double>): LinearRegression {
        val n = x.size
        val sumX = x.sum()
        val sumY = y.sum()
        val sumXY = x.zip(y).sumOf { it.first * it.second }
        val sumXX = x.sumOf { it * it }
        val sumYY = y.sumOf { it * it }
        
        // 计算斜率和截距
        val slope = (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX)
        val intercept = (sumY - slope * sumX) / n
        
        // 计算R²值
        val yMean = sumY / n
        val ssTotal = y.sumOf { (it - yMean).pow(2) }
        val ssResidual = y.zip(x).sumOf { (yi, xi) ->
            val predicted = slope * xi + intercept
            (yi - predicted).pow(2)
        }
        val rSquared = if (ssTotal > 0) 1 - (ssResidual / ssTotal) else 0.0
        
        return LinearRegression(slope, intercept, rSquared)
    }
    
    /**
     * 计算置信度
     */
    private fun calculateConfidence(rSquared: Double, sampleSize: Int): Double {
        // 基于R²值和样本大小的简单置信度计算
        val baseConfidence = rSquared
        val sizeFactor = minOf(sampleSize / 10.0, 1.0) // 样本大小因子
        return (baseConfidence * sizeFactor).coerceIn(0.0, 1.0)
    }
    
    /**
     * 线性回归结果
     */
    private data class LinearRegression(
        val slope: Double,
        val intercept: Double,
        val rSquared: Double
    )
}
