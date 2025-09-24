package com.example.xueya.presentation.components.bloodpressure

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.xueya.domain.model.BloodPressureCategory
import com.example.xueya.domain.model.BloodPressureData
import com.example.xueya.domain.usecase.HealthStatus
import java.time.format.DateTimeFormatter

/**
 * 血压显示卡片
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodPressureDisplayCard(
    record: BloodPressureData,
    onClick: () -> Unit = {},
    showActions: Boolean = false,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 头部：时间和状态
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
                
                CategoryIndicator(category = record.category)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 血压和心率数据
            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                // 血压
                BloodPressureDisplay(
                    systolic = record.systolic,
                    diastolic = record.diastolic,
                    category = record.category
                )
                
                // 心率
                HeartRateDisplay(heartRate = record.heartRate)
            }
            
            // 标签
            if (record.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                
                TagsList(tags = record.tags)
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
            
            // 操作按钮
            if (showActions && (onEdit != null || onDelete != null)) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    onEdit?.let { edit ->
                        OutlinedButton(
                            onClick = edit,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("编辑")
                        }
                    }
                    
                    onDelete?.let { delete ->
                        OutlinedButton(
                            onClick = delete,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("删除")
                        }
                    }
                }
            }
        }
    }
}

/**
 * 血压分类指示器
 */
@Composable
fun CategoryIndicator(
    category: BloodPressureCategory,
    showLabel: Boolean = true,
    modifier: Modifier = Modifier
) {
    val color = getCategoryColor(category)
    val icon = getCategoryIcon(category)
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        
        if (showLabel) {
            Spacer(modifier = Modifier.width(4.dp))
            
            Text(
                text = category.categoryName,
                style = MaterialTheme.typography.bodyMedium,
                color = color,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * 血压数值显示
 */
@Composable
fun BloodPressureDisplay(
    systolic: Int,
    diastolic: Int,
    category: BloodPressureCategory,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = "血压",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = "$systolic/$diastolic",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = getCategoryColor(category)
        )
        
        Text(
            text = "mmHg",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * 心率显示
 */
@Composable
fun HeartRateDisplay(
    heartRate: Int,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = "心率",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = "$heartRate",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = "bpm",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * 标签列表
 */
@Composable
fun TagsList(
    tags: List<String>,
    modifier: Modifier = Modifier
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier
    ) {
        items(tags) { tag ->
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

/**
 * 健康状况指示器
 */
@Composable
fun HealthStatusIndicator(
    healthStatus: HealthStatus,
    showDescription: Boolean = false,
    modifier: Modifier = Modifier
) {
    val color = getHealthStatusColor(healthStatus)
    val icon = getHealthStatusIcon(healthStatus)
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Column {
                Text(
                    text = healthStatus.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = color,
                    fontWeight = FontWeight.Medium
                )
                
                if (showDescription) {
                    Text(
                        text = getHealthStatusDescription(healthStatus),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * 血压范围显示
 */
@Composable
fun BloodPressureRangeDisplay(
    label: String,
    min: Int,
    max: Int,
    avg: Double,
    unit: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RangeValueDisplay(
                    label = "最低",
                    value = "$min",
                    unit = unit,
                    color = MaterialTheme.colorScheme.tertiary
                )
                
                RangeValueDisplay(
                    label = "平均",
                    value = "${avg.toInt()}",
                    unit = unit,
                    color = MaterialTheme.colorScheme.primary
                )
                
                RangeValueDisplay(
                    label = "最高",
                    value = "$max",
                    unit = unit,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

/**
 * 范围数值显示
 */
@Composable
private fun RangeValueDisplay(
    label: String,
    value: String,
    unit: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        
        Text(
            text = unit,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * 获取血压分类对应的颜色
 */
@Composable
fun getCategoryColor(category: BloodPressureCategory): Color {
    return when (category) {
        BloodPressureCategory.NORMAL -> Color(0xFF4CAF50)
        BloodPressureCategory.ELEVATED -> Color(0xFFFF9800)
        BloodPressureCategory.HIGH_STAGE_1 -> Color(0xFFFF5722)
        BloodPressureCategory.HIGH_STAGE_2 -> Color(0xFFF44336)
        BloodPressureCategory.HYPERTENSIVE_CRISIS -> Color(0xFF9C27B0)
    }
}

/**
 * 获取血压分类对应的图标
 */
@Composable
private fun getCategoryIcon(category: BloodPressureCategory): ImageVector {
    return when (category) {
        BloodPressureCategory.NORMAL -> Icons.Default.Favorite
        BloodPressureCategory.ELEVATED -> Icons.Default.Info
        BloodPressureCategory.HIGH_STAGE_1 -> Icons.Default.Warning
        BloodPressureCategory.HIGH_STAGE_2 -> Icons.Default.Warning
        BloodPressureCategory.HYPERTENSIVE_CRISIS -> Icons.Default.Warning
    }
}

/**
 * 获取健康状况对应的颜色
 */
@Composable
private fun getHealthStatusColor(healthStatus: HealthStatus): Color {
    return when (healthStatus) {
        HealthStatus.NO_DATA -> MaterialTheme.colorScheme.onSurfaceVariant
        HealthStatus.NORMAL -> Color(0xFF4CAF50)
        HealthStatus.ELEVATED -> Color(0xFFFF9800)
        HealthStatus.MODERATE_RISK -> Color(0xFFFF5722)
        HealthStatus.HIGH_RISK -> Color(0xFFF44336)
        HealthStatus.CRITICAL -> Color(0xFF9C27B0)
    }
}

/**
 * 获取健康状况对应的图标
 */
@Composable
private fun getHealthStatusIcon(healthStatus: HealthStatus): ImageVector {
    return when (healthStatus) {
        HealthStatus.NO_DATA -> Icons.Default.Info
        HealthStatus.NORMAL -> Icons.Default.Favorite
        HealthStatus.ELEVATED -> Icons.Default.Info
        HealthStatus.MODERATE_RISK -> Icons.Default.Warning
        HealthStatus.HIGH_RISK -> Icons.Default.Warning
        HealthStatus.CRITICAL -> Icons.Default.Warning
    }
}

/**
 * 获取健康状况描述
 */
private fun getHealthStatusDescription(healthStatus: HealthStatus): String {
    return when (healthStatus) {
        HealthStatus.NO_DATA -> "暂无数据"
        HealthStatus.NORMAL -> "血压水平正常"
        HealthStatus.ELEVATED -> "血压略有升高，需要注意"
        HealthStatus.MODERATE_RISK -> "存在中度风险，建议就医"
        HealthStatus.HIGH_RISK -> "存在高度风险，请及时就医"
        HealthStatus.CRITICAL -> "情况危急，请立即就医"
    }
}