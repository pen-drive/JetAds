package com.jet.ads.admob.providers

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.jet.ads.utils.AdProvider
import com.jet.ads.common.callbacks.InterstitialShowAdCallbacks

class AdmobInterstitialProvider : AdProvider<InterstitialAd, InterstitialShowAdCallbacks> {
    override fun load(
        adUnitId: String,
        context: Context,
        onAdLoaded: (InterstitialAd) -> Unit,
        onAdFailedToLoad: (LoadAdError) -> Unit
    ) {

        InterstitialAd.load(
            context,
            adUnitId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    onAdLoaded(ad)
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    onAdFailedToLoad(error)
                }
            })
    }

    override fun show(
        adUnitId: String,
        adPair: Pair<InterstitialAd?, Long>,
        activity: Activity,
        callbacks: InterstitialShowAdCallbacks?,
        onDismiss: () -> Unit
    ) {
        val inter = adPair.first



        inter?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                super.onAdClicked()
                callbacks?.onAdClicked()
            }

            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()

                callbacks?.onAdShowed()
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                super.onAdFailedToShowFullScreenContent(p0)


                callbacks?.onAdFailedToShow(p0)
            }

            override fun onAdImpression() {
                super.onAdImpression()
                callbacks?.onAdImpression()
            }

            override fun onAdDismissedFullScreenContent() {
                onDismiss()
                callbacks?.onAdDismissed()
            }

        }


        inter?.show(activity)
    }
}
