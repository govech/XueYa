package com.example.xueya.domain.usecase

import com.example.xueya.data.preferences.UserPreferencesDataStore
import com.example.xueya.domain.model.LanguageMode
import javax.inject.Inject

/**
 * 更新语言模式用例
 */
class UpdateLanguageUseCase @Inject constructor(
    private val userPreferencesDataStore: UserPreferencesDataStore
) {
    suspend operator fun invoke(languageMode: LanguageMode) {
        userPreferencesDataStore.updateLanguageMode(languageMode)
    }
}