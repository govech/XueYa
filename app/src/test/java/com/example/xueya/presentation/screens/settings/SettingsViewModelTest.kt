package com.example.xueya.presentation.screens.settings

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import com.example.xueya.data.preferences.UserPreferencesDataStore
import com.example.xueya.domain.model.ThemeMode
import com.example.xueya.domain.model.LanguageMode
import com.example.xueya.domain.model.UserPreferences
import kotlinx.coroutines.flow.flowOf

/**
 * 设置界面ViewModel测试
 */
class SettingsViewModelTest {

    private val mockUserPreferencesDataStore: UserPreferencesDataStore = mock()
    private val mockBloodPressureRepository = mock<com.example.xueya.domain.repository.BloodPressureRepository>()
    private val mockUpdateThemeUseCase = mock<com.example.xueya.domain.usecase.UpdateThemeUseCase>()
    private val mockUpdateLanguageUseCase = mock<com.example.xueya.domain.usecase.UpdateLanguageUseCase>()
    private val mockUpdateReminderUseCase = mock<com.example.xueya.domain.usecase.UpdateReminderUseCase>()
    private val mockExportDataUseCase = mock<com.example.xueya.domain.usecase.ExportDataUseCase>()
    private val mockClearAllDataUseCase = mock<com.example.xueya.domain.usecase.ClearAllDataUseCase>()

    @Test
    fun `设置界面初始状态应该正确`() = runTest {
        // Given
        val testPreferences = UserPreferences(
            themeMode = ThemeMode.SYSTEM,
            languageMode = LanguageMode.ZH,
            notificationsEnabled = true,
            reminderTime = "08:00",
            reminderDays = setOf(1, 2, 3, 4, 5, 6, 7),
            dynamicColorEnabled = true
        )
        
        whenever(mockUserPreferencesDataStore.userPreferences)
            .thenReturn(flowOf(testPreferences))
        
        whenever(mockBloodPressureRepository.getAllRecords())
            .thenReturn(flowOf(emptyList()))

        // When
        val viewModel = SettingsViewModel(
            mockUserPreferencesDataStore,
            mockBloodPressureRepository,
            mockUpdateThemeUseCase,
            mockUpdateLanguageUseCase,
            mockUpdateReminderUseCase,
            mockExportDataUseCase,
            mockClearAllDataUseCase
        )

        // Then
        val uiState = viewModel.uiState.first()
        assertEquals(ThemeMode.SYSTEM, uiState.themeMode)
        assertEquals(LanguageMode.ZH, uiState.languageMode)
        assertTrue(uiState.notificationsEnabled)
        assertEquals("08:00", uiState.reminderTime)
        assertEquals(setOf(1, 2, 3, 4, 5, 6, 7), uiState.reminderDays)
        assertTrue(uiState.dynamicColorEnabled)
        assertEquals(0, uiState.totalRecordsCount)
    }

    @Test
    fun `主题对话框状态管理应该正确`() = runTest {
        // Given
        whenever(mockUserPreferencesDataStore.userPreferences)
            .thenReturn(flowOf(UserPreferences()))
        whenever(mockBloodPressureRepository.getAllRecords())
            .thenReturn(flowOf(emptyList()))

        val viewModel = SettingsViewModel(
            mockUserPreferencesDataStore,
            mockBloodPressureRepository,
            mockUpdateThemeUseCase,
            mockUpdateLanguageUseCase,
            mockUpdateReminderUseCase,
            mockExportDataUseCase,
            mockClearAllDataUseCase
        )

        // When
        viewModel.showThemeDialog()

        // Then
        assertTrue(viewModel.uiState.first().showThemeDialog)

        // When
        viewModel.hideThemeDialog()

        // Then
        assertFalse(viewModel.uiState.first().showThemeDialog)
    }
}