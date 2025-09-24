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
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

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

    @Provides
    fun provideBloodPressureDao(database: BloodPressureDatabase): BloodPressureDao {
        return database.bloodPressureDao()
    }
}