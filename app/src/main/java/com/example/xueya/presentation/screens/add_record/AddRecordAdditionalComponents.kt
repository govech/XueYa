package com.example.xueya.presentation.screens.add_record

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.xueya.R
import java.time.LocalDate
import java.time.LocalTime

/**
 * 标签选择卡片
 */
@Composable
fun TagSelectionCard(
    selectedTags: Set<String>,
    onTagToggle: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.tags),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = stringResource(R.string.tag_selection_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // 标签网格
            val tags = AddRecordUiState.COMMON_TAGS
            val chunkedTags = tags.chunked(3)
            
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                chunkedTags.forEach { rowTags ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowTags.forEach { tag ->
                            TagChip(
                                tag = tag,
                                isSelected = selectedTags.contains(tag),
                                onToggle = onTagToggle,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        
                        // 填充空的位置
                        repeat(3 - rowTags.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // 选中标签数量提示
            if (selectedTags.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.selected_tags_count, selectedTags.size),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * 标签芯片
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TagChip(
    tag: String,
    isSelected: Boolean,
    onToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = isSelected,
        onClick = { onToggle(tag) },
        label = {
            Text(
                text = stringResource(getTagStringRes(tag)),
                style = MaterialTheme.typography.labelMedium
            )
        },
        modifier = modifier
    )
}

/**
 * 根据标签键名获取对应的字符串资源ID
 */
private fun getTagStringRes(tagKey: String): Int {
    return when (tagKey) {
        "tag_morning" -> R.string.tag_morning
        "tag_before_bed" -> R.string.tag_before_bed
        "tag_after_exercise" -> R.string.tag_after_exercise
        "tag_after_meal" -> R.string.tag_after_meal
        "tag_before_meal" -> R.string.tag_before_meal
        "tag_after_medication" -> R.string.tag_after_medication
        "tag_stress" -> R.string.tag_stress
        "tag_tired" -> R.string.tag_tired
        "tag_rest" -> R.string.tag_rest
        "tag_work" -> R.string.tag_work
        "tag_home" -> R.string.tag_home
        "tag_hospital" -> R.string.tag_hospital
        "tag_pharmacy" -> R.string.tag_pharmacy
        "tag_working" -> R.string.tag_working
        "tag_resting" -> R.string.tag_resting
        "tag_stressed" -> R.string.tag_stressed
        else -> R.string.tag_morning // 默认值
    }
}

/**
 * 日期选择器对话框
 */
@SuppressLint("NewApi") // 支持desugaring
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.toEpochDay() * 24 * 60 * 60 * 1000
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                        onDateSelected(date)
                    }
                }
            ) {
                Text(stringResource(R.string.dialog_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dialog_cancel))
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

/**
 * 时间选择器对话框
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    selectedTime: LocalTime,
    onTimeSelected: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = selectedTime.hour,
        initialMinute = selectedTime.minute,
        is24Hour = true
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.select_time_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                TimePicker(state = timePickerState)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.dialog_cancel))
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    TextButton(
                        onClick = {
                            val time = LocalTime.of(
                                timePickerState.hour,
                                timePickerState.minute
                            )
                            onTimeSelected(time)
                        }
                    ) {
                        Text(stringResource(R.string.dialog_confirm))
                    }
                }
            }
        }
    }
}