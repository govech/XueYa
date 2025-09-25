package com.example.xueya.utils

import androidx.compose.ui.graphics.Color
import com.example.xueya.domain.model.BloodPressureCategory
import com.example.xueya.domain.model.BloodPressureData
import com.example.xueya.ui.theme.*

/**
 * 血压相关工具类
 * 
 * 提供血压数据处理、验证、分类、趋势分析等实用功能
 * 包括血压值验证、心率验证、颜色映射、健康建议等方法
 */
object BloodPressureUtils {
    
    /**
     * 验证血压值的有效性
     * 
     * 根据医学标准验证收缩压和舒张压的数值范围
     * 检查收缩压必须大于舒张压的逻辑关系
     * 
     * @param systolic 收缩压值
     * @param diastolic 舒张压值
     * @return ValidationResult 验证结果，成功或包含错误信息
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
     * 验证心率值的有效性
     * 
     * 根据医学标准验证心率的数值范围
     * 
     * @param heartRate 心率值
     * @return ValidationResult 验证结果，成功或包含错误信息
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
     * 
     * 根据血压分类返回对应的主题颜色，用于UI显示
     * 
     * @param category 血压分类枚举值
     * @return Color 对应的颜色值
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
     * 
     * 根据血压分类返回对应的健康建议文本
     * 
     * @param category 血压分类枚举值
     * @return String 健康建议文本
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
     * 
     * 根据血压分类返回对应的紧急程度等级
     * 
     * @param category 血压分类枚举值
     * @return UrgencyLevel 紧急程度等级
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
     * 
     * 根据最近几次测量数据与之前数据的对比，计算血压变化趋势
     * 
     * @param records 血压记录列表
     * @return BloodPressureTrend 血压变化趋势枚举值
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
     * 
     * 根据心率值判断心率状态（偏低、正常、偏高）
     * 
     * @param heartRate 心率值
     * @return HeartRateStatus 心率状态枚举值
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
     * 
     * 根据心率值返回对应的状态颜色
     * 
     * @param heartRate 心率值
     * @return Color 对应的颜色值
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
     * 
     * 根据血压记录列表生成统计摘要信息
     * 包括平均值、最常见分类、趋势等信息
     * 
     * @param records 血压记录列表
     * @return BloodPressureSummary 血压摘要数据对象
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
     * 
     * 将收缩压和舒张压格式化为标准显示格式
     * 
     * @param systolic 收缩压值
     * @param diastolic 舒张压值
     * @return String 格式化后的血压显示文本
     */
    fun formatBloodPressure(systolic: Int, diastolic: Int): String {
        return "$systolic/$diastolic mmHg"
    }
    
    /**
     * 格式化心率显示
     * 
     * 将心率值格式化为标准显示格式
     * 
     * @param heartRate 心率值
     * @return String 格式化后的心率显示文本
     */
    fun formatHeartRate(heartRate: Int): String {
        return "$heartRate bpm"
    }
    
    /**
     * 判断是否需要立即关注
     * 
     * 根据血压分类和数值判断是否需要立即关注
     * 
     * @param data 血压数据对象
     * @return Boolean 是否需要立即关注
     */
    fun needsImmediateAttention(data: BloodPressureData): Boolean {
        return data.category == BloodPressureCategory.HYPERTENSIVE_CRISIS ||
                data.systolic >= 180 || data.diastolic >= 110
    }
    
    /**
     * 获取推荐的测量频率
     * 
     * 根据血压分类返回推荐的测量频率
     * 
     * @param category 血压分类枚举值
     * @return String 推荐的测量频率描述
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
 * 验证结果密封类
 * 
 * 用于封装血压和心率验证的结果状态
 * Success表示验证通过，Error表示验证失败并包含错误信息
 */
sealed class ValidationResult {
    /**
     * 验证成功
     */
    object Success : ValidationResult()
    
    /**
     * 验证失败
     * 
     * @property message 错误信息
     */
    data class Error(val message: String) : ValidationResult()
}

/**
 * 紧急程度枚举
 * 
 * 定义血压状况的紧急程度等级
 */
enum class UrgencyLevel {
    /**
     * 低紧急程度
     */
    LOW, 
    
    /**
     * 中等紧急程度
     */
    MEDIUM, 
    
    /**
     * 高紧急程度
     */
    HIGH, 
    
    /**
     * 危险紧急程度
     */
    CRITICAL
}

/**
 * 血压趋势枚举
 * 
 * 定义血压变化趋势的状态
 * 
 * @property displayName 显示名称
 */
enum class BloodPressureTrend(val displayName: String) {
    /**
     * 血压上升趋势
     */
    RISING("上升"),
    
    /**
     * 血压下降趋势
     */
    FALLING("下降"),
    
    /**
     * 血压稳定趋势
     */
    STABLE("稳定")
}

/**
 * 心率状态枚举
 * 
 * 定义心率状态的分类
 */
enum class HeartRateStatus {
    /**
     * 心率偏低
     */
    LOW, 
    
    /**
     * 心率正常
     */
    NORMAL, 
    
    /**
     * 心率偏高
     */
    HIGH
}

/**
 * 血压摘要数据类
 * 
 * 用于封装血压记录的统计摘要信息
 * 
 * @property totalRecords 总记录数
 * @property averageSystolic 平均收缩压
 * @property averageDiastolic 平均舒张压
 * @property averageHeartRate 平均心率
 * @property mostCommonCategory 最常见的血压分类
 * @property trend 血压变化趋势
 * @property categoryDistribution 各分类分布情况
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