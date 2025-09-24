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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
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
                text = "过滤和排序",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            // 快速日期范围选择
            Column {
                Text(
                    text = "时间范围",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        listOf(
                            QuickDateRangeType.ALL to "全部",
                            QuickDateRangeType.THIS_WEEK to "本周",
                            QuickDateRangeType.THIS_MONTH to "本月",
                            QuickDateRangeType.LAST_30_DAYS to "近30天"
                        )
                    ) { (type, label) ->
                        val isSelected = when (type) {
                            QuickDateRangeType.ALL -> selectedDateRange == null
                            QuickDateRangeType.THIS_WEEK -> selectedDateRange == DateRange.thisWeek()
                            QuickDateRangeType.THIS_MONTH -> selectedDateRange == DateRange.thisMonth()
                            QuickDateRangeType.LAST_30_DAYS -> selectedDateRange == DateRange.last30Days()
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
                    text = "标签过滤",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                val commonTags = listOf(
                    "晨起", "睡前", "运动后", "用餐后", 
                    "服药后", "紧张", "疲劳", "休息"
                )
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(commonTags) { tag ->
                        FilterChip(
                            selected = selectedTags.contains(tag),
                            onClick = { onTagToggle(tag) },
                            label = { Text(tag) }
                        )
                    }
                }
                
                if (selectedTags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "已选择 ${selectedTags.size} 个标签",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // 排序方式
            Column {
                Text(
                    text = "排序方式",
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
            Text("删除确认")
        },
        text = {
            Column {
                Text("确定要删除这条记录吗？")
                
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
                            text = "时间：${record.measureTime.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm"))}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "血压：${record.systolic}/${record.diastolic} mmHg",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "心率：${record.heartRate} bpm",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "此操作无法撤销",
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
                Text("删除")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}