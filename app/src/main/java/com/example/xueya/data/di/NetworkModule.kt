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
 * 
 * 使用Hilt框架管理网络相关的依赖注入
 * 配置Retrofit和OkHttp相关依赖，用于AI API服务
 * 确保在整个应用中使用单例模式
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * 提供Gson实例
     * 
     * 创建Gson对象用于JSON序列化和反序列化
     * 使用@Singleton注解确保在整个应用中只有一个Gson实例
     * 
     * @return Gson Gson实例
     */
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    /**
     * 提供OkHttpClient实例
     * 
     * 创建并配置OkHttpClient对象，用于网络请求
     * 添加日志拦截器、设置超时时间等配置
     * 使用@Singleton注解确保在整个应用中只有一个OkHttpClient实例
     * 
     * @return OkHttpClient 配置好的OkHttpClient实例
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
     * 提供Retrofit实例
     * 
     * 创建并配置Retrofit对象，用于API调用
     * 设置基础URL、OkHttpClient和Gson转换器
     * 使用@Singleton注解确保在整个应用中只有一个Retrofit实例
     * 
     * @param okHttpClient 配置好的OkHttpClient实例
     * @return Retrofit 配置好的Retrofit实例
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
     * 提供AI API服务实例
     * 
     * 通过Retrofit创建AI API服务接口的实现
     * 使用@Singleton注解确保在整个应用中只有一个AI API服务实例
     * 
     * @param retrofit 配置好的Retrofit实例
     * @return AiApiService AI API服务实例
     */
    @Provides
    @Singleton
    fun provideAiApiService(retrofit: Retrofit): AiApiService {
        return retrofit.create(AiApiService::class.java)
    }
}