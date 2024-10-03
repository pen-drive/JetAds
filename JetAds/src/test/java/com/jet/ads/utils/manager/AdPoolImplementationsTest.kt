package com.jet.ads.utils.manager

import android.os.SystemClock
import app.cash.turbine.test
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.common.base.Verify.verify
import com.google.common.truth.Truth.assertThat
import com.jet.ads.utils.pools.AdMobInterstitialPool
import com.jet.ads.utils.pools.AdMobRewardedPool
import com.jet.ads.utils.pools.AppOpenAdmobPool
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test


class AdPoolImplementationsTest {

    @Before
    fun setup() {
        mockkStatic(SystemClock::class)
        every { SystemClock.elapsedRealtime() } returns 1000L
    }

    @After
    fun tearDown() {
        unmockkStatic(SystemClock::class)
    }

    @Test
    fun `AdMobInterstitialPool clears callback correctly`() {
        val mockAd = mockk<InterstitialAd> {
            every { fullScreenContentCallback = any() } just Runs
        }
        AdMobInterstitialPool.clearCallback(mockAd)
        verify { mockAd.fullScreenContentCallback = null }
    }

    @Test
    fun `AdMobRewardedPool clears callback correctly`() {
        val mockAd = mockk<RewardedAd> {
            every { fullScreenContentCallback = any() } just Runs
        }
        AdMobRewardedPool.clearCallback(mockAd)
        verify { mockAd.fullScreenContentCallback = null }
    }

    @Test
    fun `AppOpenAdmobPool clears callback correctly`() {
        val mockAd = mockk<AppOpenAd> {
            every { fullScreenContentCallback = any() } just Runs
        }
        AppOpenAdmobPool.clearCallback(mockAd)
        verify { mockAd.fullScreenContentCallback = null }
    }

    @Test
    fun `AdMobInterstitialPool saves and retrieves ad correctly`() = runTest {
        val mockAd = mockk<InterstitialAd>(relaxed = true)
        AdMobInterstitialPool.saveAd("test_id", mockAd)

        val retrievedAd = AdMobInterstitialPool.getAd("test_id")
        assertThat(retrievedAd?.first).isEqualTo(mockAd)
        assertThat(retrievedAd?.second).isEqualTo(1000L)

        val allAds = AdMobInterstitialPool.getAllAds().first()
        assertThat(allAds).hasSize(1)
        assertThat(allAds[0].first).isEqualTo("test_id")
        assertThat(allAds[0].second.first).isEqualTo(mockAd)
        assertThat(allAds[0].second.second).isEqualTo(1000L)
    }

    @Test
    fun `AdMobRewardedPool saves and retrieves ad correctly`() = runTest {
        val mockAd = mockk<RewardedAd>(relaxed = true)
        AdMobRewardedPool.saveAd("test_id", mockAd)

        val retrievedAd = AdMobRewardedPool.getAd("test_id")
        assertThat(retrievedAd?.first).isEqualTo(mockAd)
        assertThat(retrievedAd?.second).isEqualTo(1000L)

        val allAds = AdMobRewardedPool.getAllAds().first()
        assertThat(allAds).hasSize(1)
        assertThat(allAds[0].first).isEqualTo("test_id")
        assertThat(allAds[0].second.first).isEqualTo(mockAd)
        assertThat(allAds[0].second.second).isEqualTo(1000L)
    }

    @Test
    fun `AppOpenAdmobPool saves and retrieves ad correctly`() = runTest {
        val mockAd = mockk<AppOpenAd>(relaxed = true)
        AppOpenAdmobPool.saveAd("test_id", mockAd)

        val retrievedAd = AppOpenAdmobPool.getAd("test_id")
        assertThat(retrievedAd?.first).isEqualTo(mockAd)
        assertThat(retrievedAd?.second).isEqualTo(1000L)

        val allAds = AppOpenAdmobPool.getAllAds().first()
        assertThat(allAds).hasSize(1)
        assertThat(allAds[0].first).isEqualTo("test_id")
        assertThat(allAds[0].second.first).isEqualTo(mockAd)
        assertThat(allAds[0].second.second).isEqualTo(1000L)
    }

    @Test
    fun `AdMobInterstitialPool deletes ad correctly`() = runTest {
        val mockAd = mockk<InterstitialAd>(relaxed = true)
        AdMobInterstitialPool.saveAd("test_id", mockAd)
        AdMobInterstitialPool.deleteAdFromPool("test_id", mockAd)

        val allAds = AdMobInterstitialPool.getAllAds().first()
        assertThat(allAds).isEmpty()
    }

    @Test
    fun `AdMobRewardedPool deletes ad correctly`() = runTest {
        val mockAd = mockk<RewardedAd>(relaxed = true)
        AdMobRewardedPool.saveAd("test_id", mockAd)
        AdMobRewardedPool.deleteAdFromPool("test_id", mockAd)

        val allAds = AdMobRewardedPool.getAllAds().first()
        assertThat(allAds).isEmpty()
    }

    @Test
    fun `AppOpenAdmobPool deletes ad correctly`() = runTest {
        val mockAd = mockk<AppOpenAd>(relaxed = true)
        AppOpenAdmobPool.saveAd("test_id", mockAd)
        AppOpenAdmobPool.deleteAdFromPool("test_id", mockAd)

        val allAds = AppOpenAdmobPool.getAllAds().first()
        assertThat(allAds).isEmpty()
    }

    @Test
    fun `AdMobInterstitialPool clears pool correctly`() = runTest {
        val mockAd1 = mockk<InterstitialAd>(relaxed = true)
        val mockAd2 = mockk<InterstitialAd>(relaxed = true)
        AdMobInterstitialPool.saveAd("test_id1", mockAd1)
        AdMobInterstitialPool.saveAd("test_id2", mockAd2)

        AdMobInterstitialPool.clearPool()

        val allAds = AdMobInterstitialPool.getAllAds().first()
        assertThat(allAds).isEmpty()
    }



    @Test
    fun `AdMobInterstitialPool emits updated ads after operations`() = runTest {
        val mockAd1 = mockk<InterstitialAd>(relaxed = true)
        val mockAd2 = mockk<InterstitialAd>(relaxed = true)


        AdMobInterstitialPool.getAllAds().test {

            assertThat(awaitItem()).isEmpty()


            AdMobInterstitialPool.saveAd("test_id1", mockAd1)
            assertThat(awaitItem()).hasSize(1)

            AdMobInterstitialPool.saveAd("test_id2", mockAd2)
            assertThat(awaitItem()).hasSize(2)

            AdMobInterstitialPool.deleteAdFromPool("test_id1", mockAd1)
            assertThat(awaitItem()).hasSize(1)

            AdMobInterstitialPool.clearPool()
            assertThat(awaitItem()).isEmpty()


            cancelAndConsumeRemainingEvents()
        }
    }


    @Test
    fun `AdMobRewardedPool emits updated ads after operations`() = runTest {
        val mockAd1 = mockk<RewardedAd>(relaxed = true)
        val mockAd2 = mockk<RewardedAd>(relaxed = true)


        AdMobRewardedPool.getAllAds().test {

            assertThat(awaitItem()).isEmpty()


            AdMobRewardedPool.saveAd("test_id1", mockAd1)
            assertThat(awaitItem()).hasSize(1) // After saving the first ad

            AdMobRewardedPool.saveAd("test_id2", mockAd2)
            assertThat(awaitItem()).hasSize(2) // After saving the second ad

            AdMobRewardedPool.deleteAdFromPool("test_id1", mockAd1)
            assertThat(awaitItem()).hasSize(1) // After deleting the first ad

            AdMobRewardedPool.clearPool()
            assertThat(awaitItem()).isEmpty()


            cancelAndConsumeRemainingEvents()
        }
    }

}