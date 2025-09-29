package com.example.xueya.domain.analysis

import com.example.xueya.domain.model.BloodPressureData
import com.example.xueya.domain.model.BloodPressureCategory
import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDateTime

/**
 * 血压分析算法测试
 */
class BloodPressureAnalysisTest {

    @Test
    fun testAnomalyDetection() {
        val detector = BloodPressureAnomalyDetector()
        
        // 创建测试数据：包含正常和异常血压值
        val testData = listOf(
            BloodPressureData(
                id = 1L,
                systolic = 120,
                diastolic = 80,
                heartRate = 70,
                measureTime = LocalDateTime.now().minusDays(4),
                tags = emptyList(),
                note = ""
            ),
            BloodPressureData(
                id = 2L,
                systolic = 130,
                diastolic = 85,
                heartRate = 75,
                measureTime = LocalDateTime.now().minusDays(3),
                tags = emptyList(),
                note = ""
            ),
            BloodPressureData(
                id = 3L,
                systolic = 150, // 异常值
                diastolic = 95,  // 异常值
                heartRate = 80,
                measureTime = LocalDateTime.now().minusDays(2),
                tags = emptyList(),
                note = ""
            ),
            BloodPressureData(
                id = 4L,
                systolic = 180, // 高血压危象
                diastolic = 110,
                heartRate = 90,
                measureTime = LocalDateTime.now().minusDays(1),
                tags = emptyList(),
                note = ""
            ),
            BloodPressureData(
                id = 5L,
                systolic = 125,
                diastolic = 82,
                heartRate = 72,
                measureTime = LocalDateTime.now(),
                tags = emptyList(),
                note = ""
            )
        )
        
        val anomalies = detector.detectAnomalies(testData)
        
        // 应该检测到至少1个异常点：180/110（医学标准异常）
        assertTrue("应该检测到至少1个异常点", anomalies.size >= 1)
        
        // 检查180/110的异常点（医学标准异常）
        val medicalAnomaly = anomalies.find { it.data.systolic == 180 }
        assertNotNull("应该检测到180/110的医学标准异常", medicalAnomaly)
        assertTrue("180/110应该是医学标准异常", medicalAnomaly!!.reason.contains("医学标准异常"))
        assertEquals("180/110应该是重度异常", BloodPressureAnomalyDetector.Severity.HIGH, medicalAnomaly.severity)
        
        // 检查是否有统计学异常
        val statisticalAnomalies = anomalies.filter { it.reason.contains("Z-Score") }
        println("检测到的异常点数量: ${anomalies.size}")
        anomalies.forEach { anomaly ->
            println("异常点: ${anomaly.data.systolic}/${anomaly.data.diastolic} - ${anomaly.reason}")
        }
    }

    @Test
    fun testTrendAnalysis() {
        val analyzer = TrendAnalyzer()
        
        // 创建上升趋势的测试数据
        val risingData = listOf(
            BloodPressureData(
                id = 1L,
                systolic = 120,
                diastolic = 80,
                heartRate = 70,
                measureTime = LocalDateTime.now().minusDays(4),
                tags = emptyList(),
                note = ""
            ),
            BloodPressureData(
                id = 2L,
                systolic = 125,
                diastolic = 82,
                heartRate = 72,
                measureTime = LocalDateTime.now().minusDays(3),
                tags = emptyList(),
                note = ""
            ),
            BloodPressureData(
                id = 3L,
                systolic = 130,
                diastolic = 85,
                heartRate = 75,
                measureTime = LocalDateTime.now().minusDays(2),
                tags = emptyList(),
                note = ""
            ),
            BloodPressureData(
                id = 4L,
                systolic = 135,
                diastolic = 88,
                heartRate = 78,
                measureTime = LocalDateTime.now().minusDays(1),
                tags = emptyList(),
                note = ""
            ),
            BloodPressureData(
                id = 5L,
                systolic = 140,
                diastolic = 90,
                heartRate = 80,
                measureTime = LocalDateTime.now(),
                tags = emptyList(),
                note = ""
            )
        )
        
        val trend = analyzer.analyzeTrend(risingData)
        
        // 应该检测到上升趋势
        assertEquals("应该检测到上升趋势", TrendAnalyzer.TrendDirection.INCREASING, trend.direction)
        assertTrue("斜率应该为正数", trend.slope > 0)
        assertTrue("描述应该包含上升趋势", trend.description.contains("上升趋势"))
    }

    @Test
    fun testStableTrend() {
        val analyzer = TrendAnalyzer()
        
        // 创建稳定趋势的测试数据
        val stableData = listOf(
            BloodPressureData(
                id = 1L,
                systolic = 120,
                diastolic = 80,
                heartRate = 70,
                measureTime = LocalDateTime.now().minusDays(4),
                tags = emptyList(),
                note = ""
            ),
            BloodPressureData(
                id = 2L,
                systolic = 121,
                diastolic = 81,
                heartRate = 71,
                measureTime = LocalDateTime.now().minusDays(3),
                tags = emptyList(),
                note = ""
            ),
            BloodPressureData(
                id = 3L,
                systolic = 119,
                diastolic = 79,
                heartRate = 69,
                measureTime = LocalDateTime.now().minusDays(2),
                tags = emptyList(),
                note = ""
            ),
            BloodPressureData(
                id = 4L,
                systolic = 122,
                diastolic = 82,
                heartRate = 72,
                measureTime = LocalDateTime.now().minusDays(1),
                tags = emptyList(),
                note = ""
            ),
            BloodPressureData(
                id = 5L,
                systolic = 120,
                diastolic = 80,
                heartRate = 70,
                measureTime = LocalDateTime.now(),
                tags = emptyList(),
                note = ""
            )
        )
        
        val trend = analyzer.analyzeTrend(stableData)
        
        // 应该检测到稳定趋势
        assertEquals("应该检测到稳定趋势", TrendAnalyzer.TrendDirection.STABLE, trend.direction)
        assertTrue("描述应该包含稳定", trend.description.contains("稳定"))
    }
}
