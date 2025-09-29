package com.example.xueya.presentation.components.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.xueya.domain.generator.BloodPressureTestDataGenerator
import com.example.xueya.domain.usecase.GenerateTestDataUseCase
import com.example.xueya.utils.DebugUtils

/**
 * 测试数据生成卡片
 * 
 * 只在debug模式下显示，提供血压测试数据生成功能
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestDataGeneratorCard(
    modifier: Modifier = Modifier,
    onGenerateData: (
        timeRange: BloodPressureTestDataGenerator.TimeRange,
        mode: BloodPressureTestDataGenerator.GenerationMode,
        measurementsPerDay: Int
    ) -> Unit = { _, _, _ -> },
    onClearAndGenerate: (
        timeRange: BloodPressureTestDataGenerator.TimeRange,
        mode: BloodPressureTestDataGenerator.GenerationMode,
        measurementsPerDay: Int
    ) -> Unit = { _, _, _ -> },
    isGenerating: Boolean = false,
    generationResult: GenerateTestDataUseCase.GenerateResult? = null
) {
    // 只在debug模式下显示
    if (!DebugUtils.isDebugMode()) {
        return
    }
    
    var showDialog by remember { mutableStateOf(false) }
    var selectedTimeRange by remember { mutableStateOf(BloodPressureTestDataGenerator.TimeRange.ONE_WEEK) }
    var selectedMode by remember { mutableStateOf(BloodPressureTestDataGenerator.GenerationMode.MIXED) }
    var measurementsPerDay by remember { mutableStateOf(2) }
    var showClearConfirmDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 标题
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "测试数据生成器",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.weight(1f))
                // Debug标识
                Surface(
                    color = MaterialTheme.colorScheme.error,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "DEBUG",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onError,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            
            // 描述
            Text(
                text = "生成模拟血压数据用于测试和演示",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // 生成按钮
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { showDialog = true },
                    enabled = !isGenerating,
                    modifier = Modifier.weight(1f)
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text("生成数据")
                }
                
                OutlinedButton(
                    onClick = { showClearConfirmDialog = true },
                    enabled = !isGenerating
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("清空并生成")
                }
            }
            
            // 结果提示
            generationResult?.let { result ->
                when (result) {
                    is GenerateTestDataUseCase.GenerateResult.Success -> {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "数据生成成功！",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                    is GenerateTestDataUseCase.GenerateResult.Error -> {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = result.message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    
    // 生成选项对话框
    if (showDialog) {
        TestDataGenerationDialog(
            selectedTimeRange = selectedTimeRange,
            selectedMode = selectedMode,
            measurementsPerDay = measurementsPerDay,
            onTimeRangeChange = { selectedTimeRange = it },
            onModeChange = { selectedMode = it },
            onMeasurementsPerDayChange = { measurementsPerDay = it },
            onConfirm = {
                onGenerateData(selectedTimeRange, selectedMode, measurementsPerDay)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
    
    // 清空确认对话框
    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text("确认清空数据") },
            text = { Text("此操作将清空所有现有血压记录，然后生成新的测试数据。此操作不可撤销，确定要继续吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearAndGenerate(selectedTimeRange, selectedMode, measurementsPerDay)
                        showClearConfirmDialog = false
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

/**
 * 测试数据生成选项对话框
 */
@Composable
private fun TestDataGenerationDialog(
    selectedTimeRange: BloodPressureTestDataGenerator.TimeRange,
    selectedMode: BloodPressureTestDataGenerator.GenerationMode,
    measurementsPerDay: Int,
    onTimeRangeChange: (BloodPressureTestDataGenerator.TimeRange) -> Unit,
    onModeChange: (BloodPressureTestDataGenerator.GenerationMode) -> Unit,
    onMeasurementsPerDayChange: (Int) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        modifier=Modifier.padding(top = 50.dp, bottom = 50.dp),
        onDismissRequest = onDismiss,
        title = { Text("生成测试数据") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 时间范围选择
                Column {
                    Text(
                        text = "时间范围",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    BloodPressureTestDataGenerator.TimeRange.values().forEach { range ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onTimeRangeChange(range) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedTimeRange == range,
                                onClick = { onTimeRangeChange(range) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (range) {
                                    BloodPressureTestDataGenerator.TimeRange.ONE_WEEK -> "一周 (7天)"
                                    BloodPressureTestDataGenerator.TimeRange.ONE_MONTH -> "一个月 (30天)"
                                    BloodPressureTestDataGenerator.TimeRange.SIX_MONTHS -> "半年 (180天)"
                                }
                            )
                        }
                    }
                }
                
                // 生成模式选择
                Column {
                    Text(
                        text = "生成模式",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    BloodPressureTestDataGenerator.GenerationMode.values().forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onModeChange(mode) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedMode == mode,
                                onClick = { onModeChange(mode) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (mode) {
                                    BloodPressureTestDataGenerator.GenerationMode.NORMAL -> "正常血压"
                                    BloodPressureTestDataGenerator.GenerationMode.HYPERTENSION -> "高血压"
                                    BloodPressureTestDataGenerator.GenerationMode.HYPOTENSION -> "低血压"
                                    BloodPressureTestDataGenerator.GenerationMode.MIXED -> "混合模式"
                                }
                            )
                        }
                    }
                }
                
                // 每天测量次数
                Column {
                    Text(
                        text = "每天测量次数",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { 
                                if (measurementsPerDay > 1) {
                                    onMeasurementsPerDayChange(measurementsPerDay - 1)
                                }
                            }
                        ) {
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "减少")
                        }
                        Text(
                            text = measurementsPerDay.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.width(40.dp),
                            textAlign = TextAlign.Center
                        )
                        IconButton(
                            onClick = { 
                                if (measurementsPerDay < 5) {
                                    onMeasurementsPerDayChange(measurementsPerDay + 1)
                                }
                            }
                        ) {
                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "增加")
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("生成")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
