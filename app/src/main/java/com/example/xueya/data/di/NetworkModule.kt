package com.example.xueya.data.di

import com.example.xueya.data.network.AiApiService
import com.example.xueya.utils.Constants
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * 网络依赖注入模块
 * 配置 Retrofit 和 OkHttp 相关依赖
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * 提供 Gson 实例
     */
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    /**
     * 提供 OkHttpClient 实例
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(Constants.AI.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(Constants.AI.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(Constants.AI.WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    /**
     * 提供 Retrofit 实例
     */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.AI.OPENROUTER_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * 提供 AI API 服务实例
     */
    @Provides
    @Singleton
    fun provideAiApiService(retrofit: Retrofit): AiApiService {
        return retrofit.create(AiApiService::class.java)
    }
}