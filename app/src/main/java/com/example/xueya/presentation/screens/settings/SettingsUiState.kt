package com.example.xueya.presentation.screens.settings

import com.example.xueya.domain.model.LanguageMode
import com.example.xueya.domain.model.ThemeMode
import com.example.xueya.domain.usecase.GenerateTestDataUseCase

/**
 * 设置界面UI状态
 */
data class SettingsUiState(
    // 用户偏好设置
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val languageMode: LanguageMode = LanguageMode.ZH,
    val notificationsEnabled: Boolean = true,
    val reminderTime: String = "08:00",
    val reminderDays: Set<Int> = setOf(1, 2, 3, 4, 5, 6, 7), // 1-7 代表周一到周日
    val dynamicColorEnabled: Boolean = true,
    
    // 应用信息
    val appVersion: String = "1.0.0",
    val totalRecordsCount: Int = 0,
    
    // UI 状态
    val isLoading: Boolean = false,
    val showDeleteConfirmDialog: Boolean = false,
    val showThemeDialog: Boolean = false,
    val showLanguageDialog: Boolean = false,
    val showTimePickerDialog: Boolean = false,
    val showReminderDaysDialog: Boolean = false,
    
    // 操作结果
    val exportResult: ExportState = ExportState.Idle,
    val clearDataResult: ClearDataState = ClearDataState.Idle,
    val message: String? = null,
    val messageResId: Int? = null,
    
    // 测试数据生成
    val isGeneratingTestData: Boolean = false,
    val testDataGenerationResult: GenerateTestDataUseCase.GenerateResult? = null
)

/**
 * 导出状态
 */
sealed class ExportState {
    object Idle : ExportState()
    object Exporting : ExportState()
    data class Success(val filePath: String) : ExportState()
    data class Error(val message: String) : ExportState()
}

/**
 * 清空数据状态
 */
sealed class ClearDataState {
    object Idle : ClearDataState()
    object Clearing : ClearDataState()
    object Success : ClearDataState()
    data class Error(val message: String) : ClearDataState()
}