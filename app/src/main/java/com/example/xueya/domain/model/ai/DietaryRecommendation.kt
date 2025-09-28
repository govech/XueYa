package com.example.xueya.domain.model.ai

/**
 * AI饮食推荐响应
 */
data class DietaryRecommendation(
    val recommendedDiets: List<RecommendedDiet>,
    val personalizedAdvice: String,
    val rationale: String,
    val caution: String
)

/**
 * 推荐的饮食方案
 */
data class RecommendedDiet(
    val name: String,
    val nameEn: String,
    val reason: String,
    val reasonEn: String,
    val priority: Int, // 推荐优先级，1-10
    val suitability: Double // 适合度，0.0-1.0
)