package com.example.xueya.domain.repository

import com.example.xueya.domain.model.BloodPressureData
import com.example.xueya.domain.model.ai.BloodPressureParseResult
import com.example.xueya.domain.model.ai.BloodPressureTrendAnalysis
import com.example.xueya.domain.model.ai.HealthAdvice

/**
 * AI 服务仓储接口
 * 定义 AI 相关的数据操作
 */
interface AiRepository {
    
    /**
     * 从文本中解析血压数据
     * @param text 用户输入的文本
     * @return 解析结果
     */
    suspend fun parseBloodPressureFromText(text: String): Result<BloodPressureParseResult>
    
    /**
     * 生成健康建议
     * @param bloodPressureData 血压数据列表
     * @return 健康建议
     */
    suspend fun generateHealthAdvice(
        bloodPressureData: List<BloodPressureData>
    ): Result<HealthAdvice>
    
    /**
     * 分析血压趋势
     * @param bloodPressureData 血压数据列表
     * @return 趋势分析结果
     */
    suspend fun analyzeBloodPressureTrend(
        bloodPressureData: List<BloodPressureData>
    ): Result<BloodPressureTrendAnalysis>
}