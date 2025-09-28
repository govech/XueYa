package com.example.xueya.presentation.utils

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.xueya.domain.model.DietColorScheme

/**
 * 饮食方案颜色管理器
 * 负责管理不同饮食方案的颜色主题
 */
object DietColorManager {

    /**
     * 根据颜色主题获取渐变画笔
     */
    fun getGradientBrush(colorScheme: DietColorScheme): Brush {
        return when (colorScheme) {
            DietColorScheme.DEFAULT -> Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFF4CAF50), // Material Green 500
                    Color(0xFF8BC34A)  // Material Light Green 400
                )
            )
            DietColorScheme.MEDITERRANEAN -> Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFF2196F3), // Material Blue 500
                    Color(0xFF64B5F6)  // Material Light Blue 300
                )
            )
            DietColorScheme.DASH -> Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFF9C27B0), // Material Purple 500
                    Color(0xFFBA68C8)  // Material Purple 300
                )
            )
            DietColorScheme.LOW_SODIUM -> Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFFFF9800), // Material Orange 500
                    Color(0xFFFFB74D)  // Material Orange 300
                )
            )
            DietColorScheme.PLANT_BASED -> Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFF4CAF50), // Material Green 500
                    Color(0xFF81C784)  // Material Light Green 300
                )
            )
            DietColorScheme.KETO -> Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFFF44336), // Material Red 500
                    Color(0xFFEF5350)  // Material Red 400
                )
            )
            DietColorScheme.VEGAN -> Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFF2E7D32), // Material Dark Green 800
                    Color(0xFF66BB6A)  // Material Green 400
                )
            )
            DietColorScheme.PALEO -> Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFF8D6E63), // Material Brown 400
                    Color(0xFFA1887F)  // Material Brown 300
                )
            )
        }
    }

    /**
     * 根据颜色主题获取主色调
     */
    fun getPrimaryColor(colorScheme: DietColorScheme): Color {
        return when (colorScheme) {
            DietColorScheme.DEFAULT -> Color(0xFF4CAF50)
            DietColorScheme.MEDITERRANEAN -> Color(0xFF2196F3)
            DietColorScheme.DASH -> Color(0xFF9C27B0)
            DietColorScheme.LOW_SODIUM -> Color(0xFFFF9800)
            DietColorScheme.PLANT_BASED -> Color(0xFF4CAF50)
            DietColorScheme.KETO -> Color(0xFFF44336)
            DietColorScheme.VEGAN -> Color(0xFF2E7D32)
            DietColorScheme.PALEO -> Color(0xFF8D6E63)
        }
    }

    /**
     * 根据颜色主题获取浅色调
     */
    fun getLightColor(colorScheme: DietColorScheme): Color {
        return when (colorScheme) {
            DietColorScheme.DEFAULT -> Color(0xFF8BC34A)
            DietColorScheme.MEDITERRANEAN -> Color(0xFF64B5F6)
            DietColorScheme.DASH -> Color(0xFFBA68C8)
            DietColorScheme.LOW_SODIUM -> Color(0xFFFFB74D)
            DietColorScheme.PLANT_BASED -> Color(0xFF81C784)
            DietColorScheme.KETO -> Color(0xFFEF5350)
            DietColorScheme.VEGAN -> Color(0xFF66BB6A)
            DietColorScheme.PALEO -> Color(0xFFA1887F)
        }
    }

    /**
     * 根据颜色主题获取容器颜色
     */
    fun getContainerColor(colorScheme: DietColorScheme): Color {
        return when (colorScheme) {
            DietColorScheme.DEFAULT -> Color(0xFFE8F5E8)
            DietColorScheme.MEDITERRANEAN -> Color(0xFFE3F2FD)
            DietColorScheme.DASH -> Color(0xFFF3E5F5)
            DietColorScheme.LOW_SODIUM -> Color(0xFFFFF3E0)
            DietColorScheme.PLANT_BASED -> Color(0xFFE8F5E8)
            DietColorScheme.KETO -> Color(0xFFFFEBEE)
            DietColorScheme.VEGAN -> Color(0xFFE8F5E8)
            DietColorScheme.PALEO -> Color(0xFFEFEBE9)
        }
    }

    /**
     * 根据颜色主题获取文字颜色
     */
    fun getTextColor(colorScheme: DietColorScheme): Color {
        return when (colorScheme) {
            DietColorScheme.DEFAULT -> Color(0xFF2E7D32)
            DietColorScheme.MEDITERRANEAN -> Color(0xFF1565C0)
            DietColorScheme.DASH -> Color(0xFF7B1FA2)
            DietColorScheme.LOW_SODIUM -> Color(0xFFE65100)
            DietColorScheme.PLANT_BASED -> Color(0xFF2E7D32)
            DietColorScheme.KETO -> Color(0xFFC62828)
            DietColorScheme.VEGAN -> Color(0xFF1B5E20)
            DietColorScheme.PALEO -> Color(0xFF5D4037)
        }
    }
}
