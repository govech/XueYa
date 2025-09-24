package com.example.xueya.domain.util

import com.example.xueya.domain.model.BloodPressureCategory
import org.junit.Test
import org.junit.Assert.*

/**
 * BloodPressureClassifier单元测试
 */
class BloodPressureClassifierTest {

    @Test
    fun testNormalBloodPressure() {
        val result1 = BloodPressureClassifier.classify(110, 70)
        assertEquals(BloodPressureCategory.NORMAL, result1)
        
        val result2 = BloodPressureClassifier.classify(120, 80)
        assertEquals(BloodPressureCategory.NORMAL, result2)
        
        val result3 = BloodPressureClassifier.classify(119, 79)
        assertEquals(BloodPressureCategory.NORMAL, result3)
    }

    @Test
    fun testElevatedBloodPressure() {
        val result1 = BloodPressureClassifier.classify(121, 75)
        assertEquals(BloodPressureCategory.ELEVATED, result1)
        
        val result2 = BloodPressureClassifier.classify(129, 79)
        assertEquals(BloodPressureCategory.ELEVATED, result2)
        
        val result3 = BloodPressureClassifier.classify(125, 80)
        assertEquals(BloodPressureCategory.ELEVATED, result3)
    }

    @Test
    fun testHighBloodPressureStage1() {
        val result1 = BloodPressureClassifier.classify(130, 80)
        assertEquals(BloodPressureCategory.HIGH_STAGE_1, result1)
        
        val result2 = BloodPressureClassifier.classify(139, 89)
        assertEquals(BloodPressureCategory.HIGH_STAGE_1, result2)
        
        val result3 = BloodPressureClassifier.classify(135, 85)
        assertEquals(BloodPressureCategory.HIGH_STAGE_1, result3)
        
        // 测试舒张压升高的情况
        val result4 = BloodPressureClassifier.classify(125, 85)
        assertEquals(BloodPressureCategory.HIGH_STAGE_1, result4)
    }

    @Test
    fun testHighBloodPressureStage2() {
        val result1 = BloodPressureClassifier.classify(140, 90)
        assertEquals(BloodPressureCategory.HIGH_STAGE_2, result1)
        
        val result2 = BloodPressureClassifier.classify(159, 99)
        assertEquals(BloodPressureCategory.HIGH_STAGE_2, result2)
        
        val result3 = BloodPressureClassifier.classify(150, 95)
        assertEquals(BloodPressureCategory.HIGH_STAGE_2, result3)
        
        // 测试只有舒张压高的情况
        val result4 = BloodPressureClassifier.classify(125, 95)
        assertEquals(BloodPressureCategory.HIGH_STAGE_2, result4)
    }

    @Test
    fun testHypertensiveCrisis() {
        val result1 = BloodPressureClassifier.classify(180, 120)
        assertEquals(BloodPressureCategory.HYPERTENSIVE_CRISIS, result1)
        
        val result2 = BloodPressureClassifier.classify(200, 130)
        assertEquals(BloodPressureCategory.HYPERTENSIVE_CRISIS, result2)
        
        // 测试只有收缩压极高的情况
        val result3 = BloodPressureClassifier.classify(185, 85)
        assertEquals(BloodPressureCategory.HYPERTENSIVE_CRISIS, result3)
        
        // 测试只有舒张压极高的情况
        val result4 = BloodPressureClassifier.classify(135, 125)
        assertEquals(BloodPressureCategory.HYPERTENSIVE_CRISIS, result4)
    }

    @Test
    fun testBoundaryValues() {
        // 测试边界值
        assertEquals(BloodPressureCategory.NORMAL, BloodPressureClassifier.classify(120, 80))
        assertEquals(BloodPressureCategory.ELEVATED, BloodPressureClassifier.classify(121, 80))
        assertEquals(BloodPressureCategory.HIGH_STAGE_1, BloodPressureClassifier.classify(130, 80))
        assertEquals(BloodPressureCategory.HIGH_STAGE_1, BloodPressureClassifier.classify(120, 80))
        assertEquals(BloodPressureCategory.HIGH_STAGE_2, BloodPressureClassifier.classify(140, 90))
        assertEquals(BloodPressureCategory.HYPERTENSIVE_CRISIS, BloodPressureClassifier.classify(180, 120))
    }

    @Test
    fun testGetCategoryInfo() {
        val normalInfo = BloodPressureClassifier.getCategoryInfo(BloodPressureCategory.NORMAL)
        assertEquals("正常血压", normalInfo.categoryName)
        assertTrue(normalInfo.description.contains("正常"))
        assertEquals("#4CAF50", normalInfo.color)
        assertFalse(normalInfo.isRisk)

        val elevatedInfo = BloodPressureClassifier.getCategoryInfo(BloodPressureCategory.ELEVATED)
        assertEquals("血压偏高", elevatedInfo.categoryName)
        assertTrue(elevatedInfo.description.contains("偏高"))
        assertTrue(elevatedInfo.isRisk)

        val crisisInfo = BloodPressureClassifier.getCategoryInfo(BloodPressureCategory.HYPERTENSIVE_CRISIS)
        assertEquals("高血压危象", crisisInfo.categoryName)
        assertTrue(crisisInfo.description.contains("紧急"))
        assertTrue(crisisInfo.isRisk)
    }

    @Test
    fun testGetRecommendations() {
        val normalRecommendations = BloodPressureClassifier.getRecommendations(BloodPressureCategory.NORMAL)
        assertTrue(normalRecommendations.isNotEmpty())
        assertTrue(normalRecommendations.any { it.contains("保持") })

        val elevatedRecommendations = BloodPressureClassifier.getRecommendations(BloodPressureCategory.ELEVATED)
        assertTrue(elevatedRecommendations.isNotEmpty())
        assertTrue(elevatedRecommendations.any { it.contains("生活方式") })

        val crisisRecommendations = BloodPressureClassifier.getRecommendations(BloodPressureCategory.HYPERTENSIVE_CRISIS)
        assertTrue(crisisRecommendations.isNotEmpty())
        assertTrue(crisisRecommendations.any { it.contains("立即") || it.contains("紧急") })
    }

    @Test
    fun testIsHighRisk() {
        assertFalse(BloodPressureClassifier.isHighRisk(BloodPressureCategory.NORMAL))
        assertFalse(BloodPressureClassifier.isHighRisk(BloodPressureCategory.ELEVATED))
        assertTrue(BloodPressureClassifier.isHighRisk(BloodPressureCategory.HIGH_STAGE_1))
        assertTrue(BloodPressureClassifier.isHighRisk(BloodPressureCategory.HIGH_STAGE_2))
        assertTrue(BloodPressureClassifier.isHighRisk(BloodPressureCategory.HYPERTENSIVE_CRISIS))
    }

    @Test
    fun testGetRiskLevel() {
        assertEquals(0, BloodPressureClassifier.getRiskLevel(BloodPressureCategory.NORMAL))
        assertEquals(1, BloodPressureClassifier.getRiskLevel(BloodPressureCategory.ELEVATED))
        assertEquals(2, BloodPressureClassifier.getRiskLevel(BloodPressureCategory.HIGH_STAGE_1))
        assertEquals(3, BloodPressureClassifier.getRiskLevel(BloodPressureCategory.HIGH_STAGE_2))
        assertEquals(4, BloodPressureClassifier.getRiskLevel(BloodPressureCategory.HYPERTENSIVE_CRISIS))
    }

    @Test
    fun testEdgeCases() {
        // 测试极端值
        val lowBP = BloodPressureClassifier.classify(90, 60)
        assertEquals(BloodPressureCategory.NORMAL, lowBP) // 低血压也归类为正常范围的边界
        
        val highBP = BloodPressureClassifier.classify(250, 150)
        assertEquals(BloodPressureCategory.HYPERTENSIVE_CRISIS, highBP)
    }

    @Test
    fun testCategoryNameConstants() {
        assertEquals("正常血压", BloodPressureCategory.NORMAL.categoryName)
        assertEquals("血压偏高", BloodPressureCategory.ELEVATED.categoryName)
        assertEquals("高血压1期", BloodPressureCategory.HIGH_STAGE_1.categoryName)
        assertEquals("高血压2期", BloodPressureCategory.HIGH_STAGE_2.categoryName)
        assertEquals("高血压危象", BloodPressureCategory.HYPERTENSIVE_CRISIS.categoryName)
    }
}