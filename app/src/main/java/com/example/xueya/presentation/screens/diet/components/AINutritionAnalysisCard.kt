package com.example.xueya.presentation.screens.diet.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.xueya.R
import com.example.xueya.presentation.utils.DietColorManager

/**
 * AI营养分析卡片
 * 显示基于用户血压数据的营养分析结果
 */
@Composable
fun AINutritionAnalysisCard(
    planId: String,
    isEnglish: Boolean,
    modifier: Modifier = Modifier
) {
    // 模拟AI分析数据
    val nutritionData = getMockNutritionData(planId)
    val colorScheme = when (planId) {
        "mediterranean" -> com.example.xueya.domain.model.DietColorScheme.MEDITERRANEAN
        "dash" -> com.example.xueya.domain.model.DietColorScheme.DASH
        "low_sodium" -> com.example.xueya.domain.model.DietColorScheme.LOW_SODIUM
        "plant_based" -> com.example.xueya.domain.model.DietColorScheme.PLANT_BASED
        "keto" -> com.example.xueya.domain.model.DietColorScheme.KETO
        else -> com.example.xueya.domain.model.DietColorScheme.DEFAULT
    }
    
    val primaryColor = DietColorManager.getPrimaryColor(colorScheme)
    val containerColor = DietColorManager.getContainerColor(colorScheme)

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // 标题
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = primaryColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isEnglish) "AI Nutrition Analysis" else "AI营养分析",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                )
            }

            // 分析结果
            Text(
                text = if (isEnglish) nutritionData.analysis else nutritionData.analysisZh,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.4
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 营养指标
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NutritionIndicator(
                    label = if (isEnglish) "Sodium" else "钠",
                    value = nutritionData.sodium,
                    unit = "mg",
                    color = primaryColor
                )
                NutritionIndicator(
                    label = if (isEnglish) "Potassium" else "钾",
                    value = nutritionData.potassium,
                    unit = "mg",
                    color = primaryColor
                )
                NutritionIndicator(
                    label = if (isEnglish) "Fiber" else "纤维",
                    value = nutritionData.fiber,
                    unit = "g",
                    color = primaryColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // AI建议
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = primaryColor.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isEnglish) nutritionData.recommendation else nutritionData.recommendationZh,
                        style = MaterialTheme.typography.bodyMedium,
                        color = primaryColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun NutritionIndicator(
    label: String,
    value: String,
    unit: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = unit,
            style = MaterialTheme.typography.bodySmall,
            color = color.copy(alpha = 0.7f)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// 模拟数据
private fun getMockNutritionData(planId: String): NutritionData {
    return when (planId) {
        "mediterranean" -> NutritionData(
            analysis = "This Mediterranean diet plan provides excellent cardiovascular benefits with high omega-3 fatty acids from fish and olive oil, plus abundant antioxidants from fruits and vegetables.",
            analysisZh = "这个地中海饮食方案通过鱼类和橄榄油提供丰富的omega-3脂肪酸，以及水果蔬菜中的抗氧化剂，对心血管健康非常有益。",
            sodium = "1,200",
            potassium = "4,500",
            fiber = "35",
            recommendation = "Focus on fresh fish 2-3 times per week and use extra virgin olive oil daily.",
            recommendationZh = "每周食用新鲜鱼类2-3次，每天使用特级初榨橄榄油。"
        )
        "dash" -> NutritionData(
            analysis = "The DASH diet is specifically designed for blood pressure control with low sodium and high potassium content, making it ideal for hypertension management.",
            analysisZh = "DASH饮食专门为血压控制设计，低钠高钾的特点使其非常适合高血压管理。",
            sodium = "1,500",
            potassium = "5,200",
            fiber = "30",
            recommendation = "Gradually reduce sodium intake and increase potassium-rich foods like bananas and spinach.",
            recommendationZh = "逐渐减少钠摄入，增加香蕉、菠菜等富钾食物。"
        )
        "low_sodium" -> NutritionData(
            analysis = "This low-sodium diet significantly reduces sodium intake to help lower blood pressure and reduce cardiovascular risk.",
            analysisZh = "这个低钠饮食显著减少钠摄入，有助于降低血压和减少心血管风险。",
            sodium = "800",
            potassium = "3,800",
            fiber = "25",
            recommendation = "Read food labels carefully and use herbs and spices for flavoring instead of salt.",
            recommendationZh = "仔细阅读食品标签，使用香草和香料调味而不是盐。"
        )
        "plant_based" -> NutritionData(
            analysis = "Plant-based diet provides high fiber, antioxidants, and potassium while being naturally low in sodium and saturated fats.",
            analysisZh = "植物性饮食提供高纤维、抗氧化剂和钾，同时天然低钠和低饱和脂肪。",
            sodium = "1,000",
            potassium = "4,800",
            fiber = "40",
            recommendation = "Ensure adequate protein from legumes, nuts, and seeds, and consider B12 supplementation.",
            recommendationZh = "确保从豆类、坚果和种子中获取充足蛋白质，考虑补充B12。"
        )
        "keto" -> NutritionData(
            analysis = "Ketogenic diet provides high healthy fats and very low carbohydrates, which may help with certain blood pressure conditions through weight loss and metabolic changes.",
            analysisZh = "生酮饮食提供高健康脂肪和极低碳水化合物，通过减重和代谢变化可能有助于某些血压状况。",
            sodium = "2,500",
            potassium = "3,200",
            fiber = "15",
            recommendation = "Monitor ketone levels and ensure adequate hydration while following this diet.",
            recommendationZh = "遵循此饮食时监测酮体水平并确保充足的水分摄入。"
        )
        else -> NutritionData(
            analysis = "This diet plan provides balanced nutrition with moderate sodium and good potassium content for general health maintenance.",
            analysisZh = "这个饮食方案提供均衡营养，钠含量适中，钾含量良好，适合一般健康维护。",
            sodium = "2,000",
            potassium = "3,500",
            fiber = "25",
            recommendation = "Maintain a balanced approach with regular monitoring of blood pressure.",
            recommendationZh = "保持平衡方法，定期监测血压。"
        )
    }
}

private data class NutritionData(
    val analysis: String,
    val analysisZh: String,
    val sodium: String,
    val potassium: String,
    val fiber: String,
    val recommendation: String,
    val recommendationZh: String
)
