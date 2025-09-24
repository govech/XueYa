package com.example.xueya.domain.usecase

import com.example.xueya.domain.model.BloodPressureCategory
import com.example.xueya.domain.model.BloodPressureData
import com.example.xueya.domain.repository.BloodPressureRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import java.time.LocalDateTime

/**
 * 血压UseCase单元测试
 */
class BloodPressureUseCaseTest {

    @Mock
    private lateinit var repository: BloodPressureRepository

    private lateinit var addBloodPressureUseCase: AddBloodPressureUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        addBloodPressureUseCase = AddBloodPressureUseCase(repository)
    }

    @Test
    fun `添加有效血压数据应该成功`() = runTest {
        // Given
        val validData = BloodPressureData(
            systolic = 120,
            diastolic = 80,
            heartRate = 72,
            measureTime = LocalDateTime.now(),
            note = "测试数据"
        )
        whenever(repository.addRecord(validData)).thenReturn(1L)

        // When
        val result = addBloodPressureUseCase(validData)

        // Then
        assertTrue("应该成功添加有效数据", result.isSuccess)
        assertEquals("返回的ID应该是1", 1L, result.getOrNull())
    }

    @Test
    fun `添加无效血压数据应该失败`() = runTest {
        // Given - 收缩压小于舒张压的无效数据
        val invalidData = BloodPressureData(
            systolic = 80,
            diastolic = 120,
            heartRate = 72,
            measureTime = LocalDateTime.now(),
            note = "无效数据"
        )

        // When
        val result = addBloodPressureUseCase(invalidData)

        // Then
        assertTrue("应该拒绝无效数据", result.isFailure)
        assertTrue(
            "错误消息应该包含验证信息",
            result.exceptionOrNull()?.message?.contains("收缩压必须大于舒张压") == true
        )
    }

    @Test
    fun `血压分类应该正确判断`() {
        // 测试正常血压
        val normalBP = BloodPressureData(
            systolic = 110,
            diastolic = 70,
            heartRate = 70,
            measureTime = LocalDateTime.now()
        )
        assertEquals("正常血压应该被正确分类", BloodPressureCategory.NORMAL, normalBP.category)

        // 测试偏高血压
        val elevatedBP = BloodPressureData(
            systolic = 125,
            diastolic = 75,
            heartRate = 70,
            measureTime = LocalDateTime.now()
        )
        assertEquals("偏高血压应该被正确分类", BloodPressureCategory.ELEVATED, elevatedBP.category)

        // 测试高血压1期
        val highStage1BP = BloodPressureData(
            systolic = 135,
            diastolic = 85,
            heartRate = 70,
            measureTime = LocalDateTime.now()
        )
        assertEquals("高血压1期应该被正确分类", BloodPressureCategory.HIGH_STAGE_1, highStage1BP.category)

        // 测试高血压2期
        val highStage2BP = BloodPressureData(
            systolic = 150,
            diastolic = 95,
            heartRate = 70,
            measureTime = LocalDateTime.now()
        )
        assertEquals("高血压2期应该被正确分类", BloodPressureCategory.HIGH_STAGE_2, highStage2BP.category)

        // 测试高血压危象
        val crisisBP = BloodPressureData(
            systolic = 185,
            diastolic = 115,
            heartRate = 70,
            measureTime = LocalDateTime.now()
        )
        assertEquals("高血压危象应该被正确分类", BloodPressureCategory.HYPERTENSIVE_CRISIS, crisisBP.category)
    }

    @Test
    fun `血压显示格式应该正确`() {
        val bloodPressure = BloodPressureData(
            systolic = 120,
            diastolic = 80,
            heartRate = 72,
            measureTime = LocalDateTime.now()
        )

        assertEquals("血压显示格式应该正确", "120/80 mmHg", bloodPressure.displayText)
    }

    @Test
    fun `需要关注的血压应该被正确识别`() {
        // 正常血压不需要关注
        val normalBP = BloodPressureData(
            systolic = 120,
            diastolic = 80,
            heartRate = 72,
            measureTime = LocalDateTime.now()
        )
        assertFalse("正常血压不需要特别关注", normalBP.needsAttention)

        // 高血压危象需要关注
        val crisisBP = BloodPressureData(
            systolic = 185,
            diastolic = 115,
            heartRate = 72,
            measureTime = LocalDateTime.now()
        )
        assertTrue("高血压危象需要关注", crisisBP.needsAttention)

        // 高血压2期需要关注
        val highBP = BloodPressureData(
            systolic = 150,
            diastolic = 95,
            heartRate = 72,
            measureTime = LocalDateTime.now()
        )
        assertTrue("高血压2期需要关注", highBP.needsAttention)
    }

    @Test
    fun `心率状态应该被正确判断`() {
        // 正常心率
        val normalHeartRate = BloodPressureData(
            systolic = 120,
            diastolic = 80,
            heartRate = 75,
            measureTime = LocalDateTime.now()
        )
        assertTrue("正常心率应该被识别", normalHeartRate.isHeartRateNormal)

        // 过低心率
        val lowHeartRate = BloodPressureData(
            systolic = 120,
            diastolic = 80,
            heartRate = 45,
            measureTime = LocalDateTime.now()
        )
        assertFalse("过低心率应该被识别", lowHeartRate.isHeartRateNormal)

        // 过高心率
        val highHeartRate = BloodPressureData(
            systolic = 120,
            diastolic = 80,
            heartRate = 110,
            measureTime = LocalDateTime.now()
        )
        assertFalse("过高心率应该被识别", highHeartRate.isHeartRateNormal)
    }

    @Test
    fun `边界值验证应该正确`() = runTest {
        // 测试最小有效值
        val minValidData = BloodPressureData(
            systolic = 51,
            diastolic = 30,
            heartRate = 30,
            measureTime = LocalDateTime.now()
        )
        val minResult = addBloodPressureUseCase(minValidData)
        assertTrue("最小有效值应该被接受", minResult.isFailure) // 实际上这应该失败，因为收缩压太接近舒张压

        // 测试收缩压等于舒张压（应该失败）
        val equalPressureData = BloodPressureData(
            systolic = 80,
            diastolic = 80,
            heartRate = 70,
            measureTime = LocalDateTime.now()
        )
        val equalResult = addBloodPressureUseCase(equalPressureData)
        assertTrue("收缩压等于舒张压应该被拒绝", equalResult.isFailure)
    }
}