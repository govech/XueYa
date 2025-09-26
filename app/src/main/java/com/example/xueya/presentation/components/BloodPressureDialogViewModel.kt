package com.example.xueya.presentation.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xueya.domain.model.BloodPressureData
import com.example.xueya.domain.usecase.AddBloodPressureUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * 血压记录弹框ViewModel
 * 负责管理血压记录弹框的状态和数据保存
 */
@HiltViewModel
class BloodPressureDialogViewModel @Inject constructor(
    private val addBloodPressureUseCase: AddBloodPressureUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BloodPressureDialogUiState())
    val uiState: StateFlow<BloodPressureDialogUiState> = _uiState.asStateFlow()

    /**
     * 显示弹框
     */
    fun showDialog() {
        _uiState.value = _uiState.value.copy(isVisible = true)
    }

    /**
     * 隐藏弹框
     */
    fun hideDialog() {
        _uiState.value = _uiState.value.copy(isVisible = false)
    }

    /**
     * 保存血压记录
     */
    fun saveBloodPressureRecord(bloodPressureData: BloodPressureData) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                addBloodPressureUseCase.invoke(bloodPressureData)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isVisible = false,
                    saveSuccess = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "保存失败，请重试"
                )
            }
        }
    }

    /**
     * 重置保存状态
     */
    fun resetSaveState() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }

    /**
     * 清除错误信息
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

/**
 * 血压记录弹框UI状态
 */
data class BloodPressureDialogUiState(
    val isVisible: Boolean = false,
    val isLoading: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
)