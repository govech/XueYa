package com.example.xueya.data.di

import com.example.xueya.domain.generator.BloodPressureTestDataGenerator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 数据生成器依赖注入模块
 * 
 * 提供测试数据生成器相关的依赖注入
 */
@Module
@InstallIn(SingletonComponent::class)
object GeneratorModule {

    /**
     * 提供血压测试数据生成器
     * 
     * @return BloodPressureTestDataGenerator 血压测试数据生成器实例
     */
    @Provides
    @Singleton
    fun provideBloodPressureTestDataGenerator(): BloodPressureTestDataGenerator {
        return BloodPressureTestDataGenerator()
    }
}
