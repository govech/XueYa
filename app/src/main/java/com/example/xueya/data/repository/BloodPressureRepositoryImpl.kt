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
 */
@Singleton
class BloodPressureRepositoryImpl @Inject constructor(
    private val bloodPressureDao: BloodPressureDao
) : BloodPressureRepository {

    override fun getAllRecords(): Flow<List<BloodPressureData>> {
        return bloodPressureDao.getAllRecords().map { records ->
            records.map { it.toDomainModel() }
        }
    }

    override suspend fun getRecordById(id: Long): BloodPressureData? {
        return bloodPressureDao.getRecordById(id)?.toDomainModel()
    }

    override fun getRecordsByDateRange(
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): Flow<List<BloodPressureData>> {
        return bloodPressureDao.getRecordsByDateRange(startTime, endTime).map { records ->
            records.map { it.toDomainModel() }
        }
    }

    override fun getRecentRecords(limit: Int): Flow<List<BloodPressureData>> {
        return bloodPressureDao.getRecentRecords(limit).map { records ->
            records.map { it.toDomainModel() }
        }
    }

    override suspend fun getLatestRecord(): BloodPressureData? {
        return bloodPressureDao.getLatestRecord()?.toDomainModel()
    }

    override suspend fun addRecord(bloodPressureData: BloodPressureData): Long {
        return bloodPressureDao.insertRecord(bloodPressureData.toEntity())
    }

    override suspend fun updateRecord(bloodPressureData: BloodPressureData) {
        bloodPressureDao.updateRecord(bloodPressureData.toEntity())
    }

    override suspend fun deleteRecord(bloodPressureData: BloodPressureData) {
        bloodPressureDao.deleteRecord(bloodPressureData.toEntity())
    }

    override suspend fun deleteRecordById(id: Long) {
        bloodPressureDao.deleteRecordById(id)
    }

    override suspend fun getRecordCount(): Int {
        return bloodPressureDao.getRecordCount()
    }

    override fun searchRecords(searchText: String): Flow<List<BloodPressureData>> {
        return bloodPressureDao.searchRecords(searchText).map { records ->
            records.map { it.toDomainModel() }
        }
    }

    override fun getRecordsByTag(tag: String): Flow<List<BloodPressureData>> {
        return bloodPressureDao.getRecordsByTag(tag).map { records ->
            records.map { it.toDomainModel() }
        }
    }

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