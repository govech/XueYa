package com.example.xueya.presentation.screens.home

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
import javax.inject.Inject

/**
 * 首页ViewModel
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getBloodPressureListUseCase: GetBloodPressureListUseCase,
    private val getBloodPressureStatisticsUseCase: GetBloodPressureStatisticsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    /**
     * 加载首页数据
     */
    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                // 组合多个数据流
                combine(
                    getBloodPressureListUseCase.getRecentRecords(5), // 最近5条记录
                    getBloodPressureListUseCase.getTodayRecords(),    // 今日记录
                    getBloodPressureStatisticsUseCase.getThisWeekStatistics(), // 本周统计
                    getBloodPressureStatisticsUseCase.getOverallHealthStatus() // 健康状况
                ) { recentRecords, todayRecords, weeklyStats, healthStatus ->
                    HomeUiState(
                        isLoading = false,
                        latestRecord = recentRecords.firstOrNull(),
                        recentRecords = recentRecords,
                        todayRecords = todayRecords,
                        weeklyStatistics = weeklyStats,
                        healthStatus = healthStatus,
                        error = null
                    )
                }
                .catch { throwable ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = throwable.message ?: "加载数据时发生错误"
                    )
                }
                .collect { newState ->
                    _uiState.value = newState
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "加载数据时发生错误"
                )
            }
        }
    }

    /**
     * 刷新数据
     */
    fun refresh() {
        loadHomeData()
    }

    /**
     * 清除错误
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}