package com.example.xueya.presentation.components.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * 统计数据项
 */
@Composable
fun StatisticItem(
    label: String,
    value: String,
    color: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier,
    description: String? = null
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        if (description != null) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 统计网格
 */
@Composable
fun StatisticGrid(
    items: List<StatisticData>,
    modifier: Modifier = Modifier,
    columns: Int = 3
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        items.chunked(columns).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                rowItems.forEach { item ->
                    StatisticItem(
                        label = item.label,
                        value = item.value,
                        color = item.color,
                        description = item.description,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                // 填充空位置
                repeat(columns - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            
            if (rowItems != items.chunked(columns).last()) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

/**
 * 统计数据
 */
data class StatisticData(
    val label: String,
    val value: String,
    val color: Color = Color.Unspecified,
    val description: String? = null
)

/**
 * 简单的进度条
 */
@Composable
fun SimpleProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    label: String? = null
) {
    Column(modifier = modifier) {
        if (label != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
        
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth(),
            color = color,
            trackColor = backgroundColor
        )
    }
}

/**
 * 范围显示组件
 */
@Composable
fun RangeDisplay(
    label: String,
    min: String,
    max: String,
    average: String,
    unit: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "最低: $min $unit",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "平均: $average $unit",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "最高: $max $unit",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}