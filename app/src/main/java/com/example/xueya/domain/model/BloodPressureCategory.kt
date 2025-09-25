package com.example.xueya.domain.model

/**
 * 血压分类标准枚举
 * 
 * 根据中国高血压防治指南和美国心脏协会标准定义的血压分类
 * 用于对血压测量结果进行分类和风险评估
 * 
 * 分类标准：
 * - 正常(NORMAL)：收缩压<120且舒张压<80
 * - 偏高(ELEVATED)：收缩压120-129且舒张压<80
 * - 高血压1期(HIGH_STAGE_1)：收缩压130-139或舒张压80-89
 * - 高血压2期(HIGH_STAGE_2)：收缩压140-179或舒张压90-109
 * - 高血压危象(HYPERTENSIVE_CRISIS)：收缩压≥180或舒张压≥110
 */
enum class BloodPressureCategory(
    /**
     * 分类名称（中文显示名称）
     */
    val categoryName: String,
    
    /**
     * 分类描述信息
     */
    val description: String,
    
    /**
     * 收缩压范围
     */
    val systolicRange: IntRange,
    
    /**
     * 舒张压范围
     */
    val diastolicRange: IntRange
) {
    /**
     * 正常血压
     * 
     * 收缩压：<120 mmHg
     * 舒张压：<80 mmHg
     */
    NORMAL(
        categoryName = "正常",
        description = "血压正常",
        systolicRange = 0..119,
        diastolicRange = 0..79
    ),
    
    /**
     * 血压偏高
     * 
     * 收缩压：120-129 mmHg
     * 舒张压：<80 mmHg
     */
    ELEVATED(
        categoryName = "偏高",
        description = "血压偏高",
        systolicRange = 120..129,
        diastolicRange = 0..79
    ),
    
    /**
     * 高血压1期（轻度高血压）
     * 
     * 收缩压：130-139 mmHg
     * 舒张压：80-89 mmHg
     */
    HIGH_STAGE_1(
        categoryName = "高血压1期",
        description = "轻度高血压",
        systolicRange = 130..139,
        diastolicRange = 80..89
    ),
    
    /**
     * 高血压2期（中度高血压）
     * 
     * 收缩压：140-179 mmHg
     * 舒张压：90-109 mmHg
     */
    HIGH_STAGE_2(
        categoryName = "高血压2期",
        description = "中度高血压",
        systolicRange = 140..179,
        diastolicRange = 90..109
    ),
    
    /**
     * 高血压危象
     * 
     * 收缩压：≥180 mmHg
     * 舒张压：≥110 mmHg
     * 
     * 此分类表示血压过高，需要立即就医
     */
    HYPERTENSIVE_CRISIS(
        categoryName = "高血压危象",
        description = "需要立即就医",
        systolicRange = 180..Int.MAX_VALUE,
        diastolicRange = 110..Int.MAX_VALUE
    );

    companion object {
        /**
         * 根据收缩压和舒张压判断血压分类
         * 
         * 使用医学标准算法对血压进行分类：
         * 1. 首先检查是否为高血压危象（收缩压≥180或舒张压≥110）
         * 2. 然后依次检查高血压2期、高血压1期、偏高和正常
         * 
         * @param systolic 收缩压数值（mmHg）
         * @param diastolic 舒张压数值（mmHg）
         * @return BloodPressureCategory 对应的血压分类
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