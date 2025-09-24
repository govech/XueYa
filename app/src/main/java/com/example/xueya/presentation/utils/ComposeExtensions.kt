package com.example.xueya.presentation.utils

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.RepeatMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * Compose 扩展函数工具类
 */

/**
 * 条件修饰符 - 根据条件应用修饰符
 */
fun Modifier.conditional(
    condition: Boolean,
    modifier: Modifier.() -> Modifier
): Modifier {
    return if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }
}

/**
 * 条件内边距
 */
fun Modifier.conditionalPadding(
    condition: Boolean,
    padding: Dp
): Modifier = conditional(condition) {
    padding(padding)
}

/**
 * 条件内边距（所有方向）
 */
fun Modifier.conditionalPadding(
    condition: Boolean,
    horizontal: Dp = 0.dp,
    vertical: Dp = 0.dp
): Modifier = conditional(condition) {
    padding(horizontal = horizontal, vertical = vertical)
}

/**
 * 条件内边距（PaddingValues）
 */
fun Modifier.conditionalPadding(
    condition: Boolean,
    paddingValues: PaddingValues
): Modifier = conditional(condition) {
    padding(paddingValues)
}

/**
 * 底部边框
 */
fun Modifier.bottomBorder(
    strokeWidth: Dp = 1.dp,
    color: Color
) = drawBehind {
    val strokeWidthPx = strokeWidth.toPx()
    drawLine(
        color = color,
        start = Offset(0f, size.height - strokeWidthPx / 2),
        end = Offset(size.width, size.height - strokeWidthPx / 2),
        strokeWidth = strokeWidthPx
    )
}

/**
 * 顶部边框
 */
fun Modifier.topBorder(
    strokeWidth: Dp = 1.dp,
    color: Color
) = drawBehind {
    val strokeWidthPx = strokeWidth.toPx()
    drawLine(
        color = color,
        start = Offset(0f, strokeWidthPx / 2),
        end = Offset(size.width, strokeWidthPx / 2),
        strokeWidth = strokeWidthPx
    )
}

/**
 * 左边框
 */
fun Modifier.leftBorder(
    strokeWidth: Dp = 1.dp,
    color: Color
) = drawBehind {
    val strokeWidthPx = strokeWidth.toPx()
    drawLine(
        color = color,
        start = Offset(strokeWidthPx / 2, 0f),
        end = Offset(strokeWidthPx / 2, size.height),
        strokeWidth = strokeWidthPx
    )
}

/**
 * 右边框
 */
fun Modifier.rightBorder(
    strokeWidth: Dp = 1.dp,
    color: Color
) = drawBehind {
    val strokeWidthPx = strokeWidth.toPx()
    drawLine(
        color = color,
        start = Offset(size.width - strokeWidthPx / 2, 0f),
        end = Offset(size.width - strokeWidthPx / 2, size.height),
        strokeWidth = strokeWidthPx
    )
}

/**
 * 震动反馈修饰符
 */
fun Modifier.vibrationFeedback(
    enabled: Boolean = true
) = composed {
    if (enabled) {
        // 注意：实际项目中需要添加震动权限和相关依赖
        this
    } else {
        this
    }
}

/**
 * 防抖点击修饰符
 */
fun Modifier.debounceClickable(
    enabled: Boolean = true,
    debounceTime: Long = 500L,
    onClick: () -> Unit
) = composed {
    var lastClickTime by remember { mutableLongStateOf(0L) }
    
    this.conditional(enabled) {
        clickable {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime > debounceTime) {
                lastClickTime = currentTime
                onClick()
            }
        }
    }
}

/**
 * 延迟显示修饰符
 */
@Composable
fun DelayedVisibility(
    delayTime: Long = 300L,
    content: @Composable () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(delayTime)
        isVisible = true
    }
    
    if (isVisible) {
        content()
    }
}

/**
 * 动画计数器
 */
@Composable
fun AnimatedCounter(
    targetValue: Int,
    duration: Long = 1000L
) {
    var currentValue by remember { mutableIntStateOf(0) }
    
    LaunchedEffect(targetValue) {
        val startValue = currentValue
        val range = targetValue - startValue
        val stepTime = duration / kotlin.math.abs(range).coerceAtLeast(1)
        
        for (i in 0..kotlin.math.abs(range)) {
            currentValue = if (range > 0) {
                startValue + i
            } else {
                startValue - i
            }
            delay(stepTime)
        }
        currentValue = targetValue
    }
    
    androidx.compose.material3.Text(
        text = currentValue.toString(),
        style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
    )
}

/**
 * 脉冲动画修饰符
 */
fun Modifier.pulse(
    enabled: Boolean = true,
    scale: Float = 1.1f,
    duration: Int = 1000
) = composed {
    if (enabled) {
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val scaleAnimation by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = scale,
            animationSpec = infiniteRepeatable(
                animation = tween(duration),
                repeatMode = RepeatMode.Reverse
            ),
            label = "scale"
        )
        
        graphicsLayer {
            scaleX = scaleAnimation
            scaleY = scaleAnimation
        }
    } else {
        this
    }
}

/**
 * 渐变背景修饰符
 */
fun Modifier.gradientBackground(
    colors: List<Color>,
    angle: Float = 0f
) = drawBehind {
    val gradient = androidx.compose.ui.graphics.Brush.linearGradient(
        colors = colors,
        start = Offset(0f, 0f),
        end = Offset(
            size.width * kotlin.math.cos(Math.toRadians(angle.toDouble())).toFloat(),
            size.height * kotlin.math.sin(Math.toRadians(angle.toDouble())).toFloat()
        )
    )
    drawRect(gradient)
}

/**
 * 尺寸转换扩展
 */
@Composable
fun Int.toDp(): Dp {
    return with(LocalDensity.current) { this@toDp.toDp() }
}

@Composable
fun Float.toDp(): Dp {
    return with(LocalDensity.current) { this@toDp.toDp() }
}

@Composable
fun Dp.toPx(): Float {
    return with(LocalDensity.current) { this@toPx.toPx() }
}

/**
 * 安全区域内边距扩展
 */
@Composable
fun Modifier.safeContentPadding(): Modifier {
    return this.padding(
        // 注意：实际项目中应该使用 WindowInsets.systemBars
        horizontal = 16.dp,
        vertical = 8.dp
    )
}

/**
 * 响应式布局扩展
 */
@Composable
fun rememberIsTablet(): Boolean {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    return configuration.screenWidthDp >= 600
}

@Composable
fun rememberScreenWidth(): Dp {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    return configuration.screenWidthDp.dp
}

@Composable
fun rememberScreenHeight(): Dp {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    return configuration.screenHeightDp.dp
}

/**
 * 响应式内边距
 */
@Composable
fun Modifier.responsivePadding(): Modifier {
    val isTablet = rememberIsTablet()
    return padding(
        horizontal = if (isTablet) 24.dp else 16.dp,
        vertical = if (isTablet) 16.dp else 8.dp
    )
}

/**
 * 响应式列数
 */
@Composable
fun rememberResponsiveColumns(
    compact: Int = 1,
    medium: Int = 2,
    expanded: Int = 3
): Int {
    val screenWidth = rememberScreenWidth()
    return when {
        screenWidth < 600.dp -> compact
        screenWidth < 840.dp -> medium
        else -> expanded
    }
}

/**
 * 状态保存扩展
 */
@Composable
inline fun <reified T> rememberSaveableState(
    key: String? = null,
    crossinline init: () -> T
): MutableState<T> {
    return androidx.compose.runtime.saveable.rememberSaveable(key) { mutableStateOf(init()) }
}

/**
 * 懒加载状态
 */
@Composable
fun <T> rememberLazyState(
    key: Any? = null,
    computation: suspend () -> T
): State<T?> {
    var state by remember(key) { mutableStateOf<T?>(null) }
    
    LaunchedEffect(key) {
        state = computation()
    }
    
    return remember { derivedStateOf { state } }
}