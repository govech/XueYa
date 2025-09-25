package com.example.xueya.domain.usecase

import com.example.xueya.data.preferences.UserPreferencesDataStore
import com.example.xueya.domain.model.ThemeMode
import javax.inject.Inject

/**
 * 更新主题模式用例
 * 
 * 处理更新应用主题模式的业务逻辑
 * 作为业务逻辑层的核心组件，负责协调用户偏好设置的更新操作
 * 使用@Inject注解支持Hilt依赖注入
 * 
 * @param userPreferencesDataStore 用户偏好设置数据存储，通过构造函数注入
 */
class UpdateThemeUseCase @Inject constructor(
    private val userPreferencesDataStore: UserPreferencesDataStore
) {
    /**
     * 执行更新主题模式的业务逻辑
     * 
     * 将新的主题模式保存到用户偏好设置中
     * 
     * @param themeMode 新的主题模式
     */
    suspend operator fun invoke(themeMode: ThemeMode) {
        userPreferencesDataStore.updateThemeMode(themeMode)
    }
}