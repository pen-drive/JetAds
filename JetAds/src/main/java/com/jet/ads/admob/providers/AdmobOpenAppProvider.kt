package com.jet.ads.admob.providers

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.jet.ads.common.callbacks.OpenAppShowAdCallbacks
import com.jet.ads.utils.AdProvider


class AdmobOpenAppProvider : AdProvider<AppOpenAd, OpenAppShowAdCallbacks> {
    override fun load(
        adUnitId: String,
        context: Context,
        onAdLoaded: (AppOpenAd) -> Unit,
        onAdFailedToLoad: (LoadAdError) -> Unit
    ) {
        AppOpenAd.load(context,
            adUnitId,
            AdRequest.Builder().build(),
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    onAdLoaded(ad)
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    onAdFailedToLoad(loadAdError)
                }
            })

    }


    override fun show(
        adUnitId: String,
        adPair: Pair<AppOpenAd?, Long>,
        activity: Activity,
        callbacks: OpenAppShowAdCallbacks?,
        onDismiss: () -> Unit
    ) {
        val openAd = adPair.first

        if (callbacks != null){

            openAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
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
        }

        openAd?.show(activity)
    }
}
