package com.example.xueya.presentation.screens.home

import com.example.xueya.domain.model.BloodPressureData
import com.example.xueya.domain.usecase.DetailedStatistics
import com.example.xueya.domain.usecase.HealthStatus

/**
 * 首页UI状态
 */
data class HomeUiState(
    val isLoading: Boolean = false,
    val latestRecord: BloodPressureData? = null,
    val recentRecords: List<BloodPressureData> = emptyList(),
    val todayRecords: List<BloodPressureData> = emptyList(),
    val weeklyStatistics: DetailedStatistics = DetailedStatistics(),
    val healthStatus: HealthStatus = HealthStatus.NO_DATA,
    val error: String? = null
) {
    /**
     * 今日测量次数
     */
    val todayMeasurementCount: Int
        get() = todayRecords.size

    /**
     * 本周测量次数
     */
    val weeklyMeasurementCount: Int
        get() = weeklyStatistics.totalCount

    /**
     * 是否有数据
     */
    val hasData: Boolean
        get() = latestRecord != null || recentRecords.isNotEmpty()

    /**
     * 今日平均血压
     */
    val todayAverageBP: Pair<Int, Int>?
        get() = if (todayRecords.isNotEmpty()) {
            val avgSystolic = todayRecords.map { it.systolic }.average().toInt()
            val avgDiastolic = todayRecords.map { it.diastolic }.average().toInt()
            Pair(avgSystolic, avgDiastolic)
        } else null

    /**
     * 今日平均心率
     */
    val todayAverageHeartRate: Int?
        get() = if (todayRecords.isNotEmpty()) {
            todayRecords.map { it.heartRate }.average().toInt()
        } else null
}