package com.jet.ads.admob

import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
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
import com.jet.ads.common.controller.JetAdsAdsControlImpl
import com.jet.ads.common.initializers.AdsInitializeFactory
import com.jet.ads.common.initializers.AdsInitializer
import com.jet.ads.di.JetAdsLib
import com.jet.ads.utils.pools.AdPool
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test


class AdmobInitializerTest {

    private lateinit var adMobRewardedPool: AdPool<RewardedAd>
    private lateinit var adMobInterstitialPool: AdPool<InterstitialAd>
    private lateinit var adMobAppOpenPool: AdPool<AppOpenAd>
    private lateinit var jetAdsLib: JetAdsLib
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
        jetAdsLib = mockk(relaxed = true)
        controlProvider = mockk(relaxed = true)
        adsControl = mockk(relaxed = true)
        activity = mockk(relaxed = true)
        testLifecycleOwner = TestLifecycleOwner()

        admobInitializer = AdmobInitializer(
            jetAdsLib,
            controlLocator = controlProvider,
            adMobRewardedPool = adMobRewardedPool,
            adMobInterstitialPool = adMobInterstitialPool,
            adMobAppOpenPool = adMobAppOpenPool
        )
    }


    @Test
    fun `initializeAds should set custom ad control immediately in initialization even ads are enable`() =
        runTest {
            every { adsControl.areAdsEnabled() } returns MutableStateFlow(true)

            admobInitializer.initializeAds(activity, this, adsControl)

            verify { controlProvider.setAdControl(any()) }
        }

    @Test
    fun `initializeAds should set custom ad control immediately in initialization even ads are disabled`() =
        runTest {
            every { adsControl.areAdsEnabled() } returns MutableStateFlow(false)

            val result = admobInitializer.initializeAds(activity, this, adsControl)

            verify { adsControl.areAdsEnabled() }
            verify { controlProvider.setAdControl(any()) }
            assertThat(result.first()).isTrue()
        }


    @Test
    fun `initializeAds should return true immediately when ads are disabled`() = runTest {
        every { adsControl.areAdsEnabled() } returns MutableStateFlow(false)

        val result = admobInitializer.initializeAds(activity, this, adsControl)

        verify { adsControl.areAdsEnabled() }
        assertThat(result.first()).isTrue()
    }

    @Test
    fun `initializeAds should initialize MobileAds when ads are enabled`() = runTest {

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


        val result = admobInitializer.initializeAds(activity, this, adsControl)


        result.test {
            assertThat(awaitItem()).isFalse()
            assertThat(awaitItem()).isTrue()
            cancelAndIgnoreRemainingEvents()
        }


        verify { controlProvider.setAdControl(adsControl) }
        verify { MobileAds.initialize(any(), any()) }
    }


    @Test
    fun `initializeAds shouldn't initialize MobileAds when ads are disabled`() = runTest {
        every { adsControl.areAdsEnabled() } returns MutableStateFlow(false)
        every { activity.lifecycle } returns testLifecycleOwner.lifecycle

        mockkStatic(MobileAds::class)
        admobInitializer.initializeAds(activity, this, adsControl)


        verify(exactly = 0) { MobileAds.initialize(any(), any()) }
    }


    @Test
    fun `onStateChanged should clear pools and remove observer on ON_DESTROY`() = runTest {

        every { adsControl.areAdsEnabled() } returns MutableStateFlow(true)
        admobInitializer.initializeAds(activity, this, adsControl)
        admobInitializer.onStateChanged(testLifecycleOwner, Lifecycle.Event.ON_DESTROY)



        verify { adMobRewardedPool.clearPool() }
        verify { adMobInterstitialPool.clearPool() }
        verify { adMobAppOpenPool.clearPool() }
    }


    @Test
    fun `onStateChanged should clear lifecycle observer on ON_DESTROY`() = runTest {

        every { adsControl.areAdsEnabled() } returns MutableStateFlow(true)
        admobInitializer.initializeAds(activity, this, adsControl)
        admobInitializer.onStateChanged(testLifecycleOwner, Lifecycle.Event.ON_DESTROY)

        verify { activity.lifecycle.removeObserver(any()) }
    }
}