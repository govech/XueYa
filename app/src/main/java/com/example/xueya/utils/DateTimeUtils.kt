package com.example.xueya.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

/**
 * 日期时间工具类
 */
object DateTimeUtils {
    
    // 日期时间格式化器
    private val dateFormatter = DateTimeFormatter.ofPattern(Constants.DateTime.DATE_FORMAT)
    private val timeFormatter = DateTimeFormatter.ofPattern(Constants.DateTime.TIME_FORMAT)
    private val dateTimeFormatter = DateTimeFormatter.ofPattern(Constants.DateTime.DATETIME_FORMAT)
    private val displayDateFormatter = DateTimeFormatter.ofPattern(Constants.DateTime.DISPLAY_DATE_FORMAT, Locale.CHINA)
    private val displayTimeFormatter = DateTimeFormatter.ofPattern(Constants.DateTime.DISPLAY_TIME_FORMAT)
    private val displayDateTimeFormatter = DateTimeFormatter.ofPattern(Constants.DateTime.DISPLAY_DATETIME_FORMAT, Locale.CHINA)
    
    /**
     * 格式化日期 (yyyy-MM-dd)
     */
    fun formatDate(dateTime: LocalDateTime): String {
        return dateTime.format(dateFormatter)
    }
    
    /**
     * 格式化时间 (HH:mm)
     */
    fun formatTime(dateTime: LocalDateTime): String {
        return dateTime.format(timeFormatter)
    }
    
    /**
     * 格式化日期时间 (yyyy-MM-dd HH:mm)
     */
    fun formatDateTime(dateTime: LocalDateTime): String {
        return dateTime.format(dateTimeFormatter)
    }
    
    /**
     * 格式化显示用日期 (MM月dd日)
     */
    fun formatDisplayDate(dateTime: LocalDateTime): String {
        return dateTime.format(displayDateFormatter)
    }
    
    /**
     * 格式化显示用时间 (HH:mm)
     */
    fun formatDisplayTime(dateTime: LocalDateTime): String {
        return dateTime.format(displayTimeFormatter)
    }
    
    /**
     * 格式化显示用日期时间 (MM月dd日 HH:mm)
     */
    fun formatDisplayDateTime(dateTime: LocalDateTime): String {
        return dateTime.format(displayDateTimeFormatter)
    }
    
    /**
     * 获取相对时间描述
     */
    fun getRelativeTimeDescription(dateTime: LocalDateTime): String {
        val now = LocalDateTime.now()
        val minutes = ChronoUnit.MINUTES.between(dateTime, now)
        val hours = ChronoUnit.HOURS.between(dateTime, now)
        val days = ChronoUnit.DAYS.between(dateTime, now)
        
        return when {
            minutes < 1 -> "刚刚"
            minutes < 60 -> "${minutes}分钟前"
            hours < 24 -> "${hours}小时前"
            days == 1L -> "昨天"
            days < 7 -> "${days}天前"
            days < 30 -> "${days / 7}周前"
            days < 365 -> "${days / 30}个月前"
            else -> "${days / 365}年前"
        }
    }
    
    /**
     * 判断是否为今天
     */
    fun isToday(dateTime: LocalDateTime): Boolean {
        val today = LocalDateTime.now().toLocalDate()
        return dateTime.toLocalDate() == today
    }
    
    /**
     * 判断是否为昨天
     */
    fun isYesterday(dateTime: LocalDateTime): Boolean {
        val yesterday = LocalDateTime.now().minusDays(1).toLocalDate()
        return dateTime.toLocalDate() == yesterday
    }
    
    /**
     * 判断是否为本周
     */
    fun isThisWeek(dateTime: LocalDateTime): Boolean {
        val now = LocalDateTime.now()
        val startOfWeek = now.minusDays(now.dayOfWeek.value - 1L).toLocalDate().atStartOfDay()
        val endOfWeek = startOfWeek.plusDays(6).toLocalDate().atTime(23, 59, 59)
        return dateTime.isAfter(startOfWeek) && dateTime.isBefore(endOfWeek)
    }
    
    /**
     * 判断是否为本月
     */
    fun isThisMonth(dateTime: LocalDateTime): Boolean {
        val now = LocalDateTime.now()
        return dateTime.year == now.year && dateTime.month == now.month
    }
    
    /**
     * 获取一天的开始时间
     */
    fun getStartOfDay(dateTime: LocalDateTime): LocalDateTime {
        return dateTime.toLocalDate().atStartOfDay()
    }
    
    /**
     * 获取一天的结束时间
     */
    fun getEndOfDay(dateTime: LocalDateTime): LocalDateTime {
        return dateTime.toLocalDate().atTime(23, 59, 59)
    }
    
    /**
     * 获取一周的开始时间（周一）
     */
    fun getStartOfWeek(dateTime: LocalDateTime = LocalDateTime.now()): LocalDateTime {
        return dateTime.minusDays(dateTime.dayOfWeek.value - 1L).toLocalDate().atStartOfDay()
    }
    
    /**
     * 获取一周的结束时间（周日）
     */
    fun getEndOfWeek(dateTime: LocalDateTime = LocalDateTime.now()): LocalDateTime {
        val startOfWeek = getStartOfWeek(dateTime)
        return startOfWeek.plusDays(6).toLocalDate().atTime(23, 59, 59)
    }
    
    /**
     * 获取一个月的开始时间
     */
    fun getStartOfMonth(dateTime: LocalDateTime = LocalDateTime.now()): LocalDateTime {
        return dateTime.toLocalDate().withDayOfMonth(1).atStartOfDay()
    }
    
    /**
     * 获取一个月的结束时间
     */
    fun getEndOfMonth(dateTime: LocalDateTime = LocalDateTime.now()): LocalDateTime {
        return dateTime.toLocalDate().withDayOfMonth(dateTime.toLocalDate().lengthOfMonth()).atTime(23, 59, 59)
    }
    
    /**
     * 获取智能的时间显示
     * 今天显示时间，昨天显示"昨天 时间"，其他显示日期
     */
    fun getSmartTimeDisplay(dateTime: LocalDateTime): String {
        return when {
            isToday(dateTime) -> formatDisplayTime(dateTime)
            isYesterday(dateTime) -> "昨天 ${formatDisplayTime(dateTime)}"
            else -> formatDisplayDateTime(dateTime)
        }
    }
    
    /**
     * 获取测量时间段描述
     */
    fun getMeasureTimePeriod(dateTime: LocalDateTime): String {
        val hour = dateTime.hour
        return when {
            hour in 5..8 -> "晨起"
            hour in 9..11 -> "上午"
            hour in 12..13 -> "中午"
            hour in 14..17 -> "下午"
            hour in 18..20 -> "傍晚"
            hour in 21..23 -> "晚上"
            else -> "深夜"
        }
    }
    
    /**
     * 解析日期字符串
     */
    fun parseDate(dateString: String): LocalDateTime? {
        return try {
            LocalDateTime.parse(dateString, dateTimeFormatter)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * 获取文件名用的日期格式
     */
    fun getFilenameTimestamp(dateTime: LocalDateTime = LocalDateTime.now()): String {
        val formatter = DateTimeFormatter.ofPattern(Constants.Export.DATE_FILENAME_FORMAT)
        return dateTime.format(formatter)
    }
}