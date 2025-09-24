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
 */
@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {

    @Provides
    @Singleton
    fun provideUserPreferencesDataStore(
        @ApplicationContext context: Context
    ): UserPreferencesDataStore {
        return UserPreferencesDataStore(context)
    }

    @Provides
    @Singleton
    fun provideCsvExportManager(
        @ApplicationContext context: Context,
        bloodPressureRepository: BloodPressureRepository
    ): CsvExportManager {
        return CsvExportManager(context, bloodPressureRepository)
    }
}