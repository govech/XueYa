package com.example.xueya.domain.usecase

import androidx.annotation.RequiresPermission
import com.example.xueya.data.speech.SpeechRecognitionManager
import com.example.xueya.domain.model.ai.BloodPressureParseResult
import com.example.xueya.domain.repository.AiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * 语音输入用例
 * 结合语音识别和AI解析功能
 */
class VoiceInputUseCase @Inject constructor(
    private val speechRecognitionManager: SpeechRecognitionManager,
    private val aiRepository: AiRepository
) {
    
    /**
     * 语音输入状态
     */
    sealed class VoiceInputState {
        object Idle : VoiceInputState()
        object Listening : VoiceInputState()
        object Processing : VoiceInputState()
        object AiParsing : VoiceInputState()
        data class Success(val parseResult: BloodPressureParseResult) : VoiceInputState()
        data class Error(val message: String) : VoiceInputState()
    }
    
    /**
     * 开始语音输入和解析流程
     * @param languageCode 语言代码
     * @param prompt 语音提示
     */
    @RequiresPermission(android.Manifest.permission.RECORD_AUDIO)
    operator fun invoke(
        languageCode: String = "zh-CN",
        prompt: String = "请说出血压数据，如：高压120，低压80，心率75"
    ): Flow<VoiceInputState> = flow {
        
        emit(VoiceInputState.Idle)
        
        // 检查语音识别是否可用
        if (!speechRecognitionManager.isSpeechRecognitionAvailable()) {
            emit(VoiceInputState.Error("设备不支持语音识别"))
            return@flow
        }
        
        // 开始语音识别
        speechRecognitionManager.startSpeechRecognition(languageCode, prompt).collect { speechState ->
            when (speechState) {
                is SpeechRecognitionManager.SpeechRecognitionState.Idle -> {
                    emit(VoiceInputState.Idle)
                }
                is SpeechRecognitionManager.SpeechRecognitionState.Listening -> {
                    emit(VoiceInputState.Listening)
                }
                is SpeechRecognitionManager.SpeechRecognitionState.Processing -> {
                    emit(VoiceInputState.Processing)
                }
                is SpeechRecognitionManager.SpeechRecognitionState.Success -> {
                    // 语音识别成功，开始AI解析
                    emit(VoiceInputState.AiParsing)
                    try {
                        val parseResult = aiRepository.parseBloodPressureFromText(speechState.text)
                        parseResult.fold(
                            onSuccess = { result ->
                                emit(VoiceInputState.Success(result))
                            },
                            onFailure = { error ->
                                emit(VoiceInputState.Error("AI解析失败: ${error.message}"))
                            }
                        )
                    } catch (e: Exception) {
                        emit(VoiceInputState.Error("AI解析异常: ${e.message}"))
                    }
                }
                is SpeechRecognitionManager.SpeechRecognitionState.Error -> {
                    emit(VoiceInputState.Error("语音识别失败: ${speechState.message}"))
                }
            }
        }
    }
}