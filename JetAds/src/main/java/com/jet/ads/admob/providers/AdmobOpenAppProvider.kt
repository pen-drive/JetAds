package com.jet.ads.admob.providers

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.jet.ads.common.callbacks.OpenAppShowAdCallbacks
import com.jet.ads.logging.ILogger
import com.jet.ads.utils.AdProvider


class AdmobOpenAppProvider(
    private val logger: ILogger = com.jet.ads.logging.Logger,
) : AdProvider<AppOpenAd, OpenAppShowAdCallbacks> {
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
                    logger.adLoaded(adUnitId)
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    onAdFailedToLoad(loadAdError)
                    logger.adFailedToLoad(adUnitId, loadAdError.message)
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
                    logger.adClicked(adUnitId)
                }

                override fun onAdDismissedFullScreenContent() {

                    onDismiss()
                    callbacks.onAdDismissed()
                    logger.adClosed(adUnitId)

                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {

                    callbacks.onAdFailedToShow(adError)
                    logger.adFailedToShow(adUnitId, adError.message)
                }

                override fun onAdImpression() {
                    logger.adImpressionRecorded(adUnitId)
                    callbacks.onAdImpression()
                }

                override fun onAdShowedFullScreenContent() {
                    callbacks.onAdShowed()
                    logger.adDisplayed(adUnitId)
                }
            }
        }

        openAd?.show(activity)
    }
}
