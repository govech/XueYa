package com.example.xueya.domain.analysis

import com.example.xueya.domain.model.BloodPressureData
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.math.pow

/**
 * 血压异常检测器
 * 使用统计学方法检测血压数据中的异常点
 */
class BloodPressureAnomalyDetector {
    
    /**
     * 异常点严重程度
     */
    enum class Severity {
        LOW,    // 轻度异常
        MEDIUM, // 中度异常
        HIGH    // 重度异常
    }
    
    /**
     * 异常点数据类
     */
    data class AnomalyPoint(
        val data: BloodPressureData,
        val severity: Severity,
        val systolicZScore: Double,
        val diastolicZScore: Double,
        val reason: String
    )
    
    /**
     * 检测血压数据中的异常点
     * @param data 血压数据列表
     * @param systolicThreshold Z-Score阈值，默认2.0
     * @param diastolicThreshold Z-Score阈值，默认2.0
     * @return 异常点列表
     */
    fun detectAnomalies(
        data: List<BloodPressureData>,
        systolicThreshold: Double = 2.0,
        diastolicThreshold: Double = 2.0
    ): List<AnomalyPoint> {
        if (data.size < 3) return emptyList()
        
        val systolicValues = data.map { it.systolic.toDouble() }
        val diastolicValues = data.map { it.diastolic.toDouble() }
        
        val systolicStats = calculateStatistics(systolicValues)
        val diastolicStats = calculateStatistics(diastolicValues)
        
        return data.mapNotNull { bp ->
            val systolicZ = abs((bp.systolic.toDouble() - systolicStats.mean) / systolicStats.stdDev)
            val diastolicZ = abs((bp.diastolic.toDouble() - diastolicStats.mean) / diastolicStats.stdDev)
            
            val isSystolicAnomaly = systolicZ > systolicThreshold
            val isDiastolicAnomaly = diastolicZ > diastolicThreshold
            
            if (isSystolicAnomaly || isDiastolicAnomaly) {
                val maxZ = maxOf(systolicZ, diastolicZ)
                val severity = when {
                    maxZ >= 3.0 -> Severity.HIGH
                    maxZ >= 2.5 -> Severity.MEDIUM
                    else -> Severity.LOW
                }
                
                val reason = buildString {
                    if (isSystolicAnomaly) {
                        append("收缩压异常: ${bp.systolic} (Z-Score: ${String.format("%.2f", systolicZ)})")
                    }
                    if (isDiastolicAnomaly) {
                        if (isSystolicAnomaly) append("; ")
                        append("舒张压异常: ${bp.diastolic} (Z-Score: ${String.format("%.2f", diastolicZ)})")
                    }
                }
                
                AnomalyPoint(
                    data = bp,
                    severity = severity,
                    systolicZScore = systolicZ,
                    diastolicZScore = diastolicZ,
                    reason = reason
                )
            } else null
        }
    }
    
    /**
     * 计算统计信息
     */
    private fun calculateStatistics(values: List<Double>): Statistics {
        val mean = values.average()
        val variance = values.map { (it - mean).pow(2) }.average()
        val stdDev = sqrt(variance)
        
        return Statistics(mean, stdDev)
    }
    
    /**
     * 统计信息数据类
     */
    private data class Statistics(
        val mean: Double,
        val stdDev: Double
    )
}
