package com.example.xueya.domain.model

import java.time.LocalDateTime

/**
 * 血压数据领域模型
 * 
 * 用于业务逻辑层和UI层，表示一条血压记录的完整信息
 * 包含血压值、心率、测量时间、备注和标签等信息
 * 
 * @property id 记录ID，数据库主键
 * @property systolic 收缩压（高压），单位：mmHg
 * @property diastolic 舒张压（低压），单位：mmHg
 * @property heartRate 心率，单位：bpm（每分钟心跳次数）
 * @property measureTime 测量时间
 * @property note 备注信息，可记录测量时的身体状态、用药情况等
 * @property tags 标签列表，用于分类和筛选记录（如：晨起、餐后、运动后等）
 */
data class BloodPressureData(
    val id: Long = 0,
    val systolic: Int,          // 收缩压（高压），单位：mmHg
    val diastolic: Int,         // 舒张压（低压），单位：mmHg
    val heartRate: Int,         // 心率，单位：bpm
    val measureTime: LocalDateTime, // 测量时间
    val note: String = "",      // 备注信息
    val tags: List<String> = emptyList() // 标签列表
) {
    /**
     * 获取血压分类
     * 
     * 根据收缩压和舒张压的数值，按照医学标准对血压进行分类
     * 分类标准参考中国高血压防治指南和美国心脏协会标准
     * 
     * @return BloodPressureCategory 血压分类枚举值
     */
    val category: BloodPressureCategory
        get() = BloodPressureCategory.categorize(systolic, diastolic)

    /**
     * 获取血压显示文本
     * 
     * 格式化显示血压值，格式为"收缩压/舒张压 mmHg"
     * 例如："120/80 mmHg"
     * 
     * @return String 格式化后的血压显示文本
     */
    val displayText: String
        get() = "$systolic/$diastolic mmHg"

    /**
     * 判断是否需要警告
     * 
     * 当血压分类为高血压2期或高血压危象时，需要特别关注
     * 这种情况下建议用户及时就医
     * 
     * @return Boolean 是否需要警告
     */
    val needsAttention: Boolean
        get() = category == BloodPressureCategory.HIGH_STAGE_2 || 
               category == BloodPressureCategory.HYPERTENSIVE_CRISIS

    /**
     * 心率是否正常
     * 
     * 成人静息心率正常范围为60-100 bpm
     * 超出此范围可能表示心率异常
     * 
     * @return Boolean 心率是否在正常范围内
     */
    val isHeartRateNormal: Boolean
        get() = heartRate in 60..100
}