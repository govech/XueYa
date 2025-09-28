package com.example.xueya.presentation.screens.diet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.xueya.R
import com.example.xueya.presentation.utils.LanguageManager
import com.example.xueya.presentation.screens.diet.components.AINutritionAnalysisCard
import com.example.xueya.presentation.screens.diet.components.AIFoodRecommendationsCard
import com.example.xueya.presentation.screens.diet.components.HealthImpactChart

/**
 * 饮食方案详情页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietDetailScreen(
    navController: NavController,
    planId: String,
    viewModel: DietDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val isEnglish = LanguageManager.isEnglish(context)

    // 加载饮食方案详情
    LaunchedEffect(planId) {
        viewModel.loadDietPlan(planId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (isEnglish) "Diet Plan Details" else "饮食方案详情",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = if (isEnglish) "Back" else "返回"
                        )
                    }
                },
                actions = {
                    // 分享按钮
                    IconButton(onClick = { /* TODO: 实现分享功能 */ }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = if (isEnglish) "Share" else "分享"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                DietDetailLoadingState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            
            uiState.error != null -> {
                DietDetailErrorState(
                    message = uiState.error ?: "Unknown error",
                    onRetry = { 
                        viewModel.clearError()
                        viewModel.loadDietPlan(planId)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            
            uiState.dietPlan != null -> {
                DietDetailContent(
                    plan = uiState.dietPlan!!,
                    isEnglish = isEnglish,
                    onToggleFavorite = { viewModel.toggleFavorite() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
        }
    }
}

/**
 * 详情页面内容
 */
@Composable
private fun DietDetailContent(
    plan: com.example.xueya.domain.model.DietPlan,
    isEnglish: Boolean,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // 头部信息卡片
        DietDetailHeaderCard(
            plan = plan,
            isEnglish = isEnglish,
            onToggleFavorite = onToggleFavorite,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 描述卡片
        DietDetailDescriptionCard(
            plan = plan,
            isEnglish = isEnglish,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 食物推荐卡片
        DietDetailFoodRecommendationsCard(
            plan = plan,
            isEnglish = isEnglish,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 注意事项卡片
        DietDetailPrecautionsCard(
            plan = plan,
            isEnglish = isEnglish,
            modifier = Modifier.fillMaxWidth()
        )

        // AI推荐理由（如果有）
        if (plan.isRecommended && plan.recommendationReason.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            
            DietDetailRecommendationCard(
                plan = plan,
                isEnglish = isEnglish,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // AI营养分析卡片
        Spacer(modifier = Modifier.height(16.dp))
        
        AINutritionAnalysisCard(
            planId = plan.id,
            isEnglish = isEnglish,
            modifier = Modifier.fillMaxWidth()
        )

        // AI食物推荐卡片
        Spacer(modifier = Modifier.height(16.dp))
        
        AIFoodRecommendationsCard(
            planId = plan.id,
            isEnglish = isEnglish,
            modifier = Modifier.fillMaxWidth()
        )

        // 健康影响预测图表
        Spacer(modifier = Modifier.height(16.dp))
        
        HealthImpactChart(
            planId = plan.id,
            isEnglish = isEnglish,
            modifier = Modifier.fillMaxWidth()
        )

        // 底部间距
        Spacer(modifier = Modifier.height(32.dp))
    }
}

/**
 * 详情页面头部卡片
 */
@Composable
private fun DietDetailHeaderCard(
    plan: com.example.xueya.domain.model.DietPlan,
    isEnglish: Boolean,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary
        )
    )

    Card(
        modifier = modifier.padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradientBrush)
                .padding(24.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // 图标
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.2f)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Box(
                                modifier = Modifier.padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = plan.icon ?: "🍎",
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            // 标题
                            Text(
                                text = if (isEnglish) plan.nameEn else plan.name,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            // 适用人群
                            Text(
                                text = if (isEnglish) plan.suitableForEn else plan.suitableFor,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }

                    // 收藏按钮
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (plan.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isEnglish) "Toggle favorite" else "切换收藏",
                            tint = if (plan.isFavorite) Color.Red else Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // AI推荐标签（如果有）
                if (plan.isRecommended) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.9f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isEnglish) "AI Recommended" else "AI推荐",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 描述卡片
 */
@Composable
private fun DietDetailDescriptionCard(
    plan: com.example.xueya.domain.model.DietPlan,
    isEnglish: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isEnglish) "Description" else "方案描述",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Text(
                text = if (isEnglish) plan.descriptionEn else plan.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.4
            )
        }
    }
}

/**
 * 食物推荐卡片
 */
@Composable
private fun DietDetailFoodRecommendationsCard(
    plan: com.example.xueya.domain.model.DietPlan,
    isEnglish: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isEnglish) "Food Recommendations" else "食物推荐",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            val foodList = if (isEnglish) plan.foodRecommendationsEn else plan.foodRecommendations
            foodList.forEachIndexed { index, food ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 6.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = food,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/**
 * 注意事项卡片
 */
@Composable
private fun DietDetailPrecautionsCard(
    plan: com.example.xueya.domain.model.DietPlan,
    isEnglish: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isEnglish) "Precautions" else "注意事项",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            }
            
            Text(
                text = if (isEnglish) plan.precautionsEn else plan.precautions,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onErrorContainer,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.4
            )
        }
    }
}

/**
 * AI推荐理由卡片
 */
@Composable
private fun DietDetailRecommendationCard(
    plan: com.example.xueya.domain.model.DietPlan,
    isEnglish: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isEnglish) "AI Recommendation Reason" else "AI推荐理由",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
            
            Text(
                text = if (isEnglish) plan.recommendationReasonEn else plan.recommendationReason,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.4
            )
        }
    }
}

/**
 * 详情页面加载状态
 */
@Composable
private fun DietDetailLoadingState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = if (LanguageManager.isEnglish()) "Loading diet plan details..." else "正在加载饮食方案详情...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 详情页面错误状态
 */
@Composable
private fun DietDetailErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Button(onClick = onRetry) {
                Text(text = if (LanguageManager.isEnglish()) "Retry" else "重试")
            }
        }
    }
}
