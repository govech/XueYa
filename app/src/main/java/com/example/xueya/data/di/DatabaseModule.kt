package com.example.xueya.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.xueya.data.database.BloodPressureDao
import com.example.xueya.data.database.BloodPressureDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 数据库依赖注入模块
 * 
 * 使用Hilt框架管理数据库相关的依赖注入
 * 提供Room数据库实例和数据访问对象(DAO)的绑定
 * 确保在整个应用中使用单例模式
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * 提供血压数据库实例
     * 
     * 创建并配置Room数据库实例，使用应用上下文和数据库名称
     * 设置写前日志模式以优化性能
     * 使用@Singleton注解确保在整个应用中只有一个数据库实例
     * 
     * @param context 应用上下文，通过Hilt自动注入
     * @return BloodPressureDatabase 血压数据库实例
     */
    @Provides
    @Singleton
    fun provideBloodPressureDatabase(
        @ApplicationContext context: Context
    ): BloodPressureDatabase {
        return Room.databaseBuilder(
            context,
            BloodPressureDatabase::class.java,
            BloodPressureDatabase.DATABASE_NAME
        )
        .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING) // 性能优化
        .build()
    }

    /**
     * 提供血压数据访问对象
     * 
     * 从数据库实例中获取血压数据访问对象
     * 
     * @param database 血压数据库实例
     * @return BloodPressureDao 血压数据访问对象
     */
    @Provides
    fun provideBloodPressureDao(database: BloodPressureDatabase): BloodPressureDao {
        return database.bloodPressureDao()
    }
}