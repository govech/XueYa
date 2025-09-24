package com.example.xueya.presentation.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * UI状态管理工具类
 */

/**
 * 可组合的资源状态
 */
sealed class Resource<out T> {
    object Loading : Resource<Nothing>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : Resource<Nothing>()
}

/**
 * UI状态包装器
 */
data class UiState<T>(
    val data: T? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRefreshing: Boolean = false
) {
    val hasData: Boolean get() = data != null
    val hasError: Boolean get() = error != null
    val isEmpty: Boolean get() = data == null && !isLoading && error == null
}

/**
 * 创建UI状态的便捷函数
 */
fun <T> uiStateOf(
    data: T? = null,
    isLoading: Boolean = false,
    error: String? = null,
    isRefreshing: Boolean = false
): UiState<T> = UiState(data, isLoading, error, isRefreshing)

/**
 * Resource到UiState的转换
 */
fun <T> Resource<T>.toUiState(): UiState<T> = when (this) {
    is Resource.Loading -> UiState(isLoading = true)
    is Resource.Success -> UiState(data = data)
    is Resource.Error -> UiState(error = message)
}

/**
 * StateFlow扩展 - 收集为UiState
 */
@Composable
fun <T> StateFlow<Resource<T>>.collectAsUiState(): State<UiState<T>> {
    return collectAsState().value.toUiState().let { 
        remember { mutableStateOf(it) }
    }
}

/**
 * Flow扩展 - 收集为UiState
 */
@Composable
fun <T> Flow<Resource<T>>.collectAsUiState(
    initial: UiState<T> = UiState()
): State<UiState<T>> {
    var state by remember { mutableStateOf(initial) }
    
    LaunchedEffect(this) {
        collect { resource ->
            state = resource.toUiState()
        }
    }
    
    return remember { derivedStateOf { state } }
}

/**
 * 异步操作状态管理
 */
class AsyncOperationState<T> {
    private val _state = MutableStateFlow<Resource<T>>(Resource.Loading)
    val state: StateFlow<Resource<T>> = _state.asStateFlow()
    
    suspend fun execute(operation: suspend () -> T) {
        _state.value = Resource.Loading
        try {
            val result = operation()
            _state.value = Resource.Success(result)
        } catch (e: Exception) {
            _state.value = Resource.Error(e.message ?: "Unknown error", e)
        }
    }
    
    fun reset() {
        _state.value = Resource.Loading
    }
}

/**
 * 分页状态管理
 */
data class PagingState<T>(
    val items: List<T> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val hasNextPage: Boolean = true,
    val currentPage: Int = 0
) {
    val isEmpty: Boolean get() = items.isEmpty() && !isLoading
    val hasError: Boolean get() = error != null
}

/**
 * 分页状态管理器
 */
class PagingStateManager<T> {
    private val _state = MutableStateFlow(PagingState<T>())
    val state: StateFlow<PagingState<T>> = _state.asStateFlow()
    
    fun startLoading() {
        _state.value = _state.value.copy(isLoading = true, error = null)
    }
    
    fun startLoadingMore() {
        _state.value = _state.value.copy(isLoadingMore = true, error = null)
    }
    
    fun setItems(items: List<T>, hasNextPage: Boolean = true) {
        _state.value = _state.value.copy(
            items = items,
            isLoading = false,
            isLoadingMore = false,
            error = null,
            hasNextPage = hasNextPage,
            currentPage = 1
        )
    }
    
    fun appendItems(newItems: List<T>, hasNextPage: Boolean = true) {
        _state.value = _state.value.copy(
            items = _state.value.items + newItems,
            isLoadingMore = false,
            error = null,
            hasNextPage = hasNextPage,
            currentPage = _state.value.currentPage + 1
        )
    }
    
    fun setError(error: String) {
        _state.value = _state.value.copy(
            isLoading = false,
            isLoadingMore = false,
            error = error
        )
    }
    
    fun refresh() {
        _state.value = PagingState(isLoading = true)
    }
}

/**
 * 表单状态管理
 */
data class FormFieldState(
    val value: String = "",
    val error: String? = null,
    val isValid: Boolean = true
) {
    val hasError: Boolean get() = error != null
}

class FormStateManager {
    private val _fields = mutableMapOf<String, MutableStateFlow<FormFieldState>>()
    
    fun getField(name: String): StateFlow<FormFieldState> {
        return _fields.getOrPut(name) { 
            MutableStateFlow(FormFieldState()) 
        }.asStateFlow()
    }
    
    fun updateField(name: String, value: String, error: String? = null) {
        val field = _fields.getOrPut(name) { MutableStateFlow(FormFieldState()) }
        field.value = FormFieldState(
            value = value,
            error = error,
            isValid = error == null
        )
    }
    
    fun validateField(name: String, validator: (String) -> String?): Boolean {
        val field = _fields[name] ?: return false
        val currentValue = field.value.value
        val error = validator(currentValue)
        
        field.value = field.value.copy(
            error = error,
            isValid = error == null
        )
        
        return error == null
    }
    
    fun isFormValid(): Boolean {
        return _fields.values.all { it.value.isValid }
    }
    
    fun getFormData(): Map<String, String> {
        return _fields.mapValues { it.value.value.value }
    }
    
    fun reset() {
        _fields.values.forEach { field ->
            field.value = FormFieldState()
        }
    }
}

/**
 * ViewModel扩展函数
 */
fun ViewModel.launchAsync(
    block: suspend () -> Unit,
    onError: (Throwable) -> Unit = {}
) {
    viewModelScope.launch {
        try {
            block()
        } catch (e: Exception) {
            onError(e)
        }
    }
}

/**
 * 安全的异步执行
 */
suspend fun <T> safeExecute(
    block: suspend () -> T,
    onError: (Throwable) -> T
): T {
    return try {
        block()
    } catch (e: Exception) {
        onError(e)
    }
}

/**
 * 重试机制
 */
suspend fun <T> retryOperation(
    maxAttempts: Int = 3,
    delayMillis: Long = 1000,
    operation: suspend () -> T
): T {
    var lastException: Exception? = null
    
    repeat(maxAttempts) { attempt ->
        try {
            return operation()
        } catch (e: Exception) {
            lastException = e
            if (attempt < maxAttempts - 1) {
                kotlinx.coroutines.delay(delayMillis)
            }
        }
    }
    
    throw lastException ?: Exception("Operation failed after $maxAttempts attempts")
}

/**
 * 缓存状态管理
 */
class CacheStateManager<K, V> {
    private val cache = mutableMapOf<K, V>()
    private val timestamps = mutableMapOf<K, Long>()
    private val cacheTimeout = 5 * 60 * 1000L // 5分钟
    
    fun get(key: K): V? {
        val timestamp = timestamps[key]
        return if (timestamp != null && 
                   System.currentTimeMillis() - timestamp < cacheTimeout) {
            cache[key]
        } else {
            remove(key)
            null
        }
    }
    
    fun put(key: K, value: V) {
        cache[key] = value
        timestamps[key] = System.currentTimeMillis()
    }
    
    fun remove(key: K) {
        cache.remove(key)
        timestamps.remove(key)
    }
    
    fun clear() {
        cache.clear()
        timestamps.clear()
    }
    
    fun cleanup() {
        val currentTime = System.currentTimeMillis()
        val expiredKeys = timestamps.filter { (_, timestamp) ->
            currentTime - timestamp >= cacheTimeout
        }.keys
        
        expiredKeys.forEach { key ->
            remove(key)
        }
    }
}

/**
 * 通用加载状态组合函数
 */
@Composable
fun <T> LoadingContent(
    state: UiState<T>,
    onRetry: () -> Unit = {},
    loadingContent: @Composable () -> Unit = { 
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    },
    errorContent: @Composable (String) -> Unit = { error ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text(
                    text = "重试"
                )
            }
        }
    },
    content: @Composable (T) -> Unit
) {
    when {
        state.isLoading -> loadingContent()
        state.hasError -> errorContent(state.error!!)
        state.hasData -> content(state.data!!)
    }
}