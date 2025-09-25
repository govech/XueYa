package com.example.xueya.domain.usecase

import com.example.xueya.domain.repository.BloodPressureRepository
import javax.inject.Inject

/**
 * 清空所有血压数据用例
 * 
 * 处理清空所有血压数据的业务逻辑，这是一个危险操作需要谨慎使用
 * 作为业务逻辑层的核心组件，负责协调数据清空操作
 * 使用@Inject注解支持Hilt依赖注入
 * 
 * @param repository 血压数据仓库，通过构造函数注入
 */
class ClearAllDataUseCase @Inject constructor(
    private val repository: BloodPressureRepository
) {
    /**
     * 执行清空所有血压数据的业务逻辑
     * 
     * 调用仓库的清空所有记录方法，删除数据库中的所有血压记录
     * 使用ClearDataResult密封类包装返回结果，区分成功和失败情况
     * 
     * @return ClearDataResult 清空结果，成功时返回Success，失败时返回Error
     */
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
 * 
 * 用于封装清空数据操作的结果状态
 * Success表示操作成功，Error表示操作失败并包含错误信息
 */
sealed class ClearDataResult {
    /**
     * 操作成功
     */
    object Success : ClearDataResult()
    
    /**
     * 操作失败
     * 
     * @property message 错误信息
     */
    data class Error(val message: String) : ClearDataResult()
}