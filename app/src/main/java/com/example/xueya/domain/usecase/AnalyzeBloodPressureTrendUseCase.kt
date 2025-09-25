package com.example.xueya.domain.usecase

import com.example.xueya.domain.model.BloodPressureData
import com.example.xueya.domain.model.ai.BloodPressureTrendAnalysis
import com.example.xueya.domain.repository.AiRepository
import javax.inject.Inject

/**
 * 分析血压趋势用例
 */
class AnalyzeBloodPressureTrendUseCase @Inject constructor(
    private val aiRepository: AiRepository
) {
    suspend operator fun invoke(bloodPressureData: List<BloodPressureData>): Result<BloodPressureTrendAnalysis> {
        return aiRepository.analyzeBloodPressureTrend(bloodPressureData)
    }
}