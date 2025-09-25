package com.example.xueya.presentation.screens.add_record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xueya.domain.usecase.AddBloodPressureUseCase
import com.example.xueya.domain.usecase.ParseBloodPressureFromTextUseCase
import com.example.xueya.domain.model.BloodPressureData
import com.example.xueya.domain.model.ai.AiParseState
import com.example.xueya.domain.usecase.VoiceInputUseCase
import kotlinx.coroutines.Job
import com.example.xueya.domain.model.ai.BloodPressureParseResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * 添加记录页面ViewModel
 */
@HiltViewModel
class AddRecordViewModel @Inject constructor(
    private val addBloodPressureUseCase: AddBloodPressureUseCase,
    private val parseBloodPressureFromTextUseCase: ParseBloodPressureFromTextUseCase,
    private val voiceInputUseCase: VoiceInputUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddRecordUiState())
    val uiState: StateFlow<AddRecordUiState> = _uiState.asStateFlow()
    
    private val _aiParseState = MutableStateFlow<AiParseState>(AiParseState.Idle)
    val aiParseState: StateFlow<AiParseState> = _aiParseState.asStateFlow()
    
    private val _voiceInputState = MutableStateFlow<VoiceInputUseCase.VoiceInputState>(VoiceInputUseCase.VoiceInputState.Idle)
    val voiceInputState: StateFlow<VoiceInputUseCase.VoiceInputState> = _voiceInputState.asStateFlow()
    
    private var voiceInputJob: Job? = null

    /**
     * AI 文本解析
     */
    fun parseBloodPressureText(text: String) {
        if (text.isBlank()) return
        
        viewModelScope.launch {
            _aiParseState.value = AiParseState.Loading
            
            parseBloodPressureFromTextUseCase(text)
                .onSuccess { result ->
                    _aiParseState.value = AiParseState.Success(result)
                    // 自动填充解析结果
                    applyAiParseResult(result)
                }
                .onFailure { exception ->
                    _aiParseState.value = AiParseState.Error(
                        exception.message ?: "解析失败"
                    )
                }
        }
    }
    
    /**
     * 应用 AI 解析结果
     */
    private fun applyAiParseResult(result: BloodPressureParseResult) {
        if (result.isValid) {
            val currentState = _uiState.value
            _uiState.value = currentState.copy(
                systolic = result.systolic?.toString() ?: currentState.systolic,
                diastolic = result.diastolic?.toString() ?: currentState.diastolic,
                heartRate = result.pulse?.toString() ?: currentState.heartRate,
                note = if (result.notes?.isNotBlank() == true) {
                    if (currentState.note.isBlank()) result.notes else "${currentState.note}\n${result.notes}"
                } else currentState.note,
                error = null
            )
        }
    }
    
    /**
     * 重置 AI 解析状态
     */
    fun resetAiParseState() {
        _aiParseState.value = AiParseState.Idle
    }
    
    /**
     * 开始语音输入
     */
    @androidx.annotation.RequiresPermission(android.Manifest.permission.RECORD_AUDIO)
    fun startVoiceInput() {
        // 取消之前的语音输入任务
        voiceInputJob?.cancel()
        // 点击后立即给出“正在听取”反馈，触发按钮动画
        _voiceInputState.value = VoiceInputUseCase.VoiceInputState.Listening
        
        voiceInputJob = viewModelScope.launch {
            voiceInputUseCase(
                languageCode = "zh-CN",
                prompt = "请说出血压数据，如：高压120，低压80，心率75"
            ).collect { state ->
                _voiceInputState.value = state
                
                // 如果语音输入成功，应用解析结果
                if (state is VoiceInputUseCase.VoiceInputState.Success) {
                    applyAiParseResult(state.parseResult)
                    // 同步AI解析状态
                    _aiParseState.value = AiParseState.Success(state.parseResult)
                }
                
                // 如果语音输入失败，同步错误状态
                if (state is VoiceInputUseCase.VoiceInputState.Error) {
                    _aiParseState.value = AiParseState.Error(state.message)
                }
            }
        }
    }
    
    /**
     * 停止语音输入
     */
    fun stopVoiceInput() {
        voiceInputJob?.cancel()
        _voiceInputState.value = VoiceInputUseCase.VoiceInputState.Idle
        _aiParseState.value = AiParseState.Idle
    }

    /**
     * 更新收缩压
     */
    fun updateSystolic(value: String) {
        _uiState.value = _uiState.value.copy(
            systolic = value,
            error = null
        )
    }

    /**
     * 更新舒张压
     */
    fun updateDiastolic(value: String) {
        _uiState.value = _uiState.value.copy(
            diastolic = value,
            error = null
        )
    }

    /**
     * 更新心率
     */
    fun updateHeartRate(value: String) {
        _uiState.value = _uiState.value.copy(
            heartRate = value,
            error = null
        )
    }

    /**
     * 更新备注
     */
    fun updateNote(value: String) {
        _uiState.value = _uiState.value.copy(
            note = value,
            error = null
        )
    }

    /**
     * 更新测量时间
     */
    fun updateMeasureTime(dateTime: LocalDateTime) {
        _uiState.value = _uiState.value.copy(
            measureTime = dateTime,
            error = null
        )
    }

    /**
     * 切换标签选择状态
     */
    fun toggleTag(tag: String) {
        val currentTags = _uiState.value.selectedTags.toMutableSet()
        if (currentTags.contains(tag)) {
            currentTags.remove(tag)
        } else {
            currentTags.add(tag)
        }
        _uiState.value = _uiState.value.copy(
            selectedTags = currentTags,
            error = null
        )
    }

    /**
     * 显示日期选择器
     */
    fun showDatePicker() {
        _uiState.value = _uiState.value.copy(showDatePicker = true)
    }

    /**
     * 隐藏日期选择器
     */
    fun hideDatePicker() {
        _uiState.value = _uiState.value.copy(showDatePicker = false)
    }

    /**
     * 显示时间选择器
     */
    fun showTimePicker() {
        _uiState.value = _uiState.value.copy(showTimePicker = true)
    }

    /**
     * 隐藏时间选择器
     */
    fun hideTimePicker() {
        _uiState.value = _uiState.value.copy(showTimePicker = false)
    }

    /**
     * 保存血压记录
     */
    fun saveRecord() {
        val state = _uiState.value
        
        if (!state.canSave) {
            _uiState.value = state.copy(
                error = "请检查输入的数据是否正确"
            )
            return
        }

        if (state.validationErrors.isNotEmpty()) {
            _uiState.value = state.copy(
                error = state.validationErrors.first()
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)
            
            try {
                val bloodPressureData = BloodPressureData(
                    id = 0, // 新记录ID由数据库生成
                    systolic = state.systolicValue!!,
                    diastolic = state.diastolicValue!!,
                    heartRate = state.heartRateValue!!,
                    measureTime = state.measureTime,
                    note = state.note,
                    tags = state.selectedTags.toList()
                )
                
                val result = addBloodPressureUseCase(bloodPressureData)
                
                result.fold(
                    onSuccess = { recordId ->
                        _uiState.value = state.copy(
                            isLoading = false,
                            isSaved = true,
                            error = null
                        )
                    },
                    onFailure = { throwable ->
                        _uiState.value = state.copy(
                            isLoading = false,
                            error = throwable.message ?: "保存失败，请重试"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = state.copy(
                    isLoading = false,
                    error = e.message ?: "保存失败，请重试"
                )
            }
        }
    }

    /**
     * 清除错误
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * 重置表单
     */
    fun resetForm() {
        _uiState.value = AddRecordUiState()
    }

    /**
     * 快速填充预设值（用于测试）
     */
    fun fillTestData() {
        _uiState.value = _uiState.value.copy(
            systolic = "120",
            diastolic = "80",
            heartRate = "72",
            note = "正常测量",
            selectedTags = setOf("晨起", "家中")
        )
    }
}