package com.example.xueya.presentation.screens.diet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xueya.domain.model.DietPlan
import com.example.xueya.domain.model.DietPlans
import com.example.xueya.domain.usecase.GetBloodPressureStatisticsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 饮食建议ViewModel
 * 管理饮食方案数据、AI推荐、收藏状态等
 */
@HiltViewModel
class DietViewModel @Inject constructor(
    private val getBloodPressureStatisticsUseCase: GetBloodPressureStatisticsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DietUiState())
    val uiState: StateFlow<DietUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            loadDietPlans()
            generateAIRecommendations()
        }
    }

    /**
     * 加载饮食方案数据
     */
    private suspend fun loadDietPlans() {
        _uiState.update { currentState ->
            currentState.copy(
                mainstreamPlans = DietPlans.getMainstreamDietPlans()
            )
        }
    }

    /**
     * 生成AI个性化饮食推荐
     */
    private fun generateAIRecommendations() {
        viewModelScope.launch {
            try {
                // 获取用户血压统计数据 - 获取所有时间的数据
                val now = java.time.LocalDateTime.now()
                val startTime = now.minusYears(1) // 获取过去一年的数据
                val statisticsResult = getBloodPressureStatisticsUseCase.getStatistics(startTime, now)
                
                val statistics = if (statisticsResult.isSuccess) {
                    statisticsResult.getOrNull()
                } else {
                    null
                }

                val aiRecommendations = mutableListOf<DietPlan>()
                val mainstreamPlans = DietPlans.getMainstreamDietPlans()

                // 基于血压水平进行AI推荐
                val avgSystolic = statistics?.averageSystolic ?: 120.0
                val avgDiastolic = statistics?.averageDiastolic ?: 80.0

                when {
                    // 高血压危象 - 强烈建议DASH饮食和低盐饮食
                    avgSystolic > 180 || avgDiastolic > 120 -> {
                        val dashPlan = mainstreamPlans.find { it.id == "dash" }?.copy(
                            isRecommended = true,
                            recommendationReason = "您的血压处于危险水平，DASH饮食是专门设计用于降低血压的饮食方案",
                            recommendationReasonEn = "Your blood pressure is at a dangerous level. DASH diet is specifically designed to lower blood pressure"
                        )
                        val lowSodiumPlan = mainstreamPlans.find { it.id == "low_sodium" }?.copy(
                            isRecommended = true,
                            recommendationReason = "严格控制钠摄入对您的血压控制至关重要",
                            recommendationReasonEn = "Strict sodium control is crucial for your blood pressure management"
                        )
                        dashPlan?.let { aiRecommendations.add(it) }
                        lowSodiumPlan?.let { aiRecommendations.add(it) }
                    }

                    // 高血压2期 - 推荐DASH饮食和地中海饮食
                    avgSystolic >= 140 || avgDiastolic >= 90 -> {
                        val dashPlan = mainstreamPlans.find { it.id == "dash" }?.copy(
                            isRecommended = true,
                            recommendationReason = "DASH饮食被证实能有效降低高血压，特别适合您当前的血压状况",
                            recommendationReasonEn = "DASH diet has been proven to effectively reduce hypertension, especially suitable for your current blood pressure condition"
                        )
                        val mediterraneanPlan = mainstreamPlans.find { it.id == "mediterranean" }?.copy(
                            isRecommended = true,
                            recommendationReason = "地中海饮食有助于心血管健康，可以辅助控制血压",
                            recommendationReasonEn = "Mediterranean diet helps cardiovascular health and can assist in blood pressure control"
                        )
                        dashPlan?.let { aiRecommendations.add(it) }
                        mediterraneanPlan?.let { aiRecommendations.add(it) }
                    }

                    // 高血压1期 - 推荐地中海饮食和植物性饮食
                    avgSystolic >= 130 || avgDiastolic >= 80 -> {
                        val mediterraneanPlan = mainstreamPlans.find { it.id == "mediterranean" }?.copy(
                            isRecommended = true,
                            recommendationReason = "地中海饮食富含健康脂肪和抗氧化剂，有助于维持正常血压",
                            recommendationReasonEn = "Mediterranean diet is rich in healthy fats and antioxidants, helping maintain normal blood pressure"
                        )
                        val plantBasedPlan = mainstreamPlans.find { it.id == "plant_based" }?.copy(
                            isRecommended = true,
                            recommendationReason = "植物性饮食富含纤维和钾，有助于血压控制",
                            recommendationReasonEn = "Plant-based diet is rich in fiber and potassium, helping with blood pressure control"
                        )
                        mediterraneanPlan?.let { aiRecommendations.add(it) }
                        plantBasedPlan?.let { aiRecommendations.add(it) }
                    }

                    // 血压偏高 - 推荐植物性饮食
                    avgSystolic >= 120 -> {
                        val plantBasedPlan = mainstreamPlans.find { it.id == "plant_based" }?.copy(
                            isRecommended = true,
                            recommendationReason = "植物性饮食有助于预防血压升高，是很好的预防性饮食选择",
                            recommendationReasonEn = "Plant-based diet helps prevent blood pressure elevation and is a good preventive dietary choice"
                        )
                        plantBasedPlan?.let { aiRecommendations.add(it) }
                    }

                    // 正常血压 - 推荐地中海饮食
                    else -> {
                        val mediterraneanPlan = mainstreamPlans.find { it.id == "mediterranean" }?.copy(
                            isRecommended = true,
                            recommendationReason = "您的血压正常，地中海饮食有助于维持心血管健康",
                            recommendationReasonEn = "Your blood pressure is normal, Mediterranean diet helps maintain cardiovascular health"
                        )
                        mediterraneanPlan?.let { aiRecommendations.add(it) }
                    }
                }

                _uiState.update { currentState ->
                    currentState.copy(
                        aiRecommendedPlans = aiRecommendations,
                        isLoading = false
                    )
                }

            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        error = "Failed to generate AI recommendations: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * 切换收藏状态
     */
    fun toggleFavorite(planId: String) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                val updatedMainstreamPlans = currentState.mainstreamPlans.map { plan ->
                    if (plan.id == planId) {
                        plan.copy(isFavorite = !plan.isFavorite)
                    } else {
                        plan
                    }
                }

                val updatedAIPlans = currentState.aiRecommendedPlans.map { plan ->
                    if (plan.id == planId) {
                        plan.copy(isFavorite = !plan.isFavorite)
                    } else {
                        plan
                    }
                }

                currentState.copy(
                    mainstreamPlans = updatedMainstreamPlans,
                    aiRecommendedPlans = updatedAIPlans
                )
            }
        }
    }

    /**
     * 清除错误信息
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

/**
 * 饮食建议UI状态
 */
data class DietUiState(
    val mainstreamPlans: List<DietPlan> = emptyList(),
    val aiRecommendedPlans: List<DietPlan> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val selectedTab: Int = 0 // 0: 主流方案, 1: AI推荐
)