package com.example.xueya.data.di

import android.content.Context
import com.example.xueya.data.speech.SpeechRecognitionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 语音识别依赖注入模块
 * 
 * 使用Hilt框架管理语音识别相关的依赖注入
 * 提供语音识别管理器的绑定
 * 确保在整个应用中使用单例模式
 */
@Module
@InstallIn(SingletonComponent::class)
object SpeechModule {

    /**
     * 提供语音识别管理器实例
     * 
     * 创建SpeechRecognitionManager对象，用于管理语音识别功能
     * 使用应用上下文进行初始化
     * 使用@Singleton注解确保在整个应用中只有一个实例
     * 
     * @param context 应用上下文，通过Hilt自动注入
     * @return SpeechRecognitionManager 语音识别管理器实例
     */
    @Provides
    @Singleton
    fun provideSpeechRecognitionManager(
        @ApplicationContext context: Context
    ): SpeechRecognitionManager {
        return SpeechRecognitionManager(context)
    }
}