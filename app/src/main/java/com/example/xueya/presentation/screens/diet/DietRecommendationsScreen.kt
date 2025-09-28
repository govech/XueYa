package com.example.xueya.presentation.screens.diet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.xueya.R
import com.example.xueya.domain.model.DietPlan
import com.example.xueya.domain.model.DietPlans
import com.example.xueya.presentation.screens.settings.LanguageManager
import com.example.xueya.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietRecommendationsScreen(
    navController: NavController,
    viewModel: DietViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isEnglish = LanguageManager.isEnglish()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.diet_recommendations),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // AI推荐区域
            if (uiState.aiRecommendedPlans.isNotEmpty()) {
                // 使用独立的AI推荐卡片实现
                AIRecommendedPlansSection(
                    plans = uiState.aiRecommendedPlans,
                    isEnglish = isEnglish,
                    onToggleFavorite = { plan -> viewModel.toggleFavorite(plan.id) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // 主流饮食方案区域
            MainstreamPlansSection(
                plans = uiState.mainstreamPlans,
                isEnglish = isEnglish,
                onToggleFavorite = { plan -> viewModel.toggleFavorite(plan.id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}

@Composable
private fun AIRecommendationsSection(
    plans: List<DietPlan>,
    isEnglish: Boolean,
    onToggleFavorite: (DietPlan) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = stringResource(id = R.string.diet_ai_recommendations),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = if (isEnglish) {
                "Based on your recent blood pressure data, we recommend the following dietary plans:"
            } else {
                "根据您最近的血压数据，我们推荐以下饮食方案："
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        plans.forEach { plan ->
            AIRecommendedDietCard(
                plan = plan,
                isEnglish = isEnglish,
                onToggleFavorite = { onToggleFavorite(plan) },
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}

@Composable
private fun MainstreamPlansSection(
    plans: List<DietPlan>,
    isEnglish: Boolean,
    onToggleFavorite: (DietPlan) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(id = R.string.diet_mainstream_plans),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(plans) { plan ->
                DietPlanCard(
                    plan = plan,
                    isEnglish = isEnglish,
                    onToggleFavorite = { onToggleFavorite(plan) }
                )
            }
        }
    }
}

@Composable
private fun AIRecommendedDietCard(
    plan: DietPlan,
    isEnglish: Boolean,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF4CAF50), // 绿色
            Color(0xFF8BC34A)  // 浅绿色
        )
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradientBrush)
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = plan.icon ?: "🍎",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Column {
                            Text(
                                text = if (isEnglish) plan.nameEn else plan.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            if (plan.recommendationReason.isNotEmpty()) {
                                Text(
                                    text = stringResource(
                                        id = R.string.diet_recommendation_reason,
                                        if (isEnglish) plan.recommendationReasonEn else plan.recommendationReason
                                    ),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.9f),
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }

                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (plan.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = stringResource(
                                id = if (plan.isFavorite) R.string.diet_remove_from_favorites else R.string.diet_add_to_favorites
                            ),
                            tint = if (plan.isFavorite) Color.Red else Color.White
                        )
                    }
                }

                Text(
                    text = stringResource(
                        id = R.string.diet_suitable_for,
                        if (isEnglish) plan.suitableForEn else plan.suitableFor
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.95f),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun DietPlanCard(
    plan: DietPlan,
    isEnglish: Boolean,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = plan.icon ?: "🍎",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isEnglish) plan.nameEn else plan.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(
                                id = R.string.diet_suitable_for,
                                if (isEnglish) plan.suitableForEn else plan.suitableFor
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row {
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (plan.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = stringResource(
                                id = if (plan.isFavorite) R.string.diet_remove_from_favorites else R.string.diet_add_to_favorites
                            ),
                            tint = if (plan.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    TextButton(
                        onClick = { isExpanded = !isExpanded }
                    ) {
                        Text(
                            text = stringResource(
                                id = if (isExpanded) R.string.diet_hide_details else R.string.diet_view_details
                            )
                        )
                    }
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))

                // 描述
                Text(
                    text = if (isEnglish) plan.descriptionEn else plan.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // 食物推荐
                Text(
                    text = stringResource(id = R.string.diet_food_recommendations),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                val foodList = if (isEnglish) plan.foodRecommendationsEn else plan.foodRecommendations
                foodList.forEach { food ->
                    Text(
                        text = "• $food",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 8.dp, bottom = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 注意事项
                Text(
                    text = stringResource(id = R.string.diet_precautions),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = if (isEnglish) plan.precautionsEn else plan.precautions,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * AI推荐计划区域 - 使用独立的卡片实现避免冲突
 */
@Composable
private fun AIRecommendedPlansSection(
    plans: List<DietPlan>,
    isEnglish: Boolean,
    onToggleFavorite: (DietPlan) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = stringResource(id = R.string.diet_ai_recommendations),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = if (isEnglish) {
                "Based on your recent blood pressure data, we recommend the following dietary plans:"
            } else {
                "根据您最近的血压数据，我们推荐以下饮食方案："
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        plans.forEach { plan ->
            AIRecommendedDietCard(
                plan = plan,
                isEnglish = isEnglish,
                onToggleFavorite = { onToggleFavorite(plan) },
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}