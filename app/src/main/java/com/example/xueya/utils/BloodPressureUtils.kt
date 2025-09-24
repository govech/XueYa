package com.example.xueya.utils

import androidx.compose.ui.graphics.Color
import com.example.xueya.domain.model.BloodPressureCategory
import com.example.xueya.domain.model.BloodPressureData
import com.example.xueya.ui.theme.*

/**
 * 血压相关工具类
 */
object BloodPressureUtils {
    
    /**
     * 验证血压值
     */
    fun validateBloodPressure(systolic: Int, diastolic: Int): ValidationResult {
        return when {
            systolic <= 0 || diastolic <= 0 -> 
                ValidationResult.Error("血压值必须大于0")
            systolic < Constants.BloodPressure.MIN_SYSTOLIC -> 
                ValidationResult.Error("收缩压不能低于${Constants.BloodPressure.MIN_SYSTOLIC}")
            systolic > Constants.BloodPressure.MAX_SYSTOLIC -> 
                ValidationResult.Error("收缩压不能超过${Constants.BloodPressure.MAX_SYSTOLIC}")
            diastolic < Constants.BloodPressure.MIN_DIASTOLIC -> 
                ValidationResult.Error("舒张压不能低于${Constants.BloodPressure.MIN_DIASTOLIC}")
            diastolic > Constants.BloodPressure.MAX_DIASTOLIC -> 
                ValidationResult.Error("舒张压不能超过${Constants.BloodPressure.MAX_DIASTOLIC}")
            systolic <= diastolic -> 
                ValidationResult.Error("收缩压必须大于舒张压")
            else -> ValidationResult.Success
        }
    }
    
    /**
     * 验证心率
     */
    fun validateHeartRate(heartRate: Int): ValidationResult {
        return when {
            heartRate <= 0 -> 
                ValidationResult.Error("心率必须大于0")
            heartRate < Constants.BloodPressure.MIN_HEART_RATE -> 
                ValidationResult.Error("心率不能低于${Constants.BloodPressure.MIN_HEART_RATE}")
            heartRate > Constants.BloodPressure.MAX_HEART_RATE -> 
                ValidationResult.Error("心率不能超过${Constants.BloodPressure.MAX_HEART_RATE}")
            else -> ValidationResult.Success
        }
    }
    
    /**
     * 获取血压分类对应的颜色
     */
    fun getCategoryColor(category: BloodPressureCategory): Color {
        return when (category) {
            BloodPressureCategory.NORMAL -> BloodPressureNormal
            BloodPressureCategory.ELEVATED -> BloodPressureElevated
            BloodPressureCategory.HIGH_STAGE_1 -> BloodPressureHigh1
            BloodPressureCategory.HIGH_STAGE_2 -> BloodPressureHigh2
            BloodPressureCategory.HYPERTENSIVE_CRISIS -> BloodPressureCrisis
        }
    }
    
    /**
     * 获取血压分类的健康建议
     */
    fun getHealthAdvice(category: BloodPressureCategory): String {
        return when (category) {
            BloodPressureCategory.NORMAL -> "血压正常，请保持健康的生活方式"
            BloodPressureCategory.ELEVATED -> "血压偏高，建议调整饮食和增加运动"
            BloodPressureCategory.HIGH_STAGE_1 -> "轻度高血压，建议咨询医生并改善生活方式"
            BloodPressureCategory.HIGH_STAGE_2 -> "中度高血压，建议尽快就医治疗"
            BloodPressureCategory.HYPERTENSIVE_CRISIS -> "高血压危象，请立即就医！"
        }
    }
    
    /**
     * 获取血压分类的紧急程度
     */
    fun getUrgencyLevel(category: BloodPressureCategory): UrgencyLevel {
        return when (category) {
            BloodPressureCategory.NORMAL -> UrgencyLevel.LOW
            BloodPressureCategory.ELEVATED -> UrgencyLevel.LOW
            BloodPressureCategory.HIGH_STAGE_1 -> UrgencyLevel.MEDIUM
            BloodPressureCategory.HIGH_STAGE_2 -> UrgencyLevel.HIGH
            BloodPressureCategory.HYPERTENSIVE_CRISIS -> UrgencyLevel.CRITICAL
        }
    }
    
    /**
     * 计算血压变化趋势
     */
    fun calculateTrend(records: List<BloodPressureData>): BloodPressureTrend {
        if (records.size < 2) return BloodPressureTrend.STABLE
        
        val recent = records.take(3)
        val previous = records.drop(3).take(3)
        
        if (recent.isEmpty() || previous.isEmpty()) return BloodPressureTrend.STABLE
        
        val recentAvgSystolic = recent.map { it.systolic }.average()
        val previousAvgSystolic = previous.map { it.systolic }.average()
        
        val difference = recentAvgSystolic - previousAvgSystolic
        
        return when {
            difference > 5 -> BloodPressureTrend.RISING
            difference < -5 -> BloodPressureTrend.FALLING
            else -> BloodPressureTrend.STABLE
        }
    }
    
    /**
     * 获取心率状态
     */
    fun getHeartRateStatus(heartRate: Int): HeartRateStatus {
        return when {
            heartRate < Constants.HeartRate.NORMAL_MIN -> HeartRateStatus.LOW
            heartRate <= Constants.HeartRate.NORMAL_MAX -> HeartRateStatus.NORMAL
            heartRate > Constants.HeartRate.NORMAL_MAX -> HeartRateStatus.HIGH
            else -> HeartRateStatus.NORMAL
        }
    }
    
    /**
     * 获取心率状态颜色
     */
    fun getHeartRateColor(heartRate: Int): Color {
        return when (getHeartRateStatus(heartRate)) {
            HeartRateStatus.LOW -> BloodPressureElevated
            HeartRateStatus.NORMAL -> BloodPressureNormal
            HeartRateStatus.HIGH -> BloodPressureHigh1
        }
    }
    
    /**
     * 生成血压摘要
     */
    fun generateSummary(records: List<BloodPressureData>): BloodPressureSummary {
        if (records.isEmpty()) {
            return BloodPressureSummary()
        }
        
        val avgSystolic = records.map { it.systolic }.average()
        val avgDiastolic = records.map { it.diastolic }.average()
        val avgHeartRate = records.map { it.heartRate }.average()
        
        val categoryStats = records.groupBy { it.category }
            .mapValues { it.value.size }
        
        val mostCommonCategory = categoryStats.maxByOrNull { it.value }?.key
            ?: BloodPressureCategory.NORMAL
        
        val trend = calculateTrend(records)
        
        return BloodPressureSummary(
            totalRecords = records.size,
            averageSystolic = avgSystolic,
            averageDiastolic = avgDiastolic,
            averageHeartRate = avgHeartRate,
            mostCommonCategory = mostCommonCategory,
            trend = trend,
            categoryDistribution = categoryStats
        )
    }
    
    /**
     * 格式化血压显示
     */
    fun formatBloodPressure(systolic: Int, diastolic: Int): String {
        return "$systolic/$diastolic mmHg"
    }
    
    /**
     * 格式化心率显示
     */
    fun formatHeartRate(heartRate: Int): String {
        return "$heartRate bpm"
    }
    
    /**
     * 判断是否需要立即关注
     */
    fun needsImmediateAttention(data: BloodPressureData): Boolean {
        return data.category == BloodPressureCategory.HYPERTENSIVE_CRISIS ||
                data.systolic >= 180 || data.diastolic >= 110
    }
    
    /**
     * 获取推荐的测量频率
     */
    fun getRecommendedMeasureFrequency(category: BloodPressureCategory): String {
        return when (category) {
            BloodPressureCategory.NORMAL -> "每周1-2次"
            BloodPressureCategory.ELEVATED -> "每天1次"
            BloodPressureCategory.HIGH_STAGE_1 -> "每天2次"
            BloodPressureCategory.HIGH_STAGE_2 -> "每天2-3次"
            BloodPressureCategory.HYPERTENSIVE_CRISIS -> "立即就医，按医嘱测量"
        }
    }
}

/**
 * 验证结果
 */
sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}

/**
 * 紧急程度
 */
enum class UrgencyLevel {
    LOW, MEDIUM, HIGH, CRITICAL
}

/**
 * 血压趋势
 */
enum class BloodPressureTrend(val displayName: String) {
    RISING("上升"),
    FALLING("下降"),
    STABLE("稳定")
}

/**
 * 心率状态
 */
enum class HeartRateStatus {
    LOW, NORMAL, HIGH
}

/**
 * 血压摘要
 */
data class BloodPressureSummary(
    val totalRecords: Int = 0,
    val averageSystolic: Double = 0.0,
    val averageDiastolic: Double = 0.0,
    val averageHeartRate: Double = 0.0,
    val mostCommonCategory: BloodPressureCategory = BloodPressureCategory.NORMAL,
    val trend: BloodPressureTrend = BloodPressureTrend.STABLE,
    val categoryDistribution: Map<BloodPressureCategory, Int> = emptyMap()
)