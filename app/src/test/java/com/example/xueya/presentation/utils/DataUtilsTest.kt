package com.example.xueya.presentation.utils

import org.junit.Test
import org.junit.Assert.*

/**
 * DataUtils单元测试
 */
class DataUtilsTest {

    @Test
    fun testFormatBloodPressure() {
        val result = DataUtils.formatBloodPressure(120, 80)
        assertEquals("120/80 mmHg", result)
    }

    @Test
    fun testFormatHeartRate() {
        val result = DataUtils.formatHeartRate(75)
        assertEquals("75 bpm", result)
    }

    @Test
    fun testFormatPercentage() {
        val result = DataUtils.formatPercentage(0.75, 1)
        assertEquals("75.0%", result)
        
        val result2 = DataUtils.formatPercentage(0.333, 0)
        assertEquals("33%", result2)
    }

    @Test
    fun testCalculateAverage() {
        val values = listOf(10, 20, 30, 40, 50)
        val result = DataUtils.calculateAverage(values)
        assertEquals(30.0, result, 0.001)
    }

    @Test
    fun testCalculateAverageEmptyList() {
        val values = emptyList<Int>()
        val result = DataUtils.calculateAverage(values)
        assertEquals(0.0, result, 0.001)
    }

    @Test
    fun testCalculateMedian() {
        val oddValues = listOf(1, 3, 5, 7, 9)
        val oddResult = DataUtils.calculateMedian(oddValues)
        assertEquals(5.0, oddResult, 0.001)
        
        val evenValues = listOf(2, 4, 6, 8)
        val evenResult = DataUtils.calculateMedian(evenValues)
        assertEquals(5.0, evenResult, 0.001) // (4+6)/2
    }

    @Test
    fun testCalculateStandardDeviation() {
        val values = listOf(2, 4, 4, 4, 5, 5, 7, 9)
        val result = DataUtils.calculateStandardDeviation(values)
        assertTrue("Standard deviation should be positive", result > 0)
    }

    @Test
    fun testCalculateTrend() {
        val increasingValues = listOf(1, 2, 3, 4, 5)
        val increasingTrend = DataUtils.calculateTrend(increasingValues)
        assertTrue("Trend should be positive for increasing values", increasingTrend > 0)
        
        val decreasingValues = listOf(5, 4, 3, 2, 1)
        val decreasingTrend = DataUtils.calculateTrend(decreasingValues)
        assertTrue("Trend should be negative for decreasing values", decreasingTrend < 0)
    }

    @Test
    fun testSafeDivide() {
        val result1 = DataUtils.safeDivide(10.0, 2.0)
        assertEquals(5.0, result1, 0.001)
        
        val result2 = DataUtils.safeDivide(10.0, 0.0, -1.0)
        assertEquals(-1.0, result2, 0.001)
    }

    @Test
    fun testCalculateHealthScore() {
        // 正常血压
        val normalScore = DataUtils.calculateHealthScore(120, 80, 70, 30)
        assertTrue("Normal BP should have high score", normalScore >= 80)
        
        // 高血压
        val highScore = DataUtils.calculateHealthScore(160, 100, 90, 30)
        assertTrue("High BP should have low score", highScore < 80)
    }

    @Test
    fun testSafeStringConversion() {
        assertEquals(123, "123".toIntSafely())
        assertEquals(0, "invalid".toIntSafely())
        assertEquals(-1, null.toIntSafely(-1))
        
        assertEquals(12.5, "12.5".toDoubleSafely(), 0.001)
        assertEquals(0.0, "invalid".toDoubleSafely(), 0.001)
    }

    @Test
    fun testSafeListAccess() {
        val list = listOf("a", "b", "c")
        assertEquals("a", list.safeGet(0))
        assertEquals("c", list.safeGet(2))
        assertNull(list.safeGet(5))
        assertNull(list.safeGet(-1))
    }

    @Test
    fun testEmailValidation() {
        assertTrue(DataUtils.isValidEmail("test@example.com"))
        assertTrue(DataUtils.isValidEmail("user.name@domain.co.uk"))
        assertFalse(DataUtils.isValidEmail("invalid-email"))
        assertFalse(DataUtils.isValidEmail("@domain.com"))
        assertFalse(DataUtils.isValidEmail("user@"))
    }

    @Test
    fun testPhoneValidation() {
        assertTrue(DataUtils.isValidPhoneNumber("13812345678"))
        assertTrue(DataUtils.isValidPhoneNumber("15987654321"))
        assertFalse(DataUtils.isValidPhoneNumber("12812345678")) // 不是1开头的手机号
        assertFalse(DataUtils.isValidPhoneNumber("1381234567"))  // 长度不够
        assertFalse(DataUtils.isValidPhoneNumber("138123456789")) // 长度过长
    }

    @Test
    fun testPagination() {
        val items = (1..20).toList()
        
        val page0 = DataUtils.paginate(items, 0, 5)
        assertEquals(listOf(1, 2, 3, 4, 5), page0)
        
        val page1 = DataUtils.paginate(items, 1, 5)
        assertEquals(listOf(6, 7, 8, 9, 10), page1)
        
        val page3 = DataUtils.paginate(items, 3, 5)
        assertEquals(listOf(16, 17, 18, 19, 20), page3)
        
        val outOfBounds = DataUtils.paginate(items, 10, 5)
        assertTrue(outOfBounds.isEmpty())
    }

    @Test
    fun testSearchFilter() {
        data class TestItem(val name: String, val description: String)
        
        val items = listOf(
            TestItem("Apple", "红色的苹果"),
            TestItem("Banana", "黄色的香蕉"),
            TestItem("Cherry", "樱桃很甜")
        )
        
        val searchFields = listOf<(TestItem) -> String>(
            { it.name },
            { it.description }
        )
        
        val appleResults = DataUtils.searchFilter(items, "apple", searchFields)
        assertEquals(1, appleResults.size)
        assertEquals("Apple", appleResults[0].name)
        
        val colorResults = DataUtils.searchFilter(items, "红色", searchFields)
        assertEquals(1, colorResults.size)
        assertEquals("Apple", colorResults[0].name)
        
        val emptyResults = DataUtils.searchFilter(items, "不存在", searchFields)
        assertTrue(emptyResults.isEmpty())
    }
}