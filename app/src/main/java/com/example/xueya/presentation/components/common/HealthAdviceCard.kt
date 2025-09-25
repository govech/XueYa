package com.example.xueya.presentation.components.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.xueya.domain.model.ai.HealthAdvice

/**
 * 健康建议卡片组件
 */
@Composable
fun HealthAdviceCard(
    healthAdvice: HealthAdvice?,
    isLoading: Boolean = false,
    error: String? = null,
    onRefresh: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                error != null -> MaterialTheme.colorScheme.errorContainer
                healthAdvice?.category == "danger" -> MaterialTheme.colorScheme.errorContainer
                healthAdvice?.category == "warning" -> MaterialTheme.colorScheme.secondaryContainer
                else -> MaterialTheme.colorScheme.primaryContainer
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 标题栏
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = getAdviceIcon(healthAdvice?.category),
                        contentDescription = null,
                        tint = getAdviceColor(healthAdvice?.category),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "AI 健康建议",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = getAdviceColor(healthAdvice?.category)
                    )
                }
                
                if (!isLoading) {
                    IconButton(
                        onClick = onRefresh,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "刷新建议",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // 内容区域
            when {
                isLoading -> {
                    HealthAdviceLoadingContent()
                }
                error != null -> {
                    HealthAdviceErrorContent(
                        error = error,
                        onRetry = onRefresh
                    )
                }
                healthAdvice != null -> {
                    HealthAdviceContent(healthAdvice = healthAdvice)
                }
                else -> {
                    HealthAdviceEmptyContent(onGenerate = onRefresh)
                }
            }
        }
    }
}

/**
 * 健康建议内容
 */
@Composable
private fun HealthAdviceContent(
    healthAdvice: HealthAdvice,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 主要建议
        Text(
            text = healthAdvice.advice,
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.2
        )
        
        // 推荐事项
        if (healthAdvice.recommendations.isNotEmpty()) {
            Text(
                text = "具体建议：",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )
            
            LazyColumn(
                modifier = Modifier.heightIn(max = 200.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(healthAdvice.recommendations) { recommendation ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(top = 2.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = recommendation,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * 加载中内容
 */
@Composable
private fun HealthAdviceLoadingContent(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            strokeWidth = 2.dp
        )
        Text(
            text = "AI 正在分析您的血压数据...",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/**
 * 错误内容
 */
@Composable
private fun HealthAdviceErrorContent(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "获取建议失败",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
        
        Text(
            text = error,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onErrorContainer
        )
        
        TextButton(
            onClick = onRetry,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("重试")
        }
    }
}

/**
 * 空内容
 */
@Composable
private fun HealthAdviceEmptyContent(
    onGenerate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = "点击获取个性化健康建议",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Button(
            onClick = onGenerate,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("生成建议")
        }
    }
}

/**
 * 获取建议图标
 */
private fun getAdviceIcon(category: String?): ImageVector {
    return when (category) {
        "danger" -> Icons.Default.Warning
        "warning" -> Icons.Default.Warning
        "normal" -> Icons.Default.CheckCircle
        else -> Icons.Default.Info
    }
}

/**
 * 获取建议颜色
 */
@Composable
private fun getAdviceColor(category: String?): Color {
    return when (category) {
        "danger" -> MaterialTheme.colorScheme.error
        "warning" -> MaterialTheme.colorScheme.secondary
        "normal" -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface
    }
}