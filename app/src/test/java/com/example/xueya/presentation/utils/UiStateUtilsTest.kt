package com.example.xueya.presentation.utils

import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*

/**
 * UiStateUtils单元测试
 */
class UiStateUtilsTest {

    @Test
    fun testUiStateCreation() {
        val emptyState = uiStateOf<String>()
        assertNull(emptyState.data)
        assertFalse(emptyState.isLoading)
        assertNull(emptyState.error)
        assertFalse(emptyState.isRefreshing)
        assertTrue(emptyState.isEmpty)

        val loadingState = uiStateOf<String>(isLoading = true)
        assertTrue(loadingState.isLoading)
        assertFalse(loadingState.hasData)
        assertFalse(loadingState.hasError)

        val dataState = uiStateOf(data = "test data")
        assertTrue(dataState.hasData)
        assertEquals("test data", dataState.data)
        assertFalse(dataState.isLoading)

        val errorState = uiStateOf<String>(error = "test error")
        assertTrue(errorState.hasError)
        assertEquals("test error", errorState.error)
        assertFalse(errorState.hasData)
    }

    @Test
    fun testResourceToUiState() {
        val loadingResource = Resource.Loading
        val loadingUiState = loadingResource.toUiState()
        assertTrue(loadingUiState.isLoading)
        assertFalse(loadingUiState.hasData)
        assertFalse(loadingUiState.hasError)

        val successResource = Resource.Success("test data")
        val successUiState = successResource.toUiState()
        assertTrue(successUiState.hasData)
        assertEquals("test data", successUiState.data)
        assertFalse(successUiState.isLoading)

        val errorResource = Resource.Error("test error")
        val errorUiState = errorResource.toUiState()
        assertTrue(errorUiState.hasError)
        assertEquals("test error", errorUiState.error)
        assertFalse(errorUiState.hasData)
    }

    @Test
    fun testAsyncOperationState() = runTest {
        val asyncState = AsyncOperationState<String>()
        
        // 初始状态应该是Loading
        assertTrue(asyncState.state.value is Resource.Loading)
        
        // 执行成功的操作
        asyncState.execute { "success result" }
        val successResult = asyncState.state.value
        assertTrue(successResult is Resource.Success)
        assertEquals("success result", (successResult as Resource.Success).data)
        
        // 执行失败的操作
        asyncState.execute { throw RuntimeException("test error") }
        val errorResult = asyncState.state.value
        assertTrue(errorResult is Resource.Error)
        assertEquals("test error", (errorResult as Resource.Error).message)
        
        // 重置状态
        asyncState.reset()
        assertTrue(asyncState.state.value is Resource.Loading)
    }

    @Test
    fun testPagingStateManager() {
        val pagingManager = PagingStateManager<String>()
        
        // 初始状态
        val initialState = pagingManager.state.value
        assertTrue(initialState.items.isEmpty())
        assertFalse(initialState.isLoading)
        assertTrue(initialState.isEmpty)
        
        // 开始加载
        pagingManager.startLoading()
        assertTrue(pagingManager.state.value.isLoading)
        
        // 设置数据
        val testItems = listOf("item1", "item2", "item3")
        pagingManager.setItems(testItems, hasNextPage = true)
        
        val stateWithItems = pagingManager.state.value
        assertEquals(testItems, stateWithItems.items)
        assertFalse(stateWithItems.isLoading)
        assertTrue(stateWithItems.hasNextPage)
        assertEquals(1, stateWithItems.currentPage)
        
        // 加载更多
        pagingManager.startLoadingMore()
        assertTrue(pagingManager.state.value.isLoadingMore)
        
        val moreItems = listOf("item4", "item5")
        pagingManager.appendItems(moreItems, hasNextPage = false)
        
        val finalState = pagingManager.state.value
        assertEquals(testItems + moreItems, finalState.items)
        assertFalse(finalState.isLoadingMore)
        assertFalse(finalState.hasNextPage)
        assertEquals(2, finalState.currentPage)
        
        // 设置错误
        pagingManager.setError("test error")
        val errorState = pagingManager.state.value
        assertTrue(errorState.hasError)
        assertEquals("test error", errorState.error)
        
        // 刷新
        pagingManager.refresh()
        val refreshState = pagingManager.state.value
        assertTrue(refreshState.isLoading)
        assertTrue(refreshState.items.isEmpty())
    }

    @Test
    fun testFormStateManager() {
        val formManager = FormStateManager()
        
        // 初始状态
        assertTrue(formManager.isFormValid())
        
        // 更新字段
        formManager.updateField("email", "test@example.com")
        formManager.updateField("password", "123456")
        
        val formData = formManager.getFormData()
        assertEquals("test@example.com", formData["email"])
        assertEquals("123456", formData["password"])
        
        // 验证字段
        val emailValid = formManager.validateField("email") { value ->
            if (value.contains("@")) null else "Invalid email"
        }
        assertTrue(emailValid)
        
        val passwordValid = formManager.validateField("password") { value ->
            if (value.length >= 8) null else "Password too short"
        }
        assertFalse(passwordValid)
        assertFalse(formManager.isFormValid())
        
        // 修复密码
        formManager.updateField("password", "12345678")
        formManager.validateField("password") { value ->
            if (value.length >= 8) null else "Password too short"
        }
        assertTrue(formManager.isFormValid())
        
        // 重置表单
        formManager.reset()
        assertTrue(formManager.getFormData().values.all { it.isEmpty() })
    }

    @Test
    fun testCacheStateManager() {
        val cacheManager = CacheStateManager<String, String>()
        
        // 缓存数据
        cacheManager.put("key1", "value1")
        cacheManager.put("key2", "value2")
        
        // 获取数据
        assertEquals("value1", cacheManager.get("key1"))
        assertEquals("value2", cacheManager.get("key2"))
        assertNull(cacheManager.get("nonexistent"))
        
        // 移除数据
        cacheManager.remove("key1")
        assertNull(cacheManager.get("key1"))
        assertEquals("value2", cacheManager.get("key2"))
        
        // 清空缓存
        cacheManager.clear()
        assertNull(cacheManager.get("key2"))
    }

    @Test
    fun testSafeExecute() = runTest {
        // 成功执行
        val successResult = safeExecute(
            block = { "success" },
            onError = { "error" }
        )
        assertEquals("success", successResult)
        
        // 异常处理
        val errorResult = safeExecute(
            block = { throw RuntimeException("test error") },
            onError = { "handled error" }
        )
        assertEquals("handled error", errorResult)
    }

    @Test
    fun testRetryOperation() = runTest {
        var attemptCount = 0
        
        // 第二次尝试成功
        val result = retryOperation(maxAttempts = 3, delayMillis = 1) {
            attemptCount++
            if (attemptCount < 2) {
                throw RuntimeException("Fail on attempt $attemptCount")
            }
            "Success on attempt $attemptCount"
        }
        
        assertEquals("Success on attempt 2", result)
        assertEquals(2, attemptCount)
    }

    @Test
    fun testRetryOperationFailure() = runTest {
        var attemptCount = 0
        
        try {
            retryOperation(maxAttempts = 2, delayMillis = 1) {
                attemptCount++
                throw RuntimeException("Always fail")
            }
            fail("Should have thrown exception")
        } catch (e: Exception) {
            assertEquals("Always fail", e.message)
            assertEquals(2, attemptCount)
        }
    }
}