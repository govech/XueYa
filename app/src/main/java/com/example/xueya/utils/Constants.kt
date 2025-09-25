package com.example.xueya.utils

/**
 * 应用常量定义
 */
object Constants {
    
    // 血压值限制
    object BloodPressure {
        const val MIN_SYSTOLIC = 50
        const val MAX_SYSTOLIC = 300
        const val MIN_DIASTOLIC = 30
        const val MAX_DIASTOLIC = 200
        const val MIN_HEART_RATE = 30
        const val MAX_HEART_RATE = 250
        
        // 血压分类标准值
        const val NORMAL_SYSTOLIC_MAX = 119
        const val NORMAL_DIASTOLIC_MAX = 79
        const val ELEVATED_SYSTOLIC_MAX = 129
        const val HIGH_STAGE1_SYSTOLIC_MAX = 139
        const val HIGH_STAGE1_DIASTOLIC_MAX = 89
        const val HIGH_STAGE2_SYSTOLIC_MAX = 179
        const val HIGH_STAGE2_DIASTOLIC_MAX = 109
        const val CRISIS_SYSTOLIC_MIN = 180
        const val CRISIS_DIASTOLIC_MIN = 110
    }
    
    // 心率相关
    object HeartRate {
        const val NORMAL_MIN = 60
        const val NORMAL_MAX = 100
        const val ATHLETE_MIN = 40
        const val ATHLETE_MAX = 60
    }
    
    // 日期时间相关
    object DateTime {
        const val DATE_FORMAT = "yyyy-MM-dd"
        const val TIME_FORMAT = "HH:mm"
        const val DATETIME_FORMAT = "yyyy-MM-dd HH:mm"
        const val DISPLAY_DATE_FORMAT = "MM月dd日"
        const val DISPLAY_TIME_FORMAT = "HH:mm"
        const val DISPLAY_DATETIME_FORMAT = "MM月dd日 HH:mm"
    }
    
    // 数据库相关
    object Database {
        const val NAME = "blood_pressure_database"
        const val VERSION = 1
    }
    
    // 分页相关
    object Paging {
        const val DEFAULT_PAGE_SIZE = 20
        const val RECENT_RECORDS_LIMIT = 10
        const val SEARCH_RESULTS_LIMIT = 50
    }
    
    // 统计相关
    object Statistics {
        const val DEFAULT_TREND_DAYS = 30
        const val WEEKLY_DAYS = 7
        const val MONTHLY_DAYS = 30
        const val QUARTERLY_DAYS = 90
    }
    
    // 标签相关
    object Tags {
        val DEFAULT_TAGS = listOf(
            "晨起", "餐前", "餐后", "睡前", 
            "运动后", "工作时", "休息时", "紧张时"
        )
    }
    
    // 导出相关
    object Export {
        const val CSV_FILENAME_PREFIX = "blood_pressure_"
        const val PDF_FILENAME_PREFIX = "blood_pressure_report_"
        const val DATE_FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    }
    
    // 提醒相关
    object Reminder {
        const val DEFAULT_HOUR = 8
        const val DEFAULT_MINUTE = 0
        const val MIN_INTERVAL_HOURS = 1
        const val MAX_INTERVAL_HOURS = 24
    }
    
    // 动画相关
    object Animation {
        const val DEFAULT_DURATION_MS = 300
        const val FAST_DURATION_MS = 150
        const val SLOW_DURATION_MS = 500
    }
    
    // UI相关
    object UI {
        const val CARD_ELEVATION_DP = 4
        const val CARD_CORNER_RADIUS_DP = 12
        const val BUTTON_HEIGHT_DP = 48
        const val INPUT_FIELD_HEIGHT_DP = 56
        const val LIST_ITEM_HEIGHT_DP = 72
    }
    
    // AI 服务配置
    object AI {
        const val OPENROUTER_BASE_URL = "https://openrouter.ai/"
        const val OPENROUTER_API_KEY = "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"
        
        // DeepSeek V3 模型配置
        const val DEFAULT_AI_MODEL = "deepseek/deepseek-chat-v3.1:free"
        const val MAX_TOKENS = 1000
        const val TEMPERATURE = 0.7
        
        // 网络配置
        const val CONNECT_TIMEOUT_SECONDS = 30L
        const val READ_TIMEOUT_SECONDS = 30L
        const val WRITE_TIMEOUT_SECONDS = 30L
        
        // 提示词模板
        val BLOOD_PRESSURE_PARSE_PROMPT = """
            你是一个专业的血压数据解析助手。请解析用户输入的血压相关信息，提取出以下数据：
            - 收缩压（高压）
            - 舒张压（低压）  
            - 脉搏/心率
            - 备注信息
            
            请以JSON格式返回，格式如下：
            {
                "systolic": 收缩压数值(整数),
                "diastolic": 舒张压数值(整数),
                "pulse": 脉搏数值(整数),
                "notes": "备注信息",
                "confidence": 置信度(0-1之间的小数),
                "isValid": 是否为有效的血压数据(true/false)
            }
            
            用户输入：
        """.trimIndent()
        
        val HEALTH_ADVICE_PROMPT = """
            你是一位专业的心血管健康顾问。基于用户的血压数据历史，请提供专业的健康建议。
            请以JSON格式返回，格式如下：
            {
                "advice": "主要建议内容",
                "category": "normal/warning/danger",
                "recommendations": ["建议1", "建议2", "建议3"]
            }
            
            血压数据：
        """.trimIndent()
    }
}