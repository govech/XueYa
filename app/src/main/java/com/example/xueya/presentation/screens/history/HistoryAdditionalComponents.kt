package com.example.xueya.presentation.screens.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.xueya.R
import com.example.xueya.domain.model.BloodPressureData
import java.time.format.DateTimeFormatter

/**
 * 过滤器面板
 */
@Composable
fun FilterPanel(
    selectedDateRange: DateRange?,
    selectedTags: Set<String>,
    sortOrder: SortOrder,
    onDateRangeChange: (DateRange?) -> Unit,
    onTagToggle: (String) -> Unit,
    onSortOrderChange: (SortOrder) -> Unit,
    onQuickDateRange: (QuickDateRangeType) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.filter_and_sort),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            // 快速日期范围选择
            Column {
                Text(
                    text = stringResource(R.string.time_range),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 使用索引遍历而不是直接遍历枚举值
                    items(4) { index ->
                        val type = when (index) {
                            0 -> QuickDateRangeType.ALL
                            1 -> QuickDateRangeType.THIS_WEEK
                            2 -> QuickDateRangeType.THIS_MONTH
                            3 -> QuickDateRangeType.LAST_30_DAYS
                            else -> QuickDateRangeType.ALL // 默认值
                        }
                        
                        val isSelected = when (type) {
                            QuickDateRangeType.ALL -> selectedDateRange == null
                            QuickDateRangeType.THIS_WEEK -> selectedDateRange == DateRange.thisWeek()
                            QuickDateRangeType.THIS_MONTH -> selectedDateRange == DateRange.thisMonth()
                            QuickDateRangeType.LAST_30_DAYS -> selectedDateRange == DateRange.last30Days()
                        }
                        
                        val label = when (type) {
                            QuickDateRangeType.ALL -> stringResource(R.string.all)
                            QuickDateRangeType.THIS_WEEK -> stringResource(R.string.this_week)
                            QuickDateRangeType.THIS_MONTH -> stringResource(R.string.this_month)
                            QuickDateRangeType.LAST_30_DAYS -> stringResource(R.string.last_30_days)
                        }
                        
                        FilterChip(
                            selected = isSelected,
                            onClick = { onQuickDateRange(type) },
                            label = { Text(label) }
                        )
                    }
                }
                
                // 显示当前选择的日期范围
                selectedDateRange?.let { range ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${range.startDate.format(DateTimeFormatter.ofPattern("MM-dd"))} 至 ${range.endDate.format(DateTimeFormatter.ofPattern("MM-dd"))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // 标签过滤
            Column {
                Text(
                    text = stringResource(R.string.tag_filter),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                val commonTags = listOf(
                    "tag_morning", "tag_before_bed", "tag_after_exercise", "tag_after_meal",
                    "tag_after_medication", "tag_stress", "tag_tired", "tag_rest"
                )
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(commonTags) { tag ->
                        FilterChip(
                            selected = selectedTags.contains(tag),
                            onClick = { onTagToggle(tag) },
                            label = { Text(stringResource(getTagStringRes(tag))) }
                        )
                    }
                }
                
                if (selectedTags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.selected_tags_count, selectedTags.size),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // 排序方式
            Column {
                Text(
                    text = stringResource(R.string.sort_order),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(SortOrder.values()) { order ->
                        FilterChip(
                            selected = sortOrder == order,
                            onClick = { onSortOrderChange(order) },
                            label = { Text(order.displayName) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * 删除确认对话框
 */
@Composable
fun DeleteConfirmDialog(
    record: BloodPressureData?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (record == null) return
    
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text(stringResource(R.string.dialog_delete_title))
        },
        text = {
            Column {
                Text(stringResource(R.string.dialog_delete_message))
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.time_label) + record.measureTime.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm")),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = stringResource(R.string.blood_pressure) + "：${record.systolic}/${record.diastolic} " + stringResource(R.string.unit_mmhg),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = stringResource(R.string.heart_rate) + "：${record.heartRate} " + stringResource(R.string.unit_bpm),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = stringResource(R.string.operation_irreversible),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(R.string.delete))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dialog_cancel))
            }
        }
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