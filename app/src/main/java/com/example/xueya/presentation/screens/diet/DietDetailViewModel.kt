package com.example.xueya.presentation.screens.diet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xueya.domain.model.DietPlan
import com.example.xueya.domain.model.DietPlans
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 饮食方案详情ViewModel
 * 管理详情页面的数据状态和交互
 */
@HiltViewModel
class DietDetailViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(DietDetailUiState())
    val uiState: StateFlow<DietDetailUiState> = _uiState.asStateFlow()

    /**
     * 加载饮食方案详情
     */
    fun loadDietPlan(planId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val plan = DietPlans.getDietPlanById(planId)
                if (plan != null) {
                    _uiState.update { 
                        it.copy(
                            dietPlan = plan,
                            isLoading = false,
                            error = null
                        ) 
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "Diet plan not found"
                        ) 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Failed to load diet plan: ${e.message}"
                    ) 
                }
            }
        }
    }

    /**
     * 切换收藏状态
     */
    fun toggleFavorite() {
        val currentPlan = _uiState.value.dietPlan
        if (currentPlan != null) {
            _uiState.update { 
                it.copy(
                    dietPlan = currentPlan.copy(isFavorite = !currentPlan.isFavorite)
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
 * 饮食方案详情UI状态
 */
data class DietDetailUiState(
    val dietPlan: DietPlan? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
