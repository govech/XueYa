package com.example.xueya.presentation.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xueya.R
import com.example.xueya.data.preferences.UserPreferencesDataStore
import com.example.xueya.domain.model.LanguageMode
import com.example.xueya.domain.model.ThemeMode
import com.example.xueya.domain.repository.BloodPressureRepository
import com.example.xueya.domain.usecase.*
import com.example.xueya.domain.generator.BloodPressureTestDataGenerator
import com.example.xueya.presentation.utils.AppRestartUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 设置界面ViewModel
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesDataStore: UserPreferencesDataStore,
    private val bloodPressureRepository: BloodPressureRepository,
    private val updateThemeUseCase: UpdateThemeUseCase,
    private val updateLanguageUseCase: UpdateLanguageUseCase,
    private val updateReminderUseCase: UpdateReminderUseCase,
    private val exportDataUseCase: ExportDataUseCase,
    private val clearAllDataUseCase: ClearAllDataUseCase,
    private val generateTestDataUseCase: GenerateTestDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        // 监听用户偏好设置变化
        viewModelScope.launch {
            userPreferencesDataStore.userPreferences.collect { preferences ->
                _uiState.update { currentState ->
                    currentState.copy(
                        themeMode = preferences.themeMode,
                        languageMode = preferences.languageMode,
                        notificationsEnabled = preferences.notificationsEnabled,
                        reminderTime = preferences.reminderTime,
                        reminderDays = preferences.reminderDays,
                        dynamicColorEnabled = preferences.dynamicColorEnabled
                    )
                }
            }
        }

        // 监听血压记录总数
        viewModelScope.launch {
            bloodPressureRepository.getAllRecords().collect { records ->
                _uiState.update { currentState ->
                    currentState.copy(totalRecordsCount = records.size)
                }
            }
        }
    }

    // === 主题设置 ===
    fun updateThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            updateThemeUseCase(themeMode)
            showMessage(R.string.toast_theme_changed)
        }
    }

    fun showThemeDialog() {
        _uiState.update { it.copy(showThemeDialog = true) }
    }

    fun hideThemeDialog() {
        _uiState.update { it.copy(showThemeDialog = false) }
    }

    // === 语言设置 ===
    fun updateLanguageMode(languageMode: LanguageMode, context: Context) {
        viewModelScope.launch {
            updateLanguageUseCase(languageMode)
            showMessage(R.string.toast_language_changed)
            // 重启应用以应用新的语言设置
            AppRestartUtil.restartApp(context)
        }
    }

    fun showLanguageDialog() {
        _uiState.update { it.copy(showLanguageDialog = true) }
    }

    fun hideLanguageDialog() {
        _uiState.update { it.copy(showLanguageDialog = false) }
    }

    // === 动态颜色设置 ===
    fun updateDynamicColorEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesDataStore.updateDynamicColorEnabled(enabled)
        }
    }

    // === 提醒设置 ===
    fun updateNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            updateReminderUseCase.updateNotificationsEnabled(enabled)
        }
    }

    fun updateReminderTime(time: String) {
        viewModelScope.launch {
            updateReminderUseCase.updateReminderTime(time)
            showMessage(R.string.toast_reminder_set)
        }
    }

    fun updateReminderDays(days: Set<Int>) {
        viewModelScope.launch {
            updateReminderUseCase.updateReminderDays(days)
            showMessage(R.string.toast_reminder_set)
        }
    }

    fun showTimePickerDialog() {
        _uiState.update { it.copy(showTimePickerDialog = true) }
    }

    fun hideTimePickerDialog() {
        _uiState.update { it.copy(showTimePickerDialog = false) }
    }

    fun showReminderDaysDialog() {
        _uiState.update { it.copy(showReminderDaysDialog = true) }
    }

    fun hideReminderDaysDialog() {
        _uiState.update { it.copy(showReminderDaysDialog = false) }
    }

    // === 数据管理 ===
    fun exportData() {
        viewModelScope.launch {
            _uiState.update { it.copy(exportResult = ExportState.Exporting) }
            
            val result = exportDataUseCase(_uiState.value.languageMode)
            
            when (result) {
                is ExportResult.Success -> {
                    _uiState.update { 
                        it.copy(exportResult = ExportState.Success(result.filePath))
                    }
                    showMessage(R.string.toast_export_success)
                }
                is ExportResult.Error -> {
                    _uiState.update { 
                        it.copy(exportResult = ExportState.Error(result.message))
                    }
                    showMessage(R.string.toast_export_failed)
                }
            }
        }
    }

    fun showDeleteConfirmDialog() {
        _uiState.update { it.copy(showDeleteConfirmDialog = true) }
    }

    fun hideDeleteConfirmDialog() {
        _uiState.update { it.copy(showDeleteConfirmDialog = false) }
    }

    fun clearAllData() {
        viewModelScope.launch {
            _uiState.update { it.copy(clearDataResult = ClearDataState.Clearing) }
            
            val result = clearAllDataUseCase()
            
            when (result) {
                is ClearDataResult.Success -> {
                    _uiState.update { 
                        it.copy(
                            clearDataResult = ClearDataState.Success,
                            showDeleteConfirmDialog = false
                        )
                    }
                    showMessage(R.string.toast_clear_success)
                }
                is ClearDataResult.Error -> {
                    _uiState.update { 
                        it.copy(clearDataResult = ClearDataState.Error(result.message))
                    }
                    showMessage(R.string.toast_clear_failed)
                }
            }
        }
    }

    // === 测试数据生成 ===
    fun generateTestData(
        timeRange: BloodPressureTestDataGenerator.TimeRange,
        mode: BloodPressureTestDataGenerator.GenerationMode,
        measurementsPerDay: Int
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isGeneratingTestData = true) }
            
            val result = generateTestDataUseCase.generateAndSaveTestData(
                timeRange = timeRange,
                mode = mode,
                measurementsPerDay = measurementsPerDay
            )
            
            _uiState.update { 
                it.copy(
                    isGeneratingTestData = false,
                    testDataGenerationResult = result
                )
            }
            
            when (result) {
                is GenerateTestDataUseCase.GenerateResult.Success -> {
                    showMessage("测试数据生成成功！")
                }
                is GenerateTestDataUseCase.GenerateResult.Error -> {
                    showMessage("测试数据生成失败：${result.message}")
                }
            }
        }
    }
    
    fun clearAndGenerateTestData(
        timeRange: BloodPressureTestDataGenerator.TimeRange,
        mode: BloodPressureTestDataGenerator.GenerationMode,
        measurementsPerDay: Int
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isGeneratingTestData = true) }
            
            val result = generateTestDataUseCase.clearAndGenerateTestData(
                timeRange = timeRange,
                mode = mode,
                measurementsPerDay = measurementsPerDay
            )
            
            _uiState.update { 
                it.copy(
                    isGeneratingTestData = false,
                    testDataGenerationResult = result
                )
            }
            
            when (result) {
                is GenerateTestDataUseCase.GenerateResult.Success -> {
                    showMessage("数据已清空并生成新的测试数据！")
                }
                is GenerateTestDataUseCase.GenerateResult.Error -> {
                    showMessage("清空并生成测试数据失败：${result.message}")
                }
            }
        }
    }
    
    fun resetTestDataGenerationState() {
        _uiState.update { it.copy(testDataGenerationResult = null) }
    }

    // === 通用方法 ===
    fun resetExportState() {
        _uiState.update { it.copy(exportResult = ExportState.Idle) }
    }

    fun resetClearDataState() {
        _uiState.update { it.copy(clearDataResult = ClearDataState.Idle) }
    }

    private fun showMessage(message: String) {
        _uiState.update { it.copy(message = message, messageResId = null) }
        // 3秒后清除消息
        viewModelScope.launch {
            kotlinx.coroutines.delay(3000)
            clearMessage()
        }
    }

    private fun showMessage(messageResId: Int) {
        _uiState.update { it.copy(messageResId = messageResId, message = null) }
        // 3秒后清除消息
        viewModelScope.launch {
            kotlinx.coroutines.delay(3000)
            clearMessage()
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }
}