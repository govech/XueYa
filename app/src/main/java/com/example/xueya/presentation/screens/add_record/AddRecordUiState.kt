package com.example.xueya.presentation.screens.add_record

import com.example.xueya.domain.model.BloodPressureCategory
import java.time.LocalDateTime

/**
 * 添加记录页面UI状态
 */
data class AddRecordUiState(
    val systolic: String = "",              // 收缩压输入值
    val diastolic: String = "",             // 舒张压输入值
    val heartRate: String = "",             // 心率输入值
    val measureTime: LocalDateTime = LocalDateTime.now(), // 测量时间
    val note: String = "",                  // 备注
    val tags: List<String> = emptyList(),   // 标签列表
    val selectedTags: Set<String> = emptySet(), // 选中的标签
    val isLoading: Boolean = false,         // 加载状态
    val error: String? = null,              // 错误信息
    val isSaved: Boolean = false,           // 保存成功状态
    val showDatePicker: Boolean = false,    // 显示日期选择器
    val showTimePicker: Boolean = false     // 显示时间选择器
) {
    /**
     * 收缩压数值
     */
    val systolicValue: Int?
        get() = systolic.toIntOrNull()

    /**
     * 舒张压数值
     */
    val diastolicValue: Int?
        get() = diastolic.toIntOrNull()

    /**
     * 心率数值
     */
    val heartRateValue: Int?
        get() = heartRate.toIntOrNull()

    /**
     * 是否可以保存
     */
    val canSave: Boolean
        get() = systolicValue != null && 
                diastolicValue != null && 
                heartRateValue != null &&
                !isLoading &&
                isValidBloodPressure()

    /**
     * 血压分类预览
     */
    val bloodPressureCategory: BloodPressureCategory?
        get() = if (systolicValue != null && diastolicValue != null) {
            BloodPressureCategory.categorize(systolicValue!!, diastolicValue!!)
        } else null

    /**
     * 验证血压值是否合理
     */
    private fun isValidBloodPressure(): Boolean {
        val sys = systolicValue ?: return false
        val dia = diastolicValue ?: return false
        val hr = heartRateValue ?: return false
        
        return sys in 50..300 && 
               dia in 30..200 && 
               hr in 30..200 &&
               sys > dia  // 收缩压应该大于舒张压
    }

    /**
     * 表单验证错误信息
     */
    val validationErrors: List<String>
        get() = mutableListOf<String>().apply {
            if (systolic.isNotBlank() && systolicValue == null) {
                add("收缩压必须是有效数字")
            }
            if (diastolic.isNotBlank() && diastolicValue == null) {
                add("舒张压必须是有效数字")
            }
            if (heartRate.isNotBlank() && heartRateValue == null) {
                add("心率必须是有效数字")
            }
            
            systolicValue?.let { sys ->
                if (sys !in 50..300) add("收缩压应在50-300之间")
            }
            
            diastolicValue?.let { dia ->
                if (dia !in 30..200) add("舒张压应在30-200之间")
            }
            
            heartRateValue?.let { hr ->
                if (hr !in 30..200) add("心率应在30-200之间")
            }
            
            if (systolicValue != null && diastolicValue != null && systolicValue!! <= diastolicValue!!) {
                add("收缩压应该大于舒张压")
            }
        }

    /**
     * 常用标签列表
     */
    companion object {
        val COMMON_TAGS = listOf(
            "tag_morning", "tag_before_bed", "tag_after_exercise", "tag_after_meal",
            "tag_after_medication", "tag_stress", "tag_tired", "tag_rest",
            "tag_work", "tag_home", "tag_hospital", "tag_pharmacy"
        )
    }
}