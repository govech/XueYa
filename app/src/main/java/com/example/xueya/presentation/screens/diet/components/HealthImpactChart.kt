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
 * 健康影响图表组件
 * 显示饮食方案对血压的潜在影响预测
 */
@Composable
fun HealthImpactChart(
    planId: String,
    isEnglish: Boolean,
    modifier: Modifier = Modifier
) {
    val healthImpact = getMockHealthImpact(planId)
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
                    text = if (isEnglish) "Health Impact Prediction" else "健康影响预测",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                )
            }

            // 预测说明
            Text(
                text = if (isEnglish) healthImpact.description else healthImpact.descriptionZh,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // 血压改善预测
            BloodPressureImprovementCard(
                currentSystolic = healthImpact.currentSystolic,
                predictedSystolic = healthImpact.predictedSystolic,
                currentDiastolic = healthImpact.currentDiastolic,
                predictedDiastolic = healthImpact.predictedDiastolic,
                primaryColor = primaryColor,
                isEnglish = isEnglish
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 时间线预测
            TimelinePredictionCard(
                timeline = healthImpact.timeline,
                primaryColor = primaryColor,
                isEnglish = isEnglish
            )

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
                        text = if (isEnglish) healthImpact.aiAdvice else healthImpact.aiAdviceZh,
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
private fun BloodPressureImprovementCard(
    currentSystolic: Int,
    predictedSystolic: Int,
    currentDiastolic: Int,
    predictedDiastolic: Int,
    primaryColor: Color,
    isEnglish: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = primaryColor.copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = if (isEnglish) "Blood Pressure Improvement" else "血压改善",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = primaryColor,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                BloodPressureIndicator(
                    label = if (isEnglish) "Systolic" else "收缩压",
                    current = currentSystolic,
                    predicted = predictedSystolic,
                    color = primaryColor,
                    isEnglish = isEnglish
                )
                BloodPressureIndicator(
                    label = if (isEnglish) "Diastolic" else "舒张压",
                    current = currentDiastolic,
                    predicted = predictedDiastolic,
                    color = primaryColor,
                    isEnglish = isEnglish
                )
            }
        }
    }
}

@Composable
private fun BloodPressureIndicator(
    label: String,
    current: Int,
    predicted: Int,
    color: Color,
    isEnglish: Boolean,
    modifier: Modifier = Modifier
) {
    val improvement = current - predicted
    val improvementText = if (improvement > 0) "-$improvement" else "+${-improvement}"
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // 当前值
        Text(
            text = current.toString(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Text(
            text = if (isEnglish) "Current" else "当前",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 箭头
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 预测值
        Text(
            text = predicted.toString(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        
        Text(
            text = if (isEnglish) "Predicted" else "预测",
            style = MaterialTheme.typography.labelSmall,
            color = color.copy(alpha = 0.7f)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // 改善幅度
        Text(
            text = improvementText,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = if (improvement > 0) color else MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun TimelinePredictionCard(
    timeline: List<TimelineItem>,
    primaryColor: Color,
    isEnglish: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = primaryColor.copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = if (isEnglish) "Timeline Prediction" else "时间线预测",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = primaryColor,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            timeline.forEach { item ->
                TimelineItemRow(
                    item = item,
                    primaryColor = primaryColor,
                    isEnglish = isEnglish
                )
                if (item != timeline.last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun TimelineItemRow(
    item: TimelineItem,
    primaryColor: Color,
    isEnglish: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 时间点
        Card(
            colors = CardDefaults.cardColors(
                containerColor = primaryColor
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = item.timeframe,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // 描述
        Text(
            text = if (isEnglish) item.description else item.descriptionZh,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}

// 模拟数据
private fun getMockHealthImpact(planId: String): HealthImpactData {
    return when (planId) {
        "mediterranean" -> HealthImpactData(
            description = "Based on your current blood pressure readings, this Mediterranean diet could significantly improve your cardiovascular health:",
            descriptionZh = "根据您当前的血压读数，这个地中海饮食可以显著改善您的心血管健康：",
            currentSystolic = 145,
            predictedSystolic = 130,
            currentDiastolic = 90,
            predictedDiastolic = 80,
            timeline = listOf(
                TimelineItem("2 weeks", "Initial blood pressure reduction", "2周内血压开始下降"),
                TimelineItem("1 month", "Noticeable improvement in energy", "1个月内精力明显改善"),
                TimelineItem("3 months", "Significant cardiovascular benefits", "3个月内显著心血管益处"),
                TimelineItem("6 months", "Optimal blood pressure levels", "6个月内达到最佳血压水平")
            ),
            aiAdvice = "Focus on daily olive oil consumption and weekly fish intake for maximum benefits.",
            aiAdviceZh = "专注于每日橄榄油消费和每周鱼类摄入以获得最大益处。"
        )
        "dash" -> HealthImpactData(
            description = "The DASH diet is specifically designed for blood pressure control and should show results quickly:",
            descriptionZh = "DASH饮食专门为血压控制设计，应该能快速显示效果：",
            currentSystolic = 150,
            predictedSystolic = 125,
            currentDiastolic = 95,
            predictedDiastolic = 75,
            timeline = listOf(
                TimelineItem("1 week", "Sodium reduction effects", "1周内钠减少效果"),
                TimelineItem("2 weeks", "Blood pressure starts dropping", "2周内血压开始下降"),
                TimelineItem("1 month", "Significant improvement", "1个月内显著改善"),
                TimelineItem("3 months", "Target blood pressure achieved", "3个月内达到目标血压")
            ),
            aiAdvice = "Gradually reduce sodium while increasing potassium-rich foods for best results.",
            aiAdviceZh = "逐渐减少钠的同时增加富钾食物以获得最佳效果。"
        )
        "low_sodium" -> HealthImpactData(
            description = "This low-sodium approach will provide immediate benefits for blood pressure control:",
            descriptionZh = "这种低钠方法将为血压控制提供即时益处：",
            currentSystolic = 155,
            predictedSystolic = 135,
            currentDiastolic = 100,
            predictedDiastolic = 85,
            timeline = listOf(
                TimelineItem("3 days", "Initial sodium reduction", "3天内钠减少"),
                TimelineItem("1 week", "Blood pressure improvement", "1周内血压改善"),
                TimelineItem("2 weeks", "Noticeable health benefits", "2周内明显健康益处"),
                TimelineItem("1 month", "Stable blood pressure", "1个月内血压稳定")
            ),
            aiAdvice = "Use herbs and spices to maintain flavor while reducing sodium intake.",
            aiAdviceZh = "使用香草和香料在减少钠摄入的同时保持风味。"
        )
        "plant_based" -> HealthImpactData(
            description = "A plant-based approach will provide long-term cardiovascular benefits:",
            descriptionZh = "植物性方法将提供长期心血管益处：",
            currentSystolic = 140,
            predictedSystolic = 120,
            currentDiastolic = 85,
            predictedDiastolic = 75,
            timeline = listOf(
                TimelineItem("2 weeks", "Digestive system adaptation", "2周内消化系统适应"),
                TimelineItem("1 month", "Energy and mood improvement", "1个月内精力和情绪改善"),
                TimelineItem("3 months", "Cardiovascular benefits", "3个月内心血管益处"),
                TimelineItem("6 months", "Optimal health markers", "6个月内最佳健康指标")
            ),
            aiAdvice = "Ensure adequate protein and B12 intake while following this plant-based approach.",
            aiAdviceZh = "在遵循这种植物性方法的同时确保充足的蛋白质和B12摄入。"
        )
        "keto" -> HealthImpactData(
            description = "Ketogenic diet may provide rapid weight loss benefits but requires careful monitoring:",
            descriptionZh = "生酮饮食可能提供快速减重益处，但需要仔细监测：",
            currentSystolic = 145,
            predictedSystolic = 135,
            currentDiastolic = 90,
            predictedDiastolic = 80,
            timeline = listOf(
                TimelineItem("1 week", "Ketosis adaptation", "1周内酮症适应"),
                TimelineItem("2 weeks", "Initial weight loss", "2周内初步减重"),
                TimelineItem("1 month", "Metabolic changes", "1个月内代谢变化"),
                TimelineItem("3 months", "Stabilized benefits", "3个月内稳定益处")
            ),
            aiAdvice = "Monitor ketone levels and consult healthcare provider before starting this diet.",
            aiAdviceZh = "开始此饮食前监测酮体水平并咨询医疗保健提供者。"
        )
        else -> HealthImpactData(
            description = "This balanced approach will provide steady improvement in your blood pressure:",
            descriptionZh = "这种平衡方法将稳步改善您的血压：",
            currentSystolic = 145,
            predictedSystolic = 135,
            currentDiastolic = 90,
            predictedDiastolic = 80,
            timeline = listOf(
                TimelineItem("1 week", "Initial adaptation", "1周内初步适应"),
                TimelineItem("1 month", "Gradual improvement", "1个月内逐步改善"),
                TimelineItem("3 months", "Stable benefits", "3个月内稳定益处"),
                TimelineItem("6 months", "Long-term health", "6个月内长期健康")
            ),
            aiAdvice = "Maintain consistency and monitor your progress regularly.",
            aiAdviceZh = "保持一致性并定期监测您的进展。"
        )
    }
}

private data class HealthImpactData(
    val description: String,
    val descriptionZh: String,
    val currentSystolic: Int,
    val predictedSystolic: Int,
    val currentDiastolic: Int,
    val predictedDiastolic: Int,
    val timeline: List<TimelineItem>,
    val aiAdvice: String,
    val aiAdviceZh: String
)

private data class TimelineItem(
    val timeframe: String,
    val description: String,
    val descriptionZh: String
)
