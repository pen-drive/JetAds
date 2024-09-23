package com.jet.ads.admob.providers

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.common.truth.Truth.assertThat
import com.jet.ads.common.callbacks.OpenAppShowAdCallbacks
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test

class AdmobOpenAppProviderTest {

    private lateinit var provider: AdmobOpenAppProvider
    private lateinit var mockContext: Context
    private lateinit var mockActivity: Activity
    private lateinit var mockAppOpenAd: AppOpenAd
    private lateinit var mockCallbacks: OpenAppShowAdCallbacks

    @Before
    fun setup() {
        provider = AdmobOpenAppProvider()
        mockContext = mockk(relaxed = true)
        mockActivity = mockk(relaxed = true)
        mockAppOpenAd = mockk(relaxed = true)
        mockCallbacks = mockk(relaxed = true)

        mockkStatic(AppOpenAd::class)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }


    @Test
    fun `load should call onAdLoaded when ad loads successfully`() {
        val adUnitId = "test_ad_unit_id"
        var loadedAd: AppOpenAd? = null

        every {
            AppOpenAd.load(any(), any(), any(), any())
        } answers {
            lastArg<AppOpenAd.AppOpenAdLoadCallback>().onAdLoaded(mockAppOpenAd)
        }

        provider.load(adUnitId, mockContext, { ad ->
            loadedAd = ad
        }, { })

        assertThat(loadedAd).isEqualTo(mockAppOpenAd)
        verify { AppOpenAd.load(mockContext, adUnitId, any<AdRequest>(), any()) }
    }

    @Test
    fun `load should call onAdFailedToLoad when ad fails to load`() {
        val adUnitId = "test_ad_unit_id"
        val mockError = mockk<LoadAdError>(relaxed = true)
        var loadError: LoadAdError? = null

        every {
            AppOpenAd.load(any(), any(), any(), any())
        } answers {
            lastArg<AppOpenAd.AppOpenAdLoadCallback>().onAdFailedToLoad(mockError)
        }

        provider.load(adUnitId, mockContext, { }, { error ->
            loadError = error
        })

        assertThat(loadError).isEqualTo(mockError)
        verify { AppOpenAd.load(mockContext, adUnitId, any<AdRequest>(), any()) }
    }

    @Test
    fun `show should set FullScreenContentCallback and call show on AppOpenAd`() {
        val adUnitId = "test_ad_unit_id"
        val adPair = Pair(mockAppOpenAd, 0L)
        var dismissCalled = false

        provider.show(adUnitId, adPair, mockActivity, mockCallbacks) {
            dismissCalled = true
        }

        verify { mockAppOpenAd.fullScreenContentCallback = any() }
        verify { mockAppOpenAd.show(mockActivity) }

        assertThat(dismissCalled).isFalse()
    }

    @Test
    fun `show should handle all callback events correctly`() {
        val adUnitId = "test_ad_unit_id"
        val adPair = Pair(mockAppOpenAd, 0L)
        var dismissCalled = false

        provider.show(adUnitId, adPair, mockActivity, mockCallbacks) {
            dismissCalled = true
        }

        val callbackSlot = slot<FullScreenContentCallback>()
        verify { mockAppOpenAd.fullScreenContentCallback = capture(callbackSlot) }

        // Test onAdClicked
        callbackSlot.captured.onAdClicked()
        verify { mockCallbacks.onAdClicked() }

        // Test onAdShowedFullScreenContent
        callbackSlot.captured.onAdShowedFullScreenContent()
        verify { mockCallbacks.onAdShowed() }

        // Test onAdFailedToShowFullScreenContent
        val mockAdError = mockk<AdError>(relaxed = true)
        callbackSlot.captured.onAdFailedToShowFullScreenContent(mockAdError)
        verify { mockCallbacks.onAdFailedToShow(mockAdError) }

        // Test onAdImpression
        callbackSlot.captured.onAdImpression()
        verify { mockCallbacks.onAdImpression() }

        // Test onAdDismissedFullScreenContent
        callbackSlot.captured.onAdDismissedFullScreenContent()
        verify { mockCallbacks.onAdDismissed() }
        assertThat(dismissCalled).isTrue()
    }

    @Test
    fun `show should not set FullScreenContentCallback when callbacks are null`() {
        val adUnitId = "test_ad_unit_id"
        val adPair = Pair(mockAppOpenAd, 0L)

        provider.show(adUnitId, adPair, mockActivity, null) { }

        verify(exactly = 0) { mockAppOpenAd.fullScreenContentCallback = any() }
        verify { mockAppOpenAd.show(mockActivity) }
    }

    @Test
    fun `show should handle null AppOpenAd gracefully`() {
        val adUnitId = "test_ad_unit_id"
        val adPair = Pair<AppOpenAd?, Long>(null, 0L)
        var dismissCalled = false

        provider.show(adUnitId, adPair, mockActivity, mockCallbacks) {
            dismissCalled = true
        }

        verify(exactly = 0) { mockAppOpenAd.fullScreenContentCallback = any() }
        verify(exactly = 0) { mockAppOpenAd.show(any()) }
        assertThat(dismissCalled).isFalse()
    }
}