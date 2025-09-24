package com.example.xueya.presentation.utils

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.*

/**
 * 数据格式化和处理工具类
 */
object DataUtils {
    
    /**
     * 格式化血压数值
     */
    fun formatBloodPressure(systolic: Int, diastolic: Int): String {
        return "$systolic/$diastolic mmHg"
    }
    
    /**
     * 格式化心率
     */
    fun formatHeartRate(heartRate: Int): String {
        return "$heartRate bpm"
    }
    
    /**
     * 格式化日期时间
     */
    fun formatDateTime(dateTime: LocalDateTime, pattern: String = "yyyy-MM-dd HH:mm"): String {
        return dateTime.format(DateTimeFormatter.ofPattern(pattern))
    }
    
    /**
     * 格式化相对时间
     */
    fun formatRelativeTime(dateTime: LocalDateTime): String {
        val now = LocalDateTime.now()
        val duration = java.time.Duration.between(dateTime, now)
        
        return when {
            duration.toDays() > 0 -> "${duration.toDays()}天前"
            duration.toHours() > 0 -> "${duration.toHours()}小时前"
            duration.toMinutes() > 0 -> "${duration.toMinutes()}分钟前"
            else -> "刚刚"
        }
    }
    
    /**
     * 格式化百分比
     */
    fun formatPercentage(value: Double, decimalPlaces: Int = 1): String {
        val percentage = (value * 100).roundToDecimalPlaces(decimalPlaces)
        return "$percentage%"
    }
    
    /**
     * 格式化数字（千分位分隔符）
     */
    fun formatNumber(number: Int): String {
        return String.format(Locale.getDefault(), "%,d", number)
    }
    
    fun formatNumber(number: Double, decimalPlaces: Int = 2): String {
        return String.format(Locale.getDefault(), "%,.${decimalPlaces}f", number)
    }
    
    /**
     * 四舍五入到指定小数位
     */
    fun Double.roundToDecimalPlaces(decimalPlaces: Int): Double {
        val multiplier = 10.0.pow(decimalPlaces)
        return round(this * multiplier) / multiplier
    }
    
    /**
     * 安全除法（避免除零）
     */
    fun safeDivide(numerator: Double, denominator: Double, defaultValue: Double = 0.0): Double {
        return if (denominator != 0.0) numerator / denominator else defaultValue
    }
    
    /**
     * 计算平均值
     */
    fun calculateAverage(values: List<Number>): Double {
        return if (values.isNotEmpty()) {
            values.sumOf { it.toDouble() } / values.size
        } else {
            0.0
        }
    }
    
    /**
     * 计算中位数
     */
    fun calculateMedian(values: List<Number>): Double {
        if (values.isEmpty()) return 0.0
        
        val sorted = values.map { it.toDouble() }.sorted()
        val size = sorted.size
        
        return if (size % 2 == 0) {
            (sorted[size / 2 - 1] + sorted[size / 2]) / 2.0
        } else {
            sorted[size / 2]
        }
    }
    
    /**
     * 计算标准差
     */
    fun calculateStandardDeviation(values: List<Number>): Double {
        if (values.isEmpty()) return 0.0
        
        val mean = calculateAverage(values)
        val squaredDifferences = values.map { (it.toDouble() - mean).pow(2) }
        val variance = calculateAverage(squaredDifferences)
        
        return sqrt(variance)
    }
    
    /**
     * 计算趋势（斜率）
     */
    fun calculateTrend(values: List<Number>): Double {
        if (values.size < 2) return 0.0
        
        val n = values.size
        val x = (0 until n).toList()
        val y = values.map { it.toDouble() }
        
        val sumX = x.sum()
        val sumY = y.sum()
        val sumXY = x.zip(y) { xi, yi -> xi * yi }.sum()
        val sumXX = x.map { it * it }.sum()
        
        val slope = (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX).toDouble()
        return slope
    }
    
    /**
     * 数据分组
     */
    fun <T, K> groupByKey(items: List<T>, keySelector: (T) -> K): Map<K, List<T>> {
        return items.groupBy(keySelector)
    }
    
    /**
     * 数据分页
     */
    fun <T> paginate(items: List<T>, page: Int, pageSize: Int): List<T> {
        val startIndex = page * pageSize
        val endIndex = minOf(startIndex + pageSize, items.size)
        
        return if (startIndex < items.size) {
            items.subList(startIndex, endIndex)
        } else {
            emptyList()
        }
    }
    
    /**
     * 数据过滤
     */
    fun <T> filterByDateRange(
        items: List<T>,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        dateSelector: (T) -> LocalDateTime
    ): List<T> {
        return items.filter { item ->
            val itemDate = dateSelector(item)
            itemDate.isAfter(startDate) && itemDate.isBefore(endDate)
        }
    }
    
    /**
     * 搜索过滤
     */
    fun <T> searchFilter(
        items: List<T>,
        query: String,
        searchFields: List<(T) -> String>
    ): List<T> {
        if (query.isBlank()) return items
        
        val lowercaseQuery = query.lowercase()
        return items.filter { item ->
            searchFields.any { field ->
                field(item).lowercase().contains(lowercaseQuery)
            }
        }
    }
    
    /**
     * 排序辅助函数
     */
    fun <T> sortByField(
        items: List<T>,
        selector: (T) -> Comparable<Any>?,
        ascending: Boolean = true
    ): List<T> {
        return if (ascending) {
            items.sortedBy { selector(it) }
        } else {
            items.sortedByDescending { selector(it) }
        }
    }
    
    /**
     * 数据验证
     */
    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
        return email.matches(emailRegex.toRegex())
    }
    
    fun isValidPhoneNumber(phone: String): Boolean {
        val phoneRegex = "^1[3-9]\\d{9}$"
        return phone.matches(phoneRegex.toRegex())
    }
    
    /**
     * 颜色渐变计算
     */
    fun interpolateColor(
        startColor: androidx.compose.ui.graphics.Color,
        endColor: androidx.compose.ui.graphics.Color,
        fraction: Float
    ): androidx.compose.ui.graphics.Color {
        val clampedFraction = fraction.coerceIn(0f, 1f)
        
        val startRed = startColor.red
        val startGreen = startColor.green
        val startBlue = startColor.blue
        val startAlpha = startColor.alpha
        
        val endRed = endColor.red
        val endGreen = endColor.green
        val endBlue = endColor.blue
        val endAlpha = endColor.alpha
        
        val red = startRed + (endRed - startRed) * clampedFraction
        val green = startGreen + (endGreen - startGreen) * clampedFraction
        val blue = startBlue + (endBlue - startBlue) * clampedFraction
        val alpha = startAlpha + (endAlpha - startAlpha) * clampedFraction
        
        return androidx.compose.ui.graphics.Color(red, green, blue, alpha)
    }
    
    /**
     * 健康评分计算
     */
    fun calculateHealthScore(
        systolic: Int,
        diastolic: Int,
        heartRate: Int,
        age: Int = 30
    ): Int {
        var score = 100
        
        // 血压评分
        when {
            systolic < 90 || diastolic < 60 -> score -= 30 // 低血压
            systolic <= 120 && diastolic <= 80 -> score += 0 // 正常
            systolic <= 130 && diastolic <= 80 -> score -= 10 // 高血压前期
            systolic <= 140 || diastolic <= 90 -> score -= 20 // 1期高血压
            systolic <= 180 || diastolic <= 120 -> score -= 40 // 2期高血压
            else -> score -= 60 // 高血压危象
        }
        
        // 心率评分
        val normalHeartRate = when {
            age < 30 -> 60..100
            age < 50 -> 60..95
            else -> 60..90
        }
        
        when {
            heartRate < normalHeartRate.first -> score -= 15
            heartRate in normalHeartRate -> score += 0
            heartRate > normalHeartRate.last -> score -= 20
        }
        
        return score.coerceIn(0, 100)
    }
    
    /**
     * 数据摘要生成
     */
    fun generateSummary(
        totalRecords: Int,
        averageSystolic: Double,
        averageDiastolic: Double,
        averageHeartRate: Double,
        dateRange: String
    ): String {
        return buildString {
            append("在${dateRange}期间，")
            append("共记录了${totalRecords}次血压测量。")
            append("平均血压为${averageSystolic.toInt()}/${averageDiastolic.toInt()} mmHg，")
            append("平均心率为${averageHeartRate.toInt()} bpm。")
        }
    }
    
    /**
     * 安全字符串转换
     */
    fun String?.toIntSafely(defaultValue: Int = 0): Int {
        return this?.toIntOrNull() ?: defaultValue
    }
    
    fun String?.toDoubleSafely(defaultValue: Double = 0.0): Double {
        return this?.toDoubleOrNull() ?: defaultValue
    }
    
    /**
     * 列表安全访问
     */
    fun <T> List<T>.safeGet(index: Int): T? {
        return if (index in 0 until size) this[index] else null
    }
    
    /**
     * Map安全合并
     */
    fun <K, V> Map<K, V>.safeMerge(
        other: Map<K, V>,
        merger: (V, V) -> V
    ): Map<K, V> {
        val result = this.toMutableMap()
        other.forEach { (key, value) ->
            result[key] = result[key]?.let { merger(it, value) } ?: value
        }
        return result
    }
}