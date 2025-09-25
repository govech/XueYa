package com.example.xueya.domain.model

/**
 * 主题模式枚举
 * 
 * 定义应用支持的主题模式选项
 * 用户可以在设置中选择不同的主题模式
 */
enum class ThemeMode {
    /**
     * 跟随系统主题
     * 
     * 根据系统设置自动切换浅色或深色主题
     */
    SYSTEM,  
    
    /**
     * 浅色模式
     * 
     * 使用浅色背景和深色文字的主题
     */
    LIGHT,   
     
    /**
     * 深色模式
     * 
     * 使用深色背景和浅色文字的主题
     */
    DARK     
}