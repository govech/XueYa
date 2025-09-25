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
 * 
 * 处理血压统计分析的业务逻辑，提供多种维度的统计信息
 * 包括时间范围统计、周统计、月统计、趋势分析等功能
 * 作为业务逻辑层的核心组件，负责复杂的统计计算和数据分析
 * 使用@Inject注解支持Hilt依赖注入
 * 
 * @param repository 血压数据仓库，通过构造函数注入
 */
class GetBloodPressureStatisticsUseCase @Inject constructor(
    private val repository: BloodPressureRepository
) {
    /**
     * 获取指定时间范围的统计数据
     * 
     * 从仓库获取指定时间范围内的基础统计数据
     * 使用Result包装返回结果，成功时返回统计信息，失败时返回异常信息
     * 
     * @param startTime 统计起始时间（包含）
     * @param endTime 统计结束时间（包含）
     * @return Result<BloodPressureStatistics?> 统计结果，成功时包含统计数据，失败时包含异常信息
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
     * 从仓库获取指定时间范围内的所有记录，并进行详细的分类统计计算
     * 返回Flow类型支持响应式数据流
     * 
     * @param startTime 统计起始时间（包含）
     * @param endTime 统计结束时间（包含）
     * @return Flow<DetailedStatistics> 包含详细统计信息的响应式数据流
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
     * 
     * 计算当前周的血压统计数据
     * 自动计算本周的起止时间范围
     * 返回Flow类型支持响应式数据流
     * 
     * @return Flow<DetailedStatistics> 包含本周统计信息的响应式数据流
     */
    fun getThisWeekStatistics(): Flow<DetailedStatistics> {
        val now = LocalDateTime.now()
        val startOfWeek = now.minusDays(now.dayOfWeek.value - 1L).toLocalDate().atStartOfDay()
        val endOfWeek = startOfWeek.plusDays(6).toLocalDate().atTime(23, 59, 59)
        
        return getDetailedStatistics(startOfWeek, endOfWeek)
    }

    /**
     * 获取本月统计
     * 
     * 计算当前月的血压统计数据
     * 自动计算本月的起止时间范围
     * 返回Flow类型支持响应式数据流
     * 
     * @return Flow<DetailedStatistics> 包含本月统计信息的响应式数据流
     */
    fun getThisMonthStatistics(): Flow<DetailedStatistics> {
        val now = LocalDateTime.now()
        val startOfMonth = now.toLocalDate().withDayOfMonth(1).atStartOfDay()
        val endOfMonth = now.toLocalDate().withDayOfMonth(now.toLocalDate().lengthOfMonth()).atTime(23, 59, 59)
        
        return getDetailedStatistics(startOfMonth, endOfMonth)
    }

    /**
     * 获取总体健康状况
     * 
     * 基于最近30条记录分析用户的总体健康状况
     * 根据不同血压分类的比例判断健康风险等级
     * 返回Flow类型支持响应式数据流
     * 
     * @return Flow<HealthStatus> 包含健康状况评估的响应式数据流
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
     * 分析指定天数内的血压变化趋势
     * 按天分组计算每天的平均值，用于图表展示
     * 返回Flow类型支持响应式数据流
     * 
     * @param days 天数，默认30天
     * @return Flow<List<TrendData>> 包含趋势数据的响应式数据流
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
     * 
     * 对给定的血压记录列表进行详细的统计分析
     * 包括平均值、分类统计、极值等信息
     * 
     * @param records 要统计的血压记录列表
     * @return DetailedStatistics 详细的统计信息对象
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
 * 
 * 用于封装血压详细统计信息的数据类
 * 包含各种统计指标和计算属性
 * 
 * @property totalCount 记录总数
 * @property averageSystolic 收缩压平均值
 * @property averageDiastolic 舒张压平均值
 * @property averageHeartRate 心率平均值
 * @property normalCount 正常血压记录数
 * @property elevatedCount 血压偏高记录数
 * @property highStage1Count 高血压1期记录数
 * @property highStage2Count 高血压2期记录数
 * @property crisisCount 高血压危象记录数
 * @property maxSystolic 最高压收缩压值
 * @property minSystolic 最低压收缩压值
 * @property maxDiastolic 最高压舒张压值
 * @property minDiastolic 最低压舒张压值
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
    /**
     * 正常血压记录占比
     * 
     * 计算正常血压记录占总记录数的百分比
     * 如果总记录数为0，则返回0
     */
    val normalPercentage: Float
        get() = if (totalCount > 0) normalCount.toFloat() / totalCount else 0f

    /**
     * 风险记录占比
     * 
     * 计算风险血压记录（高血压1期、2期和危象）占总记录数的百分比
     * 如果总记录数为0，则返回0
     */
    val riskPercentage: Float
        get() = if (totalCount > 0) (highStage1Count + highStage2Count + crisisCount).toFloat() / totalCount else 0f
}

/**
 * 趋势数据
 * 
 * 用于封装血压趋势信息的数据类
 * 包含日期、平均值和记录数量等信息
 * 
 * @property date 日期时间
 * @property avgSystolic 收缩压平均值
 * @property avgDiastolic 舒张压平均值
 * @property avgHeartRate 心率平均值
 * @property recordCount 当天记录数量
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
 * 
 * 定义用户血压健康状况的评估等级
 * 根据血压分类的比例和严重程度进行风险评估
 * 
 * @property displayName 显示名称
 * @property description 描述信息
 */
enum class HealthStatus(val displayName: String, val description: String) {
    /**
     * 无数据状态
     * 当没有血压记录时的评估结果
     */
    NO_DATA("无数据", "暂无血压数据"),

    /**
     * 正常状态
     * 血压状况良好的评估结果
     */
    NORMAL("正常", "血压状况良好"),

    /**
     * 偏高状态
     * 血压偏高的评估结果
     */
    ELEVATED("偏高", "血压偏高，需要注意"),

    /**
     * 中度风险状态
     * 血压偏高需要就医的评估结果
     */
    MODERATE_RISK("中度风险", "血压偏高，建议就医"),

    /**
     * 高风险状态
     * 血压较高建议尽快就医的评估结果
     */
    HIGH_RISK("高风险", "血压较高，建议尽快就医"),

    /**
     * 危险状态
     * 血压过高需要立即就医的评估结果
     */
    CRITICAL("危险", "血压过高，需要立即就医")
}