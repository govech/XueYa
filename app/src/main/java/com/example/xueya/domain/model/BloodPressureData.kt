package com.example.xueya.domain.model

import java.time.LocalDateTime

/**
 * 血压数据领域模型
 * 用于业务逻辑层和UI层
 */
data class BloodPressureData(
    val id: Long = 0,
    val systolic: Int,          // 收缩压
    val diastolic: Int,         // 舒张压
    val heartRate: Int,         // 心率
    val measureTime: LocalDateTime, // 测量时间
    val note: String = "",      // 备注
    val tags: List<String> = emptyList() // 标签（如：晨起、餐后、运动后等）
) {
    /**
     * 获取血压分类
     */
    val category: BloodPressureCategory
        get() = BloodPressureCategory.categorize(systolic, diastolic)

    /**
     * 获取血压显示文本
     */
    val displayText: String
        get() = "$systolic/$diastolic mmHg"

    /**
     * 判断是否需要警告
     */
    val needsAttention: Boolean
        get() = category == BloodPressureCategory.HIGH_STAGE_2 || 
               category == BloodPressureCategory.HYPERTENSIVE_CRISIS

    /**
     * 心率是否正常 (成人静息心率正常范围: 60-100 bpm)
     */
    val isHeartRateNormal: Boolean
        get() = heartRate in 60..100
}