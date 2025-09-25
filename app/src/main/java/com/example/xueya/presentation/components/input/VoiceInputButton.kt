package com.example.xueya.presentation.components.input

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.xueya.domain.usecase.VoiceInputUseCase

/**
 * 语音输入按钮组件
 */
@Composable
fun VoiceInputButton(
    state: VoiceInputUseCase.VoiceInputState,
    onStartVoiceInput: () -> Unit,
    onStopVoiceInput: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    // 动画状态
    val isActive = remember(state) {
        state is VoiceInputUseCase.VoiceInputState.Listening || 
        state is VoiceInputUseCase.VoiceInputState.Processing ||
        state is VoiceInputUseCase.VoiceInputState.AiParsing
    }
    
    val buttonColor by animateColorAsState(
        targetValue = when (state) {
            is VoiceInputUseCase.VoiceInputState.Listening -> MaterialTheme.colorScheme.error
            is VoiceInputUseCase.VoiceInputState.Processing,
            is VoiceInputUseCase.VoiceInputState.AiParsing -> MaterialTheme.colorScheme.secondary
            is VoiceInputUseCase.VoiceInputState.Success -> MaterialTheme.colorScheme.primary
            is VoiceInputUseCase.VoiceInputState.Error -> MaterialTheme.colorScheme.error
            else -> MaterialTheme.colorScheme.primary
        },
        animationSpec = tween(300)
    )
    
    val buttonScale by animateFloatAsState(
        targetValue = if (isActive) 1.1f else 1.0f,
        animationSpec = tween(300)
    )
    
    val buttonAlpha by animateFloatAsState(
        targetValue = when (state) {
            is VoiceInputUseCase.VoiceInputState.Processing,
            is VoiceInputUseCase.VoiceInputState.AiParsing -> 0.7f
            else -> 1.0f
        },
        animationSpec = tween(300)
    )
    
    // 脉冲动画
    var pulseScale by remember { mutableStateOf(0f) }
    var pulseAlpha by remember { mutableStateOf(0f) }
    
    // 启动脉冲动画
    if (isActive) {
        val infiniteTransition = rememberInfiniteTransition()
        val scaleAnimation by infiniteTransition.animateFloat(
            initialValue = 0.8f,
            targetValue = 1.8f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
        val alphaAnimation by infiniteTransition.animateFloat(
            initialValue = 0.7f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
        
        // 更新动画值
        pulseScale = scaleAnimation
        pulseAlpha = alphaAnimation
    } else {
        pulseScale = 0f
        pulseAlpha = 0f
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        // 主按钮
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(72.dp)
                .scale(buttonScale)
                .alpha(buttonAlpha)
        ) {
            // 脉冲动画圆圈
            if (isActive && pulseScale > 0 && pulseAlpha > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(pulseScale)
                        .alpha(pulseAlpha)
                        .background(
                            color = buttonColor.copy(alpha = 0.3f),
                            shape = CircleShape
                        )
                )
                
                // 第二个脉冲圆圈（延迟一点）
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(pulseScale * 0.7f)
                        .alpha(pulseAlpha * 0.7f)
                        .background(
                            color = buttonColor.copy(alpha = 0.2f),
                            shape = CircleShape
                        )
                )
            }
            
            // 主按钮
            FloatingActionButton(
                onClick = {
                    when (state) {
                        is VoiceInputUseCase.VoiceInputState.Idle,
                        is VoiceInputUseCase.VoiceInputState.Success,
                        is VoiceInputUseCase.VoiceInputState.Error -> {
                            onStartVoiceInput()
                        }
                        is VoiceInputUseCase.VoiceInputState.Listening -> {
                            onStopVoiceInput()
                        }
                        else -> {
                            // Processing 和 AiParsing 状态下不允许操作
                        }
                    }
                },
                containerColor = buttonColor,
                contentColor = Color.White,
                modifier = Modifier.size(56.dp)
            ) {
                when (state) {
                    is VoiceInputUseCase.VoiceInputState.Processing,
                    is VoiceInputUseCase.VoiceInputState.AiParsing -> {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    is VoiceInputUseCase.VoiceInputState.Listening -> {
                        // 在语音输入时显示脉冲动画
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "停止语音输入",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    else -> {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "开始语音输入",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 状态文本
        Text(
            text = when (state) {
                is VoiceInputUseCase.VoiceInputState.Idle -> "点击开始语音输入血压数据"
                is VoiceInputUseCase.VoiceInputState.Listening -> "正在听取... 请说出血压数据"
                is VoiceInputUseCase.VoiceInputState.Processing -> "处理中... 正在识别语音"
                is VoiceInputUseCase.VoiceInputState.AiParsing -> "AI解析中... 正在分析数据"
                is VoiceInputUseCase.VoiceInputState.Success -> "✅ 识别成功"
                is VoiceInputUseCase.VoiceInputState.Error -> "❌ 识别失败"
            },
            fontSize = 12.sp,
            fontWeight = if (isActive) FontWeight.Medium else FontWeight.Normal,
            color = when (state) {
                is VoiceInputUseCase.VoiceInputState.Error -> MaterialTheme.colorScheme.error
                is VoiceInputUseCase.VoiceInputState.Success -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.onSurface
            }
        )
        
        // 错误消息
        if (state is VoiceInputUseCase.VoiceInputState.Error) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = state.message,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.error,
                maxLines = 2
            )
        }
    }
}

/**
 * 紧凑版语音输入按钮
 */
@Composable
fun CompactVoiceInputButton(
    state: VoiceInputUseCase.VoiceInputState,
    onStartVoiceInput: () -> Unit,
    onStopVoiceInput: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isActive = remember(state) {
        state is VoiceInputUseCase.VoiceInputState.Listening || 
        state is VoiceInputUseCase.VoiceInputState.Processing ||
        state is VoiceInputUseCase.VoiceInputState.AiParsing
    }
    
    val buttonColor by animateColorAsState(
        targetValue = when (state) {
            is VoiceInputUseCase.VoiceInputState.Listening -> MaterialTheme.colorScheme.error
            is VoiceInputUseCase.VoiceInputState.Processing,
            is VoiceInputUseCase.VoiceInputState.AiParsing -> MaterialTheme.colorScheme.secondary
            else -> MaterialTheme.colorScheme.primary
        },
        animationSpec = tween(300)
    )
    
    IconButton(
        onClick = {
            when (state) {
                is VoiceInputUseCase.VoiceInputState.Idle,
                is VoiceInputUseCase.VoiceInputState.Success,
                is VoiceInputUseCase.VoiceInputState.Error -> {
                    onStartVoiceInput()
                }
                is VoiceInputUseCase.VoiceInputState.Listening -> {
                    onStopVoiceInput()
                }
                else -> {
                    // Processing 和 AiParsing 状态下不允许操作
                }
            }
        },
        modifier = modifier.size(40.dp)
    ) {
        when (state) {
            is VoiceInputUseCase.VoiceInputState.Processing,
            is VoiceInputUseCase.VoiceInputState.AiParsing -> {
                CircularProgressIndicator(
                    color = buttonColor,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp)
                )
            }
            is VoiceInputUseCase.VoiceInputState.Listening -> {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "停止语音输入",
                    tint = buttonColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            else -> {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "开始语音输入",
                    tint = buttonColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}