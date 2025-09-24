package com.example.xueya.presentation.components.input

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 数字输入框
 */
@Composable
fun NumberInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    unit: String = "",
    min: Int? = null,
    max: Int? = null,
    isError: Boolean = false,
    errorMessage: String = "",
    enabled: Boolean = true,
    singleLine: Boolean = true
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                // 只允许数字输入
                val filtered = newValue.filter { it.isDigit() }
                
                // 验证范围
                val intValue = filtered.toIntOrNull()
                if (intValue != null) {
                    val isValid = (min == null || intValue >= min) && 
                                 (max == null || intValue <= max)
                    if (isValid || filtered.isEmpty()) {
                        onValueChange(filtered)
                    }
                } else if (filtered.isEmpty()) {
                    onValueChange(filtered)
                }
            },
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            suffix = if (unit.isNotBlank()) {
                { Text(unit, style = MaterialTheme.typography.bodyMedium) }
            } else null,
            isError = isError,
            enabled = enabled,
            singleLine = singleLine,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        if (isError && errorMessage.isNotBlank()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
        
        // 显示有效范围
        if (min != null || max != null) {
            val rangeText = when {
                min != null && max != null -> "范围：$min - $max"
                min != null -> "最小值：$min"
                max != null -> "最大值：$max"
                else -> ""
            }
            
            Text(
                text = rangeText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

/**
 * 标签选择器
 */
@Composable
fun TagSelector(
    selectedTags: List<String>,
    onTagsChange: (List<String>) -> Unit,
    availableTags: List<String> = emptyList(),
    modifier: Modifier = Modifier,
    maxTags: Int = 5,
    allowCustomTags: Boolean = true
) {
    var newTagText by remember { mutableStateOf("") }
    var showAddTag by remember { mutableStateOf(false) }
    
    Column(modifier = modifier) {
        // 标题
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "标签",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            if (allowCustomTags && selectedTags.size < maxTags) {
                FilledTonalIconButton(
                    onClick = { showAddTag = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "添加标签",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 已选择的标签
        if (selectedTags.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(selectedTags) { tag ->
                    SelectedTagChip(
                        tag = tag,
                        onRemove = {
                            onTagsChange(selectedTags - tag)
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        // 可选标签
        if (availableTags.isNotEmpty()) {
            Text(
                text = "推荐标签",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(availableTags.filter { it !in selectedTags }) { tag ->
                    AvailableTagChip(
                        tag = tag,
                        onSelect = {
                            if (selectedTags.size < maxTags) {
                                onTagsChange(selectedTags + tag)
                            }
                        },
                        enabled = selectedTags.size < maxTags
                    )
                }
            }
        }
        
        // 添加自定义标签对话框
        if (showAddTag) {
            AddTagDialog(
                onDismiss = { 
                    showAddTag = false
                    newTagText = ""
                },
                onAdd = { tag ->
                    if (tag.isNotBlank() && tag !in selectedTags) {
                        onTagsChange(selectedTags + tag)
                    }
                    showAddTag = false
                    newTagText = ""
                }
            )
        }
    }
}

/**
 * 已选择的标签芯片
 */
@Composable
private fun SelectedTagChip(
    tag: String,
    onRemove: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onRemove() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = tag,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.width(4.dp))
            
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "移除",
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

/**
 * 可选标签芯片
 */
@Composable
private fun AvailableTagChip(
    tag: String,
    onSelect: () -> Unit,
    enabled: Boolean
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (enabled) {
            MaterialTheme.colorScheme.surfaceVariant
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        },
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(enabled = enabled) { onSelect() }
    ) {
        Text(
            text = tag,
            style = MaterialTheme.typography.labelMedium,
            color = if (enabled) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            },
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

/**
 * 添加标签对话框
 */
@Composable
private fun AddTagDialog(
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit
) {
    var tagText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    
    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加自定义标签") },
        text = {
            OutlinedTextField(
                value = tagText,
                onValueChange = { tagText = it },
                label = { Text("标签名称") },
                placeholder = { Text("例如：运动后、餐前等") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (tagText.isNotBlank()) {
                            onAdd(tagText.trim())
                        }
                    }
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (tagText.isNotBlank()) {
                        onAdd(tagText.trim())
                    }
                },
                enabled = tagText.isNotBlank()
            ) {
                Text("添加")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    )
}

/**
 * 日期时间选择器
 */
@Composable
fun DateTimePicker(
    selectedDateTime: LocalDateTime,
    onDateTimeChange: (LocalDateTime) -> Unit,
    label: String = "测量时间",
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 日期按钮
            OutlinedButton(
                onClick = { showDatePicker = true },
                enabled = enabled,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = selectedDateTime.format(DateTimeFormatter.ofPattern("MM月dd日")),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            // 时间按钮
            OutlinedButton(
                onClick = { showTimePicker = true },
                enabled = enabled,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = selectedDateTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
    
    // 日期选择器对话框
    if (showDatePicker) {
        SimpleDatePickerDialog(
            selectedDate = selectedDateTime.toLocalDate(),
            onDateSelected = { date ->
                onDateTimeChange(
                    selectedDateTime.with(date)
                )
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
    
    // 时间选择器对话框
    if (showTimePicker) {
        SimpleTimePickerDialog(
            selectedTime = selectedDateTime.toLocalTime(),
            onTimeSelected = { time ->
                onDateTimeChange(
                    selectedDateTime.with(time)
                )
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }
}

/**
 * 简单日期选择器对话框
 */
@Composable
private fun SimpleDatePickerDialog(
    selectedDate: java.time.LocalDate,
    onDateSelected: (java.time.LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择日期") },
        text = {
            Column {
                Text(
                    text = "当前选择：${selectedDate.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"))}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "注：完整的日期选择器需要额外的依赖库支持，这里使用简化版本",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onDateSelected(selectedDate) }) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

/**
 * 简单时间选择器对话框
 */
@Composable
private fun SimpleTimePickerDialog(
    selectedTime: java.time.LocalTime,
    onTimeSelected: (java.time.LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    var hour by remember { mutableStateOf(selectedTime.hour.toString()) }
    var minute by remember { mutableStateOf(selectedTime.minute.toString().padStart(2, '0')) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择时间") },
        text = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 小时输入
                NumberInputField(
                    value = hour,
                    onValueChange = { hour = it },
                    label = "小时",
                    min = 0,
                    max = 23,
                    modifier = Modifier.weight(1f)
                )
                
                Text(
                    text = ":",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                // 分钟输入
                NumberInputField(
                    value = minute,
                    onValueChange = { minute = it },
                    label = "分钟",
                    min = 0,
                    max = 59,
                    modifier = Modifier.weight(1f)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val h = hour.toIntOrNull() ?: selectedTime.hour
                    val m = minute.toIntOrNull() ?: selectedTime.minute
                    onTimeSelected(java.time.LocalTime.of(h, m))
                }
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

/**
 * 多行文本输入框
 */
@Composable
fun MultilineTextInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    maxLines: Int = 4,
    maxLength: Int = 200,
    enabled: Boolean = true
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                if (newValue.length <= maxLength) {
                    onValueChange(newValue)
                }
            },
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            enabled = enabled,
            maxLines = maxLines,
            modifier = Modifier.fillMaxWidth()
        )
        
        // 字符计数
        Text(
            text = "${value.length}/$maxLength",
            style = MaterialTheme.typography.bodySmall,
            color = if (value.length > maxLength * 0.9) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 4.dp),
            textAlign = TextAlign.End
        )
    }
}