package com.example.xueya.data.export

import android.content.Context
import android.os.Environment
import com.example.xueya.domain.model.BloodPressureData
import com.example.xueya.domain.repository.BloodPressureRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * CSV数据导出管理器
 */
@Singleton
class CsvExportManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val bloodPressureRepository: BloodPressureRepository
) {
    companion object {
        private const val CSV_HEADER = "日期时间,收缩压,舒张压,心率,分类,备注"
        private const val CSV_HEADER_EN = "DateTime,Systolic,Diastolic,HeartRate,Category,Note"
        private const val FILE_NAME_PREFIX = "blood_pressure_data"
    }

    /**
     * 导出血压数据为CSV文件
     * @param useEnglishHeaders 是否使用英文列标题
     * @return 导出文件路径，失败返回null
     */
    suspend fun exportToCsv(useEnglishHeaders: Boolean = false): String? {
        return withContext(Dispatchers.IO) {
            try {
                // 获取所有血压记录
                val records = bloodPressureRepository.getAllRecords().first()
                
                if (records.isEmpty()) {
                    return@withContext null
                }
                
                // 创建导出文件
                val fileName = generateFileName()
                val file = createExportFile(fileName)
                
                // 写入CSV数据
                FileWriter(file).use { writer ->
                    // 写入标题行
                    writer.appendLine(if (useEnglishHeaders) CSV_HEADER_EN else CSV_HEADER)
                    
                    // 写入数据行
                    records.forEach { record ->
                        writer.appendLine(formatRecordToCsv(record))
                    }
                }
                
                file.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    /**
     * 生成导出文件名
     */
    private fun generateFileName(): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val timestamp = dateFormat.format(Date())
        return "${FILE_NAME_PREFIX}_$timestamp.csv"
    }

    /**
     * 创建导出文件
     */
    private fun createExportFile(fileName: String): File {
        // 首选外部存储的Downloads目录
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        
        return if (downloadsDir != null && downloadsDir.exists()) {
            File(downloadsDir, fileName)
        } else {
            // 备选方案：使用应用私有外部存储
            val appExternalDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            File(appExternalDir, fileName)
        }
    }

    /**
     * 将血压记录格式化为CSV行
     */
    private fun formatRecordToCsv(record: BloodPressureData, useEnglishHeaders: Boolean = false): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val dateTime = record.measureTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        
        // 转换分类为可读文本
        val categoryText = if (useEnglishHeaders) {
            when (record.category.name) {
                "NORMAL" -> "Normal"
                "ELEVATED" -> "Elevated"
                "HIGH_STAGE_1" -> "High Stage 1"
                "HIGH_STAGE_2" -> "High Stage 2"
                "HYPERTENSIVE_CRISIS" -> "Hypertensive Crisis"
                else -> record.category.name
            }
        } else {
            when (record.category.name) {
                "NORMAL" -> "正常"
                "ELEVATED" -> "血压偏高"
                "HIGH_STAGE_1" -> "高血压1期"
                "HIGH_STAGE_2" -> "高血压2期"
                "HYPERTENSIVE_CRISIS" -> "高血压危象"
                else -> record.category.name
            }
        }
        
        // 处理可能包含逗号的备注字段
        val note = record.note.replace(",", "，")
        
        return "$dateTime,${record.systolic},${record.diastolic},${record.heartRate},${categoryText},${note}"
    }

    /**
     * 检查是否有存储权限
     */
    fun hasStoragePermission(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    /**
     * 获取导出目录路径
     */
    fun getExportDirectoryPath(): String {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        return if (downloadsDir != null && downloadsDir.exists()) {
            downloadsDir.absolutePath
        } else {
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath ?: ""
        }
    }

    /**
     * 删除旧的导出文件（保留最近10个）
     */
    suspend fun cleanupOldExportFiles() {
        withContext(Dispatchers.IO) {
            try {
                val exportDir = File(getExportDirectoryPath())
                val exportFiles = exportDir.listFiles { _, name ->
                    name.startsWith(FILE_NAME_PREFIX) && name.endsWith(".csv")
                }
                
                exportFiles?.let { files ->
                    if (files.size > 10) {
                        // 按修改时间排序，删除最旧的文件
                        files.sortBy { it.lastModified() }
                        files.take(files.size - 10).forEach { file ->
                            file.delete()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}