package com.example.xueya.domain.repository

import com.example.xueya.domain.model.BloodPressureData
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * 血压数据仓库接口
 * 
 * 定义业务层需要的数据操作接口，作为数据访问层和业务逻辑层之间的桥梁
 * 所有查询操作都返回Flow类型，支持响应式数据流
 * 所有增删改操作都使用suspend函数，支持协程异步执行
 */
interface BloodPressureRepository {

    /**
     * 获取所有血压记录
     * 
     * 获取数据库中存储的所有血压记录，按测量时间倒序排列
     * 返回Flow类型支持响应式数据流，当数据发生变化时会自动更新UI
     * 
     * @return Flow<List<BloodPressureData>> 包含所有血压记录的响应式数据流
     */
    fun getAllRecords(): Flow<List<BloodPressureData>>

    /**
     * 根据ID获取记录
     * 
     * 通过主键ID精确查找指定的血压记录
     * 如果记录不存在则返回null
     * 这是一个挂起函数，需要在协程作用域内调用
     * 
     * @param id 记录的主键ID
     * @return BloodPressureData? 查找到的血压记录，如果不存在则返回null
     */
    suspend fun getRecordById(id: Long): BloodPressureData?

    /**
     * 获取指定时间范围内的记录
     * 
     * 查询测量时间在指定起止时间范围内的所有血压记录
     * 记录按测量时间从新到旧排序
     * 返回Flow类型支持响应式数据流
     * 
     * @param startTime 查询起始时间（包含）
     * @param endTime 查询结束时间（包含）
     * @return Flow<List<BloodPressureData>> 符合时间范围的血压记录响应式数据流
     */
    fun getRecordsByDateRange(
        startTime: LocalDateTime, 
        endTime: LocalDateTime
    ): Flow<List<BloodPressureData>>

    /**
     * 获取最近N条记录
     * 
     * 查询最新的N条血压记录，按测量时间倒序排列
     * 常用于首页显示最近几次测量结果
     * 返回Flow类型支持响应式数据流
     * 
     * @param limit 要获取的记录数量
     * @return Flow<List<BloodPressureData>> 最近N条血压记录的响应式数据流
     */
    fun getRecentRecords(limit: Int): Flow<List<BloodPressureData>>

    /**
     * 获取最新的一条记录
     * 
     * 查询测量时间最新的单条血压记录
     * 如果没有记录则返回null
     * 这是一个挂起函数，需要在协程作用域内调用
     * 
     * @return BloodPressureData? 最新的血压记录，如果没有记录则返回null
     */
    suspend fun getLatestRecord(): BloodPressureData?

    /**
     * 添加新记录
     * 
     * 将新的血压记录保存到数据库中
     * 这是一个挂起函数，需要在协程作用域内调用
     * 
     * @param bloodPressureData 要添加的血压记录对象
     * @return Long 新添加记录的ID
     */
    suspend fun addRecord(bloodPressureData: BloodPressureData): Long

    /**
     * 更新记录
     * 
     * 更新数据库中已存在的血压记录
     * 根据记录的主键ID匹配要更新的记录
     * 这是一个挂起函数，需要在协程作用域内调用
     * 
     * @param bloodPressureData 要更新的血压记录对象
     */
    suspend fun updateRecord(bloodPressureData: BloodPressureData)

    /**
     * 删除记录
     * 
     * 根据记录对象删除数据库中的对应记录
     * 通过记录的主键ID匹配要删除的记录
     * 这是一个挂起函数，需要在协程作用域内调用
     * 
     * @param bloodPressureData 要删除的血压记录对象
     */
    suspend fun deleteRecord(bloodPressureData: BloodPressureData)

    /**
     * 根据ID删除记录
     * 
     * 根据记录的主键ID删除数据库中的对应记录
     * 这是一个挂起函数，需要在协程作用域内调用
     * 
     * @param id 要删除的记录的主键ID
     */
    suspend fun deleteRecordById(id: Long)

    /**
     * 清空所有记录
     * 
     * 删除数据库中的所有血压记录
     * 这是一个危险操作，请谨慎使用
     * 这是一个挂起函数，需要在协程作用域内调用
     */
    suspend fun clearAllRecords()

    /**
     * 获取记录总数
     * 
     * 统计数据库中血压记录的总数量
     * 这是一个挂起函数，需要在协程作用域内调用
     * 
     * @return Int 记录总数
     */
    suspend fun getRecordCount(): Int

    /**
     * 搜索记录
     * 
     * 在备注和标签字段中搜索包含指定文本的血压记录
     * 搜索不区分大小写，支持模糊匹配
     * 记录按测量时间从新到旧排序
     * 返回Flow类型支持响应式数据流
     * 
     * @param searchText 要搜索的文本内容
     * @return Flow<List<BloodPressureData>> 包含搜索结果的响应式数据流
     */
    fun searchRecords(searchText: String): Flow<List<BloodPressureData>>

    /**
     * 根据标签获取记录
     * 
     * 查询包含指定标签的所有血压记录
     * 标签通过模糊匹配查找
     * 记录按测量时间从新到旧排序
     * 返回Flow类型支持响应式数据流
     * 
     * @param tag 要查询的标签名称
     * @return Flow<List<BloodPressureData>> 包含指定标签记录的响应式数据流
     */
    fun getRecordsByTag(tag: String): Flow<List<BloodPressureData>>

    /**
     * 获取统计数据
     * 
     * 获取指定时间范围内的血压统计数据
     * 包括平均值、记录总数、各分类数量等信息
     * 这是一个挂起函数，需要在协程作用域内调用
     * 
     * @param startTime 统计起始时间（包含）
     * @param endTime 统计结束时间（包含）
     * @return BloodPressureStatistics? 血压统计数据对象，如果没有记录则返回null
     */
    suspend fun getStatistics(
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): BloodPressureStatistics?
}

/**
 * 血压统计数据
 * 
 * 用于封装血压统计数据的详细信息
 * 
 * @property averageSystolic 收缩压平均值
 * @property averageDiastolic 舒张压平均值
 * @property averageHeartRate 心率平均值
 * @property recordCount 记录总数
 * @property normalCount 正常血压记录数
 * @property elevatedCount 血压偏高记录数
 * @property highStage1Count 高血压1期记录数
 * @property highStage2Count 高血压2期记录数
 * @property crisisCount 高血压危象记录数
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