package com.example.xueya.presentation.screens.diet.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
 * AI食物推荐卡片
 * 基于饮食方案和用户偏好推荐具体食物搭配
 */
@Composable
fun AIFoodRecommendationsCard(
    planId: String,
    isEnglish: Boolean,
    modifier: Modifier = Modifier
) {
    val foodRecommendations = getMockFoodRecommendations(planId)
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
                    imageVector = Icons.Default.Menu,
                    contentDescription = null,
                    tint = primaryColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isEnglish) "AI Food Recommendations" else "AI食物推荐",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                )
            }

            // 推荐说明
            Text(
                text = if (isEnglish) foodRecommendations.description else foodRecommendations.descriptionZh,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 食物推荐列表
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(foodRecommendations.foods) { food ->
                    FoodItemCard(
                        food = food,
                        primaryColor = primaryColor,
                        isEnglish = isEnglish
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // AI搭配建议
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
                        text = if (isEnglish) foodRecommendations.combinationTip else foodRecommendations.combinationTipZh,
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
private fun FoodItemCard(
    food: FoodItem,
    primaryColor: Color,
    isEnglish: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.width(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = primaryColor.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = food.emoji,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isEnglish) food.name else food.nameZh,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = primaryColor,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = food.benefit,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

// 模拟数据
private fun getMockFoodRecommendations(planId: String): FoodRecommendations {
    return when (planId) {
        "mediterranean" -> FoodRecommendations(
            description = "Based on your blood pressure data, these Mediterranean foods will provide optimal cardiovascular benefits:",
            descriptionZh = "根据您的血压数据，这些地中海食物将提供最佳的心血管益处：",
            foods = listOf(
                FoodItem("🫒", "Olive Oil", "橄榄油", "Heart healthy fats"),
                FoodItem("🐟", "Salmon", "三文鱼", "Omega-3 fatty acids"),
                FoodItem("🥜", "Walnuts", "核桃", "Antioxidants"),
                FoodItem("🍅", "Tomatoes", "番茄", "Lycopene"),
                FoodItem("🥬", "Spinach", "菠菜", "Folate & iron"),
                FoodItem("🍇", "Grapes", "葡萄", "Resveratrol")
            ),
            combinationTip = "Try olive oil with tomatoes and basil for maximum antioxidant absorption.",
            combinationTipZh = "尝试橄榄油配番茄和罗勒，以获得最大的抗氧化剂吸收。"
        )
        "dash" -> FoodRecommendations(
            description = "These DASH-friendly foods are specifically chosen to help lower your blood pressure:",
            descriptionZh = "这些DASH友好食物专门选择来帮助降低您的血压：",
            foods = listOf(
                FoodItem("🍌", "Bananas", "香蕉", "High potassium"),
                FoodItem("🥛", "Low-fat Milk", "低脂牛奶", "Calcium"),
                FoodItem("🥕", "Carrots", "胡萝卜", "Beta-carotene"),
                FoodItem("🥔", "Sweet Potatoes", "红薯", "Potassium"),
                FoodItem("🥜", "Almonds", "杏仁", "Magnesium"),
                FoodItem("🍓", "Strawberries", "草莓", "Vitamin C")
            ),
            combinationTip = "Combine potassium-rich foods with low-sodium options for best results.",
            combinationTipZh = "将富钾食物与低钠选择结合以获得最佳效果。"
        )
        "low_sodium" -> FoodRecommendations(
            description = "These naturally low-sodium foods will help you maintain flavor while reducing salt:",
            descriptionZh = "这些天然低钠食物将帮助您在减少盐的同时保持风味：",
            foods = listOf(
                FoodItem("🍋", "Lemons", "柠檬", "Natural flavoring"),
                FoodItem("🧄", "Garlic", "大蒜", "Aromatic seasoning"),
                FoodItem("🌿", "Herbs", "香草", "Fresh taste"),
                FoodItem("🥒", "Cucumbers", "黄瓜", "Hydrating"),
                FoodItem("🍎", "Apples", "苹果", "Natural sweetness"),
                FoodItem("🥑", "Avocado", "牛油果", "Healthy fats")
            ),
            combinationTip = "Use herbs and citrus to replace salt for enhanced flavor.",
            combinationTipZh = "使用香草和柑橘来替代盐以增强风味。"
        )
        "plant_based" -> FoodRecommendations(
            description = "These plant-based foods provide complete nutrition while supporting blood pressure health:",
            descriptionZh = "这些植物性食物提供完整营养，同时支持血压健康：",
            foods = listOf(
                FoodItem("🥬", "Kale", "羽衣甘蓝", "Iron & calcium"),
                FoodItem("🫘", "Lentils", "扁豆", "Plant protein"),
                FoodItem("🥜", "Chia Seeds", "奇亚籽", "Omega-3"),
                FoodItem("🍄", "Mushrooms", "蘑菇", "B vitamins"),
                FoodItem("🥕", "Beets", "甜菜", "Nitrates"),
                FoodItem("🌰", "Cashews", "腰果", "Healthy fats")
            ),
            combinationTip = "Combine different plant proteins throughout the day for complete amino acids.",
            combinationTipZh = "全天结合不同的植物蛋白以获得完整的氨基酸。"
        )
        "keto" -> FoodRecommendations(
            description = "These keto-friendly foods provide high energy while maintaining low carbohydrate intake:",
            descriptionZh = "这些生酮友好食物提供高能量，同时保持低碳水化合物摄入：",
            foods = listOf(
                FoodItem("🥑", "Avocado", "牛油果", "Healthy fats"),
                FoodItem("🥩", "Grass-fed Beef", "草饲牛肉", "High protein"),
                FoodItem("🐟", "Salmon", "三文鱼", "Omega-3"),
                FoodItem("🥚", "Eggs", "鸡蛋", "Complete protein"),
                FoodItem("🥜", "Macadamia Nuts", "夏威夷果", "MCT fats"),
                FoodItem("🧀", "Cheese", "奶酪", "Calcium & fat")
            ),
            combinationTip = "Focus on high-fat, low-carb foods while monitoring ketone levels.",
            combinationTipZh = "专注于高脂肪、低碳水食物，同时监测酮体水平。"
        )
        else -> FoodRecommendations(
            description = "These balanced foods will support your overall health and blood pressure management:",
            descriptionZh = "这些均衡食物将支持您的整体健康和血压管理：",
            foods = listOf(
                FoodItem("🥗", "Mixed Greens", "混合蔬菜", "Vitamins"),
                FoodItem("🐟", "White Fish", "白鱼", "Lean protein"),
                FoodItem("🍚", "Brown Rice", "糙米", "Fiber"),
                FoodItem("🥕", "Vegetables", "蔬菜", "Antioxidants"),
                FoodItem("🍎", "Fruits", "水果", "Natural sugars"),
                FoodItem("🥜", "Nuts", "坚果", "Healthy fats")
            ),
            combinationTip = "Maintain variety in your food choices for balanced nutrition.",
            combinationTipZh = "保持食物选择的多样性以获得均衡营养。"
        )
    }
}

private data class FoodRecommendations(
    val description: String,
    val descriptionZh: String,
    val foods: List<FoodItem>,
    val combinationTip: String,
    val combinationTipZh: String
)

private data class FoodItem(
    val emoji: String,
    val name: String,
    val nameZh: String,
    val benefit: String
)
