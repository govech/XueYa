package com.example.xueya.presentation.components.input

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * 验证结果显示
 */
@Composable
fun ValidationResult(
    isValid: Boolean,
    message: String,
    modifier: Modifier = Modifier,
    showIcon: Boolean = true
) {
    if (message.isNotBlank()) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showIcon) {
                Icon(
                    imageVector = if (isValid) Icons.Default.Check else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (isValid) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
                
                Spacer(modifier = Modifier.width(4.dp))
            }
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = if (isValid) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
            )
        }
    }
}

/**
 * 输入表单验证器
 */
data class ValidationRule(
    val validate: (String) -> Boolean,
    val errorMessage: String
)

/**
 * 验证血压输入
 */
fun validateBloodPressure(systolic: String, diastolic: String): Pair<Boolean, String> {
    val systolicValue = systolic.toIntOrNull()
    val diastolicValue = diastolic.toIntOrNull()
    
    return when {
        systolicValue == null || diastolicValue == null -> {
            false to "请输入有效的血压数值"
        }
        systolicValue < 50 || systolicValue > 250 -> {
            false to "收缩压应在50-250 mmHg之间"
        }
        diastolicValue < 30 || diastolicValue > 150 -> {
            false to "舒张压应在30-150 mmHg之间"
        }
        systolicValue <= diastolicValue -> {
            false to "收缩压应大于舒张压"
        }
        else -> {
            true to "血压数值正常"
        }
    }
}

/**
 * 验证心率输入
 */
fun validateHeartRate(heartRate: String): Pair<Boolean, String> {
    val value = heartRate.toIntOrNull()
    
    return when {
        value == null -> {
            false to "请输入有效的心率数值"
        }
        value < 30 || value > 220 -> {
            false to "心率应在30-220 bpm之间"
        }
        else -> {
            true to "心率数值正常"
        }
    }
}

/**
 * 表单验证组件
 */
@Composable
fun FormValidator(
    systolic: String,
    diastolic: String,
    heartRate: String,
    onValidationChange: (Boolean) -> Unit
) {
    // 验证血压
    val (isBPValid, bpMessage) = validateBloodPressure(systolic, diastolic)
    
    // 验证心率
    val (isHRValid, hrMessage) = validateHeartRate(heartRate)
    
    // 总体验证结果
    val isFormValid = isBPValid && isHRValid
    
    // 通知父组件验证结果
    LaunchedEffect(isFormValid) {
        onValidationChange(isFormValid)
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isFormValid) {
                Color(0xFF4CAF50).copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "输入验证",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 血压验证结果
            ValidationResult(
                isValid = isBPValid,
                message = bpMessage,
                modifier = Modifier.padding(vertical = 2.dp)
            )
            
            // 心率验证结果
            ValidationResult(
                isValid = isHRValid,
                message = hrMessage,
                modifier = Modifier.padding(vertical = 2.dp)
            )
            
            if (isFormValid) {
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "✓ 所有输入均有效，可以保存记录",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * 快速输入按钮组
 */
@Composable
fun QuickInputButtons(
    onSystolicQuickInput: (Int) -> Unit,
    onDiastolicQuickInput: (Int) -> Unit,
    onHeartRateQuickInput: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "快速输入",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 收缩压快速输入
            Text(
                text = "收缩压 (mmHg)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val systolicValues = listOf(110, 120, 130, 140, 150)
                systolicValues.forEach { value ->
                    QuickInputChip(
                        value = value.toString(),
                        onClick = { onSystolicQuickInput(value) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 舒张压快速输入
            Text(
                text = "舒张压 (mmHg)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val diastolicValues = listOf(70, 80, 85, 90, 95)
                diastolicValues.forEach { value ->
                    QuickInputChip(
                        value = value.toString(),
                        onClick = { onDiastolicQuickInput(value) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 心率快速输入
            Text(
                text = "心率 (bpm)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val heartRateValues = listOf(60, 70, 80, 90, 100)
                heartRateValues.forEach { value ->
                    QuickInputChip(
                        value = value.toString(),
                        onClick = { onHeartRateQuickInput(value) }
                    )
                }
            }
        }
    }
}

/**
 * 快速输入芯片
 */
@Composable
private fun QuickInputChip(
    value: String,
    onClick: () -> Unit
) {
    FilterChip(
        onClick = onClick,
        label = { Text(value) },
        selected = false
    )
}

/**
 * 健康提示卡片
 */
@Composable
fun HealthTipsCard(
    systolic: Int?,
    diastolic: Int?,
    modifier: Modifier = Modifier
) {
    if (systolic != null && diastolic != null) {
        val category = when {
            systolic < 120 && diastolic < 80 -> "正常血压"
            systolic < 130 && diastolic < 80 -> "血压偏高"
            systolic < 140 || diastolic < 90 -> "高血压1期"
            systolic < 180 || diastolic < 120 -> "高血压2期"
            else -> "高血压危象"
        }
        
        val tips = when (category) {
            "正常血压" -> listOf(
                "继续保持健康的生活方式",
                "定期监测血压变化",
                "保持适量运动和均衡饮食"
            )
            "血压偏高" -> listOf(
                "注意控制盐分摄入",
                "增加有氧运动频率",
                "保持充足睡眠"
            )
            "高血压1期" -> listOf(
                "建议咨询医生制定治疗方案",
                "严格控制饮食，减少钠盐摄入",
                "规律监测血压"
            )
            "高血压2期" -> listOf(
                "请及时就医，可能需要药物治疗",
                "严格遵循医生的治疗建议",
                "监控并发症风险"
            )
            else -> listOf(
                "这是医疗紧急情况，请立即就医",
                "在等待医疗救助时保持冷静",
                "避免剧烈活动"
            )
        }
        
        val cardColor = when (category) {
            "正常血压" -> Color(0xFF4CAF50).copy(alpha = 0.1f)
            "血压偏高" -> Color(0xFFFF9800).copy(alpha = 0.1f)
            "高血压1期" -> Color(0xFFFF5722).copy(alpha = 0.1f)
            "高血压2期" -> Color(0xFFF44336).copy(alpha = 0.1f)
            else -> Color(0xFF9C27B0).copy(alpha = 0.1f)
        }
        
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = cardColor
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "血压分类：$category",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                tips.forEachIndexed { index, tip ->
                    Text(
                        text = "• $tip",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}