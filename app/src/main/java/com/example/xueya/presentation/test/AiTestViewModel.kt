package com.example.xueya.presentation.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xueya.domain.model.ai.AiParseState
import com.example.xueya.domain.model.ai.BloodPressureParseResult
import com.example.xueya.domain.usecase.ParseBloodPressureFromTextUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * AI 测试 ViewModel
 */
@HiltViewModel
class AiTestViewModel @Inject constructor(
    private val parseBloodPressureFromTextUseCase: ParseBloodPressureFromTextUseCase
) : ViewModel() {

    private val _parseState = MutableStateFlow<AiParseState>(AiParseState.Idle)
    val parseState: StateFlow<AiParseState> = _parseState.asStateFlow()

    private val _lastResult = MutableStateFlow<BloodPressureParseResult?>(null)
    val lastResult: StateFlow<BloodPressureParseResult?> = _lastResult.asStateFlow()

    /**
     * 解析文本
     */
    fun parseText(text: String) {
        if (text.isBlank()) return
        
        viewModelScope.launch {
            _parseState.value = AiParseState.Loading
            
            parseBloodPressureFromTextUseCase(text)
                .onSuccess { result ->
                    _lastResult.value = result
                    _parseState.value = AiParseState.Success(result)
                }
                .onFailure { exception ->
                    _parseState.value = AiParseState.Error(
                        exception.message ?: "解析失败"
                    )
                }
        }
    }

    /**
     * 重置状态
     */
    fun resetState() {
        _parseState.value = AiParseState.Idle
        _lastResult.value = null
    }

    /**
     * 开始语音输入（模拟）
     */
    fun startVoiceInput() {
        _parseState.value = AiParseState.Error("语音识别功能正在开发中，请使用文本输入")
    }

    /**
     * 停止语音输入
     */
    fun stopVoiceInput() {
        resetState()
    }
}