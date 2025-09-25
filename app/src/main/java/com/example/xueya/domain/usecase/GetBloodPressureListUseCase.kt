package com.example.xueya.domain.usecase

import com.example.xueya.domain.model.BloodPressureData
import com.example.xueya.domain.repository.BloodPressureRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * 获取血压记录列表用例
 * 
 * 处理获取血压记录列表的业务逻辑，提供多种查询方式
 * 包括全部记录、最近记录、时间范围查询、搜索等功能
 * 作为业务逻辑层的核心组件，负责协调数据查询操作
 * 使用@Inject注解支持Hilt依赖注入
 * 
 * @param repository 血压数据仓库，通过构造函数注入
 */
class GetBloodPressureListUseCase @Inject constructor(
    private val repository: BloodPressureRepository
) {
    /**
     * 获取所有血压记录
     * 
     * 从仓库获取所有血压记录，按测量时间倒序排列
     * 返回Flow类型支持响应式数据流
     * 
     * @return Flow<List<BloodPressureData>> 包含所有血压记录的响应式数据流
     */
    fun getAllRecords(): Flow<List<BloodPressureData>> {
        return repository.getAllRecords()
    }

    /**
     * 获取最近的记录
     * 
     * 从仓库获取最近的N条血压记录，按测量时间倒序排列
     * 返回Flow类型支持响应式数据流
     * 
     * @param limit 限制数量，默认10条
     * @return Flow<List<BloodPressureData>> 包含最近记录的响应式数据流
     */
    fun getRecentRecords(limit: Int = 10): Flow<List<BloodPressureData>> {
        return repository.getRecentRecords(limit)
    }

    /**
     * 获取指定日期范围的记录
     * 
     * 从仓库获取指定日期范围内的血压记录，按测量时间倒序排列
     * 返回Flow类型支持响应式数据流
     * 
     * @param startDate 查询起始日期时间（包含）
     * @param endDate 查询结束日期时间（包含）
     * @return Flow<List<BloodPressureData>> 符合时间范围的血压记录响应式数据流
     */
    fun getRecordsByDateRange(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<BloodPressureData>> {
        return repository.getRecordsByDateRange(startDate, endDate)
    }

    /**
     * 获取今天的记录
     * 
     * 从仓库获取今天日期范围内的血压记录
     * 自动计算今天的起止时间范围
     * 返回Flow类型支持响应式数据流
     * 
     * @return Flow<List<BloodPressureData>> 今天的血压记录响应式数据流
     */
    fun getTodayRecords(): Flow<List<BloodPressureData>> {
        val today = LocalDateTime.now()
        val startOfDay = today.toLocalDate().atStartOfDay()
        val endOfDay = today.toLocalDate().atTime(23, 59, 59)
        
        return repository.getRecordsByDateRange(startOfDay, endOfDay)
    }

    /**
     * 获取本周的记录
     * 
     * 从仓库获取本周日期范围内的血压记录
     * 自动计算本周的起止时间范围
     * 返回Flow类型支持响应式数据流
     * 
     * @return Flow<List<BloodPressureData>> 本周的血压记录响应式数据流
     */
    fun getThisWeekRecords(): Flow<List<BloodPressureData>> {
        val now = LocalDateTime.now()
        val startOfWeek = now.minusDays(now.dayOfWeek.value - 1L).toLocalDate().atStartOfDay()
        val endOfWeek = startOfWeek.plusDays(6).toLocalDate().atTime(23, 59, 59)
        
        return repository.getRecordsByDateRange(startOfWeek, endOfWeek)
    }

    /**
     * 获取本月的记录
     * 
     * 从仓库获取本月日期范围内的血压记录
     * 自动计算本月的起止时间范围
     * 返回Flow类型支持响应式数据流
     * 
     * @return Flow<List<BloodPressureData>> 本月的血压记录响应式数据流
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
     * 根据搜索文本在备注和标签字段中搜索匹配的记录
     * 如果搜索文本为空则返回所有记录
     * 返回Flow类型支持响应式数据流
     * 
     * @param searchText 搜索文本
     * @return Flow<List<BloodPressureData>> 包含搜索结果的响应式数据流
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
     * 从仓库获取包含指定标签的血压记录
     * 返回Flow类型支持响应式数据流
     * 
     * @param tag 标签名称
     * @return Flow<List<BloodPressureData>> 包含指定标签记录的响应式数据流
     */
    fun getRecordsByTag(tag: String): Flow<List<BloodPressureData>> {
        return repository.getRecordsByTag(tag)
    }

    /**
     * 获取需要关注的记录（高血压等）
     * 
     * 从仓库获取所有记录中需要特别关注的记录
     * 根据血压分类判断是否需要关注（高血压2期或高血压危象）
     * 返回Flow类型支持响应式数据流
     * 
     * @return Flow<List<BloodPressureData>> 需要关注的血压记录响应式数据流
     */
    fun getRecordsNeedingAttention(): Flow<List<BloodPressureData>> {
        return repository.getAllRecords().map { records ->
            records.filter { it.needsAttention }
        }
    }
}