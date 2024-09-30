package com.jet.ads.admob.providers

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.jet.ads.common.callbacks.FullRewardedShowAdCallbacks
import com.jet.ads.common.callbacks.RewardedCallBack
import com.jet.ads.utils.AdProvider

/**
 *
 * To facilitate unit testing, a factory function was introduced to handle the creation and loading of RewardedAd. This approach became necessary because direct mocking of RewardedAd proved challenging.
 *
 * When attempting to mock RewardedAd, the method internally accessed the Android Looper, which isn't available in the standard unit test environment. While using Robolectric could have been a potential solution, it introduced another problem: the need for a meta-data tag in the AndroidManifest.xml, which isn't ideal for a simple unit test setup.
 *
 * By using a factory, we can inject a mock implementation during testing, allowing us to simulate the behavior of RewardedAd without interacting with the Android framework or Google Mobile Ads SDK directly.
 * */
class AdmobRewardedProvider(
    private val rewardedAdFactory: (Context, String, AdRequest, RewardedAdLoadCallback) -> Unit = { context, adUnitId, adRequest, callback ->
        RewardedAd.load(context, adUnitId, adRequest, callback)
    }
) : AdProvider<RewardedAd, RewardedCallBack> {
    override fun load(
        adUnitId: String,
        context: Context,
        onAdLoaded: (RewardedAd) -> Unit,
        onAdFailedToLoad: (LoadAdError) -> Unit
    ) {
        rewardedAdFactory.invoke(context,
            adUnitId,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    onAdLoaded(ad)
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    onAdFailedToLoad(error)
                }
            })
    }

    override fun show(
        adUnitId: String,
        adPair: Pair<RewardedAd?, Long>,
        activity: Activity,
        callbacks: RewardedCallBack?,
        onDismiss: () -> Unit
    ) {
        val reward = adPair.first


        if (callbacks is FullRewardedShowAdCallbacks) {
            reward?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdClicked() {
                    callbacks.onAdClicked()
                }

                override fun onAdDismissedFullScreenContent() {
                    onDismiss()
                    callbacks.onAdDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    callbacks.onAdFailedToShow(adError)
                }

                override fun onAdImpression() {
                    callbacks.onAdImpression()

                }

                override fun onAdShowedFullScreenContent() {
                    callbacks.onAdShowed()

                }
            }
        }else{
            reward?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    onDismiss()
                }
            }
        }

        reward?.show(activity) { rewardItem ->
            callbacks?.onRewarded(rewardItem)
        }

    }
}
