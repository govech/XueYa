package com.example.xueya.domain.usecase

import com.example.xueya.domain.generator.BloodPressureTestDataGenerator
import com.example.xueya.domain.model.BloodPressureData
import com.example.xueya.domain.repository.BloodPressureRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 生成测试数据用例
 * 
 * 负责生成血压测试数据并保存到数据库
 * 支持不同的时间范围和生成模式
 */
@Singleton
class GenerateTestDataUseCase @Inject constructor(
    private val bloodPressureRepository: BloodPressureRepository,
    private val testDataGenerator: BloodPressureTestDataGenerator
) {
    
    /**
     * 生成测试数据结果
     */
    sealed class GenerateResult {
        object Success : GenerateResult()
        data class Error(val message: String) : GenerateResult()
    }
    
    /**
     * 生成并保存测试数据
     * 
     * @param timeRange 时间范围
     * @param mode 生成模式
     * @param measurementsPerDay 每天测量次数
     * @return 生成结果
     */
    suspend fun generateAndSaveTestData(
        timeRange: BloodPressureTestDataGenerator.TimeRange,
        mode: BloodPressureTestDataGenerator.GenerationMode = BloodPressureTestDataGenerator.GenerationMode.MIXED,
        measurementsPerDay: Int = 2
    ): GenerateResult = withContext(Dispatchers.IO) {
        try {
            // 生成测试数据
            val testData = testDataGenerator.generateTestData(
                timeRange = timeRange,
                mode = mode,
                measurementsPerDay = measurementsPerDay
            )
            
            // 批量保存到数据库
            testData.forEach { data ->
                bloodPressureRepository.addRecord(data)
            }
            
            GenerateResult.Success
        } catch (e: Exception) {
            GenerateResult.Error("生成测试数据失败: ${e.message}")
        }
    }
    
    /**
     * 生成测试数据（不保存）
     * 
     * @param timeRange 时间范围
     * @param mode 生成模式
     * @param measurementsPerDay 每天测量次数
     * @return 生成的测试数据列表
     */
    suspend fun generateTestData(
        timeRange: BloodPressureTestDataGenerator.TimeRange,
        mode: BloodPressureTestDataGenerator.GenerationMode = BloodPressureTestDataGenerator.GenerationMode.MIXED,
        measurementsPerDay: Int = 2
    ): List<BloodPressureData> = withContext(Dispatchers.IO) {
        testDataGenerator.generateTestData(
            timeRange = timeRange,
            mode = mode,
            measurementsPerDay = measurementsPerDay
        )
    }
    
    /**
     * 清空所有现有数据并生成新的测试数据
     * 
     * @param timeRange 时间范围
     * @param mode 生成模式
     * @param measurementsPerDay 每天测量次数
     * @return 生成结果
     */
    suspend fun clearAndGenerateTestData(
        timeRange: BloodPressureTestDataGenerator.TimeRange,
        mode: BloodPressureTestDataGenerator.GenerationMode = BloodPressureTestDataGenerator.GenerationMode.MIXED,
        measurementsPerDay: Int = 2
    ): GenerateResult = withContext(Dispatchers.IO) {
        try {
            // 清空现有数据
            bloodPressureRepository.clearAllRecords()
            
            // 生成并保存新数据
            generateAndSaveTestData(timeRange, mode, measurementsPerDay)
        } catch (e: Exception) {
            GenerateResult.Error("清空并生成测试数据失败: ${e.message}")
        }
    }
    
    /**
     * 获取生成统计信息
     * 
     * @param timeRange 时间范围
     * @param measurementsPerDay 每天测量次数
     * @return 统计信息
     */
    fun getGenerationStats(
        timeRange: BloodPressureTestDataGenerator.TimeRange,
        measurementsPerDay: Int = 2
    ): GenerationStats {
        val days = when (timeRange) {
            BloodPressureTestDataGenerator.TimeRange.ONE_WEEK -> 7
            BloodPressureTestDataGenerator.TimeRange.ONE_MONTH -> 30
            BloodPressureTestDataGenerator.TimeRange.SIX_MONTHS -> 180
        }
        
        val totalRecords = days * measurementsPerDay
        
        return GenerationStats(
            timeRange = timeRange,
            days = days,
            measurementsPerDay = measurementsPerDay,
            totalRecords = totalRecords,
            estimatedSize = totalRecords * 100 // 估算每条记录约100字节
        )
    }
    
    /**
     * 生成统计信息数据类
     */
    data class GenerationStats(
        val timeRange: BloodPressureTestDataGenerator.TimeRange,
        val days: Int,
        val measurementsPerDay: Int,
        val totalRecords: Int,
        val estimatedSize: Int // 字节
    ) {
        val estimatedSizeKB: Int get() = estimatedSize / 1024
        val estimatedSizeMB: Double get() = estimatedSize / (1024.0 * 1024.0)
    }
}
