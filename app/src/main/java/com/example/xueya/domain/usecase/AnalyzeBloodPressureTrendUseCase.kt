package com.example.xueya.domain.usecase

import com.example.xueya.domain.model.BloodPressureData
import com.example.xueya.domain.model.ai.BloodPressureTrendAnalysis
import com.example.xueya.domain.repository.AiRepository
import javax.inject.Inject

/**
 * 分析血压趋势用例
 * 
 * 处理血压趋势分析的业务逻辑，调用AI仓库进行智能分析
 * 作为业务逻辑层与AI服务的桥梁，负责协调数据传递和结果处理
 * 使用@Inject注解支持Hilt依赖注入
 * 
 * @param aiRepository AI数据仓库，通过构造函数注入
 */
class AnalyzeBloodPressureTrendUseCase @Inject constructor(
    private val aiRepository: AiRepository
) {
    /**
     * 执行血压趋势分析
     * 
     * 将血压数据列表传递给AI仓库进行趋势分析
     * 使用Result包装返回结果，成功时返回分析结果，失败时返回异常信息
     * 
     * @param bloodPressureData 血压数据列表
     * @return Result<BloodPressureTrendAnalysis> 分析结果，成功时包含趋势分析，失败时包含异常信息
     */
    suspend operator fun invoke(bloodPressureData: List<BloodPressureData>): Result<BloodPressureTrendAnalysis> {
        return aiRepository.analyzeBloodPressureTrend(bloodPressureData)
    }
}