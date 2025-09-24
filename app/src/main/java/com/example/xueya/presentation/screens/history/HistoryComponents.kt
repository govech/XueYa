package com.example.xueya.presentation.screens.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.xueya.domain.model.BloodPressureCategory
import com.example.xueya.domain.model.BloodPressureData
import com.example.xueya.presentation.components.common.StatisticItem
import com.example.xueya.presentation.components.common.EmptyStateCard
import com.example.xueya.presentation.components.common.ErrorCard
import com.example.xueya.presentation.components.common.LoadingCard
import java.time.format.DateTimeFormatter

/**
 * 记录卡片
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordCard(
    record: BloodPressureData,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 头部：时间和删除按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = record.measureTime.format(DateTimeFormatter.ofPattern("MM月dd日 HH:mm")),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "删除",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 血压和心率数据
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 血压
                Column {
                    Text(
                        text = "血压",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${record.systolic}/${record.diastolic}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = getCategoryColor(record.category)
                    )
                    Text(
                        text = "mmHg",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // 心率
                Column {
                    Text(
                        text = "心率",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${record.heartRate}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "bpm",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 血压分类
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = getCategoryColor(record.category),
                    modifier = Modifier.size(16.dp)
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                    text = record.category.categoryName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = getCategoryColor(record.category),
                    fontWeight = FontWeight.Medium
                )
            }
            
            // 标签
            if (record.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(record.tags) { tag ->
                        AssistChip(
                            onClick = { },
                            label = {
                                Text(
                                    text = tag,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            modifier = Modifier.height(28.dp)
                        )
                    }
                }
            }
            
            // 备注
            if (record.note.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "备注：${record.note}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * 统计信息卡片
 */
@Composable
fun StatisticsCard(
    statistics: HistoryStatistics,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "统计概览",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticItem(
                    label = "总记录",
                    value = "${statistics.totalCount}次"
                )
                
                StatisticItem(
                    label = "平均血压",
                    value = "${statistics.averageSystolic.toInt()}/${statistics.averageDiastolic.toInt()}"
                )
                
                StatisticItem(
                    label = "平均心率",
                    value = "${statistics.averageHeartRate.toInt()} bpm"
                )
            }
            
            if (statistics.mostFrequentTags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "常用标签：${statistics.mostFrequentTags.joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}



/**
 * 历史记录空状态
 */
@Composable
fun HistoryEmptyState(
    onAddRecord: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyStateCard(
        title = "还没有血压记录",
        description = "开始记录您的血压数据",
        actionText = "添加第一条记录",
        onAction = onAddRecord,
        modifier = modifier,
        icon = {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    )
}

/**
 * 无搜索结果状态
 */
@Composable
fun NoResultsState(
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyStateCard(
        title = "没有找到匹配的记录",
        description = "尝试调整搜索条件或过滤器",
        actionText = "清除所有过滤器",
        onAction = onClearFilters,
        modifier = modifier
    )
}

/**
 * 历史记录加载指示器
 */
@Composable
fun HistoryLoadingIndicator(
    modifier: Modifier = Modifier
) {
    LoadingCard(
        message = "正在加载记录...",
        modifier = modifier
    )
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