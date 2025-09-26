package com.example.xueya.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.xueya.R
import com.example.xueya.presentation.components.BloodPressureDialogViewModel
import com.example.xueya.presentation.components.BloodPressureRecordDialog

/**
 * 应用主导航组合
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    bloodPressureDialogViewModel: BloodPressureDialogViewModel = hiltViewModel()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            AppBottomNavigation(
                currentDestination = currentDestination,
                onNavigateToDestination = { destination ->
                    navController.navigate(destination.route) {
                        // 避免重复导航到同一目标
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // 避免多个副本堆叠
                        launchSingleTop = true
                        // 恢复状态
                        restoreState = true
                    }
                },
                bloodPressureDialogViewModel = bloodPressureDialogViewModel
            )
        },
        floatingActionButton = {
            // 悬浮按钮功能暂时不需要，使用底部导航栏中间的加号按钮
        }
    ) { paddingValues ->
        AppNavHost(
            navController = navController,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

/**
 * 应用底部导航栏
 */
@Composable
private fun AppBottomNavigation(
    currentDestination: NavDestination?,
    onNavigateToDestination: (AppDestination) -> Unit,
    bloodPressureDialogViewModel: BloodPressureDialogViewModel
) {
    Box(modifier = Modifier) {
        NavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
        ) {

            for ((index, destination) in bottomNavDestinations.withIndex()) {
                val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)
                if (index == bottomNavDestinations.size / 2) {
                    Spacer(
                        modifier = Modifier.size(90.dp, 1.dp)

                    ) // 中间留空，放大按钮用
                    continue
                }
                NavigationBarItem(
                    selected = selected,
                    onClick = { onNavigateToDestination(destination) },
                    icon = {
                        Icon(
                            imageVector = destination.icon,
                            contentDescription = stringResource(destination.titleResId)
                        )
                    },
                    label = {
                        Text(stringResource(destination.titleResId),
                            maxLines = 1)
                    }
                )
            }


        }

        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
                .size(90.dp)
                .padding(10.dp)
                .align(Alignment.Center),
            contentAlignment = Alignment.Center
        ) {
            // 中间突出按钮
            FloatingActionButton(
                onClick = {
                    bloodPressureDialogViewModel.showDialog()
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = CircleShape,
                modifier = Modifier.fillMaxSize()

            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.nav_add_record),
                    modifier = Modifier.size(32.dp) // 更大的图标
                )
            }
        }


    }
}

/**
 * 检查当前目标是否在层级中
 */
private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: AppDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.route, true) ?: false
    } ?: false