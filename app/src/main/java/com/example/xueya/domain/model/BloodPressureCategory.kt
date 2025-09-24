package com.example.xueya.domain.model

/**
 * 血压分类标准
 * 根据中国高血压防治指南和美国心脏协会标准
 */
enum class BloodPressureCategory(
    val categoryName: String,
    val description: String,
    val systolicRange: IntRange,
    val diastolicRange: IntRange
) {
    NORMAL(
        categoryName = "正常",
        description = "血压正常",
        systolicRange = 0..119,
        diastolicRange = 0..79
    ),
    ELEVATED(
        categoryName = "偏高",
        description = "血压偏高",
        systolicRange = 120..129,
        diastolicRange = 0..79
    ),
    HIGH_STAGE_1(
        categoryName = "高血压1期",
        description = "轻度高血压",
        systolicRange = 130..139,
        diastolicRange = 80..89
    ),
    HIGH_STAGE_2(
        categoryName = "高血压2期",
        description = "中度高血压",
        systolicRange = 140..179,
        diastolicRange = 90..109
    ),
    HYPERTENSIVE_CRISIS(
        categoryName = "高血压危象",
        description = "需要立即就医",
        systolicRange = 180..Int.MAX_VALUE,
        diastolicRange = 110..Int.MAX_VALUE
    );

    companion object {
        /**
         * 根据收缩压和舒张压判断血压分类
         */
        fun categorize(systolic: Int, diastolic: Int): BloodPressureCategory {
            return when {
                systolic >= 180 || diastolic >= 110 -> HYPERTENSIVE_CRISIS
                systolic >= 140 || diastolic >= 90 -> HIGH_STAGE_2
                systolic >= 130 || diastolic >= 80 -> HIGH_STAGE_1
                systolic >= 120 && diastolic < 80 -> ELEVATED
                else -> NORMAL
            }
        }
    }
}