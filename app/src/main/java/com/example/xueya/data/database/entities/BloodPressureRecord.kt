package com.example.xueya.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * 血压记录数据库实体
 * 
 * 用于Room数据库存储的血压记录实体类
 * 与领域模型BloodPressureData相对应，但针对数据库存储进行了优化
 * 使用Room注解标记为数据库实体
 * 
 * @property id 记录ID，数据库主键，自动生成
 * @property systolic 收缩压（高压），单位：mmHg
 * @property diastolic 舒张压（低压），单位：mmHg
 * @property heartRate 心率，单位：bpm（每分钟心跳次数）
 * @property measureTime 测量时间
 * @property note 备注信息，可记录测量时的身体状态、用药情况等
 * @property tags 标签，以逗号分隔的字符串形式存储，用于分类和筛选记录
 */
@Entity(tableName = "blood_pressure_records")
data class BloodPressureRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val systolic: Int,          // 收缩压（高压），单位：mmHg
    val diastolic: Int,         // 舒张压（低压），单位：mmHg
    val heartRate: Int,         // 心率，单位：bpm
    val measureTime: LocalDateTime, // 测量时间
    val note: String = "",      // 备注信息
    val tags: String = ""       // 标签，以逗号分隔的字符串存储
)