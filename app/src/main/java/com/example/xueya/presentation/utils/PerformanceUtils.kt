package com.example.xueya.presentation.utils

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.ConcurrentHashMap

/**
 * 性能优化工具类
 */
object PerformanceUtils {
    
    /**
     * 颜色缓存 - 避免重复创建Color对象
     */
    private val colorCache = ConcurrentHashMap<Int, Color>()
    
    fun getCachedColor(colorInt: Int): Color {
        return colorCache.getOrPut(colorInt) { Color(colorInt) }
    }
    
    /**
     * 状态缓存管理器 - 避免重复计算
     */
    private val stateCache = ConcurrentHashMap<String, Any>()
    
    @Suppress("UNCHECKED_CAST")
    fun <T> getCachedState(key: String, factory: () -> T): T {
        return stateCache.getOrPut(key, factory) as T
    }
    
    fun clearStateCache() {
        stateCache.clear()
    }
    
    /**
     * 计算结果缓存
     */
    private val computationCache = ConcurrentHashMap<String, Any>()
    
    @Suppress("UNCHECKED_CAST")
    fun <T> getCachedComputation(key: String, computation: () -> T): T {
        return computationCache.getOrPut(key, computation) as T
    }
    
    /**
     * 内存压力监控
     */
    private val _memoryPressure = MutableStateFlow(false)
    val memoryPressure: StateFlow<Boolean> = _memoryPressure
    
    fun reportMemoryPressure() {
        _memoryPressure.value = true
        // 清理缓存
        if (stateCache.size > 100) {
            stateCache.clear()
        }
        if (computationCache.size > 100) {
            computationCache.clear()
        }
        _memoryPressure.value = false
    }
}

/**
 * 记忆化计算装饰器
 */
@Composable
fun <T> rememberMemoized(
    key: Any?,
    calculation: () -> T
): T {
    return remember(key) { calculation() }
}

/**
 * 稳定的参考对象 - 避免不必要的重组
 */
@Stable
data class StableWrapper<T>(val value: T)

/**
 * 创建稳定的包装器
 */
@Composable
fun <T> rememberStable(value: T): StableWrapper<T> {
    return remember(value) { StableWrapper(value) }
}

/**
 * 延迟计算装饰器
 */
@Composable
fun <T> rememberLazy(calculation: () -> T): Lazy<T> {
    return remember { lazy(calculation) }
}

/**
 * 列表项目稳定包装器
 */
@Stable
data class StableListWrapper<T>(val items: List<T>, val version: Int = items.hashCode())

@Composable
fun <T> List<T>.toStable(): StableListWrapper<T> {
    return remember(this) { StableListWrapper(this) }
}

/**
 * 性能监控组件
 */
@Composable
fun PerformanceMonitor(
    content: @Composable () -> Unit
) {
    val startTime = remember { System.currentTimeMillis() }
    
    LaunchedEffect(Unit) {
        // 监控组件渲染时间
        val renderTime = System.currentTimeMillis() - startTime
        if (renderTime > 16) { // 超过一帧时间
            println("Performance Warning: Component render time: ${renderTime}ms")
        }
    }
    
    content()
}

/**
 * 图片缓存键生成器
 */
object ImageCacheKeyGenerator {
    fun generateKey(url: String, width: Int, height: Int): String {
        return "${url}_${width}x${height}"
    }
}