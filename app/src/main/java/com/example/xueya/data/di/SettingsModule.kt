package com.example.xueya.data.di

import android.content.Context
import com.example.xueya.data.export.CsvExportManager
import com.example.xueya.data.preferences.UserPreferencesDataStore
import com.example.xueya.domain.repository.BloodPressureRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 设置模块依赖注入配置
 * 
 * 使用Hilt框架管理设置相关的依赖注入
 * 提供用户偏好设置和数据导出管理器的绑定
 * 确保在整个应用中使用单例模式
 */
@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {

    /**
     * 提供用户偏好设置数据存储实例
     * 
     * 创建UserPreferencesDataStore对象，用于管理用户偏好设置
     * 使用应用上下文进行初始化
     * 使用@Singleton注解确保在整个应用中只有一个实例
     * 
     * @param context 应用上下文，通过Hilt自动注入
     * @return UserPreferencesDataStore 用户偏好设置数据存储实例
     */
    @Provides
    @Singleton
    fun provideUserPreferencesDataStore(
        @ApplicationContext context: Context
    ): UserPreferencesDataStore {
        return UserPreferencesDataStore(context)
    }

    /**
     * 提供CSV导出管理器实例
     * 
     * 创建CsvExportManager对象，用于管理血压数据的CSV导出功能
     * 需要应用上下文和血压数据仓库进行初始化
     * 使用@Singleton注解确保在整个应用中只有一个实例
     * 
     * @param context 应用上下文，通过Hilt自动注入
     * @param bloodPressureRepository 血压数据仓库，通过Hilt自动注入
     * @return CsvExportManager CSV导出管理器实例
     */
    @Provides
    @Singleton
    fun provideCsvExportManager(
        @ApplicationContext context: Context,
        bloodPressureRepository: BloodPressureRepository
    ): CsvExportManager {
        return CsvExportManager(context, bloodPressureRepository)
    }
}