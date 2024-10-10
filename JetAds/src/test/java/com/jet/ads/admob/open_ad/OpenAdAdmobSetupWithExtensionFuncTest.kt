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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class OpenAdAdmobSetupWithExtensionFuncTest {

    private lateinit var openAdAdmobSetupWithExtensionFunc: OpenAdAdmobSetupWithExtensionFunc
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

        openAdAdmobSetupWithExtensionFunc =
            OpenAdAdmobSetupWithExtensionFunc(mockAppLifecycleManager, mockControlProvider, mockAppOpenAdManager)

        mockkConstructor(AppOpenAdManager::class)
        every { anyConstructed<AppOpenAdManager>().loadAd(any(), any(), any(), any()) } just Runs
        every { anyConstructed<AppOpenAdManager>().showAd(any(), any(), any()) } just Runs
    }

    @Test
    fun `registerAppOpenAd when ads are disabled`() = runTest {
        adsEnabledFlow.value = false


        with(openAdAdmobSetupWithExtensionFunc) {
            mockActivity.registerAppOpenAd(
                "test_ad_unit_id", mockShowAdCallBack
            )
        }

        verify(exactly = 0) {
            mockAppLifecycleManager.setShowOnColdStart(any())
            mockAppLifecycleManager.registerCallback(any())
            anyConstructed<AppOpenAdManager>().loadAd(any(), any(), any(), any())
        }
    }

    @Test
    fun `onAppStart shows ad when ads are enabled`() = runTest {
        val adUnitId = "test_ad_unit_id"
        with(openAdAdmobSetupWithExtensionFunc) {
            mockActivity.registerAppOpenAd(adUnitId, mockShowAdCallBack)
        }

        openAdAdmobSetupWithExtensionFunc.onAppStart()

        verify {
            mockAppOpenAdManager.showAd(adUnitId, mockActivity, any())
        }
    }

    @Test
    fun `onAppStart does not show ad when ads are disabled`() = runTest {
        adsEnabledFlow.value = false
        val adUnitId = "test_ad_unit_id"
        with(openAdAdmobSetupWithExtensionFunc) {
            mockActivity.registerAppOpenAd(adUnitId, mockShowAdCallBack)
        }

        openAdAdmobSetupWithExtensionFunc.onAppStart()

        verify(exactly = 0) {
            mockAppOpenAdManager.showAd(any(), any(), any())
        }
    }

    @Test
    fun `lifecycle observer is added and removed correctly`() = runTest {
        val adUnitId = "test_ad_unit_id"
        with(openAdAdmobSetupWithExtensionFunc) {
            mockActivity.registerAppOpenAd(adUnitId, mockShowAdCallBack)
        }

        val observerSlot = slot<LifecycleEventObserver>()
        verify { mockLifecycle.addObserver(capture(observerSlot)) }

        val capturedObserver = observerSlot.captured
        capturedObserver.onStateChanged(mockk(), Lifecycle.Event.ON_DESTROY)

        verify(exactly = 1) {
            mockAppLifecycleManager.unregisterCallback(openAdAdmobSetupWithExtensionFunc)
            mockLifecycle.removeObserver(any())
        }
    }

    @Test
    fun `registerAppOpenAd sets up callbacks correctly`() = runTest {
        val adUnitId = "test_ad_unit_id"
        val closeSplashScreen: () -> Unit = mockk(relaxed = true)

        with(openAdAdmobSetupWithExtensionFunc) {
            mockActivity.registerAppOpenAdOnColdStart(
                adUnitId, mockShowAdCallBack, closeSplashScreen
            )
        }

        verify {
            mockAppLifecycleManager.setShowOnColdStart(true)
            mockAppLifecycleManager.registerCallback(openAdAdmobSetupWithExtensionFunc)
            mockAppOpenAdManager.loadAd(eq(adUnitId), eq(mockActivity), any(), any())
        }
    }

    @Test
    fun `registerAppOpenAd calls closeSplashScreen when not first entry`() = runTest {
        val adUnitId = "test_ad_unit_id"
        val closeSplashScreen: () -> Unit = mockk(relaxed = true)

        every { mockAppLifecycleManager.isFirstEntry() } returns false

        with(openAdAdmobSetupWithExtensionFunc) {
            mockActivity.registerAppOpenAdOnColdStart(
                adUnitId, mockShowAdCallBack, closeSplashScreen
            )
        }

        verify {
            closeSplashScreen()
        }
    }

    @Test
    fun `registerAppOpenAd calls closeSplashScreen when ad fails to load`() = runTest {
        val adUnitId = "test_ad_unit_id"
        val closeSplashScreen: () -> Unit = mockk(relaxed = true)

        val onFailedToLoadSlot = slot<(LoadAdError) -> Unit>()

        every { mockAppLifecycleManager.isFirstEntry() } returns true

        every {
            mockAppOpenAdManager.loadAd(
                any(), any(), capture(onFailedToLoadSlot), any()
            )
        } answers {
            onFailedToLoadSlot.captured.invoke(LoadAdError(0, "", "", null, null))
        }

        with(openAdAdmobSetupWithExtensionFunc) {
            mockActivity.registerAppOpenAdOnColdStart(
                adUnitId, mockShowAdCallBack, closeSplashScreen
            )
        }

        verify(exactly = 1) {
            closeSplashScreen()
        }
    }
}
