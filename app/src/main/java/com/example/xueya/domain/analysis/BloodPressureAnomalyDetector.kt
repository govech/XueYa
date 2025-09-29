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
     * 结合统计学方法和医学标准进行检测
     * @param data 血压数据列表
     * @param systolicThreshold Z-Score阈值，默认1.5
     * @param diastolicThreshold Z-Score阈值，默认1.5
     * @return 异常点列表
     */
    fun detectAnomalies(
        data: List<BloodPressureData>,
        systolicThreshold: Double = 1.5,
        diastolicThreshold: Double = 1.5
    ): List<AnomalyPoint> {
        if (data.size < 3) return emptyList()
        
        val systolicValues = data.map { it.systolic.toDouble() }
        val diastolicValues = data.map { it.diastolic.toDouble() }
        
        val systolicStats = calculateStatistics(systolicValues)
        val diastolicStats = calculateStatistics(diastolicValues)
        
        return data.mapNotNull { bp ->
            // 统计学异常检测
            val systolicZ = if (systolicStats.stdDev > 0) {
                abs((bp.systolic.toDouble() - systolicStats.mean) / systolicStats.stdDev)
            } else 0.0
            
            val diastolicZ = if (diastolicStats.stdDev > 0) {
                abs((bp.diastolic.toDouble() - diastolicStats.mean) / diastolicStats.stdDev)
            } else 0.0
            
            // 医学标准异常检测
            val isMedicalAnomaly = isMedicalAnomaly(bp.systolic, bp.diastolic)
            
            val isSystolicAnomaly = systolicZ > systolicThreshold
            val isDiastolicAnomaly = diastolicZ > diastolicThreshold
            
            // 如果满足统计学异常或医学标准异常，则标记为异常
            if (isSystolicAnomaly || isDiastolicAnomaly || isMedicalAnomaly) {
                val maxZ = maxOf(systolicZ, diastolicZ)
                val severity = when {
                    isMedicalAnomaly || maxZ >= 3.0 -> Severity.HIGH
                    maxZ >= 2.5 -> Severity.MEDIUM
                    else -> Severity.LOW
                }
                
                val reason = buildString {
                    if (isMedicalAnomaly) {
                        append("医学标准异常: 收缩压${bp.systolic}, 舒张压${bp.diastolic}")
                    } else {
                        if (isSystolicAnomaly) {
                            append("收缩压异常: ${bp.systolic} (Z-Score: ${String.format("%.2f", systolicZ)})")
                        }
                        if (isDiastolicAnomaly) {
                            if (isSystolicAnomaly) append("; ")
                            append("舒张压异常: ${bp.diastolic} (Z-Score: ${String.format("%.2f", diastolicZ)})")
                        }
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
     * 基于医学标准检测异常
     * 只检测明显的异常情况，避免过度敏感
     */
    private fun isMedicalAnomaly(systolic: Int, diastolic: Int): Boolean {
        return when {
            // 高血压危象 - 需要立即就医
            systolic >= 180 || diastolic >= 110 -> true
            // 严重高血压2期
            systolic >= 160 || diastolic >= 100 -> true
            // 低血压 - 可能影响健康
            systolic < 90 || diastolic < 60 -> true
            else -> false
        }
    }
    
    /**
     * 计算统计信息（使用样本标准差）
     */
    private fun calculateStatistics(values: List<Double>): Statistics {
        val mean = values.average()
        val n = values.size
        // 使用样本标准差：除以(n-1)而不是n
        val variance = if (n > 1) {
            values.map { (it - mean).pow(2) }.sum() / (n - 1)
        } else {
            0.0
        }
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
