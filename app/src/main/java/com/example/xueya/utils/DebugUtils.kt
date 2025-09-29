package com.example.xueya.utils

import com.example.xueya.BuildConfig

/**
 * Debug工具类
 * 
 * 提供debug模式检测和相关的调试功能
 */
object DebugUtils {
    
    /**
     * 检查当前是否为debug模式
     * 
     * @return true如果是debug模式，false如果是release模式
     */
    fun isDebugMode(): Boolean {
        return BuildConfig.DEBUG
    }
    
    /**
     * 检查当前是否为release模式
     * 
     * @return true如果是release模式，false如果是debug模式
     */
    fun isReleaseMode(): Boolean {
        return !BuildConfig.DEBUG
    }
    
    /**
     * 获取构建类型
     * 
     * @return 构建类型字符串（debug或release）
     */
    fun getBuildType(): String {
        return if (BuildConfig.DEBUG) "debug" else "release"
    }
    
    /**
     * 获取应用版本信息
     * 
     * @return 版本信息字符串
     */
    fun getVersionInfo(): String {
        return "v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE}) - ${getBuildType()}"
    }
}
