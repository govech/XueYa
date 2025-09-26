package com.example.xueya.presentation.components

import android.widget.Space
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.launch
import kotlin.math.abs
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.xueya.domain.model.BloodPressureData
import com.example.xueya.utils.DateTimeUtils
import java.time.LocalDateTime
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodPressureRecordDialog(
    viewModel: BloodPressureDialogViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var systolic by remember { mutableIntStateOf(120) }
    var diastolic by remember { mutableIntStateOf(80) }
    var pulse by remember { mutableIntStateOf(72) }
    var notes by remember { mutableStateOf("") }
    var selectedDateTime by remember { mutableStateOf(LocalDateTime.now()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var isAnimating by remember { mutableStateOf(false) }

    // 确保动画状态同步
    LaunchedEffect(uiState.isVisible) {
        if (uiState.isVisible) {
            isAnimating = true
        } else {
            // 延迟重置动画状态，确保退出动画完成
            kotlinx.coroutines.delay(300)
            isAnimating = false
        }
    }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            // 重置表单项
            systolic = 120
            diastolic = 80
            pulse = 72
            notes = ""
            selectedDateTime = LocalDateTime.now()
            viewModel.resetSaveState()
        }
    }

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val dialogHeight = remember { (screenHeight * 0.8f).coerceAtMost(600.dp) } // 屏幕高度的70%，最大600dp

    // 使用LaunchedEffect确保状态稳定
    LaunchedEffect(Unit) {
        // 延迟一小段时间确保布局测量完成
        kotlinx.coroutines.delay(50)
    }

    // 使用更稳定的动画方式，先测量再动画
    var targetOffset by remember { mutableStateOf(0) }

    AnimatedVisibility(
        visible = uiState.isVisible,
        enter = slideInVertically(
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            ),
            initialOffsetY = { targetOffset }
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        ),
        exit = slideOutVertically(
            animationSpec = tween(
                durationMillis = 250,
                easing = FastOutSlowInEasing
            ),
            targetOffsetY = { targetOffset }
        ) + fadeOut(
            animationSpec = tween(
                durationMillis = 250,
                easing = FastOutSlowInEasing
            )
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f))
                .clickable { viewModel.hideDialog() }
                .onGloballyPositioned { coordinates ->
                    // 当布局完成后，设置正确的偏移量
                    targetOffset = coordinates.size.height
                },
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                modifier = modifier
                    .fillMaxWidth()
                    .height(dialogHeight)
                    .clickable(enabled = false) {}, // 阻止点击事件冒泡
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 标题栏 - 固定高度
                    Row(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary)
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "添加血压记录",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(
                            onClick = { viewModel.hideDialog() },
                            modifier = Modifier.size(24.dp).padding(end = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "关闭",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // 可滚动内容区域 - 占据剩余空间
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 血压数值输入卡片
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = "血压与脉搏",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // 收缩压
                                    NumberPicker(
                                        label = "高压",
                                        value = systolic,
                                        unit = "mmHg",
                                        range = 60..250,
                                        onValueChange = { systolic = it },
                                        primaryColor = MaterialTheme.colorScheme.primary
                                    )

                                    Spacer(
                                        modifier = Modifier
                                            .width(1.dp)
                                            .height(60.dp)
                                            .background(MaterialTheme.colorScheme.outlineVariant)
                                    )

                                    // 舒张压
                                    NumberPicker(
                                        label = "低压",
                                        value = diastolic,
                                        unit = "mmHg",
                                        range = 40..150,
                                        onValueChange = { diastolic = it },
                                        primaryColor = MaterialTheme.colorScheme.primary
                                    )

                                    Spacer(
                                        modifier = Modifier
                                            .width(1.dp)
                                            .height(60.dp)
                                            .background(MaterialTheme.colorScheme.outlineVariant)
                                    )

                                    // 脉搏
                                    NumberPicker(
                                        label = "脉搏",
                                        value = pulse,
                                        unit = "bpm",
                                        range = 30..200,
                                        onValueChange = { pulse = it },
                                        primaryColor = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        // 日期时间选择器
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showDatePicker = true }
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "测量时间",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = DateTimeUtils.formatDateTime(selectedDateTime),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        // 备注输入
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "备注",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                BasicTextField(
                                    value = notes,
                                    onValueChange = { notes = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(60.dp) // 降低高度
                                        .background(
                                            MaterialTheme.colorScheme.surface,
                                            RoundedCornerShape(12.dp)
                                        )
                                        .padding(12.dp),
                                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                                        color = MaterialTheme.colorScheme.onSurface
                                    ),
                                    decorationBox = { innerTextField ->
                                        if (notes.isEmpty()) {
                                            Text(
                                                "添加备注信息（如：运动后、服药后等）",
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                    alpha = 0.6f
                                                )
                                            )
                                        }
                                        innerTextField()
                                    }
                                )
                            }
                        }
                    }

                    // 保存按钮 - 固定在底部
                    Button(
                        onClick = {
                            val bloodPressureData = BloodPressureData(
                                systolic = systolic,
                                diastolic = diastolic,
                                heartRate = pulse,
                                measureTime = selectedDateTime,
                                note = notes
                            )
                            viewModel.saveBloodPressureRecord(bloodPressureData)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp, 15.dp, 8.dp, 15.dp)
                            .height(48.dp), // 稍微降低按钮高度
                        shape = RoundedCornerShape(24.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        enabled = !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(
                                text = "保存记录",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }

    // 日期选择器对话框
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDateTime.toInstant(ZoneOffset.UTC).toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val newDate = java.time.Instant.ofEpochMilli(millis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()
                            selectedDateTime = selectedDateTime.with(newDate)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("取消")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // 时间选择器对话框
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = selectedDateTime.hour,
            initialMinute = selectedDateTime.minute
        )
        DatePickerDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedDateTime = selectedDateTime.withHour(timePickerState.hour)
                            .withMinute(timePickerState.minute)
                        showTimePicker = false
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("取消")
                }
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NumberPicker(
    label: String,
    value: Int,
    unit: String,
    range: IntRange,
    onValueChange: (Int) -> Unit,
    primaryColor: Color,
    modifier: Modifier = Modifier
) {
    val items = remember { range.toList() }
    val itemHeight = 40.dp

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = items.indexOf(value).coerceAtLeast(0)
    )
    val coroutineScope = rememberCoroutineScope()

    val snappingLayout = remember(listState) { SnapLayoutInfoProvider(listState) }
    val flingBehavior = rememberSnapFlingBehavior(snappingLayout)

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val layoutInfo = listState.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            if (visibleItemsInfo.isNotEmpty()) {
                val centerItem = visibleItemsInfo.minByOrNull {
                    abs((it.offset + it.size / 2) - (layoutInfo.viewportEndOffset + layoutInfo.viewportStartOffset) / 2)
                }
                if (centerItem != null) {
                    val newValue = items[centerItem.index]
                    if (newValue != value) {
                        onValueChange(newValue)
                    }
                    listState.animateScrollToItem(centerItem.index)
                }
            }
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Box(
            modifier = Modifier
                .height(itemHeight * 3)
                .width(80.dp),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                state = listState,
                flingBehavior = flingBehavior,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = itemHeight)

            ) {
                items(items.size) { index ->
                    val itemValue = items[index]

                    val visibleItemsInfo = listState.layoutInfo.visibleItemsInfo
                    val currentItemInfo = visibleItemsInfo.find { it.index == index }

                    val (scale, color) = if (currentItemInfo != null) {
                        val viewportHeight = listState.layoutInfo.viewportSize.height
                        val itemCenter = currentItemInfo.offset + currentItemInfo.size / 2
                        val viewportCenter = viewportHeight / 2
                        val distance = abs(itemCenter - viewportCenter).toFloat()
                        val maxDistance = viewportHeight / 2f
                        val normalizedDistance = (distance / maxDistance).coerceIn(0f, 1f)

                        val scale = 1f + (1f - normalizedDistance) * 0.6f
                        val itemColor = lerp(
                            start = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            stop = primaryColor,
                            fraction = 1f - normalizedDistance
                        )

                        scale to itemColor
                    } else {
                        1f to if (itemValue == value) primaryColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    }

                    Text(
                        text = itemValue.toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        color = color,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .height(itemHeight)
                            .padding(top = 8.dp)
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight)
                    .border(2.dp, primaryColor, RoundedCornerShape(12.dp))
            )
        }
        Text(
            text = unit,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}