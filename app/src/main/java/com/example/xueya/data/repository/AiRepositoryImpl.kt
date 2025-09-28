package com.example.xueya.data.repository

import com.example.xueya.data.network.AiApiService
import com.example.xueya.domain.model.BloodPressureData
import com.example.xueya.domain.model.ai.AiRequest
import com.example.xueya.domain.model.ai.BloodPressureParseResult
import com.example.xueya.domain.model.ai.BloodPressureTrendAnalysis
import com.example.xueya.domain.model.ai.DietaryRecommendation
import com.example.xueya.domain.model.ai.HealthAdvice
import com.example.xueya.domain.model.ai.Message
import com.example.xueya.domain.model.ai.RecommendedDiet
import com.example.xueya.domain.repository.AiRepository
import com.example.xueya.utils.Constants
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AI 服务仓储实现
 */
@Singleton
class AiRepositoryImpl @Inject constructor(
    private val aiApiService: AiApiService,
    private val gson: Gson = Gson()
) : AiRepository {

    private fun createAuthHeader(): String = "Bearer ${Constants.AI.OPENROUTER_API_KEY}"

    override suspend fun parseBloodPressureFromText(text: String): Result<BloodPressureParseResult> {
        return withContext(Dispatchers.IO) {
            try {
                val request = AiRequest(
                    model = Constants.AI.DEFAULT_AI_MODEL,
                    messages = listOf(
                        Message(
                            role = "user",
                            content = "${Constants.AI.BLOOD_PRESSURE_PARSE_PROMPT}$text"
                        )
                    ),
                    temperature = Constants.AI.TEMPERATURE,
                    max_tokens = Constants.AI.MAX_TOKENS
                )

                val response = aiApiService.chatCompletion(
                    authorization = createAuthHeader(),
                    request = request
                )

                if (response.isSuccessful) {
                    val aiResponse = response.body()
                    val content = aiResponse?.choices?.firstOrNull()?.message?.content
                    
                    if (content != null) {
                        try {
                            val parseResult = gson.fromJson(content, BloodPressureParseResult::class.java)
                            Result.success(parseResult)
                        } catch (e: JsonSyntaxException) {
                            Result.failure(Exception("AI 响应格式解析失败: ${e.message}"))
                        }
                    } else {
                        Result.failure(Exception("AI 响应内容为空"))
                    }
                } else {
                    Result.failure(Exception("API 调用失败: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("网络请求失败: ${e.message}"))
            }
        }
    }

    override suspend fun generateHealthAdvice(
        bloodPressureData: List<BloodPressureData>
    ): Result<HealthAdvice> {
        return withContext(Dispatchers.IO) {
            try {
                // 构建血压数据摘要
                val dataSummary = bloodPressureData.takeLast(10).joinToString("\n") { data ->
                    "日期: ${data.measureTime}, 血压: ${data.systolic}/${data.diastolic}, 心率: ${data.heartRate ?: "未记录"}"
                }

                val request = AiRequest(
                    model = Constants.AI.DEFAULT_AI_MODEL,
                    messages = listOf(
                        Message(
                            role = "user",
                            content = "${Constants.AI.HEALTH_ADVICE_PROMPT}$dataSummary"
                        )
                    ),
                    temperature = Constants.AI.TEMPERATURE,
                    max_tokens = Constants.AI.MAX_TOKENS
                )

                val response = aiApiService.chatCompletion(
                    authorization = createAuthHeader(),
                    request = request
                )

                if (response.isSuccessful) {
                    val aiResponse = response.body()
                    val content = aiResponse?.choices?.firstOrNull()?.message?.content
                    
                    if (content != null) {
                        try {
                            val healthAdvice = gson.fromJson(content, HealthAdvice::class.java)
                            Result.success(healthAdvice)
                        } catch (e: JsonSyntaxException) {
                            Result.failure(Exception("AI 响应格式解析失败: ${e.message}"))
                        }
                    } else {
                        Result.failure(Exception("AI 响应内容为空"))
                    }
                } else {
                    Result.failure(Exception("API 调用失败: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("网络请求失败: ${e.message}"))
            }
        }
    }

    override suspend fun analyzeBloodPressureTrend(
        bloodPressureData: List<BloodPressureData>
    ): Result<BloodPressureTrendAnalysis> {
        return withContext(Dispatchers.IO) {
            try {
                // 构建趋势分析数据
                val trendData = bloodPressureData.takeLast(30).joinToString("\n") { data ->
                    "${data.measureTime}: ${data.systolic}/${data.diastolic}"
                }

                val request = AiRequest(
                    model = Constants.AI.DEFAULT_AI_MODEL,
                    messages = listOf(
                        Message(
                            role = "user",
                            content = "${Constants.AI.TREND_ANALYSIS_PROMPT}$trendData"
                        )
                    ),
                    temperature = Constants.AI.TEMPERATURE,
                    max_tokens = Constants.AI.MAX_TOKENS
                )

                val response = aiApiService.chatCompletion(
                    authorization = createAuthHeader(),
                    request = request
                )

                if (response.isSuccessful) {
                    val aiResponse = response.body()
                    val content = aiResponse?.choices?.firstOrNull()?.message?.content
                    
                    if (content != null) {
                        try {
                            val trendAnalysis = gson.fromJson(content, BloodPressureTrendAnalysis::class.java)
                            Result.success(trendAnalysis)
                        } catch (e: JsonSyntaxException) {
                            Result.failure(Exception("AI 响应格式解析失败: ${e.message}"))
                        }
                    } else {
                        Result.failure(Exception("AI 响应内容为空"))
                    }
                } else {
                    Result.failure(Exception("API 调用失败: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("网络请求失败: ${e.message}"))
            }
        }
    }

    override suspend fun generateDietaryRecommendations(
        bloodPressureData: List<BloodPressureData>,
        userInfo: Map<String, String>
    ): Result<DietaryRecommendation> {
        return withContext(Dispatchers.IO) {
            try {
                // 构建血压数据描述
                val bloodPressureDescription = buildString {
                    if (bloodPressureData.isNotEmpty()) {
                        val avgSystolic = bloodPressureData.map { it.systolic }.average().toInt()
                        val avgDiastolic = bloodPressureData.map { it.diastolic }.average().toInt()
                        val avgPulse = bloodPressureData.map { it.heartRate }.average().toInt()

                        append("平均血压: ${avgSystolic}/${avgDiastolic} mmHg, 平均心率: ${avgPulse} bpm\n")
                        append("数据点数量: ${bloodPressureData.size} 个\n")

                        // 添加最近的血压数据
                        val recentData = bloodPressureData.takeLast(5)
                        append("最近5次测量数据:\n")
                        recentData.forEach { data ->
                            append("- ${data.systolic}/${data.diastolic} mmHg, 心率: ${data.heartRate} bpm\n")
                        }
                    }
                }

                val request = AiRequest(
                    model = Constants.AI.DEFAULT_AI_MODEL,
                    messages = listOf(
                        Message(
                            role = "user",
                            content = "${Constants.AI.DIETARY_RECOMMENDATION_PROMPT}${bloodPressureDescription}"
                        )
                    ),
                    temperature = Constants.AI.TEMPERATURE,
                    max_tokens = Constants.AI.MAX_TOKENS
                )

                val response = aiApiService.chatCompletion(
                    authorization = createAuthHeader(),
                    request = request
                )

                if (response.isSuccessful) {
                    val aiResponse = response.body()
                    val content = aiResponse?.choices?.firstOrNull()?.message?.content

                    if (content != null) {
                        try {
                            val dietaryRecommendation = gson.fromJson(content, DietaryRecommendation::class.java)
                            Result.success(dietaryRecommendation)
                        } catch (e: JsonSyntaxException) {
                            Result.failure(Exception("AI 响应格式解析失败: ${e.message}"))
                        }
                    } else {
                        Result.failure(Exception("AI 响应内容为空"))
                    }
                } else {
                    Result.failure(Exception("API 调用失败: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("网络请求失败: ${e.message}"))
            }
        }
    }
}