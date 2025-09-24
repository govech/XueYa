package com.example.xueya.domain.usecase

import com.example.xueya.domain.model.BloodPressureData
import com.example.xueya.domain.repository.BloodPressureRepository
import javax.inject.Inject

/**
 * 更新血压记录用例
 * 处理更新血压记录的业务逻辑
 */
class UpdateBloodPressureUseCase @Inject constructor(
    private val repository: BloodPressureRepository
) {
    /**
     * 更新血压记录
     * 
     * @param bloodPressureData 更新的血压数据
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
     * 验证血压数据
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