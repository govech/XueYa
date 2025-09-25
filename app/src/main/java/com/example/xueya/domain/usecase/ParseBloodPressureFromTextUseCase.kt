package com.example.xueya.domain.usecase

import com.example.xueya.domain.model.ai.BloodPressureParseResult
import com.example.xueya.domain.repository.AiRepository
import javax.inject.Inject

/**
 * 从文本解析血压数据用例
 */
class ParseBloodPressureFromTextUseCase @Inject constructor(
    private val aiRepository: AiRepository
) {
    suspend operator fun invoke(text: String): Result<BloodPressureParseResult> {
        return aiRepository.parseBloodPressureFromText(text)
    }
}