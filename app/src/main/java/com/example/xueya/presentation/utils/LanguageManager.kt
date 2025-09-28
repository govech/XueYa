package com.example.xueya.presentation.utils

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.core.os.ConfigurationCompat
import com.example.xueya.domain.model.LanguageMode
import java.util.*

object LanguageManager {
    fun setLanguage(context: Context, languageMode: LanguageMode) {
        val locale = when (languageMode) {
            LanguageMode.ZH -> Locale.SIMPLIFIED_CHINESE
            LanguageMode.EN -> Locale.ENGLISH
        }
        
        val resources = context.resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        
        // 对于较新版本的Android，使用更现代的方法
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            context.createConfigurationContext(configuration)
        }
        
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }
    
    fun getCurrentLanguage(context: Context): LanguageMode {
        val configuration = context.resources.configuration
        val locale = ConfigurationCompat.getLocales(configuration)[0]
        return when (locale?.language) {
            "zh" -> LanguageMode.ZH
            "en" -> LanguageMode.EN
            else -> LanguageMode.ZH // 默认为中文
        }
    }
    
    fun isEnglish(context: Context): Boolean {
        return getCurrentLanguage(context) == LanguageMode.EN
    }
    
    fun isEnglish(): Boolean {
        // 默认返回false（中文），实际使用时应该传入context
        return false
    }
}