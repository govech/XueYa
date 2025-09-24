package com.example.xueya.presentation.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xueya.domain.model.BloodPressureData
import com.example.xueya.domain.usecase.AddBloodPressureUseCase
import com.example.xueya.domain.usecase.GetBloodPressureListUseCase
import com.example.xueya.domain.usecase.DeleteBloodPressureUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * 测试ViewModel
 * 用于验证业务逻辑是否正常工作
 */
@HiltViewModel
class TestViewModel @Inject constructor(
    private val addBloodPressureUseCase: AddBloodPressureUseCase,
    private val getBloodPressureListUseCase: GetBloodPressureListUseCase,
    private val deleteBloodPressureUseCase: DeleteBloodPressureUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TestUiState())
    val uiState: StateFlow<TestUiState> = _uiState.asStateFlow()

    init {
        loadBloodPressureRecords()
    }

    /**
     * 加载血压记录
     */
    private fun loadBloodPressureRecords() {
        viewModelScope.launch {
            getBloodPressureListUseCase.getAllRecords()
                .catch { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "加载数据失败: ${error.message}"
                    )
                }
                .collect { records ->
                    _uiState.value = _uiState.value.copy(
                        bloodPressureList = records,
                        isLoading = false,
                        errorMessage = null
                    )
                }
        }
    }

    /**
     * 添加测试数据
     */
    fun addTestData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val testData = BloodPressureData(
                systolic = 120,
                diastolic = 80,
                heartRate = 72,
                measureTime = LocalDateTime.now(),
                note = "测试数据",
                tags = listOf("测试")
            )

            val result = addBloodPressureUseCase(testData)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    successMessage = "添加数据成功",
                    isLoading = false
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "添加数据失败: ${result.exceptionOrNull()?.message}",
                    isLoading = false
                )
            }
        }
    }

    /**
     * 添加高血压测试数据
     */
    fun addHighBloodPressureData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val testData = BloodPressureData(
                systolic = 150,
                diastolic = 95,
                heartRate = 85,
                measureTime = LocalDateTime.now(),
                note = "高血压测试数据",
                tags = listOf("高血压", "测试")
            )

            val result = addBloodPressureUseCase(testData)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    successMessage = "添加高血压数据成功",
                    isLoading = false
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "添加数据失败: ${result.exceptionOrNull()?.message}",
                    isLoading = false
                )
            }
        }
    }

    /**
     * 删除记录
     */
    fun deleteRecord(record: BloodPressureData) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val result = deleteBloodPressureUseCase(record)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    successMessage = "删除记录成功",
                    isLoading = false
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "删除记录失败: ${result.exceptionOrNull()?.message}",
                    isLoading = false
                )
            }
        }
    }

    /**
     * 清除消息
     */
    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            successMessage = null,
            errorMessage = null
        )
    }

    /**
     * 测试数据验证
     */
    fun testDataValidation() {
        viewModelScope.launch {
            // 测试无效数据
            val invalidData = BloodPressureData(
                systolic = 80, // 错误：收缩压小于舒张压
                diastolic = 120,
                heartRate = 72,
                measureTime = LocalDateTime.now(),
                note = "无效数据测试"
            )

            val result = addBloodPressureUseCase(invalidData)
            if (result.isFailure) {
                _uiState.value = _uiState.value.copy(
                    successMessage = "数据验证正常工作: ${result.exceptionOrNull()?.message}"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "数据验证失败：应该拒绝无效数据"
                )
            }
        }
    }
}

/**
 * 测试UI状态
 */
data class TestUiState(
    val bloodPressureList: List<BloodPressureData> = emptyList(),
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)