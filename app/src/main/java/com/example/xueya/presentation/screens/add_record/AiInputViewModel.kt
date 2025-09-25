package com.example.xueya.presentation.screens.add_record

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
 * AI 输入 ViewModel
 * 管理语音输入和文本解析相关状态
 */
@HiltViewModel
class AiInputViewModel @Inject constructor(
    private val parseBloodPressureFromTextUseCase: ParseBloodPressureFromTextUseCase
) : ViewModel() {

    private val _parseState = MutableStateFlow<AiParseState>(AiParseState.Idle)
    val parseState: StateFlow<AiParseState> = _parseState.asStateFlow()

    private val _lastParseResult = MutableStateFlow<BloodPressureParseResult?>(null)
    val lastParseResult: StateFlow<BloodPressureParseResult?> = _lastParseResult.asStateFlow()

    /**
     * 解析血压文本
     */
    fun parseBloodPressureText(text: String) {
        if (text.isBlank()) return
        
        viewModelScope.launch {
            _parseState.value = AiParseState.Loading
            
            parseBloodPressureFromTextUseCase(text)
                .onSuccess { result ->
                    _lastParseResult.value = result
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
     * 重置解析状态
     */
    fun resetParseState() {
        _parseState.value = AiParseState.Idle
        _lastParseResult.value = null
    }

    /**
     * 开始语音监听（模拟实现）
     * 在实际应用中，这里会集成语音识别服务
     */
    fun startVoiceListening() {
        // TODO: 集成语音识别服务
        // 目前先显示提示信息
        _parseState.value = AiParseState.Error("语音识别功能正在开发中，请使用文本输入")
    }

    /**
     * 停止语音监听
     */
    fun stopVoiceListening() {
        // TODO: 停止语音识别服务
        resetParseState()
    }
}