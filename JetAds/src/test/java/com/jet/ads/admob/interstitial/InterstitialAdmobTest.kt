package com.jet.ads.admob.interstitial

import android.app.Activity
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.common.truth.Truth.assertThat
import com.jet.ads.common.callbacks.InterstitialShowAdCallbacks
import com.jet.ads.common.callbacks.ShowAdCallBack
import com.jet.ads.utils.pools.AdMobInterstitialPool
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class InterstitialAdmobTest {

    private lateinit var interstitialAdmob: InterstitialAdmob
    private lateinit var mockInterstitialAdManager: InterstitialAdManager
    private lateinit var mockAdMobInterstitialPool: AdMobInterstitialPool

    @Before
    fun setup() {
        mockInterstitialAdManager = mockk(relaxed = true)
        mockAdMobInterstitialPool = mockk(relaxed = true)
        interstitialAdmob = InterstitialAdmob(mockInterstitialAdManager, mockAdMobInterstitialPool)
    }

    @Test
    fun `loadedAds returns flow from AdMobInterstitialPool`() = runTest {
        val mockAds = listOf(
            "ad1" to (mockk<InterstitialAd>() to 1000L),
            "ad2" to (mockk<InterstitialAd>() to 2000L)
        )
        every { mockAdMobInterstitialPool.getAllAds() } returns flowOf(mockAds)

        val result = interstitialAdmob.loadedAds()

        assertThat(result).isInstanceOf(flowOf(mockAds)::class.java)
    }

    @Test
    fun `show calls interstitialAdManager showAd with callback`() {
        val mockActivity = mockk<Activity>()
        val mockCallback = mockk<ShowAdCallBack>(relaxed = true)
        val adUnitId = "test_ad_unit_id"

        interstitialAdmob.show(adUnitId, mockActivity, mockCallback)

        verify {
            mockInterstitialAdManager.showAd(
                adUnitId,
                mockActivity,
                any()
            )
        }
    }

    @Test
    fun `show calls interstitialAdManager showAd without callback`() {
        val mockActivity = mockk<Activity>()
        val adUnitId = "test_ad_unit_id"

        interstitialAdmob.show(adUnitId, mockActivity)

        verify {
            mockInterstitialAdManager.showAd(adUnitId, mockActivity)
        }
    }

    @Test
    fun `show with callback triggers all callback methods`() {
        val mockActivity = mockk<Activity>()
        val mockCallback = mockk<ShowAdCallBack>(relaxed = true)
        val adUnitId = "test_ad_unit_id"
        val callbackSlot = slot<InterstitialShowAdCallbacks>()

        interstitialAdmob.show(adUnitId, mockActivity, mockCallback)

        verify {
            mockInterstitialAdManager.showAd(
                adUnitId,
                mockActivity,
                capture(callbackSlot)
            )
        }

        callbackSlot.captured.apply {
            onAdClicked()
            onAdDismissed()
            onAdFailedToShow(mockk())
            onAdImpression()
            onAdShowed()
        }

        verify {
            mockCallback.onAdClicked()
            mockCallback.onAdDismissed()
            mockCallback.onAdFailedToShow()
            mockCallback.onAdImpression()
            mockCallback.onAdShowed()
        }
    }
}