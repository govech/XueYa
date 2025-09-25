package com.example.xueya.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * 应用形状系统定义
 * 
 * 定义血压监测应用的Material Design 3形状系统
 * 包括不同圆角大小的形状定义，用于确保应用中UI元素的一致性
 */
val Shapes = Shapes(
    /**
     * 超小圆角形状
     * 用于小型UI元素，如小按钮、标签等
     */
    extraSmall = RoundedCornerShape(4.dp),
    
    /**
     * 小圆角形状
     * 用于中等UI元素，如输入框、小卡片等
     */
    small = RoundedCornerShape(8.dp),
    
    /**
     * 中等圆角形状
     * 用于较大UI元素，如卡片、列表项等
     */
    medium = RoundedCornerShape(12.dp),
    
    /**
     * 大圆角形状
     * 用于大型UI元素，如对话框、大卡片等
     */
    large = RoundedCornerShape(16.dp),
    
    /**
     * 超大圆角形状
     * 用于特殊UI元素，如全屏卡片等
     */
    extraLarge = RoundedCornerShape(28.dp)
)