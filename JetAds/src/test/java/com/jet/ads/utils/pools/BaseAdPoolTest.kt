package com.jet.ads.utils.pools

import android.os.SystemClock
import androidx.annotation.VisibleForTesting
import com.google.common.base.Verify.verify
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.unmockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test


class BaseAdPoolTest {
    private lateinit var adPool: TestAdPool

    @Before
    fun setup() {
        mockkStatic(SystemClock::class)
        every { SystemClock.elapsedRealtime() } returns 1000L
        adPool = spyk(TestAdPool())
    }

    @After
    fun tearDown() {
        unmockkStatic(SystemClock::class)
    }

    @Test
    fun `saveAd adds ad to pool and emits updated list`() = runTest {
        val ad = mockk<TestAd>()
        adPool.saveAd("test_id", ad)

        val result = adPool.getAllAds().first()
        assertThat(result).hasSize(1)
        assertThat(result[0].first).isEqualTo("test_id")
        assertThat(result[0].second.first).isEqualTo(ad)
        assertThat(result[0].second.second).isEqualTo(1000L)
    }

    @Test
    fun `getAd returns correct ad`() = runTest {
        val ad = mockk<TestAd>()
        adPool.saveAd("test_id", ad)

        val result = adPool.getAd("test_id")
        assertThat(result?.first).isEqualTo(ad)
        assertThat(result?.second).isEqualTo(1000L)
    }

    @Test
    fun `deleteAdFromPool removes ad and emits updated list`() = runTest {
        val ad = mockk<TestAd>()
        adPool.saveAd("test_id", ad)
        adPool.deleteAdFromPool("test_id", ad)

        val result = adPool.getAllAds().first()
        assertThat(result).isEmpty()
//        verify { adPool.clearCallback(ad) }
    }

    @Test
    fun `clearPool removes all ads and emits empty list`() = runTest {
        val ad1 = mockk<TestAd>()
        val ad2 = mockk<TestAd>()
        adPool.saveAd("test_id1", ad1)
        adPool.saveAd("test_id2", ad2)

        adPool.clearPool()

        val result = adPool.getAllAds().first()
        assertThat(result).isEmpty()
    }

    private class TestAdPool : BaseAdPool<TestAd>() {

        override fun clearCallback(ad: TestAd?) {
            // Do nothing for test
        }
    }

    private interface TestAd
}