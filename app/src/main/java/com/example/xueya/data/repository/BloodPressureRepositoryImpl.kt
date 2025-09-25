package com.example.xueya.data.repository

import com.example.xueya.data.database.BloodPressureDao
import com.example.xueya.data.database.entities.BloodPressureRecord
import com.example.xueya.domain.model.BloodPressureCategory
import com.example.xueya.domain.model.BloodPressureData
import com.example.xueya.domain.repository.BloodPressureRepository
import com.example.xueya.domain.repository.BloodPressureStatistics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 血压数据仓库实现类
 * 
 * 实现BloodPressureRepository接口，负责具体的血压数据操作逻辑
 * 作为数据访问层和业务逻辑层之间的桥梁，处理数据转换和业务逻辑
 * 使用@Singleton注解确保在整个应用中只有一个实例
 * 使用@Inject注解支持Hilt依赖注入
 * 
 * @param bloodPressureDao 血压记录数据访问对象，通过构造函数注入
 */
@Singleton
class BloodPressureRepositoryImpl @Inject constructor(
    private val bloodPressureDao: BloodPressureDao
) : BloodPressureRepository {

    /**
     * 获取所有血压记录
     * 
     * 从数据库获取所有血压记录，并将数据库实体转换为领域模型
     * 使用Flow.map操作符进行数据转换，保持响应式数据流特性
     * 
     * @return Flow<List<BloodPressureData>> 包含所有血压记录的响应式数据流
     */
    override fun getAllRecords(): Flow<List<BloodPressureData>> {
        return bloodPressureDao.getAllRecords().map { records ->
            records.map { it.toDomainModel() }
        }
    }

    /**
     * 根据ID获取记录
     * 
     * 通过主键ID从数据库获取指定的血压记录，并将数据库实体转换为领域模型
     * 如果记录不存在则返回null
     * 
     * @param id 记录的主键ID
     * @return BloodPressureData? 查找到的血压记录，如果不存在则返回null
     */
    override suspend fun getRecordById(id: Long): BloodPressureData? {
        return bloodPressureDao.getRecordById(id)?.toDomainModel()
    }

    /**
     * 获取指定时间范围内的记录
     * 
     * 从数据库获取指定时间范围内的血压记录，并将数据库实体转换为领域模型
     * 使用Flow.map操作符进行数据转换，保持响应式数据流特性
     * 
     * @param startTime 查询起始时间（包含）
     * @param endTime 查询结束时间（包含）
     * @return Flow<List<BloodPressureData>> 符合时间范围的血压记录响应式数据流
     */
    override fun getRecordsByDateRange(
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): Flow<List<BloodPressureData>> {
        return bloodPressureDao.getRecordsByDateRange(startTime, endTime).map { records ->
            records.map { it.toDomainModel() }
        }
    }

    /**
     * 获取最近N条记录
     * 
     * 从数据库获取最新的N条血压记录，并将数据库实体转换为领域模型
     * 使用Flow.map操作符进行数据转换，保持响应式数据流特性
     * 
     * @param limit 要获取的记录数量
     * @return Flow<List<BloodPressureData>> 最近N条血压记录的响应式数据流
     */
    override fun getRecentRecords(limit: Int): Flow<List<BloodPressureData>> {
        return bloodPressureDao.getRecentRecords(limit).map { records ->
            records.map { it.toDomainModel() }
        }
    }

    /**
     * 获取最新的一条记录
     * 
     * 从数据库获取测量时间最新的单条血压记录，并将数据库实体转换为领域模型
     * 如果没有记录则返回null
     * 
     * @return BloodPressureData? 最新的血压记录，如果没有记录则返回null
     */
    override suspend fun getLatestRecord(): BloodPressureData? {
        return bloodPressureDao.getLatestRecord()?.toDomainModel()
    }

    /**
     * 添加新记录
     * 
     * 将领域模型转换为数据库实体后插入数据库
     * 返回新插入记录的ID
     * 
     * @param bloodPressureData 要添加的血压记录对象
     * @return Long 新添加记录的ID
     */
    override suspend fun addRecord(bloodPressureData: BloodPressureData): Long {
        return bloodPressureDao.insertRecord(bloodPressureData.toEntity())
    }

    /**
     * 更新记录
     * 
     * 将领域模型转换为数据库实体后更新数据库中的记录
     * 
     * @param bloodPressureData 要更新的血压记录对象
     */
    override suspend fun updateRecord(bloodPressureData: BloodPressureData) {
        bloodPressureDao.updateRecord(bloodPressureData.toEntity())
    }

    /**
     * 删除记录
     * 
     * 将领域模型转换为数据库实体后删除数据库中的记录
     * 
     * @param bloodPressureData 要删除的血压记录对象
     */
    override suspend fun deleteRecord(bloodPressureData: BloodPressureData) {
        bloodPressureDao.deleteRecord(bloodPressureData.toEntity())
    }

    /**
     * 根据ID删除记录
     * 
     * 根据记录的主键ID删除数据库中的对应记录
     * 
     * @param id 要删除的记录的主键ID
     */
    override suspend fun deleteRecordById(id: Long) {
        bloodPressureDao.deleteRecordById(id)
    }

    /**
     * 清空所有记录
     * 
     * 删除数据库中的所有血压记录
     * 这是一个危险操作，请谨慎使用
     */
    override suspend fun clearAllRecords() {
        bloodPressureDao.deleteAllRecords()
    }

    /**
     * 获取记录总数
     * 
     * 统计数据库中血压记录的总数量
     * 
     * @return Int 记录总数
     */
    override suspend fun getRecordCount(): Int {
        return bloodPressureDao.getRecordCount()
    }

    /**
     * 搜索记录
     * 
     * 在数据库中搜索包含指定文本的血压记录，并将数据库实体转换为领域模型
     * 使用Flow.map操作符进行数据转换，保持响应式数据流特性
     * 
     * @param searchText 要搜索的文本内容
     * @return Flow<List<BloodPressureData>> 包含搜索结果的响应式数据流
     */
    override fun searchRecords(searchText: String): Flow<List<BloodPressureData>> {
        return bloodPressureDao.searchRecords(searchText).map { records ->
            records.map { it.toDomainModel() }
        }
    }

    /**
     * 根据标签获取记录
     * 
     * 从数据库获取包含指定标签的血压记录，并将数据库实体转换为领域模型
     * 使用Flow.map操作符进行数据转换，保持响应式数据流特性
     * 
     * @param tag 要查询的标签名称
     * @return Flow<List<BloodPressureData>> 包含指定标签记录的响应式数据流
     */
    override fun getRecordsByTag(tag: String): Flow<List<BloodPressureData>> {
        return bloodPressureDao.getRecordsByTag(tag).map { records ->
            records.map { it.toDomainModel() }
        }
    }

    /**
     * 获取统计数据
     * 
     * 从数据库获取指定时间范围内的血压统计数据
     * 包括平均值、记录总数等基本信息
     * 分类统计信息将在UseCase层通过Flow实现
     * 
     * @param startTime 统计起始时间（包含）
     * @param endTime 统计结束时间（包含）
     * @return BloodPressureStatistics? 血压统计数据对象，如果没有记录则返回null
     */
    override suspend fun getStatistics(
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): BloodPressureStatistics? {
        val averageValues = bloodPressureDao.getAverageValues(startTime, endTime)
        if (averageValues == null) return null

        // 获取指定时间范围的记录总数
        val recordCount = bloodPressureDao.getRecordCountByDateRange(startTime, endTime)
        if (recordCount == 0) return null

        // 由于这里需要统计分类，暂时返回基本统计信息
        // 具体的分类统计将在UseCase层通过Flow实现
        return BloodPressureStatistics(
            averageSystolic = averageValues.avgSystolic,
            averageDiastolic = averageValues.avgDiastolic,
            averageHeartRate = averageValues.avgHeartRate,
            recordCount = recordCount,
            normalCount = 0, // 将在UseCase层计算
            elevatedCount = 0,
            highStage1Count = 0,
            highStage2Count = 0,
            crisisCount = 0
        )
    }

    /**
     * 将数据库实体转换为领域模型
     * 
     * 将BloodPressureRecord数据库实体转换为BloodPressureData领域模型
     * 处理标签字段的格式转换（从逗号分隔字符串转换为列表）
     * 
     * @return BloodPressureData 转换后的领域模型对象
     */
    private fun BloodPressureRecord.toDomainModel(): BloodPressureData {
        return BloodPressureData(
            id = id,
            systolic = systolic,
            diastolic = diastolic,
            heartRate = heartRate,
            measureTime = measureTime,
            note = note,
            tags = if (tags.isBlank()) emptyList() else tags.split(",").map { it.trim() }
        )
    }

    /**
     * 将领域模型转换为数据库实体
     * 
     * 将BloodPressureData领域模型转换为BloodPressureRecord数据库实体
     * 处理标签字段的格式转换（从列表转换为逗号分隔字符串）
     * 
     * @return BloodPressureRecord 转换后的数据库实体对象
     */
    private fun BloodPressureData.toEntity(): BloodPressureRecord {
        return BloodPressureRecord(
            id = id,
            systolic = systolic,
            diastolic = diastolic,
            heartRate = heartRate,
            measureTime = measureTime,
            note = note,
            tags = tags.joinToString(",")
        )
    }
}