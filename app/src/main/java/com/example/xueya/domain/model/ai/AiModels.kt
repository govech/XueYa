package com.example.xueya.domain.model.ai

/**
 * OpenRouter API 请求数据模型
 */
data class AiRequest(
    val model: String,
    val messages: List<Message>,
    val temperature: Double = 0.7,
    val max_tokens: Int = 1000
)

/**
 * 对话消息
 */
data class Message(
    val role: String, // "system", "user", "assistant"
    val content: String
)

/**
 * OpenRouter API 响应数据模型
 */
data class AiResponse(
    val id: String,
    val choices: List<Choice>,
    val usage: Usage?
)

/**
 * 响应选择项
 */
data class Choice(
    val message: Message,
    val finish_reason: String?
)

/**
 * API 使用统计
 */
data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)