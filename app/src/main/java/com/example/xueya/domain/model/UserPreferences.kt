package com.example.xueya.domain.model

/**
 * 用户偏好设置数据模型
 * 
 * 用于存储和管理用户的个性化设置
 * 包括主题模式、语言、通知提醒等配置信息
 * 
 * @property themeMode 主题模式（系统、浅色、深色）
 * @property languageMode 语言模式（中文、英文）
 * @property notificationsEnabled 是否启用通知
 * @property reminderTime 提醒时间（24小时格式，如 "08:00"）
 * @property reminderDays 提醒日期（1-7 代表周一到周日）
 * @property dynamicColorEnabled 是否启用动态颜色
 */
data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val languageMode: LanguageMode = LanguageMode.ZH,
    val notificationsEnabled: Boolean = true,
    val reminderTime: String = "08:00", // 24小时格式，如 "08:00"
    val reminderDays: Set<Int> = setOf(1, 2, 3, 4, 5, 6, 7), // 1-7 代表周一到周日
    val dynamicColorEnabled: Boolean = true // 是否启用动态颜色
)