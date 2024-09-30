package com.jet.ads.admob.providers

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.common.truth.Truth.assertThat
import com.jet.ads.common.callbacks.FullRewardedShowAdCallbacks
import com.jet.ads.common.callbacks.OnlyOnRewardedCallback
import io.mockk.*
import org.junit.Before
import org.junit.Test

class AdmobRewardedProviderTest {

    private lateinit var mockContext: Context
    private lateinit var mockActivity: Activity
    private lateinit var mockRewardedAd: RewardedAd
    private lateinit var mockRewardItem: RewardItem
    private lateinit var mockRewardedAdFactory: (Context, String, AdRequest, RewardedAdLoadCallback) -> Unit
    private lateinit var provider: AdmobRewardedProvider

    @Before
    fun setup() {
        mockContext = mockk(relaxed = true)
        mockActivity = mockk(relaxed = true)
        mockRewardedAd = mockk(relaxed = true)
        mockRewardItem = mockk(relaxed = true)
        mockRewardedAdFactory = mockk()

        provider = AdmobRewardedProvider(mockRewardedAdFactory)
    }

    @Test
    fun `load should call onAdLoaded when ad loads successfully`() {
        val adUnitId = "test_ad_unit_id"
        var loadedAd: RewardedAd? = null

        every {
            mockRewardedAdFactory.invoke(any(), any(), any(), any())
        } answers {
            lastArg<RewardedAdLoadCallback>().onAdLoaded(mockRewardedAd)
        }

        provider.load(adUnitId, mockContext, { ad ->
            loadedAd = ad
        }, { })

        assertThat(loadedAd).isEqualTo(mockRewardedAd)
        verify { mockRewardedAdFactory.invoke(mockContext, adUnitId, any(), any()) }
    }

    @Test
    fun `load should call onAdFailedToLoad when ad fails to load`() {
        val adUnitId = "test_ad_unit_id"
        val mockError = mockk<LoadAdError>(relaxed = true)
        var loadError: LoadAdError? = null

        every {
            mockRewardedAdFactory.invoke(any(), any(), any(), any())
        } answers {
            lastArg<RewardedAdLoadCallback>().onAdFailedToLoad(mockError)
        }

        provider.load(adUnitId, mockContext, { }, { error ->
            loadError = error
        })

        assertThat(loadError).isEqualTo(mockError)
        verify { mockRewardedAdFactory.invoke(mockContext, adUnitId, any(), any()) }
    }

    @Test
    fun `show should handle OnlyOnRewardedCallback correctly`() {
        val adUnitId = "test_ad_unit_id"
        val adPair = Pair(mockRewardedAd, 0L)
        val mockCallback = mockk<OnlyOnRewardedCallback>(relaxed = true)

        val listenerSlot = slot<OnUserEarnedRewardListener>()
        every {
            mockRewardedAd.show(any(), capture(listenerSlot))
        } answers {
            listenerSlot.captured.onUserEarnedReward(mockRewardItem)
        }

        provider.show(adUnitId, adPair, mockActivity, mockCallback) { }

        verify { mockRewardedAd.show(mockActivity, any()) }
        verify { mockCallback.onRewarded(mockRewardItem) }
    }

    @Test
    fun `show should handle FullRewardedShowAdCallbacks correctly`() {
        val adUnitId = "test_ad_unit_id"
        val adPair = Pair(mockRewardedAd, 0L)
        val mockCallback = mockk<FullRewardedShowAdCallbacks>(relaxed = true)
        var dismissCalled = false

        val listenerSlot = slot<OnUserEarnedRewardListener>()
        every {
            mockRewardedAd.show(any(), capture(listenerSlot))
        } answers {
            listenerSlot.captured.onUserEarnedReward(mockRewardItem)
        }

        provider.show(adUnitId, adPair, mockActivity, mockCallback) {
            dismissCalled = true
        }

        verify { mockRewardedAd.show(mockActivity, any()) }
        verify { mockCallback.onRewarded(mockRewardItem) }

        val callbackSlot = slot<FullScreenContentCallback>()
        verify { mockRewardedAd.fullScreenContentCallback = capture(callbackSlot) }

        // Test all callbacks
        callbackSlot.captured.onAdClicked()
        verify { mockCallback.onAdClicked() }

        callbackSlot.captured.onAdDismissedFullScreenContent()
        verify { mockCallback.onAdDismissed() }
        assertThat(dismissCalled).isTrue()

        val mockAdError = mockk<AdError>(relaxed = true)
        callbackSlot.captured.onAdFailedToShowFullScreenContent(mockAdError)
        verify { mockCallback.onAdFailedToShow(mockAdError) }

        callbackSlot.captured.onAdImpression()
        verify { mockCallback.onAdImpression() }

        callbackSlot.captured.onAdShowedFullScreenContent()
        verify { mockCallback.onAdShowed() }
    }


    @Test
    fun `show should handle Dismiss callback correctly when callback are null`() {
        val adUnitId = "test_ad_unit_id"
        val adPair = Pair(mockRewardedAd, 0L)
        var dismissCalled = false

        provider.show(adUnitId, adPair, mockActivity, null) {
            dismissCalled = true
        }

        val callbackSlot = slot<FullScreenContentCallback>()
        verify { mockRewardedAd.fullScreenContentCallback = capture(callbackSlot) }


        callbackSlot.captured.onAdDismissedFullScreenContent()



        assertThat(dismissCalled).isTrue()
    }


    @Test
    fun `show should handle null RewardedAd gracefully`() {
        val adUnitId = "test_ad_unit_id"
        val adPair = Pair<RewardedAd?, Long>(null, 0L)
        val mockCallback = mockk<FullRewardedShowAdCallbacks>(relaxed = true)
        var dismissCalled = false

        provider.show(adUnitId, adPair, mockActivity, mockCallback) {
            dismissCalled = true
        }

        verify(exactly = 0) { mockRewardedAd.show(any(), any()) }
        verify(exactly = 0) { mockCallback.onRewarded(any()) }
        assertThat(dismissCalled).isFalse()
    }
}