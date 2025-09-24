package com.example.xueya.presentation.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.xueya.domain.usecase.DetailedStatistics
import com.example.xueya.presentation.components.common.EmptyStateCard
import com.example.xueya.presentation.components.common.ErrorCard
import com.example.xueya.presentation.components.common.LoadingCard
import com.example.xueya.presentation.components.common.StatisticItem

/**
 * 本周概览卡片
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyOverviewCard(
    statistics: DetailedStatistics,
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
                    text = "本周概览",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 统计数据网格
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticItem(
                    label = "总测量",
                    value = "${statistics.totalCount}次",
                    color = MaterialTheme.colorScheme.primary
                )
                
                StatisticItem(
                    label = "正常率",
                    value = "${(statistics.normalPercentage * 100).toInt()}%",
                    color = Color(0xFF4CAF50)
                )
                
                StatisticItem(
                    label = "风险率",
                    value = "${(statistics.riskPercentage * 100).toInt()}%",
                    color = if (statistics.riskPercentage > 0.3f) Color(0xFFF44336) else Color(0xFFFF9800)
                )
            }
            
            if (statistics.averageSystolic > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "平均血压：${statistics.averageSystolic.toInt()}/${statistics.averageDiastolic.toInt()} mmHg",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}



/**
 * 快速操作卡片
 */
@Composable
fun QuickActionsCard(
    onAddRecord: () -> Unit,
    onViewHistory: () -> Unit,
    onViewStatistics: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "快速操作",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 添加记录
                OutlinedButton(
                    onClick = onAddRecord,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("添加")
                }
                
                // 查看历史
                OutlinedButton(
                    onClick = onViewHistory,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.List,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("历史")
                }
                
                // 查看统计
                OutlinedButton(
                    onClick = onViewStatistics,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("统计")
                }
            }
        }
    }
}

/**
 * 首页空状态卡片
 */
@Composable
fun HomeEmptyStateCard(
    onAddRecord: () -> Unit
) {
    EmptyStateCard(
        title = "还没有血压记录",
        description = "点击下方按钮开始记录您的血压数据",
        actionText = "添加第一条记录",
        onAction = onAddRecord,
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



