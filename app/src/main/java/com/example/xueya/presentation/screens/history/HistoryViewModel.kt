package com.example.xueya.presentation.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xueya.domain.usecase.GetBloodPressureListUseCase
import com.example.xueya.domain.usecase.DeleteBloodPressureUseCase
import com.example.xueya.domain.model.BloodPressureData
import com.example.xueya.domain.analysis.BloodPressureStatisticsCalculator
import com.example.xueya.domain.analysis.BloodPressureStatistics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * 历史记录页面ViewModel
 */
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getBloodPressureListUseCase: GetBloodPressureListUseCase,
    private val deleteBloodPressureUseCase: DeleteBloodPressureUseCase,
    private val statisticsCalculator: BloodPressureStatisticsCalculator = BloodPressureStatisticsCalculator()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()
    
    // 增强统计数据
    private val _enhancedStatistics = MutableStateFlow<BloodPressureStatistics?>(null)
    val enhancedStatistics: StateFlow<BloodPressureStatistics?> = _enhancedStatistics.asStateFlow()

    init {
        loadRecords()
    }

    /**
     * 加载历史记录
     */
    fun loadRecords() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                getBloodPressureListUseCase.getAllRecords()
                    .catch { throwable ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = throwable.message ?: "加载记录失败"
                        )
                    }
                    .collect { records ->
                        _uiState.value = _uiState.value.copy(
                            records = records,
                            isLoading = false,
                            error = null
                        )
                        
                        // 计算增强统计数据
                        if (records.isNotEmpty()) {
                            val enhancedStats = statisticsCalculator.calculateStatistics(records)
                            _enhancedStatistics.value = enhancedStats
                        }
                        
                        applyFilters()
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "加载记录失败"
                )
            }
        }
    }

    /**
     * 更新搜索关键词
     */
    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFilters()
    }

    /**
     * 更新日期范围
     */
    fun updateDateRange(dateRange: DateRange?) {
        _uiState.value = _uiState.value.copy(selectedDateRange = dateRange)
        applyFilters()
    }

    /**
     * 切换标签过滤
     */
    fun toggleTagFilter(tag: String) {
        val currentTags = _uiState.value.selectedTags.toMutableSet()
        if (currentTags.contains(tag)) {
            currentTags.remove(tag)
        } else {
            currentTags.add(tag)
        }
        _uiState.value = _uiState.value.copy(selectedTags = currentTags)
        applyFilters()
    }

    /**
     * 更新排序方式
     */
    fun updateSortOrder(sortOrder: SortOrder) {
        _uiState.value = _uiState.value.copy(sortOrder = sortOrder)
    }

    /**
     * 切换过滤器显示状态
     */
    fun toggleFilters() {
        _uiState.value = _uiState.value.copy(showFilters = !_uiState.value.showFilters)
    }

    /**
     * 显示日期选择器
     */
    fun showDatePicker() {
        _uiState.value = _uiState.value.copy(showDatePicker = true)
    }

    /**
     * 隐藏日期选择器
     */
    fun hideDatePicker() {
        _uiState.value = _uiState.value.copy(showDatePicker = false)
    }

    /**
     * 选择记录（用于详情或删除）
     */
    fun selectRecord(record: BloodPressureData) {
        _uiState.value = _uiState.value.copy(selectedRecord = record)
    }

    /**
     * 显示删除确认对话框
     */
    fun showDeleteDialog(record: BloodPressureData) {
        _uiState.value = _uiState.value.copy(
            selectedRecord = record,
            showDeleteDialog = true
        )
    }

    /**
     * 隐藏删除确认对话框
     */
    fun hideDeleteDialog() {
        _uiState.value = _uiState.value.copy(
            selectedRecord = null,
            showDeleteDialog = false
        )
    }

    /**
     * 删除记录
     */
    fun deleteRecord() {
        val record = _uiState.value.selectedRecord ?: return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val result = deleteBloodPressureUseCase(record)
                result.fold(
                    onSuccess = {
                        hideDeleteDialog()
                        // 重新加载数据
                        loadRecords()
                    },
                    onFailure = { throwable ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = throwable.message ?: "删除失败"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "删除失败"
                )
            }
        }
    }

    /**
     * 清除所有过滤条件
     */
    fun clearFilters() {
        _uiState.value = _uiState.value.copy(
            searchQuery = "",
            selectedDateRange = null,
            selectedTags = emptySet()
        )
        applyFilters()
    }

    /**
     * 清除错误
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    /**
     * 设置视图模式
     */
    fun setViewMode(viewMode: ViewMode) {
        _uiState.value = _uiState.value.copy(viewMode = viewMode)
    }

    /**
     * 应用过滤条件
     */
    private fun applyFilters() {
        val state = _uiState.value
        var filtered = state.records

        // 应用搜索过滤
        if (state.searchQuery.isNotBlank()) {
            filtered = filtered.filter { record ->
                record.note.contains(state.searchQuery, ignoreCase = true) ||
                record.tags.any { tag -> tag.contains(state.searchQuery, ignoreCase = true) }
            }
        }

        // 应用日期范围过滤
        state.selectedDateRange?.let { dateRange ->
            filtered = filtered.filter { record ->
                val recordDate = record.measureTime.toLocalDate()
                recordDate >= dateRange.startDate && recordDate <= dateRange.endDate
            }
        }

        // 应用标签过滤
        if (state.selectedTags.isNotEmpty()) {
            filtered = filtered.filter { record ->
                record.tags.any { tag -> state.selectedTags.contains(tag) }
            }
        }

        _uiState.value = state.copy(filteredRecords = filtered)
    }

    /**
     * 设置预定义日期范围
     */
    fun setQuickDateRange(type: QuickDateRangeType) {
        val dateRange = when (type) {
            QuickDateRangeType.THIS_WEEK -> DateRange.thisWeek()
            QuickDateRangeType.THIS_MONTH -> DateRange.thisMonth()
            QuickDateRangeType.LAST_30_DAYS -> DateRange.last30Days()
            QuickDateRangeType.ALL -> null
        }
        updateDateRange(dateRange)
    }
}

/**
 * 快速日期范围类型
 */
enum class QuickDateRangeType {
    THIS_WEEK, THIS_MONTH, LAST_30_DAYS, ALL
}