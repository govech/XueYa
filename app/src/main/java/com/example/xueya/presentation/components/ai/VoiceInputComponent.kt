package com.example.xueya.presentation.components.ai

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.xueya.R
import com.example.xueya.domain.model.ai.AiParseState

/**
 * 语音输入组件
 */
@Composable
fun VoiceInputButton(
    parseState: AiParseState,
    onStartListening: () -> Unit,
    onStopListening: () -> Unit,
    onTextInput: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showTextInputDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 语音输入按钮
        when (parseState) {
            is AiParseState.Loading -> {
                Button(
                    onClick = { },
                    enabled = false,
                    modifier = Modifier.size(64.dp),
                    shape = RoundedCornerShape(32.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                
                Text(
                    text = "AI 解析中...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            else -> {
                Button(
                    onClick = onStartListening,
                    modifier = Modifier.size(64.dp),
                    shape = RoundedCornerShape(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "语音输入",
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Text(
                    text = "语音输入",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 文本输入按钮
        OutlinedButton(
            onClick = { showTextInputDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("或者手动输入")
        }
        
        // 显示解析状态
        when (parseState) {
            is AiParseState.Success<*> -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = "✅ 解析成功！",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            is AiParseState.Error -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = "❌ ${parseState.message}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            
            else -> { /* 其他状态不显示 */ }
        }
    }
    
    // 文本输入对话框
    if (showTextInputDialog) {
        TextInputDialog(
            onConfirm = { text ->
                onTextInput(text)
                showTextInputDialog = false
            },
            onDismiss = { showTextInputDialog = false }
        )
    }
}

/**
 * 文本输入对话框
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TextInputDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("输入血压信息")
        },
        text = {
            Column {
                Text(
                    text = "请输入血压相关信息，例如：",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "\"我今天的血压是130/80，心率75\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("血压信息") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { 
                    if (text.isNotBlank()) {
                        onConfirm(text)
                    }
                },
                enabled = text.isNotBlank()
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}