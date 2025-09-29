package com.example.xueya.domain.analysis

import com.example.xueya.domain.model.BloodPressureData
import com.example.xueya.domain.model.BloodPressureCategory
import kotlin.math.sqrt
import kotlin.math.pow

/**
 * 血压统计分析结果
 */
data class BloodPressureStatistics(
    val recordCount: Int,                    // 记录总数
    val averageSystolic: Double,             // 平均收缩压
    val averageDiastolic: Double,            // 平均舒张压
    val averageHeartRate: Double?,           // 平均心率
    val systolicStdDev: Double,              // 收缩压标准差
    val diastolicStdDev: Double,             // 舒张压标准差
    val minSystolic: Double,                 // 最低收缩压
    val maxSystolic: Double,                 // 最高收缩压
    val minDiastolic: Double,                // 最低舒张压
    val maxDiastolic: Double,                // 最高舒张压
    val normalCount: Int,                    // 正常血压次数
    val elevatedCount: Int,                  // 血压偏高次数
    val hypertension1Count: Int,             // 高血压1期次数
    val hypertension2Count: Int,             // 高血压2期次数
    val crisisCount: Int,                    // 高血压危象次数
    val anomalyCount: Int,                   // 异常数据次数
    val trendResult: TrendAnalyzer.TrendResult?, // 趋势分析结果
    val anomalies: List<BloodPressureAnomalyDetector.AnomalyPoint> // 异常点列表
) {
    /**
     * 计算血压分类分布
     */
    fun getCategoryDistribution(): Map<BloodPressureCategory, Int> {
        return mapOf(
            BloodPressureCategory.NORMAL to normalCount,
            BloodPressureCategory.ELEVATED to elevatedCount,
            BloodPressureCategory.HIGH_STAGE_1 to hypertension1Count,
            BloodPressureCategory.HIGH_STAGE_2 to hypertension2Count,
            BloodPressureCategory.HYPERTENSIVE_CRISIS to crisisCount
        )
    }
    
    /**
     * 获取健康状态描述
     */
    fun getHealthStatusDescription(): String {
        val totalRecords = recordCount
        val normalPercentage = (normalCount.toDouble() / totalRecords * 100).toInt()
        val anomalyPercentage = (anomalyCount.toDouble() / totalRecords * 100).toInt()
        
        return when {
            normalPercentage >= 80 -> "血压控制良好"
            normalPercentage >= 60 -> "血压基本正常，需要关注"
            anomalyPercentage >= 30 -> "血压异常较多，建议就医"
            else -> "血压波动较大，需要密切监测"
        }
    }
    
    /**
     * 获取趋势描述
     */
    fun getTrendDescription(): String {
        return trendResult?.description ?: "暂无趋势数据"
    }
}

/**
 * 血压统计分析器
 */
class BloodPressureStatisticsCalculator @javax.inject.Inject constructor() {
    
    /**
     * 计算血压统计数据
     */
    fun calculateStatistics(
        data: List<BloodPressureData>,
        anomalyDetector: BloodPressureAnomalyDetector = BloodPressureAnomalyDetector(),
        trendAnalyzer: TrendAnalyzer = TrendAnalyzer()
    ): BloodPressureStatistics {
        if (data.isEmpty()) {
            return BloodPressureStatistics(
                recordCount = 0,
                averageSystolic = 0.0,
                averageDiastolic = 0.0,
                averageHeartRate = null,
                systolicStdDev = 0.0,
                diastolicStdDev = 0.0,
                minSystolic = 0.0,
                maxSystolic = 0.0,
                minDiastolic = 0.0,
                maxDiastolic = 0.0,
                normalCount = 0,
                elevatedCount = 0,
                hypertension1Count = 0,
                hypertension2Count = 0,
                crisisCount = 0,
                anomalyCount = 0,
                trendResult = null,
                anomalies = emptyList()
            )
        }
        
        val systolicValues = data.map { it.systolic.toDouble() }
        val diastolicValues = data.map { it.diastolic.toDouble() }
        val heartRateValues = data.mapNotNull { it.heartRate?.toDouble() }
        
        val averageSystolic = systolicValues.average()
        val averageDiastolic = diastolicValues.average()
        val averageHeartRate = if (heartRateValues.isNotEmpty()) heartRateValues.average() else null
        
        val systolicStdDev = calculateStandardDeviation(systolicValues, averageSystolic)
        val diastolicStdDev = calculateStandardDeviation(diastolicValues, averageDiastolic)
        
        val minSystolic = systolicValues.minOrNull() ?: 0.0
        val maxSystolic = systolicValues.maxOrNull() ?: 0.0
        val minDiastolic = diastolicValues.minOrNull() ?: 0.0
        val maxDiastolic = diastolicValues.maxOrNull() ?: 0.0
        
        val categoryCounts = calculateCategoryCounts(data)
        val anomalies = anomalyDetector.detectAnomalies(data)
        val trendResult = trendAnalyzer.analyzeTrend(data)
        
        return BloodPressureStatistics(
            recordCount = data.size,
            averageSystolic = averageSystolic,
            averageDiastolic = averageDiastolic,
            averageHeartRate = averageHeartRate,
            systolicStdDev = systolicStdDev,
            diastolicStdDev = diastolicStdDev,
            minSystolic = minSystolic,
            maxSystolic = maxSystolic,
            minDiastolic = minDiastolic,
            maxDiastolic = maxDiastolic,
            normalCount = categoryCounts[BloodPressureCategory.NORMAL] ?: 0,
            elevatedCount = categoryCounts[BloodPressureCategory.ELEVATED] ?: 0,
            hypertension1Count = categoryCounts[BloodPressureCategory.HIGH_STAGE_1] ?: 0,
            hypertension2Count = categoryCounts[BloodPressureCategory.HIGH_STAGE_2] ?: 0,
            crisisCount = categoryCounts[BloodPressureCategory.HYPERTENSIVE_CRISIS] ?: 0,
            anomalyCount = anomalies.size,
            trendResult = trendResult,
            anomalies = anomalies
        )
    }
    
    /**
     * 计算标准差
     */
    private fun calculateStandardDeviation(values: List<Double>, mean: Double): Double {
        val variance = values.map { (it - mean).pow(2) }.average()
        return sqrt(variance)
    }
    
    /**
     * 计算血压分类统计
     */
    private fun calculateCategoryCounts(data: List<BloodPressureData>): Map<BloodPressureCategory, Int> {
        return data.groupingBy { it.category }.eachCount()
    }
}
