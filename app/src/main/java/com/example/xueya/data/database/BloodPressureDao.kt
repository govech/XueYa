package com.example.xueya.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.xueya.data.database.entities.BloodPressureRecord
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * 血压记录数据访问对象（DAO）
 * 
 * 定义所有血压记录相关的数据库操作接口，使用Room框架实现
 * 所有查询操作都返回Flow类型，支持响应式数据流
 * 所有增删改操作都使用suspend函数，支持协程异步执行
 */
@Dao
interface BloodPressureDao {

    /**
     * 获取所有血压记录，按测量时间倒序排列
     * 
     * 使用Flow返回数据，当数据库数据发生变化时会自动更新
     * 记录按测量时间从新到旧排序，最新的记录排在最前面
     * 
     * @return Flow<List<BloodPressureRecord>> 包含所有血压记录的响应式数据流
     */
    @Query("SELECT * FROM blood_pressure_records ORDER BY measureTime DESC")
    fun getAllRecords(): Flow<List<BloodPressureRecord>>

    /**
     * 根据ID获取指定的血压记录
     * 
     * 通过主键ID精确查找记录，如果记录不存在则返回null
     * 这是一个挂起函数，需要在协程作用域内调用
     * 
     * @param id 记录的主键ID
     * @return BloodPressureRecord? 查找到的血压记录，如果不存在则返回null
     */
    @Query("SELECT * FROM blood_pressure_records WHERE id = :id")
    suspend fun getRecordById(id: Long): BloodPressureRecord?

    /**
     * 获取指定时间范围内的血压记录
     * 
     * 查询测量时间在指定起止时间范围内的所有记录
     * 记录按测量时间从新到旧排序，最新的记录排在最前面
     * 
     * @param startTime 查询起始时间（包含）
     * @param endTime 查询结束时间（包含）
     * @return Flow<List<BloodPressureRecord>> 符合时间范围的血压记录响应式数据流
     */
    @Query("SELECT * FROM blood_pressure_records WHERE measureTime BETWEEN :startTime AND :endTime ORDER BY measureTime DESC")
    fun getRecordsByDateRange(startTime: LocalDateTime, endTime: LocalDateTime): Flow<List<BloodPressureRecord>>

    /**
     * 获取最近N条血压记录
     * 
     * 查询最新的N条记录，按测量时间倒序排列
     * 常用于首页显示最近几次测量结果
     * 
     * @param limit 要获取的记录数量
     * @return Flow<List<BloodPressureRecord>> 最近N条血压记录的响应式数据流
     */
    @Query("SELECT * FROM blood_pressure_records ORDER BY measureTime DESC LIMIT :limit")
    fun getRecentRecords(limit: Int): Flow<List<BloodPressureRecord>>

    /**
     * 获取最新的一条血压记录
     * 
     * 查询测量时间最新的单条记录，如果没有记录则返回null
     * 这是一个挂起函数，需要在协程作用域内调用
     * 
     * @return BloodPressureRecord? 最新的血压记录，如果没有记录则返回null
     */
    @Query("SELECT * FROM blood_pressure_records ORDER BY measureTime DESC LIMIT 1")
    suspend fun getLatestRecord(): BloodPressureRecord?

    /**
     * 插入新血压记录
     * 
     * 将新的血压记录插入数据库，如果记录ID已存在则替换原有记录
     * 这是一个挂起函数，需要在协程作用域内调用
     * 
     * @param record 要插入的血压记录对象
     * @return Long 插入记录的ID
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: BloodPressureRecord): Long

    /**
     * 批量插入血压记录
     * 
     * 将多个血压记录批量插入数据库，提高插入效率
     * 如果记录ID已存在则替换原有记录
     * 这是一个挂起函数，需要在协程作用域内调用
     * 
     * @param records 要插入的血压记录列表
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecords(records: List<BloodPressureRecord>)

    /**
     * 更新血压记录
     * 
     * 更新数据库中已存在的血压记录
     * 根据记录的主键ID匹配要更新的记录
     * 这是一个挂起函数，需要在协程作用域内调用
     * 
     * @param record 要更新的血压记录对象
     */
    @Update
    suspend fun updateRecord(record: BloodPressureRecord)

    /**
     * 删除血压记录
     * 
     * 根据记录对象删除数据库中的对应记录
     * 通过记录的主键ID匹配要删除的记录
     * 这是一个挂起函数，需要在协程作用域内调用
     * 
     * @param record 要删除的血压记录对象
     */
    @Delete
    suspend fun deleteRecord(record: BloodPressureRecord)

    /**
     * 根据ID删除血压记录
     * 
     * 根据记录的主键ID删除数据库中的对应记录
     * 这是一个挂起函数，需要在协程作用域内调用
     * 
     * @param id 要删除的记录的主键ID
     */
    @Query("DELETE FROM blood_pressure_records WHERE id = :id")
    suspend fun deleteRecordById(id: Long)

    /**
     * 删除所有血压记录
     * 
     * 清空血压记录表中的所有数据
     * 这是一个危险操作，请谨慎使用
     * 这是一个挂起函数，需要在协程作用域内调用
     */
    @Query("DELETE FROM blood_pressure_records")
    suspend fun deleteAllRecords()

    /**
     * 获取血压记录总数
     * 
     * 统计血压记录表中的记录总数
     * 这是一个挂起函数，需要在协程作用域内调用
     * 
     * @return Int 记录总数
     */
    @Query("SELECT COUNT(*) FROM blood_pressure_records")
    suspend fun getRecordCount(): Int

    /**
     * 获取指定时间范围的血压记录总数
     * 
     * 统计指定时间范围内的血压记录数量
     * 
     * @param startTime 统计起始时间（包含）
     * @param endTime 统计结束时间（包含）
     * @return Int 指定时间范围内的记录总数
     */
    @Query("SELECT COUNT(*) FROM blood_pressure_records WHERE measureTime BETWEEN :startTime AND :endTime")
    suspend fun getRecordCountByDateRange(startTime: LocalDateTime, endTime: LocalDateTime): Int

    /**
     * 搜索包含指定文本的血压记录
     * 
     * 在备注和标签字段中搜索包含指定文本的记录
     * 搜索不区分大小写，支持模糊匹配
     * 记录按测量时间从新到旧排序
     * 
     * @param searchText 要搜索的文本内容
     * @return Flow<List<BloodPressureRecord>> 包含搜索结果的响应式数据流
     */
    @Query("SELECT * FROM blood_pressure_records WHERE note LIKE '%' || :searchText || '%' OR tags LIKE '%' || :searchText || '%' ORDER BY measureTime DESC")
    fun searchRecords(searchText: String): Flow<List<BloodPressureRecord>>

    /**
     * 获取指定时间范围内的平均血压值
     * 
     * 计算指定时间范围内所有记录的收缩压、舒张压和心率的平均值
     * 如果没有记录则返回null
     * 这是一个挂起函数，需要在协程作用域内调用
     * 
     * @param startTime 统计起始时间（包含）
     * @param endTime 统计结束时间（包含）
     * @return AverageValues? 包含平均值的对象，如果没有记录则返回null
     */
    @Query("SELECT AVG(systolic) as avgSystolic, AVG(diastolic) as avgDiastolic, AVG(heartRate) as avgHeartRate FROM blood_pressure_records WHERE measureTime BETWEEN :startTime AND :endTime")
    suspend fun getAverageValues(startTime: LocalDateTime, endTime: LocalDateTime): AverageValues?

    /**
     * 获取指定标签的血压记录
     * 
     * 查询包含指定标签的所有血压记录
     * 标签存储为逗号分隔的字符串，通过模糊匹配查找
     * 记录按测量时间从新到旧排序
     * 
     * @param tag 要查询的标签名称
     * @return Flow<List<BloodPressureRecord>> 包含指定标签记录的响应式数据流
     */
    @Query("SELECT * FROM blood_pressure_records WHERE tags LIKE '%' || :tag || '%' ORDER BY measureTime DESC")
    fun getRecordsByTag(tag: String): Flow<List<BloodPressureRecord>>
}

/**
 * 平均值数据类
 * 
 * 用于封装血压统计数据的平均值信息
 * 
 * @property avgSystolic 收缩压平均值
 * @property avgDiastolic 舒张压平均值
 * @property avgHeartRate 心率平均值
 */
data class AverageValues(
    val avgSystolic: Double,
    val avgDiastolic: Double,
    val avgHeartRate: Double
)