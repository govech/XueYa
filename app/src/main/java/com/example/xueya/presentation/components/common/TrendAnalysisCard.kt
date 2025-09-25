package com.example.xueya.presentation.components.common

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.xueya.domain.model.ai.BloodPressureTrendAnalysis

/**
 * 趋势分析卡片组件
 */
@Composable
fun TrendAnalysisCard(
    trendAnalysis: BloodPressureTrendAnalysis?,
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
                trendAnalysis?.trend == "worsening" -> MaterialTheme.colorScheme.errorContainer
                trendAnalysis?.trend == "improving" -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
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
                        imageVector = getTrendIcon(trendAnalysis?.trend),
                        contentDescription = null,
                        tint = getTrendColor(trendAnalysis?.trend),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "AI 趋势分析",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = getTrendColor(trendAnalysis?.trend)
                    )
                }
                
                if (!isLoading) {
                    IconButton(
                        onClick = onRefresh,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "刷新分析",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // 内容区域
            when {
                isLoading -> {
                    TrendAnalysisLoadingContent()
                }
                error != null -> {
                    TrendAnalysisErrorContent(
                        error = error,
                        onRetry = onRefresh
                    )
                }
                trendAnalysis != null -> {
                    TrendAnalysisContent(trendAnalysis = trendAnalysis)
                }
                else -> {
                    TrendAnalysisEmptyContent(onGenerate = onRefresh)
                }
            }
        }
    }
}

/**
 * 趋势分析内容
 */
@Composable
private fun TrendAnalysisContent(
    trendAnalysis: BloodPressureTrendAnalysis,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 趋势状态指示器
        Card(
            colors = CardDefaults.cardColors(
                containerColor = getTrendBackgroundColor(trendAnalysis.trend)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = getTrendIcon(trendAnalysis.trend),
                    contentDescription = null,
                    tint = getTrendColor(trendAnalysis.trend),
                    modifier = Modifier.size(32.dp)
                )
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = getTrendText(trendAnalysis.trend),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = getTrendColor(trendAnalysis.trend)
                    )
                    Text(
                        text = trendAnalysis.summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        
        // 洞察分析
        if (trendAnalysis.insights.isNotEmpty()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "关键洞察：",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
                
                LazyColumn(
                    modifier = Modifier.heightIn(max = 150.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(trendAnalysis.insights) { insight ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(top = 2.dp),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = insight,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
        
        // 改善建议
        if (trendAnalysis.recommendations.isNotEmpty()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "改善建议：",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
                
                LazyColumn(
                    modifier = Modifier.heightIn(max = 150.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(trendAnalysis.recommendations) { recommendation ->
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
}

/**
 * 加载中内容
 */
@Composable
private fun TrendAnalysisLoadingContent(
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
            text = "AI 正在分析血压趋势...",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/**
 * 错误内容
 */
@Composable
private fun TrendAnalysisErrorContent(
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
                text = "分析失败",
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
private fun TrendAnalysisEmptyContent(
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
            text = "点击生成血压趋势分析",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Button(
            onClick = onGenerate,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("生成分析")
        }
    }
}

/**
 * 获取趋势图标
 */
private fun getTrendIcon(trend: String?): ImageVector {
    return when (trend) {
        "improving" -> Icons.Default.Check
        "worsening" -> Icons.Default.Add
        "stable" -> Icons.Default.Settings
        else -> Icons.Default.Info
    }
}

/**
 * 获取趋势颜色
 */
@Composable
private fun getTrendColor(trend: String?): Color {
    return when (trend) {
        "improving" -> MaterialTheme.colorScheme.primary
        "worsening" -> MaterialTheme.colorScheme.error
        "stable" -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.onSurface
    }
}

/**
 * 获取趋势背景颜色
 */
@Composable
private fun getTrendBackgroundColor(trend: String): Color {
    return when (trend) {
        "improving" -> MaterialTheme.colorScheme.primaryContainer
        "worsening" -> MaterialTheme.colorScheme.errorContainer
        "stable" -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
}

/**
 * 获取趋势文本
 */
private fun getTrendText(trend: String): String {
    return when (trend) {
        "improving" -> "血压趋势改善"
        "worsening" -> "血压趋势恶化"
        "stable" -> "血压趋势稳定"
        else -> "趋势未知"
    }
}