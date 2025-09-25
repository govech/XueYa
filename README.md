# 血压记录应用 (XueYa)

一个用于记录和分析血压数据的Android应用程序。

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

## 技术栈

- Kotlin
- Jetpack Compose
- Hilt (依赖注入)
- Room (数据库)
- Retrofit (网络请求)
- DataStore (偏好设置)
- MVVM架构

## 功能特性

- 血压数据记录
- 语音识别输入
- AI数据分析
- 趋势图表展示
- 健康建议生成
- 数据导出

## 安全注意事项

- `local.properties`文件已被添加到`.gitignore`中，不会被提交到版本控制系统
- 请勿将包含真实API密钥的文件提交到公共仓库
- 在生产环境中，建议使用更安全的密钥管理方案，如Android Keystore系统