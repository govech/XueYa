package com.example.xueya.data.database

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Room数据库类型转换器
 */
class Converters {
    
    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): String? {
        return dateTime?.format(dateTimeFormatter)
    }

    @TypeConverter
    fun toLocalDateTime(dateTimeString: String?): LocalDateTime? {
        return dateTimeString?.let { 
            LocalDateTime.parse(it, dateTimeFormatter) 
        }
    }

    @TypeConverter
    fun fromStringList(tags: List<String>): String {
        return tags.joinToString(",")
    }

    @TypeConverter
    fun toStringList(tagsString: String): List<String> {
        return if (tagsString.isBlank()) {
            emptyList()
        } else {
            tagsString.split(",").map { it.trim() }
        }
    }
}