package com.jet.ads.admob.rewarded

import android.app.Activity
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.common.truth.Truth.assertThat
import com.jet.ads.common.callbacks.FullRewardedShowAdCallbacks
import com.jet.ads.common.callbacks.OnlyOnRewardedCallback
import com.jet.ads.common.callbacks.ShowAdCallBack
import com.jet.ads.utils.pools.AdMobRewardedPool
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class RewardedAdmobTest {

    private lateinit var rewardedAdmob: RewardedAdmob
    private lateinit var mockRewardedAdManager: RewardedAdManager
    private lateinit var mockAdMobRewardedPool: AdMobRewardedPool

    @Before
    fun setup() {
        mockRewardedAdManager = mockk(relaxed = true)
        mockAdMobRewardedPool = mockk(relaxed = true)
        rewardedAdmob = RewardedAdmob(mockRewardedAdManager, mockAdMobRewardedPool)
    }

    @Test
    fun `loadedAds returns flow from AdMobRewardedPool`() = runBlocking {
        val mockAds = listOf(
            "ad1" to (mockk<RewardedAd>() to 1000L),
            "ad2" to (mockk<RewardedAd>() to 2000L)
        )
        every { mockAdMobRewardedPool.getAllAds() } returns flowOf(mockAds)

        val result = rewardedAdmob.loadedAds().first()

        assertThat(result).isEqualTo(mockAds)
    }

    @Test
    fun `show calls rewardedAdManager showAd with full callbacks when callback is provided`() {
        val mockActivity = mockk<Activity>()
        val mockCallback = mockk<ShowAdCallBack>(relaxed = true)
        val adUnitId = "test_ad_unit_id"
        val mockRewardItem = mockk<RewardItem>()
        val onRewarded: (RewardItem) -> Unit = mockk(relaxed = true)

        rewardedAdmob.show(adUnitId, mockActivity, mockCallback, onRewarded)

        verify {
            mockRewardedAdManager.showAd(
                adUnitId,
                mockActivity,
                callbacks = any<FullRewardedShowAdCallbacks>()
            )
        }
    }

    @Test
    fun `show calls rewardedAdManager showAd with only onRewarded callback when no callback is provided`() {
        val mockActivity = mockk<Activity>()
        val adUnitId = "test_ad_unit_id"
        val onRewarded: (RewardItem) -> Unit = mockk(relaxed = true)

        rewardedAdmob.show(adUnitId, mockActivity, null, onRewarded)

        verify {
            mockRewardedAdManager.showAd(
                adUnitId,
                mockActivity,
                callbacks = any<OnlyOnRewardedCallback>()
            )
        }
    }

    @Test
    fun `show with full callback triggers all callback methods`() {
        val mockActivity = mockk<Activity>()
        val mockCallback = mockk<ShowAdCallBack>(relaxed = true)
        val adUnitId = "test_ad_unit_id"
        val mockRewardItem = mockk<RewardItem>()
        val onRewarded: (RewardItem) -> Unit = mockk(relaxed = true)
        val callbackSlot = slot<FullRewardedShowAdCallbacks>()

        rewardedAdmob.show(adUnitId, mockActivity, mockCallback, onRewarded)

        verify {
            mockRewardedAdManager.showAd(
                adUnitId,
                mockActivity,
                callbacks = capture(callbackSlot)
            )
        }

        callbackSlot.captured.apply {
            onAdClicked()
            onAdDismissed()
            onAdFailedToShow(mockk())
            onAdImpression()
            onAdShowed()
            onRewarded(mockRewardItem)
        }

        verify {
            mockCallback.onAdClicked()
            mockCallback.onAdDismissed()
            mockCallback.onAdFailedToShow()
            mockCallback.onAdImpression()
            mockCallback.onAdShowed()
            onRewarded(mockRewardItem)
        }
    }

    @Test
    fun `show without callback only triggers onRewarded`() {
        val mockActivity = mockk<Activity>()
        val adUnitId = "test_ad_unit_id"
        val mockRewardItem = mockk<RewardItem>()
        val onRewarded: (RewardItem) -> Unit = mockk(relaxed = true)
        val callbackSlot = slot<OnlyOnRewardedCallback>()

        rewardedAdmob.show(adUnitId, mockActivity, null, onRewarded)

        verify {
            mockRewardedAdManager.showAd(
                adUnitId,
                mockActivity,
                callbacks = capture(callbackSlot)
            )
        }

        callbackSlot.captured.onRewarded(mockRewardItem)

        verify {
            onRewarded(mockRewardItem)
        }
    }
}