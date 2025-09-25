package com.example.xueya.domain.usecase

import com.example.xueya.domain.model.BloodPressureData
import com.example.xueya.domain.repository.BloodPressureRepository
import javax.inject.Inject

/**
 * 删除血压记录用例
 * 
 * 处理删除血压记录的业务逻辑，提供多种删除方式
 * 包括单个删除、按ID删除、批量删除等功能
 * 作为业务逻辑层的核心组件，负责协调数据删除操作
 * 使用@Inject注解支持Hilt依赖注入
 * 
 * @param repository 血压数据仓库，通过构造函数注入
 */
class DeleteBloodPressureUseCase @Inject constructor(
    private val repository: BloodPressureRepository
) {
    /**
     * 删除单个血压记录
     * 
     * 根据血压数据对象删除对应的数据库记录
     * 使用Result包装返回结果，成功时返回Unit，失败时返回异常信息
     * 
     * @param bloodPressureData 要删除的血压数据对象
     * @return Result<Unit> 删除结果，成功时返回Unit，失败时包含异常信息
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
     * 根据ID删除血压记录
     * 
     * 根据记录ID删除对应的数据库记录
     * 使用Result包装返回结果，成功时返回Unit，失败时返回异常信息
     * 
     * @param recordId 要删除的记录ID
     * @return Result<Unit> 删除结果，成功时返回Unit，失败时包含异常信息
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
     * 批量删除血压记录
     * 
     * 根据血压数据对象列表批量删除对应的数据库记录
     * 使用Result包装返回结果，成功时返回Unit，失败时返回异常信息
     * 
     * @param records 要删除的血压数据对象列表
     * @return Result<Unit> 删除结果，成功时返回Unit，失败时包含异常信息
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
     * 根据ID列表批量删除血压记录
     * 
     * 根据记录ID列表批量删除对应的数据库记录
     * 使用Result包装返回结果，成功时返回Unit，失败时返回异常信息
     * 
     * @param recordIds 要删除的记录ID列表
     * @return Result<Unit> 删除结果，成功时返回Unit，失败时包含异常信息
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