package com.example.xueya.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * 血压记录数据库实体
 */
@Entity(tableName = "blood_pressure_records")
data class BloodPressureRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val systolic: Int,          // 收缩压
    val diastolic: Int,         // 舒张压
    val heartRate: Int,         // 心率
    val measureTime: LocalDateTime, // 测量时间
    val note: String = "",      // 备注
    val tags: String = ""       // 标签，以逗号分隔的字符串存储
)