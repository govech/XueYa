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
 * 
 * 负责首页界面的数据管理和业务逻辑处理
 * 协调多个UseCase获取首页所需的各种数据
 * 使用Hilt进行依赖注入，管理数据流和状态更新
 * 
 * @param getBloodPressureListUseCase 获取血压记录列表的UseCase
 * @param getBloodPressureStatisticsUseCase 获取血压统计数据的UseCase
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getBloodPressureListUseCase: GetBloodPressureListUseCase,
    private val getBloodPressureStatisticsUseCase: GetBloodPressureStatisticsUseCase
) : ViewModel() {

    /**
     * UI状态的可变状态流
     * 用于存储和更新首页的所有UI状态信息
     */
    private val _uiState = MutableStateFlow(HomeUiState())
    
    /**
     * UI状态的只读状态流
     * 提供给UI层观察和使用
     */
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    /**
     * 初始化函数
     * 在ViewModel创建时自动加载首页数据
     */
    init {
        loadHomeData()
    }

    /**
     * 加载首页数据
     * 
     * 通过组合多个数据流获取首页所需的各类数据
     * 包括最近记录、今日记录、本周统计、健康状况等
     * 处理加载状态和错误状态的更新
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
     * 
     * 重新加载首页数据，用于用户手动刷新操作
     */
    fun refresh() {
        loadHomeData()
    }

    /**
     * 清除错误
     * 
     * 清除当前的错误状态，隐藏错误提示
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}