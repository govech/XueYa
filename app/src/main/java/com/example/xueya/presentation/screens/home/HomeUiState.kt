package com.example.xueya.presentation.screens.home

import com.example.xueya.domain.model.BloodPressureData
import com.example.xueya.domain.usecase.DetailedStatistics
import com.example.xueya.domain.usecase.HealthStatus

/**
 * 首页UI状态数据类
 * 
 * 用于存储首页界面所需的所有状态信息
 * 包括加载状态、数据列表、统计数据、错误信息等
 * 通过计算属性提供派生数据
 * 
 * @param isLoading 是否正在加载数据
 * @param latestRecord 最新的血压记录
 * @param recentRecords 最近的血压记录列表
 * @param todayRecords 今天的血压记录列表
 * @param weeklyStatistics 本周统计信息
 * @param healthStatus 健康状况评估
 * @param error 错误信息
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
     * 
     * 根据今日记录列表的大小计算得出
     */
    val todayMeasurementCount: Int
        get() = todayRecords.size

    /**
     * 本周测量次数
     * 
     * 从本周统计信息中获取总记录数
     */
    val weeklyMeasurementCount: Int
        get() = weeklyStatistics.totalCount

    /**
     * 是否有数据
     * 
     * 判断是否存在血压记录数据，用于显示空状态提示
     */
    val hasData: Boolean
        get() = latestRecord != null || recentRecords.isNotEmpty()

    /**
     * 今日平均血压
     * 
     * 根据今日记录计算收缩压和舒张压的平均值
     * 如果今日没有记录则返回null
     */
    val todayAverageBP: Pair<Int, Int>?
        get() = if (todayRecords.isNotEmpty()) {
            val avgSystolic = todayRecords.map { it.systolic }.average().toInt()
            val avgDiastolic = todayRecords.map { it.diastolic }.average().toInt()
            Pair(avgSystolic, avgDiastolic)
        } else null

    /**
     * 今日平均心率
     * 
     * 根据今日记录计算心率的平均值
     * 如果今日没有记录则返回null
     */
    val todayAverageHeartRate: Int?
        get() = if (todayRecords.isNotEmpty()) {
            todayRecords.map { it.heartRate }.average().toInt()
        } else null
}