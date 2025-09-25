package com.example.xueya.presentation.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.xueya.R
import com.example.xueya.domain.model.BloodPressureCategory
import com.example.xueya.domain.model.BloodPressureData
import com.example.xueya.domain.usecase.DetailedStatistics
import com.example.xueya.domain.usecase.HealthStatus
import java.time.format.DateTimeFormatter

/**
 * 健康状况卡片
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthStatusCard(
    healthStatus: HealthStatus,
    todayMeasurementCount: Int,
    weeklyMeasurementCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (healthStatus) {
                HealthStatus.NO_DATA -> MaterialTheme.colorScheme.surfaceVariant
                HealthStatus.NORMAL -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                HealthStatus.ELEVATED -> Color(0xFFFF9800).copy(alpha = 0.1f)
                HealthStatus.MODERATE_RISK -> Color(0xFFFF5722).copy(alpha = 0.1f)
                HealthStatus.HIGH_RISK -> Color(0xFFF44336).copy(alpha = 0.1f)
                HealthStatus.CRITICAL -> Color(0xFF9C27B0).copy(alpha = 0.1f)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = when (healthStatus) {
                        HealthStatus.NO_DATA -> Icons.Default.Info
                        HealthStatus.NORMAL -> Icons.Default.Favorite
                        else -> Icons.Default.Warning
                    },
                    contentDescription = null,
                    tint = when (healthStatus) {
                        HealthStatus.NO_DATA -> MaterialTheme.colorScheme.onSurfaceVariant
                        HealthStatus.NORMAL -> Color(0xFF4CAF50)
                        HealthStatus.ELEVATED -> Color(0xFFFF9800)
                        HealthStatus.MODERATE_RISK -> Color(0xFFFF5722)
                        HealthStatus.HIGH_RISK -> Color(0xFFF44336)
                        HealthStatus.CRITICAL -> Color(0xFF9C27B0)
                    }
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Column {
                    Text(
                        text = stringResource(R.string.health_status),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = healthStatus.displayName,
                        style = MaterialTheme.typography.bodyLarge,
                        color = when (healthStatus) {
                            HealthStatus.NO_DATA -> MaterialTheme.colorScheme.onSurfaceVariant
                            HealthStatus.NORMAL -> Color(0xFF4CAF50)
                            HealthStatus.ELEVATED -> Color(0xFFFF9800)
                            HealthStatus.MODERATE_RISK -> Color(0xFFFF5722)
                            HealthStatus.HIGH_RISK -> Color(0xFFF44336)
                            HealthStatus.CRITICAL -> Color(0xFF9C27B0)
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = healthStatus.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (todayMeasurementCount > 0 || weeklyMeasurementCount > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (todayMeasurementCount > 0) {
                        Column {
                            Text(
                                text = stringResource(R.string.today_measurement),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = stringResource(R.string.record_count, todayMeasurementCount),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    
                    if (weeklyMeasurementCount > 0) {
                        Column {
                            Text(
                                text = stringResource(R.string.weekly_measurement),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = stringResource(R.string.record_count, weeklyMeasurementCount),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 最新记录卡片
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LatestRecordCard(
    record: BloodPressureData,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.latest_record),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = record.measureTime.format(DateTimeFormatter.ofPattern("MM/dd HH:mm")),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 血压值
                Column {
                    Text(
                        text = stringResource(R.string.blood_pressure),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${record.systolic}/${record.diastolic}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = getCategoryColor(record.category)
                    )
                    Text(
                        text = stringResource(R.string.unit_mmhg),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // 心率
                Column {
                    Text(
                        text = stringResource(R.string.heart_rate),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${record.heartRate}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.unit_bpm),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 分类标签
            AssistChip(
                onClick = { },
                label = {
                    Text(
                        text = record.category.categoryName,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = getCategoryColor(record.category).copy(alpha = 0.2f),
                    labelColor = getCategoryColor(record.category)
                )
            )
            
            // 备注
            if (record.note.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.note_label, record.note),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * 今日统计卡片
 */
@Composable
fun TodayStatisticsCard(
    averageBP: Pair<Int, Int>?,
    averageHeartRate: Int?,
    measurementCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.today_statistics),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 平均血压
                if (averageBP != null) {
                    Column {
                        Text(
                            text = stringResource(R.string.average_blood_pressure),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${averageBP.first}/${averageBP.second}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(R.string.unit_mmhg),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // 平均心率
                if (averageHeartRate != null) {
                    Column {
                        Text(
                            text = stringResource(R.string.average_heart_rate),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$averageHeartRate",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(R.string.unit_bpm),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // 测量次数
                Column {
                    Text(
                        text = stringResource(R.string.measurement_count),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$measurementCount",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.unit_count),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * 获取分类对应的颜色
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