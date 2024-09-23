package com.jet.ads.utils.manager

import android.app.Activity
import android.content.Context
import com.jet.ads.common.controller.AdsControl
import com.jet.ads.common.controller.ControlProvider
import com.jet.ads.utils.AdNotAvailableException
import com.jet.ads.utils.AdProvider
import com.jet.ads.utils.expiration.AdExpirationHandler
import com.jet.ads.utils.pools.AdPool
import com.jet.ads.utils.retry.RetryPolicy
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test


class BaseAdManagerTest {

    private lateinit var controlProvider: ControlProvider
    private lateinit var adPool: AdPool<TestAd>
    private lateinit var adProvider: AdProvider<TestAd, TestCallbacks>
    private lateinit var adExpirationHandler: AdExpirationHandler
    private lateinit var retryPolicy: RetryPolicy
    private lateinit var adManager: TestAdManager

    private lateinit var context: Context
    private lateinit var activity: Activity

    @Before
    fun setup() {
        controlProvider = mockk()
        adPool = mockk()
        adProvider = mockk()
        adExpirationHandler = mockk()
        retryPolicy = mockk()
        context = mockk()
        activity = mockk()

        every { context.applicationContext } returns context

        adManager = TestAdManager(
            controlProvider, adPool, adProvider, adExpirationHandler, retryPolicy
        )
    }

    @Test
    fun `loadAd should use applicationContext`() = runTest {
        val adsControl: AdsControl = mockk()
        every { controlProvider.getAdsControl() } returns adsControl
        every { adsControl.areAdsEnabled() } returns MutableStateFlow(true)
        every { adProvider.load(any(), any(), any(), any()) } just Runs
        adManager.loadAd("testAdUnitId", context)

        verify { context.applicationContext }
    }


    @Test
    fun `loadAd should not load when ads are disabled`() = runTest {
        val adsControl: AdsControl = mockk()
        every { controlProvider.getAdsControl() } returns adsControl
        every { adsControl.areAdsEnabled() } returns MutableStateFlow(false)

        adManager.loadAd("testAdUnitId", context)

        verify(exactly = 0) { adProvider.load(any(), any(), any(), any()) }
    }

    @Test
    fun `loadAd should load ad when ads are enabled`() = runTest {
        val adsControl: AdsControl = mockk()
        every { controlProvider.getAdsControl() } returns adsControl
        every { adsControl.areAdsEnabled() } returns MutableStateFlow(true)
        every { adProvider.load(any(), any(), any(), any()) } just Runs

        adManager.loadAd("testAdUnitId", context)

        verify(exactly = 1) { adProvider.load(any(), any(), any(), any()) }
    }

    @Test
    fun `loadAd should save ad to pool when loaded successfully`() = runTest {
        val adsControl: AdsControl = mockk()
        every { controlProvider.getAdsControl() } returns adsControl
        every { adsControl.areAdsEnabled() } returns MutableStateFlow(true)
        every { adPool.saveAd(any(), any()) } just Runs

        every { adProvider.load(any(), any(), any(), any()) } answers {
            thirdArg<(TestAd) -> Unit>().invoke(TestAd())
        }

        adManager.loadAd("testAdUnitId", context)

        verify(exactly = 1) { adPool.saveAd(any(), any()) }
    }

    @Test
    fun `showAd should load ad when ad is not available`() = runTest {

        val adsControl: AdsControl = mockk()
        every { controlProvider.getAdsControl() } returns adsControl
        every { activity.applicationContext } returns activity
        every { adsControl.areAdsEnabled() } returns MutableStateFlow(true)

        every { adPool.getAd(any()) } returns null
        every { adProvider.load(any(), any(), any(), any()) } just Runs

        adManager.showAd("testAdUnitId", activity)




        verify(exactly = 1) { adProvider.load(any(), any(), any(), any()) }
    }


    @Test
    fun `showAd should show ad when available and not expired`() = runTest {
        val adsControl: AdsControl = mockk()
        every { controlProvider.getAdsControl() } returns adsControl
        every { adsControl.areAdsEnabled() } returns MutableStateFlow(true)
        every { adPool.getAd(any()) } returns Pair(TestAd(), 0L)
        every { adExpirationHandler.isAdExpired<TestAd>(any()) } returns false
        every { adProvider.show(any(), any(), any(), any(), any()) } just Runs

        adManager.showAd("testAdUnitId", activity)

        verify(exactly = 1) { adProvider.show(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `showAd should load new ad when current ad is expired`() = runTest {
        val adsControl: AdsControl = mockk()
        every { controlProvider.getAdsControl() } returns adsControl
        every { activity.applicationContext } returns activity

        every { adsControl.areAdsEnabled() } returns MutableStateFlow(true)
        every { adPool.getAd(any()) } returns Pair(TestAd(), 0L)
        every { adExpirationHandler.isAdExpired<TestAd>(any()) } returns true
        every { adPool.deleteAdFromPool(any(), any()) } just Runs
        every { adProvider.load(any(), any(), any(), any()) } just Runs

        adManager.showAd("testAdUnitId", activity)

        verify(exactly = 1) { adPool.deleteAdFromPool(any(), any()) }
        verify(exactly = 1) { adProvider.load(any(), any(), any(), any()) }
    }

    @Test
    fun `showAd should load new ad when ad is dismissed`() = runTest {

        val adsControl: AdsControl = mockk()
        val testAdUnitId = "testAdUnitId"
        val currentAd = TestAd()
        var onDismissCallback: () -> Unit = {}

        every { controlProvider.getAdsControl() } returns adsControl
        every { activity.applicationContext } returns activity
        every { adsControl.areAdsEnabled() } returns MutableStateFlow(true)
        every { adPool.getAd(testAdUnitId) } returns Pair(currentAd, 0L)
        every { adExpirationHandler.isAdExpired<TestAd>(any()) } returns false
        every { adPool.deleteAdFromPool(any(), any()) } just Runs
        every {
            adProvider.show(any(), any(), any(), any(), captureLambda())
        } answers {
            onDismissCallback = lambda<() -> Unit>().captured
            onDismissCallback.invoke() // Simulate immediate dismissal
        }
        every { adProvider.load(any(), any(), any(), any()) } just Runs


        adManager.showAd(testAdUnitId, activity)

        verifyOrder {
            controlProvider.getAdsControl()
            adsControl.areAdsEnabled()
            adPool.getAd(testAdUnitId)
            adExpirationHandler.isAdExpired<TestAd>(any())
            adProvider.show(testAdUnitId, any(), activity, null, any())
            adPool.deleteAdFromPool(testAdUnitId, currentAd)
            adProvider.load(testAdUnitId, activity, any(), any())
        }


        verify(exactly = 1) { adProvider.show(any(), any(), any(), any(), any()) }
        verify(exactly = 1) { adPool.deleteAdFromPool(any(), any()) }
        verify(exactly = 1) { adProvider.load(any(), any(), any(), any()) }
    }

    private class TestAdManager(
        controlProvider: ControlProvider,
        adPool: AdPool<TestAd>,
        adProvider: AdProvider<TestAd, TestCallbacks>,
        adExpirationHandler: AdExpirationHandler,
        retryPolicy: RetryPolicy
    ) : BaseAdManager<TestAd, TestCallbacks>(
        controlProvider, adPool, adProvider, adExpirationHandler, retryPolicy
    )

    private class TestAd
    private class TestCallbacks
}