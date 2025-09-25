package com.example.xueya

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.xueya.data.preferences.UserPreferencesDataStore
import com.example.xueya.domain.model.ThemeMode
import com.example.xueya.presentation.navigation.AppNavigation
import com.example.xueya.ui.theme.XueYaTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var userPreferencesDataStore: UserPreferencesDataStore
    
    // 注册权限请求
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // 处理权限请求结果
        if (isGranted) {
            // 权限已授予
        } else {
            // 权限被拒绝，可以在UI中提示用户
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 检查并请求录音权限
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                // 权限已授予
            }
            else -> {
                // 请求录音权限
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
        
        setContent {
            val userPreferences by userPreferencesDataStore.userPreferences.collectAsState(
                initial = com.example.xueya.domain.model.UserPreferences()
            )
            
            val darkTheme = when (userPreferences.themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }
            
            XueYaTheme(
                darkTheme = darkTheme,
                dynamicColor = userPreferences.dynamicColorEnabled
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}