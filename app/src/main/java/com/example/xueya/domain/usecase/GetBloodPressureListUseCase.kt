package com.example.xueya.domain.usecase

import com.example.xueya.domain.model.BloodPressureData
import com.example.xueya.domain.repository.BloodPressureRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * 获取血压记录列表用例
 * 处理获取血压记录列表的业务逻辑
 */
class GetBloodPressureListUseCase @Inject constructor(
    private val repository: BloodPressureRepository
) {
    /**
     * 获取所有血压记录
     */
    fun getAllRecords(): Flow<List<BloodPressureData>> {
        return repository.getAllRecords()
    }

    /**
     * 获取最近的记录
     * 
     * @param limit 限制数量，默认10条
     */
    fun getRecentRecords(limit: Int = 10): Flow<List<BloodPressureData>> {
        return repository.getRecentRecords(limit)
    }

    /**
     * 获取指定日期范围的记录
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     */
    fun getRecordsByDateRange(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<BloodPressureData>> {
        return repository.getRecordsByDateRange(startDate, endDate)
    }

    /**
     * 获取今天的记录
     */
    fun getTodayRecords(): Flow<List<BloodPressureData>> {
        val today = LocalDateTime.now()
        val startOfDay = today.toLocalDate().atStartOfDay()
        val endOfDay = today.toLocalDate().atTime(23, 59, 59)
        
        return repository.getRecordsByDateRange(startOfDay, endOfDay)
    }

    /**
     * 获取本周的记录
     */
    fun getThisWeekRecords(): Flow<List<BloodPressureData>> {
        val now = LocalDateTime.now()
        val startOfWeek = now.minusDays(now.dayOfWeek.value - 1L).toLocalDate().atStartOfDay()
        val endOfWeek = startOfWeek.plusDays(6).toLocalDate().atTime(23, 59, 59)
        
        return repository.getRecordsByDateRange(startOfWeek, endOfWeek)
    }

    /**
     * 获取本月的记录
     */
    fun getThisMonthRecords(): Flow<List<BloodPressureData>> {
        val now = LocalDateTime.now()
        val startOfMonth = now.toLocalDate().withDayOfMonth(1).atStartOfDay()
        val endOfMonth = now.toLocalDate().withDayOfMonth(now.toLocalDate().lengthOfMonth()).atTime(23, 59, 59)
        
        return repository.getRecordsByDateRange(startOfMonth, endOfMonth)
    }

    /**
     * 搜索记录
     * 
     * @param searchText 搜索文本
     */
    fun searchRecords(searchText: String): Flow<List<BloodPressureData>> {
        return if (searchText.isBlank()) {
            repository.getAllRecords()
        } else {
            repository.searchRecords(searchText)
        }
    }

    /**
     * 根据标签获取记录
     * 
     * @param tag 标签
     */
    fun getRecordsByTag(tag: String): Flow<List<BloodPressureData>> {
        return repository.getRecordsByTag(tag)
    }

    /**
     * 获取需要关注的记录（高血压等）
     */
    fun getRecordsNeedingAttention(): Flow<List<BloodPressureData>> {
        return repository.getAllRecords().map { records ->
            records.filter { it.needsAttention }
        }
    }
}