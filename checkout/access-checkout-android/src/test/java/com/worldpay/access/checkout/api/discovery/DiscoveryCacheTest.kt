package com.worldpay.access.checkout.api.discovery

import com.worldpay.access.checkout.api.AsyncTaskResult
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DiscoveryCacheTest {

    @Before
    fun setUp() {
        DiscoveryCache.results.clear()
    }

    @Test
    fun `should be a singleton`() {
        val cache1 = DiscoveryCache
        val cache2 =  DiscoveryCache
        assertEquals(cache1, cache2)
    }

    @Test
    fun `should set value when setResult is called`() {
        val expectedResult = AsyncTaskResult("some href")
        assertNull(DiscoveryCache.results["service:verifiedTokens"])

        DiscoveryCache.setResult(expectedResult, DiscoverLinks.verifiedTokens)

        assertEquals(expectedResult, DiscoveryCache.results["service:verifiedTokens"])
    }

    @Test
    fun `should return result when getResult is called`() {
        val expectedResult = AsyncTaskResult("some href")
        DiscoveryCache.results["service:verifiedTokens"] = expectedResult

        assertEquals(expectedResult, DiscoveryCache.getResult(DiscoverLinks.verifiedTokens))
    }
}
