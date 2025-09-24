package com.example.xueya.presentation.screens.history

import com.example.xueya.domain.model.BloodPressureData
import java.time.LocalDate

/**
 * 历史记录页面UI状态
 */
data class HistoryUiState(
    val records: List<BloodPressureData> = emptyList(),    // 记录列表
    val filteredRecords: List<BloodPressureData> = emptyList(), // 过滤后的记录列表
    val isLoading: Boolean = false,                        // 加载状态
    val error: String? = null,                            // 错误信息
    val searchQuery: String = "",                         // 搜索关键词
    val selectedDateRange: DateRange? = null,             // 选择的日期范围
    val selectedTags: Set<String> = emptySet(),           // 选择的标签过滤
    val sortOrder: SortOrder = SortOrder.NEWEST_FIRST,    // 排序方式
    val showFilters: Boolean = false,                     // 显示过滤器
    val showDatePicker: Boolean = false,                  // 显示日期选择器
    val selectedRecord: BloodPressureData? = null,        // 选中的记录（用于详情或删除）
    val showDeleteDialog: Boolean = false,                // 显示删除确认对话框
) {
    /**
     * 是否有记录
     */
    val hasRecords: Boolean
        get() = records.isNotEmpty()

    /**
     * 显示的记录列表（搜索和过滤后）
     */
    val displayRecords: List<BloodPressureData>
        get() = if (filteredRecords.isNotEmpty() || searchQuery.isNotBlank() || 
                    selectedDateRange != null || selectedTags.isNotEmpty()) {
            filteredRecords
        } else {
            records
        }.let { list ->
            when (sortOrder) {
                SortOrder.NEWEST_FIRST -> list.sortedByDescending { it.measureTime }
                SortOrder.OLDEST_FIRST -> list.sortedBy { it.measureTime }
                SortOrder.HIGHEST_BP -> list.sortedByDescending { it.systolic }
                SortOrder.LOWEST_BP -> list.sortedBy { it.systolic }
            }
        }

    /**
     * 是否有活动的过滤条件
     */
    val hasActiveFilters: Boolean
        get() = searchQuery.isNotBlank() || selectedDateRange != null || selectedTags.isNotEmpty()

    /**
     * 过滤器数量提示
     */
    val filterCount: Int
        get() = listOfNotNull(
            if (searchQuery.isNotBlank()) 1 else null,
            if (selectedDateRange != null) 1 else null,
            if (selectedTags.isNotEmpty()) selectedTags.size else null
        ).sum()

    /**
     * 统计信息
     */
    val statistics: HistoryStatistics
        get() = HistoryStatistics.calculate(displayRecords)
}

/**
 * 日期范围
 */
data class DateRange(
    val startDate: LocalDate,
    val endDate: LocalDate
) {
    companion object {
        fun thisWeek(): DateRange {
            val today = LocalDate.now()
            val startOfWeek = today.minusDays(today.dayOfWeek.value - 1L)
            return DateRange(startOfWeek, today)
        }

        fun thisMonth(): DateRange {
            val today = LocalDate.now()
            val startOfMonth = today.withDayOfMonth(1)
            return DateRange(startOfMonth, today)
        }

        fun last30Days(): DateRange {
            val today = LocalDate.now()
            return DateRange(today.minusDays(29), today)
        }
    }
}

/**
 * 排序方式
 */
enum class SortOrder(val displayName: String) {
    NEWEST_FIRST("最新优先"),
    OLDEST_FIRST("最早优先"), 
    HIGHEST_BP("血压最高"),
    LOWEST_BP("血压最低")
}

/**
 * 历史记录统计信息
 */
data class HistoryStatistics(
    val totalCount: Int = 0,
    val averageSystolic: Double = 0.0,
    val averageDiastolic: Double = 0.0,
    val averageHeartRate: Double = 0.0,
    val highestBP: Pair<Int, Int>? = null,
    val lowestBP: Pair<Int, Int>? = null,
    val mostFrequentTags: List<String> = emptyList()
) {
    companion object {
        fun calculate(records: List<BloodPressureData>): HistoryStatistics {
            if (records.isEmpty()) return HistoryStatistics()

            val avgSystolic = records.map { it.systolic }.average()
            val avgDiastolic = records.map { it.diastolic }.average()
            val avgHeartRate = records.map { it.heartRate }.average()

            val highest = records.maxByOrNull { it.systolic }
            val lowest = records.minByOrNull { it.systolic }

            // 统计标签使用频率
            val tagFrequency = records.flatMap { it.tags }
                .groupingBy { it }
                .eachCount()
                .toList()
                .sortedByDescending { it.second }
                .take(3)
                .map { it.first }

            return HistoryStatistics(
                totalCount = records.size,
                averageSystolic = avgSystolic,
                averageDiastolic = avgDiastolic,
                averageHeartRate = avgHeartRate,
                highestBP = highest?.let { Pair(it.systolic, it.diastolic) },
                lowestBP = lowest?.let { Pair(it.systolic, it.diastolic) },
                mostFrequentTags = tagFrequency
            )
        }
    }
}