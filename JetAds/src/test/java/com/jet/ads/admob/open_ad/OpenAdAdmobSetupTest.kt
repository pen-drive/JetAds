package com.jet.ads.admob.open_ad

import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.gms.ads.LoadAdError
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.seconds



class OpenAdAdmobSetupTest {

    private lateinit var openAdAdmobSetup: OpenAdAdmobSetup
    private lateinit var mockAppLifecycleManager: AppLifecycleManager
    private lateinit var mockControlProvider: ControlProvider
    private lateinit var mockAdsControl: AdsControl
    private lateinit var mockActivity: ComponentActivity
    private lateinit var mockAppOpenAdManager: AppOpenAdManager
    private lateinit var mockShowAdCallBack: ShowAdCallBack
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

        adsEnabledFlow = MutableStateFlow(true)
        every { mockControlProvider.getAdsControl() } returns mockAdsControl
        every { mockAdsControl.areAdsEnabled() } returns adsEnabledFlow
        every { mockActivity.lifecycle } returns mockLifecycle

        openAdAdmobSetup =
            OpenAdAdmobSetup(mockAppLifecycleManager, mockControlProvider, mockAppOpenAdManager)

        mockkConstructor(AppOpenAdManager::class)
        every { anyConstructed<AppOpenAdManager>().loadAd(any(), any(), any(), any()) } just Runs
        every { anyConstructed<AppOpenAdManager>().showAd(any(), any(), any()) } just Runs
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
            anyConstructed<AppOpenAdManager>().loadAd(any(), any(), any(), any())
        }
    }

    @Test
    fun `onAppStart shows ad when ads are enabled`() = runTest {
        val adUnitId = "test_ad_unit_id"
        openAdAdmobSetup.registerOpenAppAd(adUnitId, mockActivity, true, mockShowAdCallBack) {}

        openAdAdmobSetup.onAppStart()

        verify {
            mockAppOpenAdManager.showAd(adUnitId, mockActivity, any())
        }
    }

    @Test
    fun `onAppStart does not show ad when ads are disabled`() = runTest {
        adsEnabledFlow.value = false
        val adUnitId = "test_ad_unit_id"
        openAdAdmobSetup.registerOpenAppAd(adUnitId, mockActivity, true, mockShowAdCallBack) {}

        openAdAdmobSetup.onAppStart()

        verify(exactly = 0) {
            mockAppOpenAdManager.showAd(any(), any(), any())
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

        verify(exactly = 1) {
            mockAppLifecycleManager.unregisterCallback(openAdAdmobSetup)
            mockLifecycle.removeObserver(any())
        }
    }

    @Test
    fun `registerOpenAppAd sets up callbacks correctly`() = runTest {
        val adUnitId = "test_ad_unit_id"
        val closeSplashScreen: () -> Unit = mockk(relaxed = true)

        openAdAdmobSetup.registerOpenAppAd(
            adUnitId, mockActivity, true, mockShowAdCallBack, closeSplashScreen
        )

        verify {
            mockAppLifecycleManager.setShowOnColdStart(true)
            mockAppLifecycleManager.registerCallback(openAdAdmobSetup)
            mockAppOpenAdManager.loadAd(eq(adUnitId), eq(mockActivity), any(), any())
        }
    }

    @Test
    fun `registerOpenAppAd calls closeSplashScreen when not first entry`() = runTest {
        val adUnitId = "test_ad_unit_id"
        val closeSplashScreen: () -> Unit = mockk(relaxed = true)

        every { mockAppLifecycleManager.isFirstEntry() } returns false

        openAdAdmobSetup.registerOpenAppAd(
            adUnitId, mockActivity, true, mockShowAdCallBack, closeSplashScreen
        )

        verify {
            closeSplashScreen()
        }
    }

    @Test
    fun `registerOpenAppAd calls closeSplashScreen when ad fails to load`() = runTest {
        val adUnitId = "test_ad_unit_id"
        val closeSplashScreen: () -> Unit = mockk(relaxed = true)

        val onFailedToLoadSlot = slot<(LoadAdError) -> Unit>()

        adsEnabledFlow = MutableStateFlow(true)
        every { mockControlProvider.getAdsControl() } returns mockAdsControl
        every { mockAdsControl.areAdsEnabled() } returns adsEnabledFlow
        every { mockActivity.lifecycle } returns mockLifecycle


        openAdAdmobSetup =
            OpenAdAdmobSetup(mockAppLifecycleManager, mockControlProvider, mockAppOpenAdManager)


        every { mockAppLifecycleManager.isFirstEntry() } returns true

        every {
            mockAppOpenAdManager.loadAd(
                any(), any(), capture(onFailedToLoadSlot), any()
            )
        } answers {
            onFailedToLoadSlot.captured.invoke(LoadAdError(0, "", "", null, null))
        }

        openAdAdmobSetup.registerOpenAppAd(
            adUnitId, mockActivity, true, mockShowAdCallBack, closeSplashScreen
        )


        verify(exactly = 1) {
            closeSplashScreen()
        }
    }
}