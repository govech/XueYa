package com.example.xueya.presentation.screens.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.xueya.R
import com.example.xueya.presentation.components.common.ErrorCard
import com.example.xueya.presentation.screens.history.*

/**
 * 历史记录界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateToAddRecord: () -> Unit,
    onNavigateToRecordDetail: (Long) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            HistoryTopAppBar(
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = viewModel::updateSearchQuery,
                hasActiveFilters = uiState.hasActiveFilters,
                filterCount = uiState.filterCount,
                onToggleFilters = viewModel::toggleFilters,
                onClearFilters = viewModel::clearFilters
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 过滤器面板
            if (uiState.showFilters) {
                FilterPanel(
                    selectedDateRange = uiState.selectedDateRange,
                    selectedTags = uiState.selectedTags,
                    sortOrder = uiState.sortOrder,
                    onDateRangeChange = viewModel::updateDateRange,
                    onTagToggle = viewModel::toggleTagFilter,
                    onSortOrderChange = viewModel::updateSortOrder,
                    onQuickDateRange = viewModel::setQuickDateRange
                )
            }

            // 错误提示
            uiState.error?.let { error ->
                ErrorCard(
                    error = error,
                    onDismiss = viewModel::clearError,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // 记录列表
            Box(
                modifier = Modifier.weight(1f)
            ) {
                when {
                    uiState.isLoading -> {
                        HistoryLoadingIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    !uiState.hasRecords -> {
                        HistoryEmptyState(
                            onAddRecord = onNavigateToAddRecord,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    uiState.displayRecords.isEmpty() && uiState.hasActiveFilters -> {
                        NoResultsState(
                            onClearFilters = viewModel::clearFilters,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // 统计信息卡片 - 放在列表顶部，跟随滚动
                            if (uiState.hasRecords && !uiState.isLoading) {
                                item {
                                    StatisticsCard(
                                        statistics = uiState.statistics,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 0.dp, vertical = 8.dp)
                                    )
                                }
                            }

                            items(
                                items = uiState.displayRecords,
                                key = { it.id }
                            ) { record ->
                                RecordCard(
                                    record = record,
                                    onClick = { onNavigateToRecordDetail(record.id) },
                                    onDelete = { viewModel.showDeleteDialog(record) }
                                )
                            }

                            // 底部间距
                            item {
                                Spacer(modifier = Modifier.height(80.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // 删除确认对话框
    if (uiState.showDeleteDialog) {
        DeleteConfirmDialog(
            record = uiState.selectedRecord,
            onConfirm = viewModel::deleteRecord,
            onDismiss = viewModel::hideDeleteDialog
        )
    }
}

/**
 * 顶部应用栏
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryTopAppBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    hasActiveFilters: Boolean,
    filterCount: Int,
    onToggleFilters: () -> Unit,
    onClearFilters: () -> Unit
) {
    Column {
        TopAppBar(
            title = { Text(stringResource(R.string.nav_history)) },
            actions = {
                // 清除过滤器按钮
                if (hasActiveFilters) {
                    IconButton(onClick = onClearFilters) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "清除过滤器"
                        )
                    }
                }
                
                // 过滤器按钮
                IconButton(onClick = onToggleFilters) {
                    Badge(
                        modifier = if (hasActiveFilters) Modifier else Modifier.size(0.dp)
                    ) {
                        if (hasActiveFilters) {
                            Text(
                                text = filterCount.toString(),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "过滤器"
                    )
                }
            }
        )
        
        // 搜索栏
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("搜索备注或标签...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "清除搜索"
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            singleLine = true
        )
    }
}