package com.example.xueya.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.example.xueya.data.database.entities.BloodPressureRecord

/**
 * 血压应用数据库
 */
@Database(
    entities = [BloodPressureRecord::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class BloodPressureDatabase : RoomDatabase() {

    abstract fun bloodPressureDao(): BloodPressureDao

    companion object {
        const val DATABASE_NAME = "blood_pressure_database"
    }
}