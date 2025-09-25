package com.example.xueya.utils

/**
 * 应用常量定义
 * 
 * 定义应用中使用的各种常量值，按功能模块进行分组
 * 包括血压值限制、心率相关、日期时间、数据库、分页、统计、标签、导出、提醒、动画、UI、AI服务、语音识别等配置
 */
object Constants {
    
    /**
     * 血压值限制常量
     * 
     * 定义血压和心率的有效数值范围，以及血压分类的标准值
     */
    object BloodPressure {
        /**
         * 收缩压最小值
         */
        const val MIN_SYSTOLIC = 50
        
        /**
         * 收缩压最大值
         */
        const val MAX_SYSTOLIC = 300
        
        /**
         * 舒张压最小值
         */
        const val MIN_DIASTOLIC = 30
        
        /**
         * 舒张压最大值
         */
        const val MAX_DIASTOLIC = 200
        
        /**
         * 心率最小值
         */
        const val MIN_HEART_RATE = 30
        
        /**
         * 心率最大值
         */
        const val MAX_HEART_RATE = 250
        
        /**
         * 正常血压收缩压最大值
         */
        const val NORMAL_SYSTOLIC_MAX = 119
        
        /**
         * 正常血压舒张压最大值
         */
        const val NORMAL_DIASTOLIC_MAX = 79
        
        /**
         * 血压偏高收缩压最大值
         */
        const val ELEVATED_SYSTOLIC_MAX = 129
        
        /**
         * 高血压1期收缩压最大值
         */
        const val HIGH_STAGE1_SYSTOLIC_MAX = 139
        
        /**
         * 高血压1期舒张压最大值
         */
        const val HIGH_STAGE1_DIASTOLIC_MAX = 89
        
        /**
         * 高血压2期收缩压最大值
         */
        const val HIGH_STAGE2_SYSTOLIC_MAX = 179
        
        /**
         * 高血压2期舒张压最大值
         */
        const val HIGH_STAGE2_DIASTOLIC_MAX = 109
        
        /**
         * 高血压危象收缩压最小值
         */
        const val CRISIS_SYSTOLIC_MIN = 180
        
        /**
         * 高血压危象舒张压最小值
         */
        const val CRISIS_DIASTOLIC_MIN = 110
    }
    
    /**
     * 心率相关常量
     * 
     * 定义正常心率范围和运动员心率范围
     */
    object HeartRate {
        /**
         * 正常心率最小值
         */
        const val NORMAL_MIN = 60
        
        /**
         * 正常心率最大值
         */
        const val NORMAL_MAX = 100
        
        /**
         * 运动员心率最小值
         */
        const val ATHLETE_MIN = 40
        
        /**
         * 运动员心率最大值
         */
        const val ATHLETE_MAX = 60
    }
    
    /**
     * 日期时间相关常量
     * 
     * 定义各种日期时间格式字符串
     */
    object DateTime {
        /**
         * 日期格式 (yyyy-MM-dd)
         */
        const val DATE_FORMAT = "yyyy-MM-dd"
        
        /**
         * 时间格式 (HH:mm)
         */
        const val TIME_FORMAT = "HH:mm"
        
        /**
         * 日期时间格式 (yyyy-MM-dd HH:mm)
         */
        const val DATETIME_FORMAT = "yyyy-MM-dd HH:mm"
        
        /**
         * 显示用日期格式 (MM月dd日)
         */
        const val DISPLAY_DATE_FORMAT = "MM月dd日"
        
        /**
         * 显示用时间格式 (HH:mm)
         */
        const val DISPLAY_TIME_FORMAT = "HH:mm"
        
        /**
         * 显示用日期时间格式 (MM月dd日 HH:mm)
         */
        const val DISPLAY_DATETIME_FORMAT = "MM月dd日 HH:mm"
    }
    
    /**
     * 数据库相关常量
     * 
     * 定义数据库名称和版本号
     */
    object Database {
        /**
         * 数据库名称
         */
        const val NAME = "blood_pressure_database"
        
        /**
         * 数据库版本号
         */
        const val VERSION = 1
    }
    
    /**
     * 分页相关常量
     * 
     * 定义分页和限制查询的默认值
     */
    object Paging {
        /**
         * 默认页面大小
         */
        const val DEFAULT_PAGE_SIZE = 20
        
        /**
         * 最近记录限制数量
         */
        const val RECENT_RECORDS_LIMIT = 10
        
        /**
         * 搜索结果限制数量
         */
        const val SEARCH_RESULTS_LIMIT = 50
    }
    
    /**
     * 统计相关常量
     * 
     * 定义统计分析的时间范围
     */
    object Statistics {
        /**
         * 默认趋势分析天数
         */
        const val DEFAULT_TREND_DAYS = 30
        
        /**
         * 每周天数
         */
        const val WEEKLY_DAYS = 7
        
        /**
         * 每月天数
         */
        const val MONTHLY_DAYS = 30
        
        /**
         * 每季度天数
         */
        const val QUARTERLY_DAYS = 90
    }
    
    /**
     * 标签相关常量
     * 
     * 定义默认标签列表
     */
    object Tags {
        /**
         * 默认标签列表
         */
        val DEFAULT_TAGS = listOf(
            "tag_morning", "tag_before_meal", "tag_after_meal", "tag_before_bed",
            "tag_after_exercise", "tag_working", "tag_resting", "tag_stressed"
        )
    }
    
    /**
     * 导出相关常量
     * 
     * 定义数据导出的文件名前缀和格式
     */
    object Export {
        /**
         * CSV文件名前缀
         */
        const val CSV_FILENAME_PREFIX = "blood_pressure_"
        
        /**
         * PDF文件名前缀
         */
        const val PDF_FILENAME_PREFIX = "blood_pressure_report_"
        
        /**
         * 文件名日期格式
         */
        const val DATE_FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    }
    
    /**
     * 提醒相关常量
     * 
     * 定义提醒功能的默认值和限制
     */
    object Reminder {
        /**
         * 默认提醒小时
         */
        const val DEFAULT_HOUR = 8
        
        /**
         * 默认提醒分钟
         */
        const val DEFAULT_MINUTE = 0
        
        /**
         * 最小提醒间隔小时数
         */
        const val MIN_INTERVAL_HOURS = 1
        
        /**
         * 最大提醒间隔小时数
         */
        const val MAX_INTERVAL_HOURS = 24
    }
    
    /**
     * 动画相关常量
     * 
     * 定义动画持续时间
     */
    object Animation {
        /**
         * 默认动画持续时间（毫秒）
         */
        const val DEFAULT_DURATION_MS = 300
        
        /**
         * 快速动画持续时间（毫秒）
         */
        const val FAST_DURATION_MS = 150
        
        /**
         * 慢速动画持续时间（毫秒）
         */
        const val SLOW_DURATION_MS = 500
    }
    
    /**
     * UI相关常量
     * 
     * 定义UI组件的尺寸值
     */
    object UI {
        /**
         * 卡片阴影高度（dp）
         */
        const val CARD_ELEVATION_DP = 4
        
        /**
         * 卡片圆角半径（dp）
         */
        const val CARD_CORNER_RADIUS_DP = 12
        
        /**
         * 按钮高度（dp）
         */
        const val BUTTON_HEIGHT_DP = 48
        
        /**
         * 输入框高度（dp）
         */
        const val INPUT_FIELD_HEIGHT_DP = 56
        
        /**
         * 列表项高度（dp）
         */
        const val LIST_ITEM_HEIGHT_DP = 72
    }
    
    /**
     * AI服务配置常量
     * 
     * 定义AI服务的相关配置，包括API密钥、模型、网络超时等
     */
    object AI {
        /**
         * OpenRouter基础URL
         */
        const val OPENROUTER_BASE_URL = "https://openrouter.ai/"
        
        /**
         * OpenRouter API密钥
         * 
         * 通过反射获取BuildConfig中的API密钥，如果获取失败则返回默认值
         */
        val OPENROUTER_API_KEY: String
            get() = try {
                // 尝试获取BuildConfig中的API密钥
                val buildConfigClass = Class.forName("com.example.xueya.BuildConfig")
                val field = buildConfigClass.getField("OPENROUTER_API_KEY")
                field.get(null) as String
            } catch (e: Exception) {
                // 如果获取失败，返回默认值
                "YOUR_API_KEY_HERE"
            }
        
        /**
         * 默认AI模型
         */
        const val DEFAULT_AI_MODEL = "deepseek/deepseek-chat-v3.1:free"
        
        /**
         * 最大令牌数
         */
        const val MAX_TOKENS = 1000
        
        /**
         * 温度参数
         */
        const val TEMPERATURE = 0.7
        
        /**
         * 连接超时时间（秒）
         */
        const val CONNECT_TIMEOUT_SECONDS = 30L
        
        /**
         * 读取超时时间（秒）
         */
        const val READ_TIMEOUT_SECONDS = 30L
        
        /**
         * 写入超时时间（秒）
         */
        const val WRITE_TIMEOUT_SECONDS = 30L
        
        /**
         * 血压数据解析提示词模板
         */
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
        
        /**
         * 健康建议提示词模板
         */
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
        
        /**
         * 趋势分析提示词模板
         */
        val TREND_ANALYSIS_PROMPT = """
            你是一位专业的医疗数据分析师。基于用户的血压数据历史，请分析血压变化趋势。
            请以JSON格式返回，格式如下：
            {
                "trend": "improving/stable/worsening",
                "summary": "趋势分析总结",
                "insights": ["洞察1", "洞察2", "洞察3"],
                "recommendations": ["建议1", "建议2", "建议3"]
            }
            
            血压数据：
        """.trimIndent()
    }
    
    /**
     * 语音识别配置常量
     * 
     * 定义语音识别功能的相关配置
     */
    object SpeechRecognition {
        /**
         * 语音输入请求码
         */
        const val REQUEST_CODE_SPEECH_INPUT = 1000
        
        /**
         * 监听超时时间（毫秒）
         */
        const val LISTENING_TIMEOUT_MS = 20000L
        
        /**
         * 最大识别结果数
         */
        const val MAX_RESULTS = 1
        
        /**
         * 自由形式语言模型
         */
        const val LANGUAGE_MODEL_FREE_FORM = "android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM"
        
        /**
         * 语言模型额外参数键
         */
        const val EXTRA_LANGUAGE_MODEL = "android.speech.extra.LANGUAGE_MODEL"
        
        /**
         * 语言额外参数键
         */
        const val EXTRA_LANGUAGE = "android.speech.extra.LANGUAGE"
        
        /**
         * 提示额外参数键
         */
        const val EXTRA_PROMPT = "android.speech.extra.PROMPT"
        
        /**
         * 最大结果数额外参数键
         */
        const val EXTRA_MAX_RESULTS = "android.speech.extra.MAX_RESULTS"
        
        /**
         * 结果额外参数键
         */
        const val EXTRA_RESULTS = "android.speech.extra.RESULTS"
    }
}