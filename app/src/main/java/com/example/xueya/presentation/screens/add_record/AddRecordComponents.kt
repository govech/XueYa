package com.example.xueya.presentation.screens.add_record

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.xueya.R
import com.example.xueya.domain.model.BloodPressureCategory
import com.example.xueya.domain.model.ai.AiParseState
import com.example.xueya.presentation.components.input.VoiceInputButton
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 血压输入卡片
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodPressureInputCard(
    systolic: String,
    diastolic: String,
    onSystolicChange: (String) -> Unit,
    onDiastolicChange: (String) -> Unit,
    category: BloodPressureCategory?,
    validationErrors: List<String>,
    // AI 输入相关参数
    aiParseState: AiParseState = AiParseState.Idle,
    voiceInputState: com.example.xueya.domain.usecase.VoiceInputUseCase.VoiceInputState = com.example.xueya.domain.usecase.VoiceInputUseCase.VoiceInputState.Idle,
    onAiTextInput: (String) -> Unit = {},
    onStartVoiceInput: () -> Unit = {},
    onStopVoiceInput: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "血压值",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                // AI 输入指示器
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "AI 智能输入",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // AI 语音输入组件
            VoiceInputButton(
                state = voiceInputState,
                onStartVoiceInput = onStartVoiceInput,
                onStopVoiceInput = onStopVoiceInput,
                modifier = Modifier.fillMaxWidth()
            )
            
            // 分隔线
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )
            
            Text(
                text = "手动输入",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 收缩压输入
                OutlinedTextField(
                    value = systolic,
                    onValueChange = onSystolicChange,
                    label = { Text("收缩压") },
                    suffix = { Text("mmHg") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    isError = validationErrors.any { it.contains("收缩压") }
                )

                Text(
                    text = "/",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                // 舒张压输入
                OutlinedTextField(
                    value = diastolic,
                    onValueChange = onDiastolicChange,
                    label = { Text("舒张压") },
                    suffix = { Text("mmHg") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    isError = validationErrors.any { it.contains("舒张压") }
                )
            }

            // 血压分类预览
            if (category != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = getCategoryColor(category),
                        modifier = Modifier.size(20.dp)
                    )
                    
                    Text(
                        text = "分类：${category.categoryName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = getCategoryColor(category),
                        fontWeight = FontWeight.Medium
                    )
                    
                    if (category != BloodPressureCategory.NORMAL) {
                        Text(
                            text = "· ${category.description}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 验证错误提示
            validationErrors.forEach { error ->
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

/**
 * 心率输入卡片
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeartRateInputCard(
    heartRate: String,
    onHeartRateChange: (String) -> Unit,
    validationErrors: List<String>
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "心率",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            OutlinedTextField(
                value = heartRate,
                onValueChange = onHeartRateChange,
                label = { Text(stringResource(R.string.heart_rate_label)) },
                suffix = { Text("bpm") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                isError = validationErrors.isNotEmpty()
            )

            // 心率范围提示
            Text(
                text = stringResource(R.string.normal_resting_heart_rate),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // 验证错误提示
            validationErrors.forEach { error ->
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

/**
 * 测量时间卡片
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeasureTimeCard(
    measureTime: LocalDateTime,
    onShowDatePicker: () -> Unit,
    onShowTimePicker: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.measure_time),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 日期选择
                OutlinedButton(
                    onClick = onShowDatePicker,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(measureTime.format(DateTimeFormatter.ofPattern("MM-dd")))
                }

                // 时间选择
                OutlinedButton(
                    onClick = onShowTimePicker,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(measureTime.format(DateTimeFormatter.ofPattern("HH:mm")))
                }
            }

            Text(
                text = stringResource(R.string.current_time, measureTime.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm"))),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 备注输入卡片
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteInputCard(
    note: String,
    onNoteChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.note),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            OutlinedTextField(
                value = note,
                onValueChange = onNoteChange,
                label = { Text(stringResource(R.string.note_label_optional)) },
                placeholder = { Text(stringResource(R.string.note_placeholder)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )
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