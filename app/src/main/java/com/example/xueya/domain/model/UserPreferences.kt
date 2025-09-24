package com.example.xueya.domain.model

/**
 * 用户偏好设置数据模型
 */
data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val languageMode: LanguageMode = LanguageMode.ZH,
    val notificationsEnabled: Boolean = true,
    val reminderTime: String = "08:00", // 24小时格式，如 "08:00"
    val reminderDays: Set<Int> = setOf(1, 2, 3, 4, 5, 6, 7), // 1-7 代表周一到周日
    val dynamicColorEnabled: Boolean = true // 是否启用动态颜色
)