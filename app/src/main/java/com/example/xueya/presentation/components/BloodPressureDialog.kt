package com.example.xueya.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.xueya.domain.model.BloodPressureData
import com.example.xueya.utils.DateTimeUtils
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodPressureRecordDialog(
    viewModel: BloodPressureDialogViewModel = hiltViewModel(), modifier: Modifier = Modifier
) {
    // 收集并观察UI状态的变化，将Flow转换为Compose State
    val uiState by viewModel.uiState.collectAsState()

    // 记住收缩压数值状态，默认值为120
    var systolic by remember { mutableIntStateOf(120) }

    // 记住舒张压数值状态，默认值为80
    var diastolic by remember { mutableIntStateOf(80) }

    // 记住脉搏数值状态，默认值为72
    var pulse by remember { mutableIntStateOf(72) }

    // 记住备注文本状态，默认值为空字符串
    var notes by remember { mutableStateOf("") }

    // 记住选中的日期时间状态，默认值为当前时间
    var selectedDateTime by remember { mutableStateOf(LocalDateTime.now()) }

    // 控制日期选择器显示状态的布尔值
    var showDatePicker by remember { mutableStateOf(false) }

    // 控制时间选择器显示状态的布尔值
    var showTimePicker by remember { mutableStateOf(false) }

    // 控制动画执行状态的布尔值
    var isAnimating by remember { mutableStateOf(false) }


    // 确保动画状态同步
    LaunchedEffect(uiState.isVisible) {
        if (uiState.isVisible) {
            isAnimating = true
        } else {
            // 延迟重置动画状态，确保退出动画完成
            delay(300)
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
    val dialogHeight = remember { (screenHeight * 0.8f).coerceAtMost(600.dp) } // 屏幕高度的80%，最大600dp

    // 使用LaunchedEffect确保状态稳定
    LaunchedEffect(Unit) {
        // 延迟一小段时间确保布局测量完成
        delay(50)
    }

    // 使用更稳定的动画方式，先测量再动画
    var targetOffset by remember { mutableStateOf(0) }

    if (uiState.isVisible) {
        BackHandler {
            viewModel.hideDialog()
        }
    }

    AnimatedVisibility(
        visible = uiState.isVisible, enter = slideInVertically(
            animationSpec = tween(
                durationMillis = 300, easing = FastOutSlowInEasing
            ), initialOffsetY = { targetOffset }) + fadeIn(
            animationSpec = tween(
                durationMillis = 300, easing = FastOutSlowInEasing
            )
        ), exit = slideOutVertically(
            animationSpec = tween(
                durationMillis = 250, easing = FastOutSlowInEasing
            ), targetOffsetY = { targetOffset }) + fadeOut(
            animationSpec = tween(
                durationMillis = 250, easing = FastOutSlowInEasing
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
                }, contentAlignment = Alignment.BottomCenter
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
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 5.dp)
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
                            modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ), shape = RoundedCornerShape(16.dp)
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
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    // 收缩压
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center

                                    ) {
                                        Text(
                                            text = "高压",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                        WheelPicker(
                                            width = 100.dp,
                                            itemHeight = 50.dp,
                                            items = (60..250).map { it },
                                            initialItem = systolic,
                                            visibleItemCount = 3,
                                            itemScale = 1.2f,
                                            onItemSelected = {
                                                systolic = it
                                            }) { item, isSelected ->


                                            Text(
                                                text = item.toString(),
                                                style = MaterialTheme.typography.labelMedium,
                                                fontSize = if (isSelected) 24.sp else 16.sp,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                    alpha = 0.6f
                                                )
                                            )




                                        }
                                        Text(
                                            "mmHg",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Spacer(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(60.dp)

                                    )

                                    // 舒张压
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "低压",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                        WheelPicker(
                                            width = 100.dp,
                                            itemHeight = 50.dp,
                                            items = (40..150).map { it },
                                            initialItem = diastolic,
                                            visibleItemCount = 3,
                                            itemScale = 1.2f,
                                            onItemSelected = {
                                                systolic = it
                                            }) { item, isSelected ->
                                            Text(
                                                text = item.toString(),
                                                style = MaterialTheme.typography.labelMedium,
                                                fontSize = if (isSelected) 24.sp else 16.sp,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                    alpha = 0.6f
                                                )
                                            )
                                        }
                                        Text(
                                            "mmHg",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Spacer(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(60.dp)

                                    )

                                    // 脉搏
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "脉搏",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                        WheelPicker(
                                            width = 100.dp,
                                            itemHeight = 50.dp,
                                            items = (30..250).map { it },
                                            initialItem = pulse,
                                            visibleItemCount = 3,
                                            itemScale = 1.2f,
                                            onItemSelected = {
                                                systolic = it
                                            }) { item, isSelected ->
                                            Text(
                                                text = item.toString(),
                                                style = MaterialTheme.typography.labelMedium,
                                                fontSize = if (isSelected) 24.sp else 16.sp,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                    alpha = 0.6f
                                                )
                                            )
                                        }
                                        Text(
                                            text = "bpm",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                }
                            }
                        }

                        // 日期时间选择器
                        Card(
                            modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ), shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showDatePicker = true }
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically) {
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
                            modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ), shape = RoundedCornerShape(16.dp)
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
                                    })
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
        DatePickerDialog(onDismissRequest = { showDatePicker = false }, confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val newDate = java.time.Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC)
                            .toLocalDate()
                        selectedDateTime = selectedDateTime.with(newDate)
                    }
                    showDatePicker = false
                }) {
                Text("确定")
            }
        }, dismissButton = {
            TextButton(onClick = { showDatePicker = false }) {
                Text("取消")
            }
        }) {
            DatePicker(state = datePickerState)
        }
    }

    // 时间选择器对话框
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = selectedDateTime.hour, initialMinute = selectedDateTime.minute
        )
        DatePickerDialog(onDismissRequest = { showTimePicker = false }, confirmButton = {
            TextButton(
                onClick = {
                    selectedDateTime = selectedDateTime.withHour(timePickerState.hour)
                        .withMinute(timePickerState.minute)
                    showTimePicker = false
                }) {
                Text("确定")
            }
        }, dismissButton = {
            TextButton(onClick = { showTimePicker = false }) {
                Text("取消")
            }
        }) {
            TimePicker(state = timePickerState)
        }
    }
}






/*************************************************************************/


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> WheelPicker(
    width: Dp,
    itemHeight: Dp,
    items: List<T>,
    initialItem: T,
    visibleItemCount: Int = 3,
    itemScale: Float = 1.5f,
    onItemSelected: (T) -> Unit = {},
    content: @Composable (item: T, isSelected: Boolean) -> Unit
) {
    // 项目高度一半的像素值，用于计算选中判定
    val halfItemPx = LocalDensity.current.run { itemHeight.toPx() / 2f }
    // 创建无限滚动的状态
    val scrollState = rememberLazyListState()
    var lastSelectedIndex by remember { mutableStateOf(0) }
    var itemsState by remember { mutableStateOf(items) }

    // 初始化滚动到给定的初始项，使其位于中心
    LaunchedEffect(items) {
        val index = items.indexOf(initialItem).coerceAtLeast(0)
        // 取 Int.MAX_VALUE 中点对齐实际数据
        val mid = Int.MAX_VALUE / 2
        val offset = (mid / items.size) * items.size
        val targetIndex = offset + index - 1
        itemsState = items
        // 初始设置 lastSelectedIndex 为 top item 使后续 onGloballyPositioned 能触发回调
        lastSelectedIndex = targetIndex
        scrollState.scrollToItem(targetIndex)
    }

    Box(
        modifier = Modifier
            .width(width)
            .height(itemHeight * visibleItemCount),
        contentAlignment = Alignment.Center
    ) {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = scrollState,
            flingBehavior = rememberSnapFlingBehavior(scrollState)
        ) {
            items(count = Int.MAX_VALUE) { globalIndex ->
                val item = itemsState[globalIndex % itemsState.size]
                Box(
                    modifier = Modifier
                        .height(itemHeight)
                        .fillMaxWidth()
                        .onGloballyPositioned { coords ->
                            // 计算此项相对于父容器中心的偏移
                            val y = coords.positionInParent().y - halfItemPx
                            val parentHalf = (coords.parentCoordinates?.size?.height ?: 0) / 2f
                            val isSelected =
                                (y > parentHalf - halfItemPx && y < parentHalf + halfItemPx)
                            if (isSelected && lastSelectedIndex != globalIndex) {
                                // 当前项成为选中项时触发回调
                                onItemSelected(item)
                                lastSelectedIndex = globalIndex
                            }
                        }, contentAlignment = Alignment.Center
                ) {
                    // 判断此项是否为当前选中项
                    val isSelected = (globalIndex == lastSelectedIndex)
                    // 对选中项应用放大动画
                    val scale = animateFloatAsState(if (isSelected) itemScale else 1f)
                    Box(
                        modifier = Modifier.graphicsLayer {
                            scaleX = scale.value
                            scaleY = scale.value
                        }, contentAlignment = Alignment.Center
                    ) {
                        // 用户自定义内容绘制
                        content(item, isSelected)
                    }


                }
            }
        }
        // 选择框
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
        )
    }
}
