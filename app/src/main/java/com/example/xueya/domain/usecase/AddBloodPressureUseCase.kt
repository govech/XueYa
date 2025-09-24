package com.example.xueya.domain.usecase

import com.example.xueya.domain.model.BloodPressureData
import com.example.xueya.domain.repository.BloodPressureRepository
import javax.inject.Inject

/**
 * 添加血压记录用例
 * 处理新增血压记录的业务逻辑
 */
class AddBloodPressureUseCase @Inject constructor(
    private val repository: BloodPressureRepository
) {
    /**
     * 执行添加血压记录
     * 
     * @param bloodPressureData 血压数据
     * @return 添加成功返回记录ID，失败返回null
     */
    suspend operator fun invoke(bloodPressureData: BloodPressureData): Result<Long> {
        return try {
            // 数据验证
            validateBloodPressureData(bloodPressureData)
            
            // 添加记录
            val recordId = repository.addRecord(bloodPressureData)
            Result.success(recordId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 验证血压数据
     */
    private fun validateBloodPressureData(data: BloodPressureData) {
        require(data.systolic > 0) { "收缩压必须大于0" }
        require(data.diastolic > 0) { "舒张压必须大于0" }
        require(data.systolic > data.diastolic) { "收缩压必须大于舒张压" }
        require(data.heartRate in 30..250) { "心率必须在30-250之间" }
        require(data.systolic <= 300) { "收缩压不能超过300" }
        require(data.diastolic <= 200) { "舒张压不能超过200" }
    }
}