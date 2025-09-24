package com.example.xueya.presentation.screens.statistics

import com.example.xueya.domain.model.BloodPressureData
import com.example.xueya.domain.usecase.DetailedStatistics
import com.example.xueya.domain.usecase.TrendData
import com.example.xueya.domain.usecase.HealthStatus
import java.time.LocalDate

/**
 * 统计分析页面UI状态
 */
data class StatisticsUiState(
    val isLoading: Boolean = false,                        // 加载状态
    val error: String? = null,                            // 错误信息
    val selectedTimeRange: TimeRange = TimeRange.LAST_30_DAYS, // 选择的时间范围
    val currentStatistics: DetailedStatistics = DetailedStatistics(), // 当前统计数据
    val trendData: List<TrendData> = emptyList(),          // 趋势数据
    val healthStatus: HealthStatus = HealthStatus.NO_DATA, // 健康状况
    val records: List<BloodPressureData> = emptyList(),    // 原始记录数据
    val showTimeRangePicker: Boolean = false,              // 显示时间范围选择器
    val customStartDate: LocalDate? = null,                // 自定义开始日期
    val customEndDate: LocalDate? = null,                  // 自定义结束日期
) {
    /**
     * 是否有数据
     */
    val hasData: Boolean
        get() = records.isNotEmpty()

    /**
     * 当前时间范围的记录数量
     */
    val recordCount: Int
        get() = currentStatistics.totalCount

    /**
     * 血压分类分布数据
     */
    val categoryDistribution: List<CategoryData>
        get() = listOf(
            CategoryData("正常", currentStatistics.normalCount, "#4CAF50"),
            CategoryData("偏高", currentStatistics.elevatedCount, "#FF9800"),
            CategoryData("高血压1期", currentStatistics.highStage1Count, "#FF5722"),
            CategoryData("高血压2期", currentStatistics.highStage2Count, "#F44336"),
            CategoryData("高血压危象", currentStatistics.crisisCount, "#9C27B0")
        ).filter { it.count > 0 }

    /**
     * 血压范围统计
     */
    val bloodPressureRanges: BloodPressureRanges
        get() = BloodPressureRanges(
            systolicMax = currentStatistics.maxSystolic,
            systolicMin = currentStatistics.minSystolic,
            systolicAvg = currentStatistics.averageSystolic,
            diastolicMax = currentStatistics.maxDiastolic,
            diastolicMin = currentStatistics.minDiastolic,
            diastolicAvg = currentStatistics.averageDiastolic,
            heartRateAvg = currentStatistics.averageHeartRate
        )

    /**
     * 趋势分析结果
     */
    val trendAnalysis: TrendAnalysis
        get() = TrendAnalysis.analyze(trendData)
}

/**
 * 时间范围枚举
 */
enum class TimeRange(val displayName: String, val days: Int) {
    LAST_7_DAYS("近7天", 7),
    LAST_30_DAYS("近30天", 30),
    LAST_90_DAYS("近90天", 90),
    LAST_6_MONTHS("近6个月", 180),
    LAST_YEAR("近1年", 365),
    CUSTOM("自定义", 0)
}

/**
 * 分类数据
 */
data class CategoryData(
    val name: String,
    val count: Int,
    val color: String
) {
    val percentage: Float
        get() = if (count > 0) count.toFloat() else 0f
}

/**
 * 血压范围统计
 */
data class BloodPressureRanges(
    val systolicMax: Int,
    val systolicMin: Int,
    val systolicAvg: Double,
    val diastolicMax: Int,
    val diastolicMin: Int,
    val diastolicAvg: Double,
    val heartRateAvg: Double
)

/**
 * 趋势分析结果
 */
data class TrendAnalysis(
    val systolicTrend: TrendDirection,
    val diastolicTrend: TrendDirection,
    val heartRateTrend: TrendDirection,
    val overallTrend: TrendDirection,
    val improvementSuggestion: String
) {
    companion object {
        fun analyze(trendData: List<TrendData>): TrendAnalysis {
            if (trendData.size < 2) {
                return TrendAnalysis(
                    systolicTrend = TrendDirection.STABLE,
                    diastolicTrend = TrendDirection.STABLE,
                    heartRateTrend = TrendDirection.STABLE,
                    overallTrend = TrendDirection.STABLE,
                    improvementSuggestion = "需要更多数据进行趋势分析"
                )
            }

            // 计算线性趋势
            val systolicTrend = calculateTrend(trendData.map { it.avgSystolic })
            val diastolicTrend = calculateTrend(trendData.map { it.avgDiastolic })
            val heartRateTrend = calculateTrend(trendData.map { it.avgHeartRate })

            // 综合评估
            val overallTrend = when {
                systolicTrend == TrendDirection.IMPROVING && diastolicTrend == TrendDirection.IMPROVING -> TrendDirection.IMPROVING
                systolicTrend == TrendDirection.WORSENING || diastolicTrend == TrendDirection.WORSENING -> TrendDirection.WORSENING
                else -> TrendDirection.STABLE
            }

            // 改善建议
            val suggestion = when (overallTrend) {
                TrendDirection.IMPROVING -> "血压趋势良好，请继续保持健康的生活方式"
                TrendDirection.WORSENING -> "血压有上升趋势，建议咨询医生并调整生活习惯"
                TrendDirection.STABLE -> "血压保持稳定，继续监测并维持当前状态"
            }

            return TrendAnalysis(
                systolicTrend = systolicTrend,
                diastolicTrend = diastolicTrend,
                heartRateTrend = heartRateTrend,
                overallTrend = overallTrend,
                improvementSuggestion = suggestion
            )
        }

        private fun calculateTrend(values: List<Double>): TrendDirection {
            if (values.size < 2) return TrendDirection.STABLE

            val first = values.take(values.size / 3).average()
            val last = values.takeLast(values.size / 3).average()
            val change = ((last - first) / first) * 100

            return when {
                change < -3 -> TrendDirection.IMPROVING  // 血压下降为改善
                change > 3 -> TrendDirection.WORSENING   // 血压上升为恶化
                else -> TrendDirection.STABLE
            }
        }
    }
}

/**
 * 趋势方向
 */
enum class TrendDirection(val displayName: String, val color: String) {
    IMPROVING("改善", "#4CAF50"),
    STABLE("稳定", "#FF9800"), 
    WORSENING("恶化", "#F44336")
}