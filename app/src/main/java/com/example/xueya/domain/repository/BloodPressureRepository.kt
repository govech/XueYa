package com.example.xueya.domain.repository

import com.example.xueya.domain.model.BloodPressureData
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * 血压数据仓库接口
 * 定义业务层需要的数据操作
 */
interface BloodPressureRepository {

    /**
     * 获取所有血压记录
     */
    fun getAllRecords(): Flow<List<BloodPressureData>>

    /**
     * 根据ID获取记录
     */
    suspend fun getRecordById(id: Long): BloodPressureData?

    /**
     * 获取指定时间范围内的记录
     */
    fun getRecordsByDateRange(
        startTime: LocalDateTime, 
        endTime: LocalDateTime
    ): Flow<List<BloodPressureData>>

    /**
     * 获取最近N条记录
     */
    fun getRecentRecords(limit: Int): Flow<List<BloodPressureData>>

    /**
     * 获取最新的一条记录
     */
    suspend fun getLatestRecord(): BloodPressureData?

    /**
     * 添加新记录
     */
    suspend fun addRecord(bloodPressureData: BloodPressureData): Long

    /**
     * 更新记录
     */
    suspend fun updateRecord(bloodPressureData: BloodPressureData)

    /**
     * 删除记录
     */
    suspend fun deleteRecord(bloodPressureData: BloodPressureData)

    /**
     * 根据ID删除记录
     */
    suspend fun deleteRecordById(id: Long)

    /**
     * 获取记录总数
     */
    suspend fun getRecordCount(): Int

    /**
     * 搜索记录
     */
    fun searchRecords(searchText: String): Flow<List<BloodPressureData>>

    /**
     * 根据标签获取记录
     */
    fun getRecordsByTag(tag: String): Flow<List<BloodPressureData>>

    /**
     * 获取统计数据
     */
    suspend fun getStatistics(
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): BloodPressureStatistics?
}

/**
 * 血压统计数据
 */
data class BloodPressureStatistics(
    val averageSystolic: Double,
    val averageDiastolic: Double,
    val averageHeartRate: Double,
    val recordCount: Int,
    val normalCount: Int,
    val elevatedCount: Int,
    val highStage1Count: Int,
    val highStage2Count: Int,
    val crisisCount: Int
)