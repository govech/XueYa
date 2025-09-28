package com.example.xueya.presentation.screens.settings

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.xueya.R
import com.example.xueya.domain.model.LanguageMode
import com.example.xueya.domain.model.ThemeMode
import com.example.xueya.presentation.components.settings.*
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * 设置界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onNavigateToAiTest: () -> Unit = {},
    onNavigateToDietRecommendations: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    // 时间选择器对话框状态
    val timeDialogState = rememberMaterialDialogState()
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // 顶部栏
        TopAppBar(
            title = {
                Text(text = stringResource(R.string.settings_title))
            }
        )
        
        // 设置内容
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 饮食建议卡片 - 放在最顶部
            DietRecommendationsCard(
                onClick = onNavigateToDietRecommendations,
                modifier = Modifier.fillMaxWidth()
            )

            // 提醒设置
            SettingSectionHeader(title = stringResource(R.string.settings_reminder_section))
            SettingsGroup {
                SwitchSettingItem(
                    title = stringResource(R.string.settings_reminder_enable),
                    description = stringResource(R.string.settings_reminder_enable_desc),
                    icon = Icons.Default.Notifications,
                    checked = uiState.notificationsEnabled,
                    onCheckedChange = viewModel::updateNotificationsEnabled
                )
                
                SettingItem(
                    title = stringResource(R.string.settings_reminder_time),
                    description = stringResource(R.string.settings_reminder_time_desc),
                    icon = Icons.Default.Settings,
                    value = uiState.reminderTime,
                    enabled = uiState.notificationsEnabled,
                    onClick = { timeDialogState.show() }
                )
                
                SettingItem(
                    title = stringResource(R.string.settings_reminder_days),
                    description = stringResource(R.string.settings_reminder_days_desc),
                    icon = Icons.Default.DateRange,
                    value = if (uiState.languageMode == LanguageMode.EN) "${uiState.reminderDays.size} days" else "${uiState.reminderDays.size} 天",
                    enabled = uiState.notificationsEnabled,
                    onClick = viewModel::showReminderDaysDialog
                )
            }
            
            // 外观设置
            SettingSectionHeader(title = stringResource(R.string.settings_appearance_section))
            SettingsGroup {
                SettingItem(
                    title = stringResource(R.string.settings_theme_mode),
                    description = stringResource(R.string.settings_theme_mode_desc),
                    icon = Icons.Default.Settings,
                    value = when (uiState.themeMode) {
                        ThemeMode.SYSTEM -> stringResource(R.string.theme_system)
                        ThemeMode.LIGHT -> stringResource(R.string.theme_light)
                        ThemeMode.DARK -> stringResource(R.string.theme_dark)
                    },
                    onClick = viewModel::showThemeDialog
                )
                
                SwitchSettingItem(
                    title = stringResource(R.string.settings_dynamic_color),
                    description = stringResource(R.string.settings_dynamic_color_desc),
                    icon = Icons.Default.Settings,
                    checked = uiState.dynamicColorEnabled,
                    onCheckedChange = viewModel::updateDynamicColorEnabled
                )
                
                SettingItem(
                    title = stringResource(R.string.settings_language),
                    description = stringResource(R.string.settings_language_desc),
                    icon = Icons.Default.Settings,
                    value = when (uiState.languageMode) {
                        LanguageMode.ZH -> stringResource(R.string.language_chinese)
                        LanguageMode.EN -> stringResource(R.string.language_english)
                    },
                    onClick = viewModel::showLanguageDialog
                )
            }
            
            // 数据管理
            SettingSectionHeader(title = stringResource(R.string.settings_data_section))
            SettingsGroup {
                SettingItem(
                    title = stringResource(R.string.settings_export_data),
                    description = stringResource(R.string.settings_export_data_desc),
                    icon = Icons.Default.Settings,
                    value = if (uiState.totalRecordsCount > 0) {
                    if (uiState.languageMode == LanguageMode.EN) "${uiState.totalRecordsCount} records" else "${uiState.totalRecordsCount} 条记录"
                } else null,
                    enabled = uiState.totalRecordsCount > 0,
                    onClick = viewModel::exportData
                )
                
                SettingItem(
                    title = stringResource(R.string.settings_clear_data),
                    description = stringResource(R.string.settings_clear_data_desc),
                    icon = Icons.Default.Delete,
                    value = if (uiState.totalRecordsCount > 0) {
                    if (uiState.languageMode == LanguageMode.EN) "${uiState.totalRecordsCount} records" else "${uiState.totalRecordsCount} 条记录"
                } else {
                    if (uiState.languageMode == LanguageMode.EN) "No data" else "无数据"
                },
                    enabled = uiState.totalRecordsCount > 0,
                    onClick = viewModel::showDeleteConfirmDialog
                )
            }
            
            // 关于应用
            SettingSectionHeader(title = stringResource(R.string.settings_about_section))
            SettingsGroup {
                SettingItem(
                    title = stringResource(R.string.settings_app_version),
                    description = null,
                    icon = Icons.Default.Info,
                    value = uiState.appVersion,
                    showChevron = false,
                    onClick = { }
                )
                
                SettingItem(
                    title = stringResource(R.string.settings_developer_info),
                    description = stringResource(R.string.settings_developer_name),
                    icon = Icons.Default.Build,
                    showChevron = false,
                    onClick = { }
                )
                
                SettingItem(
                    title = stringResource(R.string.ai_function_test),
                    description = stringResource(R.string.ai_function_test_description),
                    icon = Icons.Default.Star,
                    onClick = onNavigateToAiTest
                )
            }
        }
    }
    
    // 对话框
    if (uiState.showThemeDialog) {
        ThemeSelectionDialog(
            currentTheme = uiState.themeMode,
            onThemeSelected = viewModel::updateThemeMode,
            onDismiss = viewModel::hideThemeDialog
        )
    }
    
    if (uiState.showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = uiState.languageMode,
            onLanguageSelected = { languageMode -> 
                viewModel.updateLanguageMode(languageMode, context)
            },
            onDismiss = viewModel::hideLanguageDialog
        )
    }
    
    if (uiState.showDeleteConfirmDialog) {
        DeleteConfirmationDialog(
            recordCount = uiState.totalRecordsCount,
            onConfirm = viewModel::clearAllData,
            onDismiss = viewModel::hideDeleteConfirmDialog
        )
    }
    
    if (uiState.showReminderDaysDialog) {
        ReminderDaysSelectionDialog(
            selectedDays = uiState.reminderDays,
            onDaysSelected = viewModel::updateReminderDays,
            onDismiss = viewModel::hideReminderDaysDialog
        )
    }
    
    // 时间选择器对话框
    MaterialDialog(
        dialogState = timeDialogState,
        buttons = {
            positiveButton(text = stringResource(R.string.ok))
            negativeButton(text = stringResource(R.string.cancel))
        }
    ) {
        timepicker(
            initialTime = try {
                val parts = uiState.reminderTime.split(":")
                LocalTime.of(parts[0].toInt(), parts[1].toInt())
            } catch (e: Exception) {
                LocalTime.of(8, 0)
            },
            title = stringResource(R.string.settings_reminder_time)
        ) { time ->
            val timeString = time.format(DateTimeFormatter.ofPattern("HH:mm"))
            viewModel.updateReminderTime(timeString)
        }
    }
    
    // 消息显示
    uiState.message?.let { message ->
        LaunchedEffect(message) {
            // 这里可以显示 Snackbar 或 Toast
            // 现在先简单处理，实际项目中可以添加 SnackbarHost
        }
    }
    
    uiState.messageResId?.let { resId ->
        LaunchedEffect(resId) {
            // 这里可以显示 Snackbar 或 Toast
            // 现在先简单处理，实际项目中可以添加 SnackbarHost
        }
    }
    
    // 加载状态处理
    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

/**
 * 饮食建议卡片 - 渐变色背景
 */
@Composable
private fun DietRecommendationsCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF4CAF50), // Material Green 500
            Color(0xFF8BC34A)  // Material Light Green 400
        )
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradientBrush)
                .padding(20.dp)
                .clickable { onClick() }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // 图标背景
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier.padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = stringResource(id = R.string.settings_diet_card_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = stringResource(id = R.string.settings_diet_card_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                // 操作按钮
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.9f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.settings_diet_card_action),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}