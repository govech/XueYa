package com.example.xueya.domain.usecase

import com.example.xueya.domain.repository.BloodPressureRepository
import javax.inject.Inject

/**
 * 清空所有血压数据用例
 */
class ClearAllDataUseCase @Inject constructor(
    private val repository: BloodPressureRepository
) {
    suspend operator fun invoke(): ClearDataResult {
        return try {
            repository.clearAllRecords()
            ClearDataResult.Success
        } catch (e: Exception) {
            ClearDataResult.Error(e.message ?: "Failed to clear data")
        }
    }
}

/**
 * 清空数据结果密封类
 */
sealed class ClearDataResult {
    object Success : ClearDataResult()
    data class Error(val message: String) : ClearDataResult()
}