package com.example.xueya.domain.usecase

import com.example.xueya.domain.model.BloodPressureData
import com.example.xueya.domain.model.ai.HealthAdvice
import com.example.xueya.domain.repository.AiRepository
import javax.inject.Inject

/**
 * 生成健康建议用例
 */
class GenerateHealthAdviceUseCase @Inject constructor(
    private val aiRepository: AiRepository
) {
    suspend operator fun invoke(bloodPressureData: List<BloodPressureData>): Result<HealthAdvice> {
        return aiRepository.generateHealthAdvice(bloodPressureData)
    }
}