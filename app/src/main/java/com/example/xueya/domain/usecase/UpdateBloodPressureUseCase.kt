package com.example.xueya.domain.usecase

import com.example.xueya.domain.model.BloodPressureData
import com.example.xueya.domain.repository.BloodPressureRepository
import javax.inject.Inject

/**
 * 更新血压记录用例
 * 
 * 处理更新血压记录的业务逻辑，包括数据验证和更新操作
 * 作为业务逻辑层的核心组件，负责协调数据验证和数据更新操作
 * 使用@Inject注解支持Hilt依赖注入
 * 
 * @param repository 血压数据仓库，通过构造函数注入
 */
class UpdateBloodPressureUseCase @Inject constructor(
    private val repository: BloodPressureRepository
) {
    /**
     * 执行更新血压记录的业务逻辑
     * 
     * 首先验证血压数据的有效性，然后检查记录是否存在，最后更新数据
     * 使用Result包装返回结果，成功时返回Unit，失败时返回异常信息
     * 
     * @param bloodPressureData 要更新的血压数据对象
     * @return Result<Unit> 更新结果，成功时返回Unit，失败时包含异常信息
     */
    suspend operator fun invoke(bloodPressureData: BloodPressureData): Result<Unit> {
        return try {
            // 数据验证
            validateBloodPressureData(bloodPressureData)
            
            // 检查记录是否存在
            val existingRecord = repository.getRecordById(bloodPressureData.id)
            if (existingRecord == null) {
                return Result.failure(IllegalArgumentException("记录不存在"))
            }
            
            // 更新记录
            repository.updateRecord(bloodPressureData)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 验证血压数据的有效性
     * 
     * 根据医学标准和实际使用场景，对血压数据进行多维度验证
     * 包括ID有效性、数值范围验证、逻辑关系验证等
     * 
     * @param data 要验证的血压数据对象
     * @throws IllegalArgumentException 当数据不符合验证规则时抛出异常
     */
    private fun validateBloodPressureData(data: BloodPressureData) {
        require(data.id > 0) { "记录ID必须大于0" }
        require(data.systolic > 0) { "收缩压必须大于0" }
        require(data.diastolic > 0) { "舒张压必须大于0" }
        require(data.systolic > data.diastolic) { "收缩压必须大于舒张压" }
        require(data.heartRate in 30..250) { "心率必须在30-250之间" }
        require(data.systolic <= 300) { "收缩压不能超过300" }
        require(data.diastolic <= 200) { "舒张压不能超过200" }
    }
}