package com.example.xueya.presentation.screens.add_record

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.xueya.domain.model.BloodPressureCategory
import java.time.format.DateTimeFormatter

/**
 * 添加记录界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecordScreen(
    onNavigateBack: () -> Unit,
    onRecordSaved: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddRecordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val aiParseState by viewModel.aiParseState.collectAsState()
    val voiceInputState by viewModel.voiceInputState.collectAsState()
    val scrollState = rememberScrollState()

    // 监听保存成功状态
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onRecordSaved()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("添加血压记录") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    // 暂时移除调试按钮
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = viewModel::saveRecord,
                modifier = Modifier.padding(16.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "保存"
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 错误提示
            uiState.error?.let { error ->
                ErrorCard(
                    error = error,
                    onDismiss = viewModel::clearError
                )
            }

            // 血压输入卡片
            BloodPressureInputCard(
                systolic = uiState.systolic,
                diastolic = uiState.diastolic,
                onSystolicChange = viewModel::updateSystolic,
                onDiastolicChange = viewModel::updateDiastolic,
                category = uiState.bloodPressureCategory,
                validationErrors = uiState.validationErrors.filter { 
                    it.contains("收缩压") || it.contains("舒张压") 
                },
                // AI 输入相关参数
                aiParseState = aiParseState,
                voiceInputState = voiceInputState,
                onAiTextInput = viewModel::parseBloodPressureText,
                onStartVoiceInput = viewModel::startVoiceInput,
                onStopVoiceInput = viewModel::stopVoiceInput
            )

            // 心率输入卡片
            HeartRateInputCard(
                heartRate = uiState.heartRate,
                onHeartRateChange = viewModel::updateHeartRate,
                validationErrors = uiState.validationErrors.filter { 
                    it.contains("心率") 
                }
            )

            // 测量时间卡片
            MeasureTimeCard(
                measureTime = uiState.measureTime,
                onShowDatePicker = viewModel::showDatePicker,
                onShowTimePicker = viewModel::showTimePicker
            )

            // 标签选择卡片
            TagSelectionCard(
                selectedTags = uiState.selectedTags,
                onTagToggle = viewModel::toggleTag
            )

            // 备注输入卡片
            NoteInputCard(
                note = uiState.note,
                onNoteChange = viewModel::updateNote
            )

            // 底部间距
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // 日期时间选择器
    if (uiState.showDatePicker) {
        DatePickerDialog(
            selectedDate = uiState.measureTime.toLocalDate(),
            onDateSelected = { date ->
                viewModel.updateMeasureTime(
                    uiState.measureTime.with(date)
                )
                viewModel.hideDatePicker()
            },
            onDismiss = viewModel::hideDatePicker
        )
    }

    if (uiState.showTimePicker) {
        TimePickerDialog(
            selectedTime = uiState.measureTime.toLocalTime(),
            onTimeSelected = { time ->
                viewModel.updateMeasureTime(
                    uiState.measureTime.with(time)
                )
                viewModel.hideTimePicker()
            },
            onDismiss = viewModel::hideTimePicker
        )
    }
}

/**
 * 错误卡片
 */
@Composable
private fun ErrorCard(
    error: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f)
            )
            
            TextButton(onClick = onDismiss) {
                Text("关闭")
            }
        }
    }
}

/**
 * 获取血压分类对应的颜色
 */
@Composable
private fun getCategoryColor(category: BloodPressureCategory): Color {
    return when (category) {
        BloodPressureCategory.NORMAL -> Color(0xFF4CAF50)
        BloodPressureCategory.ELEVATED -> Color(0xFFFF9800)
        BloodPressureCategory.HIGH_STAGE_1 -> Color(0xFFFF5722)
        BloodPressureCategory.HIGH_STAGE_2 -> Color(0xFFF44336)
        BloodPressureCategory.HYPERTENSIVE_CRISIS -> Color(0xFF9C27B0)
    }
}