package com.jet.ads.admob.providers

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.common.truth.Truth.assertThat
import com.jet.ads.common.callbacks.InterstitialShowAdCallbacks
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test


class AdmobInterstitialProviderTest {

    private lateinit var provider: AdmobInterstitialProvider
    private lateinit var mockContext: Context
    private lateinit var mockActivity: Activity
    private lateinit var mockInterstitialAd: InterstitialAd
    private lateinit var mockCallbacks: InterstitialShowAdCallbacks

    @Before
    fun setup() {
        provider = AdmobInterstitialProvider()
        mockContext = mockk(relaxed = true)
        mockActivity = mockk(relaxed = true)
        mockInterstitialAd = mockk(relaxed = true)
        mockCallbacks = mockk(relaxed = true)

        mockkStatic(InterstitialAd::class)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `load should call onAdLoaded when ad loads successfully`() {
        val adUnitId = "test_ad_unit_id"
        var loadedAd: InterstitialAd? = null

        every {
            InterstitialAd.load(any(), any(), any(), any())
        } answers {
            lastArg<InterstitialAdLoadCallback>().onAdLoaded(mockInterstitialAd)
        }

        provider.load(adUnitId, mockContext, { ad ->
            loadedAd = ad
        }, { })

        assertEquals(mockInterstitialAd, loadedAd)
        verify { InterstitialAd.load(mockContext, adUnitId, any<AdRequest>(), any()) }
    }

    @Test
    fun `load should call onAdFailedToLoad when ad fails to load`() {
        val adUnitId = "test_ad_unit_id"
        val mockError = mockk<LoadAdError>(relaxed = true)
        var loadError: LoadAdError? = null

        every {
            InterstitialAd.load(any(), any(), any(), any())
        } answers {
            lastArg<InterstitialAdLoadCallback>().onAdFailedToLoad(mockError)
        }

        provider.load(adUnitId, mockContext, { }, { error ->
            loadError = error
        })

        assertEquals(mockError, loadError)
        verify { InterstitialAd.load(mockContext, adUnitId, any<AdRequest>(), any()) }
    }


    @Test
    fun `show should handle ad click event`() {
        val adUnitId = "test_ad_unit_id"
        val mockCallbacks = mockk<InterstitialShowAdCallbacks>(relaxed = true)
        val adPair = Pair(mockInterstitialAd, 0L)

        provider.show(adUnitId, adPair, mockActivity, mockCallbacks) { }

        val callbackSlot = slot<FullScreenContentCallback>()
        verify { mockInterstitialAd.fullScreenContentCallback = capture(callbackSlot) }

        callbackSlot.captured.onAdClicked()

        verify { mockCallbacks.onAdClicked() }
    }


    @Test
    fun `show should set FullScreenContentCallback and call show on InterstitialAd`() {
        val adUnitId = "test_ad_unit_id"
        val adPair = Pair(mockInterstitialAd, 0L)
        var dismissCalled = false

        provider.show(adUnitId, adPair, mockActivity, mockCallbacks) {
            dismissCalled = true
        }

        verify { mockInterstitialAd.fullScreenContentCallback = any() }
        verify { mockInterstitialAd.show(mockActivity) }

        assertThat(dismissCalled).isFalse()
    }


    @Test
    fun `show should handle Dismiss callback correctly when callback are null`() {
        val adUnitId = "test_ad_unit_id"
        val adPair = Pair(mockInterstitialAd, 0L)
        var dismissCalled = false

        provider.show(adUnitId, adPair, mockActivity, null) {
            dismissCalled = true
        }

        val callbackSlot = slot<FullScreenContentCallback>()
        verify { mockInterstitialAd.fullScreenContentCallback = capture(callbackSlot) }


        callbackSlot.captured.onAdDismissedFullScreenContent()



        assertThat(dismissCalled).isTrue()
    }


    @Test
    fun `show should handle all callback events correctly`() {
        val adUnitId = "test_ad_unit_id"
        val adPair = Pair(mockInterstitialAd, 0L)
        var dismissCalled = false

        provider.show(adUnitId, adPair, mockActivity, mockCallbacks) {
            dismissCalled = true
        }

        val callbackSlot = slot<FullScreenContentCallback>()
        verify { mockInterstitialAd.fullScreenContentCallback = capture(callbackSlot) }


        callbackSlot.captured.onAdClicked()
        verify { mockCallbacks.onAdClicked() }


        callbackSlot.captured.onAdShowedFullScreenContent()
        verify { mockCallbacks.onAdShowed() }


        val mockAdError = mockk<AdError>(relaxed = true)
        callbackSlot.captured.onAdFailedToShowFullScreenContent(mockAdError)
        verify { mockCallbacks.onAdFailedToShow(mockAdError) }


        callbackSlot.captured.onAdImpression()
        verify { mockCallbacks.onAdImpression() }


        callbackSlot.captured.onAdDismissedFullScreenContent()
        verify { mockCallbacks.onAdDismissed() }
        assertThat(dismissCalled).isTrue()
    }



    @Test
    fun `show should handle null InterstitialAd gracefully`() {
        val adUnitId = "test_ad_unit_id"
        val adPair = Pair<InterstitialAd?, Long>(null, 0L)
        var dismissCalled = false

        provider.show(adUnitId, adPair, mockActivity, mockCallbacks) {
            dismissCalled = true
        }

        verify(exactly = 0) { mockInterstitialAd.fullScreenContentCallback = any() }
        verify(exactly = 0) { mockInterstitialAd.show(any()) }
        assertThat(dismissCalled).isFalse()
    }
}