package com.example.xueya.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.xueya.domain.model.LanguageMode
import com.example.xueya.domain.model.ThemeMode
import com.example.xueya.domain.model.UserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

// 创建DataStore实例
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * 用户偏好设置数据存储管理器
 * 使用DataStore来持久化用户偏好设置
 */
@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        // 偏好设置键
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val LANGUAGE_MODE = stringPreferencesKey("language_mode")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val REMINDER_TIME = stringPreferencesKey("reminder_time")
        val REMINDER_DAYS = stringSetPreferencesKey("reminder_days")
        val DYNAMIC_COLOR_ENABLED = booleanPreferencesKey("dynamic_color_enabled")
    }

    /**
     * 获取用户偏好设置流
     */
    val userPreferences: Flow<UserPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            UserPreferences(
                themeMode = try {
                    ThemeMode.valueOf(
                        preferences[THEME_MODE] ?: ThemeMode.SYSTEM.name
                    )
                } catch (e: IllegalArgumentException) {
                    ThemeMode.SYSTEM
                },
                languageMode = try {
                    LanguageMode.valueOf(
                        preferences[LANGUAGE_MODE] ?: LanguageMode.ZH.name
                    )
                } catch (e: IllegalArgumentException) {
                    LanguageMode.ZH
                },
                notificationsEnabled = preferences[NOTIFICATIONS_ENABLED] ?: true,
                reminderTime = preferences[REMINDER_TIME] ?: "08:00",
                reminderDays = preferences[REMINDER_DAYS]?.map { it.toInt() }?.toSet() 
                    ?: setOf(1, 2, 3, 4, 5, 6, 7),
                dynamicColorEnabled = preferences[DYNAMIC_COLOR_ENABLED] ?: true
            )
        }

    /**
     * 更新主题模式
     */
    suspend fun updateThemeMode(themeMode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE] = themeMode.name
        }
    }

    /**
     * 更新语言模式
     */
    suspend fun updateLanguageMode(languageMode: LanguageMode) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_MODE] = languageMode.name
        }
    }

    /**
     * 更新通知开关
     */
    suspend fun updateNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
    }

    /**
     * 更新提醒时间
     */
    suspend fun updateReminderTime(time: String) {
        context.dataStore.edit { preferences ->
            preferences[REMINDER_TIME] = time
        }
    }

    /**
     * 更新提醒日期（周几）
     */
    suspend fun updateReminderDays(days: Set<Int>) {
        context.dataStore.edit { preferences ->
            preferences[REMINDER_DAYS] = days.map { it.toString() }.toSet()
        }
    }

    /**
     * 更新动态颜色开关
     */
    suspend fun updateDynamicColorEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DYNAMIC_COLOR_ENABLED] = enabled
        }
    }

    /**
     * 清除所有偏好设置
     */
    suspend fun clearAllPreferences() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}