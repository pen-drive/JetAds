package com.jet.ads.admob

import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.testing.TestLifecycleOwner
import app.cash.turbine.test
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.initialization.AdapterStatus
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.common.truth.Truth.assertThat
import com.jet.ads.common.controller.AdsControl
import com.jet.ads.common.controller.ControlProvider
import com.jet.ads.utils.pools.AdPool
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class AdmobInitializerExtensionFunctionTest {

    private lateinit var adMobRewardedPool: AdPool<RewardedAd>
    private lateinit var adMobInterstitialPool: AdPool<InterstitialAd>
    private lateinit var adMobAppOpenPool: AdPool<AppOpenAd>
    private lateinit var controlProvider: ControlProvider
    private lateinit var adsControl: AdsControl
    private lateinit var activity: ComponentActivity
    private lateinit var admobInitializer: AdmobInitializer
    private lateinit var testLifecycleOwner: TestLifecycleOwner


    @Before
    fun setup() {
        adMobRewardedPool = mockk(relaxed = true)
        adMobInterstitialPool = mockk(relaxed = true)
        adMobAppOpenPool = mockk(relaxed = true)
        controlProvider = mockk(relaxed = true)
        adsControl = mockk(relaxed = true)
        activity = mockk(relaxed = true)

        testLifecycleOwner = TestLifecycleOwner()





        admobInitializer = AdmobInitializer(
            controlLocator = controlProvider,
            adMobRewardedPool = adMobRewardedPool,
            adMobInterstitialPool = adMobInterstitialPool,
            adMobAppOpenPool = adMobAppOpenPool
        )
    }

    @Test
    fun `initializeAds extension function should set custom ad control when ads are enabled`() = runTest {
        every { adsControl.areAdsEnabled() } returns MutableStateFlow(true)


        with(admobInitializer) {
            activity.initializeAds(adsControl)
        }

        verify { controlProvider.setAdControl(any()) }
    }

    @Test
    fun `initializeAds extension function should return true immediately when ads are disabled`() = runTest {
        every { adsControl.areAdsEnabled() } returns MutableStateFlow(false)

        val result = with(admobInitializer) {
            activity.initializeAds(adsControl)
        }

        result.test {
            assertThat(awaitItem()).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `initializeAds extension function should initialize MobileAds when ads are enabled`() = runTest {
        every { adsControl.areAdsEnabled() } returns MutableStateFlow(true)
        every { activity.lifecycle } returns testLifecycleOwner.lifecycle
        mockkStatic(MobileAds::class)

        every {
            MobileAds.initialize(any(), any())
        } answers {
            val listener = secondArg<OnInitializationCompleteListener>()

            val statusMock = mockk<InitializationStatus> {
                every { adapterStatusMap } returns mapOf("TestAdapter" to mockk {
                    every { initializationState } returns AdapterStatus.State.READY
                })
            }

            listener.onInitializationComplete(statusMock)
        }

        val result = with(admobInitializer) {
            activity.initializeAds(adsControl)
        }

        result.test {
            assertThat(awaitItem()).isFalse()
            assertThat(awaitItem()).isTrue()
            cancelAndIgnoreRemainingEvents()
        }

        verify { controlProvider.setAdControl(adsControl) }
        verify { MobileAds.initialize(any(), any()) }
    }

    @Test
    fun `initializeAds extension function shouldn't initialize MobileAds when ads are disabled`() = runTest {
        every { adsControl.areAdsEnabled() } returns MutableStateFlow(false)


        mockkStatic(MobileAds::class)

        with(admobInitializer) {
            activity.initializeAds(adsControl)
        }

        verify(exactly = 0) { MobileAds.initialize(any(), any()) }
    }

    @Test
    fun `onStateChanged should clear ad pools and remove observer on ON_DESTROY`() = runTest {
        every { adsControl.areAdsEnabled() } returns MutableStateFlow(true)

        with(admobInitializer) {
            activity.initializeAds(adsControl)
        }

        admobInitializer.onStateChanged(activity, Lifecycle.Event.ON_DESTROY)

        verify { adMobRewardedPool.clearPool() }
        verify { adMobInterstitialPool.clearPool() }
        verify { adMobAppOpenPool.clearPool() }


        verify { activity.lifecycle.removeObserver(any()) }
    }
}
