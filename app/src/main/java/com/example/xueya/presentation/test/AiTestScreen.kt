package com.example.xueya.presentation.test

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.xueya.domain.model.ai.AiParseState
import com.example.xueya.presentation.components.ai.VoiceInputButton

/**
 * AI 功能测试页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiTestScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AiTestViewModel = hiltViewModel()
) {
    val parseState by viewModel.parseState.collectAsState()
    val lastResult by viewModel.lastResult.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI 功能测试") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "AI 文本解析测试",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "请输入血压相关文本，测试 AI 解析功能。例如：",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("• 我今天的血压是130/80，心率75")
                    Text("• 血压测量结果：收缩压120，舒张压85")
                    Text("• 高压140低压90脉搏80")
                    Text("• 血压160/100，感觉有点头晕")
                }
            }
            
            // AI 输入组件
            VoiceInputButton(
                parseState = parseState,
                onStartListening = viewModel::startVoiceInput,
                onStopListening = viewModel::stopVoiceInput,
                onTextInput = viewModel::parseText,
                modifier = Modifier.fillMaxWidth()
            )
            
            // 显示解析结果
            lastResult?.let { result ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (result.isValid) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.errorContainer
                        }
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "解析结果",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Text("收缩压: ${result.systolic ?: "未识别"}")
                        Text("舒张压: ${result.diastolic ?: "未识别"}")
                        Text("心率: ${result.pulse ?: "未识别"}")
                        Text("备注: ${result.notes ?: "无"}")
                        Text("置信度: ${String.format("%.2f", result.confidence)}")
                        Text("是否有效: ${if (result.isValid) "是" else "否"}")
                    }
                }
            }
            
            // 快速测试按钮
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "快速测试",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.parseText("我今天的血压是130/80，心率75") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("测试1")
                        }
                        
                        Button(
                            onClick = { viewModel.parseText("高压140低压90脉搏80") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("测试2")
                        }
                    }
                    
                    Button(
                        onClick = { viewModel.parseText("血压160/100，感觉有点头晕") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("测试3")
                    }
                }
            }
        }
    }
}