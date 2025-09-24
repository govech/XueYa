package com.example.xueya.domain.usecase

import com.example.xueya.data.export.CsvExportManager
import com.example.xueya.domain.model.LanguageMode
import javax.inject.Inject

/**
 * 导出数据用例
 */
class ExportDataUseCase @Inject constructor(
    private val csvExportManager: CsvExportManager
) {
    suspend operator fun invoke(languageMode: LanguageMode = LanguageMode.ZH): ExportResult {
        return try {
            val useEnglishHeaders = languageMode == LanguageMode.EN
            val filePath = csvExportManager.exportToCsv(useEnglishHeaders)
            
            if (filePath != null) {
                // 清理旧文件
                csvExportManager.cleanupOldExportFiles()
                ExportResult.Success(filePath)
            } else {
                ExportResult.Error("No data to export")
            }
        } catch (e: Exception) {
            ExportResult.Error(e.message ?: "Export failed")
        }
    }
}

/**
 * 导出结果密封类
 */
sealed class ExportResult {
    data class Success(val filePath: String) : ExportResult()
    data class Error(val message: String) : ExportResult()
}