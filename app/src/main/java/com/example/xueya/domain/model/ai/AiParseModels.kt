package com.example.xueya.domain.model.ai

/**
 * 血压解析结果数据模型
 */
data class BloodPressureParseResult(
    val systolic: Int?,
    val diastolic: Int?,
    val pulse: Int?,
    val notes: String?,
    val confidence: Float,
    val isValid: Boolean
)

/**
 * 健康建议数据模型
 */
data class HealthAdvice(
    val advice: String,
    val category: String, // "normal", "warning", "danger"
    val recommendations: List<String>
)

/**
 * AI 解析状态
 */
sealed class AiParseState {
    object Idle : AiParseState()
    object Loading : AiParseState()
    data class Success<T>(val data: T) : AiParseState()
    data class Error(val message: String) : AiParseState()
}

/**
 * 血压趋势分析结果
 */
data class BloodPressureTrendAnalysis(
    val trend: String, // "improving", "stable", "worsening"
    val summary: String,
    val insights: List<String>,
    val recommendations: List<String>
)