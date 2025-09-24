package com.example.xueya.domain.usecase

import com.example.xueya.domain.model.BloodPressureData
import com.example.xueya.domain.repository.BloodPressureRepository
import javax.inject.Inject

/**
 * 删除血压记录用例
 * 处理删除血压记录的业务逻辑
 */
class DeleteBloodPressureUseCase @Inject constructor(
    private val repository: BloodPressureRepository
) {
    /**
     * 删除单个记录
     * 
     * @param bloodPressureData 要删除的血压数据
     */
    suspend operator fun invoke(bloodPressureData: BloodPressureData): Result<Unit> {
        return try {
            repository.deleteRecord(bloodPressureData)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 根据ID删除记录
     * 
     * @param recordId 记录ID
     */
    suspend fun deleteById(recordId: Long): Result<Unit> {
        return try {
            repository.deleteRecordById(recordId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 批量删除记录
     * 
     * @param records 要删除的记录列表
     */
    suspend fun deleteMultiple(records: List<BloodPressureData>): Result<Unit> {
        return try {
            records.forEach { record ->
                repository.deleteRecord(record)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 根据ID列表批量删除
     * 
     * @param recordIds 记录ID列表
     */
    suspend fun deleteMultipleById(recordIds: List<Long>): Result<Unit> {
        return try {
            recordIds.forEach { id ->
                repository.deleteRecordById(id)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}