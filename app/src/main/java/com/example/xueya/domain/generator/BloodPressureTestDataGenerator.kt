package com.example.xueya.domain.generator

import com.example.xueya.domain.model.BloodPressureData
import kotlin.random.Random
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 * 血压测试数据生成器
 * 
 * 用于生成模拟的血压测试数据，支持不同时间范围和模式
 * 生成的数据符合医学标准，包含正常、异常和边界值
 */
class BloodPressureTestDataGenerator {
    
    /**
     * 数据生成模式
     */
    enum class GenerationMode {
        NORMAL,        // 正常血压模式
        HYPERTENSION,  // 高血压模式
        HYPOTENSION,   // 低血压模式
        MIXED          // 混合模式（包含各种情况）
    }
    
    /**
     * 时间范围枚举
     */
    enum class TimeRange {
        ONE_WEEK,      // 一周
        ONE_MONTH,     // 一个月
        SIX_MONTHS     // 半年
    }
    
    /**
     * 生成血压测试数据
     * 
     * @param timeRange 时间范围
     * @param mode 生成模式
     * @param measurementsPerDay 每天测量次数，默认为2次（早晚各一次）
     * @return 生成的血压数据列表
     */
    fun generateTestData(
        timeRange: TimeRange,
        mode: GenerationMode = GenerationMode.MIXED,
        measurementsPerDay: Int = 2
    ): List<BloodPressureData> {
        val days = when (timeRange) {
            TimeRange.ONE_WEEK -> 7
            TimeRange.ONE_MONTH -> 30
            TimeRange.SIX_MONTHS -> 180
        }
        
        val data = mutableListOf<BloodPressureData>()
        val startDate = LocalDateTime.now().minusDays(days.toLong())
        
        repeat(days) { dayOffset ->
            val currentDate = startDate.plusDays(dayOffset.toLong())
            
            repeat(measurementsPerDay) { measurementIndex ->
                val measurementTime = when (measurementIndex) {
                    0 -> currentDate.withHour(8).withMinute(Random.nextInt(0, 60)) // 早上8点左右
                    1 -> currentDate.withHour(20).withMinute(Random.nextInt(0, 60)) // 晚上8点左右
                    else -> currentDate.withHour(Random.nextInt(6, 23)).withMinute(Random.nextInt(0, 60))
                }
                
                val bloodPressure = generateBloodPressure(mode, dayOffset, measurementIndex)
                val heartRate = generateHeartRate(bloodPressure.systolic, bloodPressure.diastolic)
                val note = generateNote(bloodPressure, measurementTime)
                val tags = generateTags(measurementTime, bloodPressure)
                
                data.add(
                    BloodPressureData(
                        systolic = bloodPressure.systolic,
                        diastolic = bloodPressure.diastolic,
                        heartRate = heartRate,
                        measureTime = measurementTime,
                        note = note,
                        tags = tags
                    )
                )
            }
        }
        
        return data.sortedBy { it.measureTime }
    }
    
    /**
     * 生成血压值
     */
    private fun generateBloodPressure(
        mode: GenerationMode,
        dayOffset: Int,
        measurementIndex: Int
    ): BloodPressureValues {
        return when (mode) {
            GenerationMode.NORMAL -> generateNormalBloodPressure(dayOffset, measurementIndex)
            GenerationMode.HYPERTENSION -> generateHypertensionBloodPressure(dayOffset, measurementIndex)
            GenerationMode.HYPOTENSION -> generateHypotensionBloodPressure(dayOffset, measurementIndex)
            GenerationMode.MIXED -> generateMixedBloodPressure(dayOffset, measurementIndex)
        }
    }
    
    /**
     * 生成正常血压值
     */
    private fun generateNormalBloodPressure(dayOffset: Int, measurementIndex: Int): BloodPressureValues {
        // 正常血压范围：收缩压90-120，舒张压60-80
        val systolic = Random.nextInt(90, 121)
        val diastolic = Random.nextInt(60, 81)
        
        // 添加一些自然波动
        val variation = Random.nextInt(-5, 6)
        return BloodPressureValues(
            systolic = (systolic + variation).coerceIn(90, 130),
            diastolic = (diastolic + variation).coerceIn(60, 85)
        )
    }
    
    /**
     * 生成高血压值
     */
    private fun generateHypertensionBloodPressure(dayOffset: Int, measurementIndex: Int): BloodPressureValues {
        // 高血压范围：收缩压140-180，舒张压90-110
        val systolic = Random.nextInt(140, 181)
        val diastolic = Random.nextInt(90, 111)
        
        // 添加一些波动
        val variation = Random.nextInt(-8, 9)
        return BloodPressureValues(
            systolic = (systolic + variation).coerceIn(135, 185),
            diastolic = (diastolic + variation).coerceIn(85, 115)
        )
    }
    
    /**
     * 生成低血压值
     */
    private fun generateHypotensionBloodPressure(dayOffset: Int, measurementIndex: Int): BloodPressureValues {
        // 低血压范围：收缩压70-90，舒张压40-60
        val systolic = Random.nextInt(70, 91)
        val diastolic = Random.nextInt(40, 61)
        
        // 添加一些波动
        val variation = Random.nextInt(-5, 6)
        return BloodPressureValues(
            systolic = (systolic + variation).coerceIn(65, 95),
            diastolic = (diastolic + variation).coerceIn(35, 65)
        )
    }
    
    /**
     * 生成混合血压值（包含各种情况）
     */
    private fun generateMixedBloodPressure(dayOffset: Int, measurementIndex: Int): BloodPressureValues {
        val random = Random.nextFloat()
        
        return when {
            random < 0.6f -> generateNormalBloodPressure(dayOffset, measurementIndex) // 60%正常
            random < 0.8f -> generateMildHypertension(dayOffset, measurementIndex)    // 20%轻度高血压
            random < 0.9f -> generateModerateHypertension(dayOffset, measurementIndex) // 10%中度高血压
            random < 0.95f -> generateHypotensionBloodPressure(dayOffset, measurementIndex) // 5%低血压
            else -> generateSevereHypertension(dayOffset, measurementIndex) // 5%重度高血压
        }
    }
    
    /**
     * 生成轻度高血压
     */
    private fun generateMildHypertension(dayOffset: Int, measurementIndex: Int): BloodPressureValues {
        val systolic = Random.nextInt(130, 141)
        val diastolic = Random.nextInt(80, 91)
        return BloodPressureValues(systolic, diastolic)
    }
    
    /**
     * 生成中度高血压
     */
    private fun generateModerateHypertension(dayOffset: Int, measurementIndex: Int): BloodPressureValues {
        val systolic = Random.nextInt(140, 161)
        val diastolic = Random.nextInt(90, 101)
        return BloodPressureValues(systolic, diastolic)
    }
    
    /**
     * 生成重度高血压
     */
    private fun generateSevereHypertension(dayOffset: Int, measurementIndex: Int): BloodPressureValues {
        val systolic = Random.nextInt(160, 181)
        val diastolic = Random.nextInt(100, 111)
        return BloodPressureValues(systolic, diastolic)
    }
    
    /**
     * 生成心率
     */
    private fun generateHeartRate(systolic: Int, diastolic: Int): Int {
        // 根据血压值调整心率范围
        val baseRate = when {
            systolic >= 160 || diastolic >= 100 -> Random.nextInt(80, 101) // 高血压时心率偏高
            systolic < 90 || diastolic < 60 -> Random.nextInt(50, 71)      // 低血压时心率偏低
            else -> Random.nextInt(60, 81)                                  // 正常血压时心率正常
        }
        
        // 添加一些随机波动
        return (baseRate + Random.nextInt(-5, 6)).coerceIn(40, 120)
    }
    
    /**
     * 生成备注
     */
    private fun generateNote(bloodPressure: BloodPressureValues, time: LocalDateTime): String {
        val notes = mutableListOf<String>()
        
        // 根据血压值添加相关备注
        when {
            bloodPressure.systolic >= 180 || bloodPressure.diastolic >= 110 -> {
                notes.add("血压异常，需要关注")
            }
            bloodPressure.systolic >= 140 || bloodPressure.diastolic >= 90 -> {
                notes.add("血压偏高")
            }
            bloodPressure.systolic < 90 || bloodPressure.diastolic < 60 -> {
                notes.add("血压偏低")
            }
        }
        
        // 根据时间添加备注
        when (time.hour) {
            in 6..9 -> notes.add("晨起测量")
            in 18..22 -> notes.add("晚间测量")
        }
        
        // 随机添加一些状态备注
        val randomNotes = listOf(
            "测量前休息5分钟",
            "测量时心情平静",
            "测量前未服用降压药",
            "测量前已服用降压药",
            "运动后测量",
            "餐后测量",
            "测量时感到紧张",
            "测量时感到疲劳"
        )
        
        if (Random.nextFloat() < 0.3f) { // 30%概率添加随机备注
            notes.add(randomNotes.random())
        }
        
        return notes.joinToString("，")
    }
    
    /**
     * 生成标签
     */
    private fun generateTags(time: LocalDateTime, bloodPressure: BloodPressureValues): List<String> {
        val tags = mutableListOf<String>()
        
        // 时间标签
        when (time.hour) {
            in 6..9 -> tags.add("晨起")
            in 12..14 -> tags.add("午间")
            in 18..22 -> tags.add("晚间")
        }
        
        // 血压状态标签
        when {
            bloodPressure.systolic >= 180 || bloodPressure.diastolic >= 110 -> {
                tags.add("高血压危象")
            }
            bloodPressure.systolic >= 160 || bloodPressure.diastolic >= 100 -> {
                tags.add("高血压2期")
            }
            bloodPressure.systolic >= 140 || bloodPressure.diastolic >= 90 -> {
                tags.add("高血压1期")
            }
            bloodPressure.systolic < 90 || bloodPressure.diastolic < 60 -> {
                tags.add("低血压")
            }
            else -> {
                tags.add("正常")
            }
        }
        
        // 随机添加其他标签
        val otherTags = listOf("测试数据", "餐前", "餐后", "运动前", "运动后", "服药前", "服药后")
        if (Random.nextFloat() < 0.2f) { // 20%概率添加其他标签
            tags.add(otherTags.random())
        }
        
        return tags
    }
    
    /**
     * 血压值数据类
     */
    private data class BloodPressureValues(
        val systolic: Int,
        val diastolic: Int
    )
}
