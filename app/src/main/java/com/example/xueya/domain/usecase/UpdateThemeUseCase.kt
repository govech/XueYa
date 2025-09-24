package com.example.xueya.domain.usecase

import com.example.xueya.data.preferences.UserPreferencesDataStore
import com.example.xueya.domain.model.ThemeMode
import javax.inject.Inject

/**
 * 更新主题模式用例
 */
class UpdateThemeUseCase @Inject constructor(
    private val userPreferencesDataStore: UserPreferencesDataStore
) {
    suspend operator fun invoke(themeMode: ThemeMode) {
        userPreferencesDataStore.updateThemeMode(themeMode)
    }
}