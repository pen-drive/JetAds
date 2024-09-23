package com.jet.ads.admob.open_ad

import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.jet.ads.common.callbacks.ShowAdCallBack
import com.jet.ads.common.controller.AdsControl
import com.jet.ads.common.controller.ControlProvider
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class OpenAdAdmobSetupTest {

    private lateinit var openAdAdmobSetup: OpenAdAdmobSetup
    private lateinit var mockAppLifecycleManager: AppLifecycleManager
    private lateinit var mockControlProvider: ControlProvider
    private lateinit var mockAdsControl: AdsControl
    private lateinit var mockActivity: ComponentActivity
    private lateinit var mockAppOpenAdManager: AppOpenAdManager
    private lateinit var mockShowAdCallBack: ShowAdCallBack
    private lateinit var appOpenAdManager: AppOpenAdManager
    private lateinit var mockLifecycle: Lifecycle
    private lateinit var adsEnabledFlow: MutableStateFlow<Boolean>

    @Before
    fun setup() {
        mockAppLifecycleManager = mockk(relaxed = true)
        mockControlProvider = mockk(relaxed = true)
        mockAdsControl = mockk(relaxed = true)
        mockActivity = mockk(relaxed = true)
        mockAppOpenAdManager = mockk(relaxed = true)
        mockShowAdCallBack = mockk(relaxed = true)
        mockLifecycle = mockk(relaxed = true)
        appOpenAdManager = mockk(relaxed = true)

        adsEnabledFlow = MutableStateFlow(true)
        every { mockControlProvider.getAdsControl() } returns mockAdsControl
        every { mockAdsControl.areAdsEnabled() } returns adsEnabledFlow
        every { mockActivity.lifecycle } returns mockLifecycle

        openAdAdmobSetup =
            OpenAdAdmobSetup(mockAppLifecycleManager,  mockControlProvider)

        mockkConstructor(AppOpenAdManager::class)
        every { anyConstructed<AppOpenAdManager>().loadAd(any(), any(), any()) } just Runs
        every { anyConstructed<AppOpenAdManager>().showAd(any(), any(), any()) } just Runs
    }

    @Test
    fun `registerOpenAppAd when ads are enabled`() = runTest {
        val adUnitId = "test_ad_unit_id"
        val showOnColdStart = true
        val closeSplashScreen: () -> Unit = mockk(relaxed = true)

        openAdAdmobSetup.registerOpenAppAd(
            adUnitId, mockActivity, showOnColdStart, mockShowAdCallBack, closeSplashScreen
        )

        verify {
            mockAppLifecycleManager.setShowOnColdStart(showOnColdStart)
            mockAppLifecycleManager.registerCallback(openAdAdmobSetup)
            anyConstructed<AppOpenAdManager>().loadAd(adUnitId, mockActivity, any())
        }
    }

    @Test
    fun `registerOpenAppAd when ads are disabled`() = runTest {
        adsEnabledFlow.value = false

        openAdAdmobSetup.registerOpenAppAd(
            "test_ad_unit_id", mockActivity, true, mockShowAdCallBack
        ) {}

        verify(exactly = 0) {
            mockAppLifecycleManager.setShowOnColdStart(any())
            mockAppLifecycleManager.registerCallback(any())
            anyConstructed<AppOpenAdManager>().loadAd(any(), any(), any())
        }
    }

    @Test
    fun `onAppStart shows ad when ads are enabled`() = runTest {
        val adUnitId = "test_ad_unit_id"
        openAdAdmobSetup.registerOpenAppAd(adUnitId, mockActivity, true, mockShowAdCallBack) {}

        openAdAdmobSetup.onAppStart()

        verify {
            anyConstructed<AppOpenAdManager>().showAd(adUnitId, mockActivity, any())
        }
    }

    @Test
    fun `onAppStart does not show ad when ads are disabled`() = runTest {
        adsEnabledFlow.value = false

        openAdAdmobSetup.onAppStart()

        verify(exactly = 0) {
            anyConstructed<AppOpenAdManager>().showAd(any(), any(), any())
        }
    }

    @Test
    fun `lifecycle observer is added and removed correctly`() = runTest {
        val adUnitId = "test_ad_unit_id"
        openAdAdmobSetup.registerOpenAppAd(adUnitId, mockActivity, true, mockShowAdCallBack) {}

        val observerSlot = slot<LifecycleEventObserver>()
        verify { mockLifecycle.addObserver(capture(observerSlot)) }

        val capturedObserver = observerSlot.captured
        capturedObserver.onStateChanged(mockk(), Lifecycle.Event.ON_DESTROY)

        verify {
            mockAppLifecycleManager.unregisterCallback(openAdAdmobSetup)
            mockLifecycle.removeObserver(any())
        }
    }


    @Test
    fun `timeout triggers closeSplashScreen when ad doesn't load in time`() = runTest {
        val adUnitId = "test_ad_unit_id"
        val closeSplashScreen: () -> Unit = mockk(relaxed = true)

        openAdAdmobSetup.registerOpenAppAd(
            adUnitId, mockActivity, true, mockShowAdCallBack, closeSplashScreen
        )

        advanceTimeBy(2.seconds.inWholeMilliseconds)

        verify(exactly = 1) { closeSplashScreen() }
    }


    @Test
    fun `ad load success prevents timeout from triggering closeSplashScreen`() = runTest {
        // Arrange
        val adUnitId = "test_ad_unit_id"
        val closeSplashScreen: () -> Unit = mockk(relaxed = true)
        var adLoadCallback: (() -> Unit)? = null

        every { anyConstructed<AppOpenAdManager>().loadAd(any(), any(), captureLambda()) } answers {
            adLoadCallback = lambda<() -> Unit>().captured
        }

        // Act
        openAdAdmobSetup.registerOpenAppAd(
            adUnitId, mockActivity, true, mockShowAdCallBack, closeSplashScreen
        )

        // Simulate ad load success immediately
        adLoadCallback?.invoke()

        // Wait for full timeout duration
        advanceTimeBy(2.seconds.inWholeMilliseconds)

        // Assert
        verify(exactly = 0) { closeSplashScreen() }
        verify(exactly = 1) { mockAppLifecycleManager.notifyAdShown() }
    }


}