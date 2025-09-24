package com.example.xueya.presentation.screens.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xueya.domain.usecase.GetBloodPressureListUseCase
import com.example.xueya.domain.usecase.GetBloodPressureStatisticsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * 统计分析页面ViewModel
 */
@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getBloodPressureListUseCase: GetBloodPressureListUseCase,
    private val getBloodPressureStatisticsUseCase: GetBloodPressureStatisticsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        loadStatistics()
    }

    /**
     * 加载统计数据
     */
    fun loadStatistics() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val timeRange = _uiState.value.selectedTimeRange
                val (startDate, endDate) = getDateRange(timeRange)

                // 组合多个数据流
                combine(
                    getBloodPressureListUseCase.getRecordsByDateRange(startDate, endDate),
                    getBloodPressureStatisticsUseCase.getDetailedStatistics(startDate, endDate),
                    getBloodPressureStatisticsUseCase.getBloodPressureTrend(timeRange.days),
                    getBloodPressureStatisticsUseCase.getOverallHealthStatus()
                ) { records, statistics, trendData, healthStatus ->
                    StatisticsUiState(
                        isLoading = false,
                        selectedTimeRange = timeRange,
                        records = records,
                        currentStatistics = statistics,
                        trendData = trendData,
                        healthStatus = healthStatus,
                        customStartDate = _uiState.value.customStartDate,
                        customEndDate = _uiState.value.customEndDate,
                        error = null
                    )
                }
                .catch { throwable ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = throwable.message ?: "加载统计数据失败"
                    )
                }
                .collect { newState ->
                    _uiState.value = newState
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "加载统计数据失败"
                )
            }
        }
    }

    /**
     * 更新时间范围
     */
    fun updateTimeRange(timeRange: TimeRange) {
        _uiState.value = _uiState.value.copy(selectedTimeRange = timeRange)
        loadStatistics()
    }

    /**
     * 更新自定义日期范围
     */
    fun updateCustomDateRange(startDate: LocalDate?, endDate: LocalDate?) {
        _uiState.value = _uiState.value.copy(
            customStartDate = startDate,
            customEndDate = endDate,
            selectedTimeRange = TimeRange.CUSTOM
        )
        loadStatistics()
    }

    /**
     * 显示时间范围选择器
     */
    fun showTimeRangePicker() {
        _uiState.value = _uiState.value.copy(showTimeRangePicker = true)
    }

    /**
     * 隐藏时间范围选择器
     */
    fun hideTimeRangePicker() {
        _uiState.value = _uiState.value.copy(showTimeRangePicker = false)
    }

    /**
     * 刷新数据
     */
    fun refresh() {
        loadStatistics()
    }

    /**
     * 清除错误
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * 获取指定时间范围的开始和结束日期
     */
    private fun getDateRange(timeRange: TimeRange): Pair<LocalDateTime, LocalDateTime> {
        val now = LocalDateTime.now()
        
        return when (timeRange) {
            TimeRange.LAST_7_DAYS -> {
                val start = now.minusDays(7).toLocalDate().atStartOfDay()
                Pair(start, now)
            }
            TimeRange.LAST_30_DAYS -> {
                val start = now.minusDays(30).toLocalDate().atStartOfDay()
                Pair(start, now)
            }
            TimeRange.LAST_90_DAYS -> {
                val start = now.minusDays(90).toLocalDate().atStartOfDay()
                Pair(start, now)
            }
            TimeRange.LAST_6_MONTHS -> {
                val start = now.minusMonths(6).toLocalDate().atStartOfDay()
                Pair(start, now)
            }
            TimeRange.LAST_YEAR -> {
                val start = now.minusYears(1).toLocalDate().atStartOfDay()
                Pair(start, now)
            }
            TimeRange.CUSTOM -> {
                val startDate = _uiState.value.customStartDate
                val endDate = _uiState.value.customEndDate
                
                if (startDate != null && endDate != null) {
                    Pair(
                        startDate.atStartOfDay(),
                        endDate.atTime(23, 59, 59)
                    )
                } else {
                    // 默认使用近30天
                    val start = now.minusDays(30).toLocalDate().atStartOfDay()
                    Pair(start, now)
                }
            }
        }
    }

    /**
     * 生成报告数据
     */
    fun generateReport(): StatisticsReport {
        val state = _uiState.value
        return StatisticsReport(
            timeRange = state.selectedTimeRange,
            statistics = state.currentStatistics,
            trendAnalysis = state.trendAnalysis,
            healthStatus = state.healthStatus,
            recommendations = generateRecommendations(state)
        )
    }

    /**
     * 生成健康建议
     */
    private fun generateRecommendations(state: StatisticsUiState): List<String> {
        val recommendations = mutableListOf<String>()
        val stats = state.currentStatistics

        // 基于血压分类的建议
        if (stats.crisisCount > 0) {
            recommendations.add("发现高血压危象记录，请立即就医")
        } else if (stats.highStage2Count > stats.totalCount * 0.3) {
            recommendations.add("高血压2期记录较多，建议尽快就医调整治疗方案")
        } else if (stats.highStage1Count > stats.totalCount * 0.5) {
            recommendations.add("高血压1期记录较多，建议咨询医生并调整生活方式")
        } else if (stats.elevatedCount > stats.totalCount * 0.3) {
            recommendations.add("血压偏高记录较多，建议加强运动并控制饮食")
        }

        // 基于趋势的建议
        when (state.trendAnalysis.overallTrend) {
            TrendDirection.WORSENING -> {
                recommendations.add("血压趋势上升，建议：减少盐分摄入、增加有氧运动、保证充足睡眠")
            }
            TrendDirection.IMPROVING -> {
                recommendations.add("血压控制良好，请继续保持当前的健康生活方式")
            }
            TrendDirection.STABLE -> {
                recommendations.add("血压保持稳定，建议继续监测并维持健康习惯")
            }
        }

        // 基于心率的建议
        if (stats.averageHeartRate > 100) {
            recommendations.add("平均心率偏高，建议减少咖啡因摄入并加强放松训练")
        } else if (stats.averageHeartRate < 60) {
            recommendations.add("平均心率偏低，如有不适症状请咨询医生")
        }

        // 通用建议
        recommendations.add("建议定期测量血压，每日同一时间测量以获得更准确的趋势")
        recommendations.add("保持健康饮食：多吃蔬果，减少盐分和饱和脂肪摄入")
        recommendations.add("坚持适量运动：每周至少150分钟中等强度有氧运动")

        return recommendations
    }
}

/**
 * 统计报告数据
 */
data class StatisticsReport(
    val timeRange: TimeRange,
    val statistics: com.example.xueya.domain.usecase.DetailedStatistics,
    val trendAnalysis: TrendAnalysis,
    val healthStatus: com.example.xueya.domain.usecase.HealthStatus,
    val recommendations: List<String>
)