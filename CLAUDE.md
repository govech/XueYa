# 血压记录应用 (XueYa)

一个用于记录和分析血压数据的Android应用程序。

## 项目简介

血压记录应用(XueYa)是一款专为血压监测设计的健康管理工具。该应用帮助用户方便地记录血压测量数据，提供数据分析和趋势展示功能，并集成AI技术提供个性化的健康建议。通过直观的界面和强大的功能，用户可以更好地了解自己的血压状况并采取相应的健康管理措施。

## 技术栈

- **编程语言**: Kotlin
- **UI框架**: Jetpack Compose + Material Design 3
- **架构模式**: MVVM (Model-View-ViewModel)
- **架构设计**: Clean Architecture (分层架构)
- **依赖注入**: Hilt
- **数据库**: Room (SQLite)
- **网络请求**: Retrofit + OkHttp
- **数据存储**: DataStore (偏好设置)
- **异步处理**: Kotlin Coroutines + Flow
- **图表展示**: Vico (图表库)
- **语音识别**: Android Speech API
- **AI服务**: OpenRouter API (DeepSeek V3)

## 功能特性

### 核心功能
- 📊 血压数据记录与管理
- 🎙️ 语音识别快速输入
- 📈 数据趋势图表分析
- 🤖 AI智能健康建议
- 📋 历史记录查看与搜索
- 📊 统计报告生成
- 📤 数据导出(CSV/PDF)
- 🎨 主题切换(深色/浅色模式)
- 🌍 多语言支持(中/英)

### 特色功能
- **智能分类**: 根据医学标准自动分类血压水平
- **健康评估**: 基于历史数据分析整体健康状况
- **提醒功能**: 自定义测量提醒，培养健康习惯
- **标签管理**: 为记录添加标签，便于分类和筛选
- **数据验证**: 智能验证输入数据的有效性

## 项目架构

本项目采用Clean Architecture分层架构设计，分为三个主要层次：

### 1. 表现层 (Presentation Layer)
- **技术**: Jetpack Compose, ViewModel, StateFlow
- **职责**: UI展示、用户交互处理、状态管理
- **组件**: Screens, ViewModels, UI Components

### 2. 业务逻辑层 (Domain Layer)
- **技术**: Kotlin Coroutines, Flow, UseCases
- **职责**: 业务规则实现、数据转换、用例定义
- **组件**: UseCases, Models, Repositories(接口)

### 3. 数据层 (Data Layer)
- **技术**: Room, Retrofit, DataStore
- **职责**: 数据持久化、网络请求、数据源管理
- **组件**: DAOs, Repositories(实现), Network Services

## 目录结构

```
app/
├── src/main/java/com/example/xueya/
│   ├── data/              # 数据层
│   │   ├── database/      # 数据库相关
│   │   ├── di/           # 依赖注入模块
│   │   ├── export/       # 数据导出功能
│   │   ├── network/      # 网络请求相关
│   │   ├── preferences/   # 用户偏好设置
│   │   ├── repository/    # 数据仓库实现
│   │   └── speech/       # 语音识别功能
│   ├── domain/            # 业务逻辑层
│   │   ├── model/        # 领域模型
│   │   ├── repository/    # 数据仓库接口
│   │   └── usecase/      # 业务用例
│   ├── presentation/      # 表现层
│   │   ├── components/    # UI组件
│   │   ├── navigation/    # 导航管理
│   │   ├── screens/       # 屏幕组件
│   │   └── utils/        # 表现层工具
│   ├── ui/                # UI主题配置
│   │   └── theme/        # 主题定义
│   └── utils/             # 通用工具类
└── src/test/              # 单元测试
```

## 主要文件介绍

### 核心领域模型
- [BloodPressureData.kt](file:///c%3A/Users/cairong/work/prcatice/XueYa/app/src/main/java/com/example/xueya/domain/model/BloodPressureData.kt) - 血压数据实体，包含血压值、心率、时间等信息
- [BloodPressureCategory.kt](file:///c%3A/Users/cairong/work/prcatice/XueYa/app/src/main/java/com/example/xueya/domain/model/BloodPressureCategory.kt) - 血压分类枚举，基于医学标准定义不同血压水平

### 数据访问层
- [BloodPressureDao.kt](file:///c%3A/Users/cairong/work/prcatice/XueYa/app/src/main/java/com/example/xueya/data/database/BloodPressureDao.kt) - Room数据库访问对象，定义所有数据库操作接口
- [BloodPressureRepository.kt](file:///c%3A/Users/cairong/work/prcatice/XueYa/app/src/main/java/com/example/xueya/domain/repository/BloodPressureRepository.kt) - 数据仓库接口，定义业务层需要的数据操作
- [BloodPressureRepositoryImpl.kt](file:///c%3A/Users/cairong/work/prcatice/XueYa/app/src/main/java/com/example/xueya/data/repository/BloodPressureRepositoryImpl.kt) - 数据仓库实现，处理数据转换和业务逻辑

### 业务逻辑层
- [AddBloodPressureUseCase.kt](/app/src/main/java/com/example/xueya/domain/usecase/AddBloodPressureUseCase.kt) - 添加血压记录用例
- [GetBloodPressureStatisticsUseCase.kt](/app/src/main/java/com/example/xueya/domain/usecase/GetBloodPressureStatisticsUseCase.kt) - 获取血压统计数据用例
- [AnalyzeBloodPressureTrendUseCase.kt](file:///c%3A/Users/cairong/work/prcatice/XueYa/app/src/main/java/com/example/xueya/domain/usecase/AnalyzeBloodPressureTrendUseCase.kt) - 分析血压趋势用例

### 表现层
- [HomeScreen.kt](file:///c%3A/Users/cairong/work/prcatice/XueYa/app/src/main/java/com/example/xueya/presentation/screens/home/HomeScreen.kt) - 主页界面，展示概览信息
- [HomeViewModel.kt](file:///c%3A/Users/cairong/work/prcatice/XueYa/app/src/main/java/com/example/xueya/presentation/screens/home/HomeViewModel.kt) - 主页视图模型，处理主页业务逻辑

### 工具类
- [BloodPressureUtils.kt](file:///c%3A/Users/cairong/work/prcatice/XueYa/app/src/main/java/com/example/xueya/utils/BloodPressureUtils.kt) - 血压相关工具类，包含验证、分类、趋势分析等功能
- [DateTimeUtils.kt](file:///c%3A/Users/cairong/work/prcatice/XueYa/app/src/main/java/com/example/xueya/utils/DateTimeUtils.kt) - 日期时间工具类，提供格式化、解析等功能

## 安全设置API密钥

为了防止API密钥泄露，本项目采用环境变量的方式来管理敏感信息。

### 设置OpenRouter API密钥

1. 获取OpenRouter API密钥：
   - 访问 [OpenRouter](https://openrouter.ai/) 注册账户
   - 在账户设置中生成API密钥

2. 设置环境变量：
   - 在你的开发环境中设置环境变量：
     ```bash
     export OPENROUTER_API_KEY=your_actual_api_key_here
     ```
   - 或者在项目的根目录创建一个`local.properties`文件（该文件不会被提交到版本控制系统）：
     ```properties
     OPENROUTER_API_KEY=your_actual_api_key_here
     ```

3. 项目会自动从以下位置读取API密钥（按优先级排序）：
   - local.properties文件中的配置
   - 系统环境变量
   - 默认值（仅用于构建，无法访问实际API）

## 构建项目

```bash
./gradlew assembleDebug
```

## 运行测试

```bash
./gradlew test
```

## 血压分类标准

本应用采用国际通用的血压分类标准：

- **正常**: 收缩压 < 120 mmHg 且 舒张压 < 80 mmHg
- **血压偏高**: 收缩压 120-129 mmHg 且 舒张压 < 80 mmHg
- **高血压1期**: 收缩压 130-139 mmHg 或 舒张压 80-89 mmHg
- **高血压2期**: 收缩压 ≥ 140 mmHg 或 舒张压 ≥ 90 mmHg
- **高血压危象**: 收缩压 > 180 mmHg 或 舒张压 > 120 mmHg

## 后续改进方向

1. **功能增强**
   - 添加更多健康指标跟踪（血糖、体重等）
   - 集成更多AI模型提供更精准的健康建议
   - 增加家庭成员管理功能

2. **用户体验优化**
   - 优化图表展示效果和交互体验
   - 增加更多个性化设置选项
   - 改进语音识别准确性和响应速度

3. **技术架构改进**
   - 引入更多自动化测试提高代码质量
   - 优化数据库查询性能
   - 增强数据同步功能支持多设备使用

## 安全注意事项

- `local.properties`文件已被添加到`.gitignore`中，不会被提交到版本控制系统
- 请勿将包含真实API密钥的文件提交到公共仓库
- 在生产环境中，建议使用更安全的密钥管理方案，如Android Keystore系统