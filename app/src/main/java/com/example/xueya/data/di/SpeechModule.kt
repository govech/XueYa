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
 */
@Module
@InstallIn(SingletonComponent::class)
object SpeechModule {

    @Provides
    @Singleton
    fun provideSpeechRecognitionManager(
        @ApplicationContext context: Context
    ): SpeechRecognitionManager {
        return SpeechRecognitionManager(context)
    }
}