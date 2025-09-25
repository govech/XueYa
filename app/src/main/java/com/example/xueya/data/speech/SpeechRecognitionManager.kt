package com.example.xueya.data.speech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.annotation.RequiresPermission
import com.example.xueya.utils.Constants
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 语音识别管理器
 */
@Singleton
class SpeechRecognitionManager @Inject constructor(
    private val context: Context
) {
    
    /**
     * 语音识别状态
     */
    sealed class SpeechRecognitionState {
        object Idle : SpeechRecognitionState()
        object Listening : SpeechRecognitionState()
        object Processing : SpeechRecognitionState()
        data class Success(val text: String) : SpeechRecognitionState()
        data class Error(val message: String) : SpeechRecognitionState()
    }
    
    /**
     * 检查语音识别是否可用
     */
    fun isSpeechRecognitionAvailable(): Boolean {
        return SpeechRecognizer.isRecognitionAvailable(context)
    }
    
    /**
     * 开始语音识别
     * @param languageCode 语言代码，如 "zh-CN", "en-US"
     * @param prompt 提示文本
     */
    @RequiresPermission(android.Manifest.permission.RECORD_AUDIO)
    fun startSpeechRecognition(
        languageCode: String = "zh-CN",
        prompt: String = "请说出血压数据"
    ): Flow<SpeechRecognitionState> = callbackFlow {
        
        if (!isSpeechRecognitionAvailable()) {
            trySend(SpeechRecognitionState.Error("设备不支持语音识别"))
            close()
            return@callbackFlow
        }
        
        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageCode)
            putExtra(RecognizerIntent.EXTRA_PROMPT, prompt)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, Constants.SpeechRecognition.MAX_RESULTS)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
        
        val recognitionListener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                trySend(SpeechRecognitionState.Listening)
            }
            
            override fun onBeginningOfSpeech() {
                // 用户开始说话
            }
            
            override fun onRmsChanged(rmsdB: Float) {
                // 音量变化，可用于显示音量指示器
            }
            
            override fun onBufferReceived(buffer: ByteArray?) {
                // 接收音频缓冲区
            }
            
            override fun onEndOfSpeech() {
                trySend(SpeechRecognitionState.Processing)
            }
            
            override fun onError(error: Int) {
                val errorMessage = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "音频错误"
                    SpeechRecognizer.ERROR_CLIENT -> "客户端错误"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "权限不足"
                    SpeechRecognizer.ERROR_NETWORK -> "网络错误"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "网络超时"
                    SpeechRecognizer.ERROR_NO_MATCH -> "无法识别语音"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "识别器忙碌"
                    SpeechRecognizer.ERROR_SERVER -> "服务器错误"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "语音超时"
                    else -> "未知错误"
                }
                trySend(SpeechRecognitionState.Error(errorMessage))
                close()
            }
            
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    trySend(SpeechRecognitionState.Success(matches[0]))
                } else {
                    trySend(SpeechRecognitionState.Error("无法识别语音内容"))
                }
                close()
            }
            
            override fun onPartialResults(partialResults: Bundle?) {
                // 部分识别结果，可用于实时显示
            }
            
            override fun onEvent(eventType: Int, params: Bundle?) {
                // 其他事件
            }
        }
        
        speechRecognizer.setRecognitionListener(recognitionListener)
        speechRecognizer.startListening(intent)
        
        // 设置超时
        val timeoutJob = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
            kotlinx.coroutines.delay(Constants.SpeechRecognition.LISTENING_TIMEOUT_MS)
            if (!channel.isClosedForSend) {
                trySend(SpeechRecognitionState.Error("语音识别超时"))
                close()
            }
        }
        
        awaitClose {
            timeoutJob.cancel()
            speechRecognizer.stopListening()
            speechRecognizer.destroy()
        }
    }
}