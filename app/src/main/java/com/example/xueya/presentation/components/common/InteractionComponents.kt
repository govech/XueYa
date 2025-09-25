package com.example.xueya.presentation.components.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.xueya.R

/**
 * 带图标的操作按钮
 */
@Composable
fun IconTextButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    style: ButtonStyle = ButtonStyle.Filled
) {
    when (style) {
        ButtonStyle.Filled -> {
            Button(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled && !isLoading
            ) {
                ButtonContent(text, icon, isLoading)
            }
        }
        ButtonStyle.Outlined -> {
            OutlinedButton(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled && !isLoading
            ) {
                ButtonContent(text, icon, isLoading)
            }
        }
        ButtonStyle.Text -> {
            TextButton(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled && !isLoading
            ) {
                ButtonContent(text, icon, isLoading)
            }
        }
        ButtonStyle.Tonal -> {
            FilledTonalButton(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled && !isLoading
            ) {
                ButtonContent(text, icon, isLoading)
            }
        }
    }
}

@Composable
private fun ButtonContent(
    text: String,
    icon: ImageVector,
    isLoading: Boolean
) {
    if (isLoading) {
        CircularProgressIndicator(
            modifier = Modifier.size(18.dp),
            strokeWidth = 2.dp
        )
    } else {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
    }
    
    Spacer(modifier = Modifier.width(8.dp))
    Text(text)
}

/**
 * 按钮样式枚举
 */
enum class ButtonStyle {
    Filled, Outlined, Text, Tonal
}

/**
 * 快速操作按钮组
 */
@Composable
fun QuickActionButtons(
    actions: List<QuickAction>,
    modifier: Modifier = Modifier,
    arrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp)
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = arrangement
    ) {
        actions.forEach { action ->
            IconTextButton(
                text = action.text,
                icon = action.icon,
                onClick = action.onClick,
                style = action.style,
                enabled = action.enabled,
                isLoading = action.isLoading,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * 快速操作数据
 */
data class QuickAction(
    val text: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
    val style: ButtonStyle = ButtonStyle.Outlined,
    val enabled: Boolean = true,
    val isLoading: Boolean = false
)

/**
 * 标签选择器
 */
@Composable
fun TagSelector(
    availableTags: List<String>,
    selectedTags: Set<String>,
    onTagToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null
) {
    Column(modifier = modifier) {
        if (title != null) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        // 标签网格，每行3个
        availableTags.chunked(3).forEach { rowTags ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowTags.forEach { tag ->
                    FilterChip(
                        selected = selectedTags.contains(tag),
                        onClick = { onTagToggle(tag) },
                        label = {
                            Text(
                                text = stringResource(getTagStringRes(tag)),
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                // 填充空位置
                repeat(3 - rowTags.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            
            if (rowTags != availableTags.chunked(3).last()) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        
        // 选中数量提示
        if (selectedTags.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.selected_tags_count, selectedTags.size),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
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

/**
 * 状态指示器
 */
@Composable
fun StatusIndicator(
    status: String,
    color: Color,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        
        Text(
            text = status,
            style = MaterialTheme.typography.bodyMedium,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}