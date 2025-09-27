import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.devtools.ksp")
}

// 读取local.properties文件
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

// 读取keystore.properties文件
val keystoreProperties = Properties()
val keystorePropertiesFile = rootProject.file("keystore.properties")
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

android {
    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties.getProperty("keyAlias")
            keyPassword = keystoreProperties.getProperty("keyPassword")
            storeFile = keystoreProperties.getProperty("storeFile")?.let { rootProject.file(it) }
            storePassword = keystoreProperties.getProperty("storePassword")
            enableV1Signing = true
            enableV2Signing = true
        }
    }
    namespace = "com.example.xueya"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.xueya"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // 从local.properties或环境变量中读取API密钥
        val openRouterApiKey =
            localProperties.getProperty("OPENROUTER_API_KEY") ?: System.getenv("OPENROUTER_API_KEY")
            ?: "YOUR_API_KEY_HERE"
        buildConfigField("String", "OPENROUTER_API_KEY", "\"$openRouterApiKey\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            // 在debug版本中也可以设置API密钥
            val openRouterApiKey = localProperties.getProperty("OPENROUTER_API_KEY")
                ?: System.getenv("OPENROUTER_API_KEY") ?: "YOUR_API_KEY_HERE"
            buildConfigField("String", "OPENROUTER_API_KEY", "\"$openRouterApiKey\"")
        }
    }


    // JVM Toolchain configuration for consistent Java version
    kotlin {
        jvmToolchain(17)
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        // Enable core library desugaring for Java 8+ APIs on older Android versions
        isCoreLibraryDesugaringEnabled = true
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    // 使用KSP替代KAPT以避免SQLite权限问题
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    // 设置输出APK文件名格式 - 简化版本
    applicationVariants.configureEach {
        outputs.configureEach {

            // 应用名称
            val appName = "XueYa"
            // 版本名称
            val version = versionName
            // 构建类型
            val buildType = buildType.name
            // 渠道名称（如果没有则为空）
            val flavorName = flavorName ?: ""
            // 当前日期
            val date = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(Date())
            // 构建文件名
            var fileName = "${appName}_v${version}_${buildType}"
            if (flavorName.isNotEmpty()) {
                fileName += "_${flavorName}"
            }
            fileName += "_${date}.apk"

            (this as? com.android.build.gradle.internal.api.BaseVariantOutputImpl)?.let {
                it.outputFileName = "${appName}_v${version}_${buildType}_${date}.apk"
            }
        }
    }
}

dependencies {

    // Core library desugaring for Java 8+ APIs support on older Android versions
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")

    // Compose BOM - Material Design 3
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material3:material3-window-size-class")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.6")

    // ViewModel and StateFlow
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // Room database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // Hilt Dependency Injection
    implementation("com.google.dagger:hilt-android:2.48.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    kapt("com.google.dagger:hilt-compiler:2.48.1")

    // Network - Retrofit & OkHttp for AI API
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")

    // Charts for data visualization
    implementation("com.patrykandpatrick.vico:compose-m3:1.13.1")

    // Date and Time Picker
    implementation("io.github.vanpra.compose-material-dialogs:datetime:0.9.0")

    // DataStore for preferences
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.1.1")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("androidx.arch.core:core-testing:2.2.0")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}