package com.example.xueya.data.di

import com.example.xueya.data.repository.AiRepositoryImpl
import com.example.xueya.data.repository.BloodPressureRepositoryImpl
import com.example.xueya.domain.repository.AiRepository
import com.example.xueya.domain.repository.BloodPressureRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Repository依赖注入模块
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindBloodPressureRepository(
        bloodPressureRepositoryImpl: BloodPressureRepositoryImpl
    ): BloodPressureRepository
    
    @Binds
    @Singleton
    abstract fun bindAiRepository(
        aiRepositoryImpl: AiRepositoryImpl
    ): AiRepository
}