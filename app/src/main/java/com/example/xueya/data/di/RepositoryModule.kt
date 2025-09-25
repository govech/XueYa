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
 * 
 * 使用Hilt框架管理Repository层的依赖注入
 * 将Repository实现类绑定到对应的接口上
 * 确保在整个应用中使用单例模式
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * 绑定血压数据仓库实现类到接口
     * 
     * 将BloodPressureRepositoryImpl实现类绑定到BloodPressureRepository接口
     * 使用@Singleton注解确保在整个应用中只有一个实例
     * 
     * @param bloodPressureRepositoryImpl 血压数据仓库实现类实例
     * @return BloodPressureRepository 血压数据仓库接口
     */
    @Binds
    @Singleton
    abstract fun bindBloodPressureRepository(
        bloodPressureRepositoryImpl: BloodPressureRepositoryImpl
    ): BloodPressureRepository
    
    /**
     * 绑定AI数据仓库实现类到接口
     * 
     * 将AiRepositoryImpl实现类绑定到AiRepository接口
     * 使用@Singleton注解确保在整个应用中只有一个实例
     * 
     * @param aiRepositoryImpl AI数据仓库实现类实例
     * @return AiRepository AI数据仓库接口
     */
    @Binds
    @Singleton
    abstract fun bindAiRepository(
        aiRepositoryImpl: AiRepositoryImpl
    ): AiRepository
}