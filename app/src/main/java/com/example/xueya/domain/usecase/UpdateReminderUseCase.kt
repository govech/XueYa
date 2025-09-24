package com.example.xueya.domain.usecase

import com.example.xueya.data.preferences.UserPreferencesDataStore
import javax.inject.Inject

/**
 * 更新提醒设置用例
 */
class UpdateReminderUseCase @Inject constructor(
    private val userPreferencesDataStore: UserPreferencesDataStore
) {
    /**
     * 启用/禁用提醒
     */
    suspend fun updateNotificationsEnabled(enabled: Boolean) {
        userPreferencesDataStore.updateNotificationsEnabled(enabled)
    }

    /**
     * 更新提醒时间
     */
    suspend fun updateReminderTime(time: String) {
        userPreferencesDataStore.updateReminderTime(time)
    }

    /**
     * 更新提醒日期（星期几）
     */
    suspend fun updateReminderDays(days: Set<Int>) {
        userPreferencesDataStore.updateReminderDays(days)
    }
}

/**
 * 提醒设置结果
 */
sealed class ReminderResult {
    object Success : ReminderResult()
    data class Error(val message: String) : ReminderResult()
}