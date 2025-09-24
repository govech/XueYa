package com.example.xueya.data.database

import androidx.room.*
import com.example.xueya.data.database.entities.BloodPressureRecord
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * 血压记录数据访问对象
 */
@Dao
interface BloodPressureDao {

    /**
     * 获取所有血压记录，按时间倒序排列
     */
    @Query("SELECT * FROM blood_pressure_records ORDER BY measureTime DESC")
    fun getAllRecords(): Flow<List<BloodPressureRecord>>

    /**
     * 根据ID获取记录
     */
    @Query("SELECT * FROM blood_pressure_records WHERE id = :id")
    suspend fun getRecordById(id: Long): BloodPressureRecord?

    /**
     * 获取指定时间范围内的记录
     */
    @Query("SELECT * FROM blood_pressure_records WHERE measureTime BETWEEN :startTime AND :endTime ORDER BY measureTime DESC")
    fun getRecordsByDateRange(startTime: LocalDateTime, endTime: LocalDateTime): Flow<List<BloodPressureRecord>>

    /**
     * 获取最近N条记录
     */
    @Query("SELECT * FROM blood_pressure_records ORDER BY measureTime DESC LIMIT :limit")
    fun getRecentRecords(limit: Int): Flow<List<BloodPressureRecord>>

    /**
     * 获取最新的一条记录
     */
    @Query("SELECT * FROM blood_pressure_records ORDER BY measureTime DESC LIMIT 1")
    suspend fun getLatestRecord(): BloodPressureRecord?

    /**
     * 插入新记录
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: BloodPressureRecord): Long

    /**
     * 批量插入记录
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecords(records: List<BloodPressureRecord>)

    /**
     * 更新记录
     */
    @Update
    suspend fun updateRecord(record: BloodPressureRecord)

    /**
     * 删除记录
     */
    @Delete
    suspend fun deleteRecord(record: BloodPressureRecord)

    /**
     * 根据ID删除记录
     */
    @Query("DELETE FROM blood_pressure_records WHERE id = :id")
    suspend fun deleteRecordById(id: Long)

    /**
     * 删除所有记录
     */
    @Query("DELETE FROM blood_pressure_records")
    suspend fun deleteAllRecords()

    /**
     * 获取记录总数
     */
    @Query("SELECT COUNT(*) FROM blood_pressure_records")
    suspend fun getRecordCount(): Int

    /**
     * 获取指定时间范围的记录总数
     */
    @Query("SELECT COUNT(*) FROM blood_pressure_records WHERE measureTime BETWEEN :startTime AND :endTime")
    suspend fun getRecordCountByDateRange(startTime: LocalDateTime, endTime: LocalDateTime): Int

    /**
     * 搜索包含指定文本的记录
     */
    @Query("SELECT * FROM blood_pressure_records WHERE note LIKE '%' || :searchText || '%' OR tags LIKE '%' || :searchText || '%' ORDER BY measureTime DESC")
    fun searchRecords(searchText: String): Flow<List<BloodPressureRecord>>

    /**
     * 获取统计数据 - 平均值
     */
    @Query("SELECT AVG(systolic) as avgSystolic, AVG(diastolic) as avgDiastolic, AVG(heartRate) as avgHeartRate FROM blood_pressure_records WHERE measureTime BETWEEN :startTime AND :endTime")
    suspend fun getAverageValues(startTime: LocalDateTime, endTime: LocalDateTime): AverageValues?

    /**
     * 获取指定标签的记录
     */
    @Query("SELECT * FROM blood_pressure_records WHERE tags LIKE '%' || :tag || '%' ORDER BY measureTime DESC")
    fun getRecordsByTag(tag: String): Flow<List<BloodPressureRecord>>
}

/**
 * 平均值数据类
 */
data class AverageValues(
    val avgSystolic: Double,
    val avgDiastolic: Double,
    val avgHeartRate: Double
)