package com.example.xueya.data.network

import com.example.xueya.domain.model.ai.AiRequest
import com.example.xueya.domain.model.ai.AiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * AI API 服务接口
 * 用于与 OpenRouter 进行通信
 */
interface AiApiService {
    
    /**
     * 聊天补全 API
     * @param authorization Bearer token 认证
     * @param contentType 内容类型
     * @param request AI 请求数据
     * @return AI 响应数据
     */
    @POST("api/v1/chat/completions")
    suspend fun chatCompletion(
        @Header("Authorization") authorization: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Body request: AiRequest
    ): Response<AiResponse>
}