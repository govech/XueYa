package com.example.xueya.domain.usecase

import com.example.xueya.domain.model.BloodPressureCategory
import com.example.xueya.domain.model.BloodPressureData
import com.example.xueya.domain.repository.BloodPressureRepository
import com.example.xueya.domain.repository.BloodPressureStatistics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * 获取血压统计数据用例
 * 处理血压统计分析的业务逻辑
 */
class GetBloodPressureStatisticsUseCase @Inject constructor(
    private val repository: BloodPressureRepository
) {
    /**
     * 获取指定时间范围的统计数据
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    suspend fun getStatistics(
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): Result<BloodPressureStatistics?> {
        return try {
            val statistics = repository.getStatistics(startTime, endTime)
            Result.success(statistics)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 获取详细的统计数据（包含分类统计）
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    fun getDetailedStatistics(
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): Flow<DetailedStatistics> {
        return repository.getRecordsByDateRange(startTime, endTime).map { records ->
            calculateDetailedStatistics(records)
        }
    }

    /**
     * 获取本周统计
     */
    fun getThisWeekStatistics(): Flow<DetailedStatistics> {
        val now = LocalDateTime.now()
        val startOfWeek = now.minusDays(now.dayOfWeek.value - 1L).toLocalDate().atStartOfDay()
        val endOfWeek = startOfWeek.plusDays(6).toLocalDate().atTime(23, 59, 59)
        
        return getDetailedStatistics(startOfWeek, endOfWeek)
    }

    /**
     * 获取本月统计
     */
    fun getThisMonthStatistics(): Flow<DetailedStatistics> {
        val now = LocalDateTime.now()
        val startOfMonth = now.toLocalDate().withDayOfMonth(1).atStartOfDay()
        val endOfMonth = now.toLocalDate().withDayOfMonth(now.toLocalDate().lengthOfMonth()).atTime(23, 59, 59)
        
        return getDetailedStatistics(startOfMonth, endOfMonth)
    }

    /**
     * 获取总体健康状况
     */
    fun getOverallHealthStatus(): Flow<HealthStatus> {
        return repository.getRecentRecords(30).map { records ->
            if (records.isEmpty()) {
                HealthStatus.NO_DATA
            } else {
                val stats = calculateDetailedStatistics(records)
                when {
                    stats.crisisCount > 0 -> HealthStatus.CRITICAL
                    stats.highStage2Count > stats.totalCount * 0.3 -> HealthStatus.HIGH_RISK
                    stats.highStage1Count > stats.totalCount * 0.5 -> HealthStatus.MODERATE_RISK
                    stats.elevatedCount > stats.totalCount * 0.3 -> HealthStatus.ELEVATED
                    else -> HealthStatus.NORMAL
                }
            }
        }
    }

    /**
     * 获取血压趋势
     * 
     * @param days 天数，默认30天
     */
    fun getBloodPressureTrend(days: Int = 30): Flow<List<TrendData>> {
        val endTime = LocalDateTime.now()
        val startTime = endTime.minusDays(days.toLong())
        
        return repository.getRecordsByDateRange(startTime, endTime).map { records ->
            // 按天分组并计算每天的平均值
            records.groupBy { it.measureTime.toLocalDate() }
                .map { (date, dayRecords) ->
                    val avgSystolic = dayRecords.map { it.systolic }.average()
                    val avgDiastolic = dayRecords.map { it.diastolic }.average()
                    val avgHeartRate = dayRecords.map { it.heartRate }.average()
                    
                    TrendData(
                        date = date.atStartOfDay(),
                        avgSystolic = avgSystolic,
                        avgDiastolic = avgDiastolic,
                        avgHeartRate = avgHeartRate,
                        recordCount = dayRecords.size
                    )
                }
                .sortedBy { it.date }
        }
    }

    /**
     * 计算详细统计数据
     */
    private fun calculateDetailedStatistics(records: List<BloodPressureData>): DetailedStatistics {
        if (records.isEmpty()) {
            return DetailedStatistics()
        }

        val totalCount = records.size
        val avgSystolic = records.map { it.systolic }.average()
        val avgDiastolic = records.map { it.diastolic }.average()
        val avgHeartRate = records.map { it.heartRate }.average()

        // 按分类统计
        val normalCount = records.count { it.category == BloodPressureCategory.NORMAL }
        val elevatedCount = records.count { it.category == BloodPressureCategory.ELEVATED }
        val highStage1Count = records.count { it.category == BloodPressureCategory.HIGH_STAGE_1 }
        val highStage2Count = records.count { it.category == BloodPressureCategory.HIGH_STAGE_2 }
        val crisisCount = records.count { it.category == BloodPressureCategory.HYPERTENSIVE_CRISIS }

        return DetailedStatistics(
            totalCount = totalCount,
            averageSystolic = avgSystolic,
            averageDiastolic = avgDiastolic,
            averageHeartRate = avgHeartRate,
            normalCount = normalCount,
            elevatedCount = elevatedCount,
            highStage1Count = highStage1Count,
            highStage2Count = highStage2Count,
            crisisCount = crisisCount,
            maxSystolic = records.maxOfOrNull { it.systolic } ?: 0,
            minSystolic = records.minOfOrNull { it.systolic } ?: 0,
            maxDiastolic = records.maxOfOrNull { it.diastolic } ?: 0,
            minDiastolic = records.minOfOrNull { it.diastolic } ?: 0
        )
    }
}

/**
 * 详细统计数据
 */
data class DetailedStatistics(
    val totalCount: Int = 0,
    val averageSystolic: Double = 0.0,
    val averageDiastolic: Double = 0.0,
    val averageHeartRate: Double = 0.0,
    val normalCount: Int = 0,
    val elevatedCount: Int = 0,
    val highStage1Count: Int = 0,
    val highStage2Count: Int = 0,
    val crisisCount: Int = 0,
    val maxSystolic: Int = 0,
    val minSystolic: Int = 0,
    val maxDiastolic: Int = 0,
    val minDiastolic: Int = 0
) {
    val normalPercentage: Float
        get() = if (totalCount > 0) normalCount.toFloat() / totalCount else 0f

    val riskPercentage: Float
        get() = if (totalCount > 0) (highStage1Count + highStage2Count + crisisCount).toFloat() / totalCount else 0f
}

/**
 * 趋势数据
 */
data class TrendData(
    val date: LocalDateTime,
    val avgSystolic: Double,
    val avgDiastolic: Double,
    val avgHeartRate: Double,
    val recordCount: Int
)

/**
 * 健康状况枚举
 */
enum class HealthStatus(val displayName: String, val description: String) {
    NO_DATA("无数据", "暂无血压数据"),
    NORMAL("正常", "血压状况良好"),
    ELEVATED("偏高", "血压偏高，需要注意"),
    MODERATE_RISK("中度风险", "血压偏高，建议就医"),
    HIGH_RISK("高风险", "血压较高，建议尽快就医"),
    CRITICAL("危险", "血压过高，需要立即就医")
}