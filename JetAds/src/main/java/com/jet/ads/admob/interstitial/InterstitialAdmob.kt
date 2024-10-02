package com.jet.ads.admob.interstitial

import android.app.Activity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.jet.ads.common.interstitial.InterstitialsController
import com.jet.ads.common.callbacks.ShowAdCallBack
import com.jet.ads.common.callbacks.InterstitialShowAdCallbacks
import com.jet.ads.utils.pools.AdMobInterstitialPool
import kotlinx.coroutines.flow.Flow



internal class InterstitialAdmob(
    private val interstitialAdmobManager: InterstitialAdManager = InterstitialAdManager(),
    private val adMobInterstitialPool: AdMobInterstitialPool = AdMobInterstitialPool,
) : InterstitialsController {


    override fun loadedAds(): Flow<List<Pair<String, Pair<InterstitialAd, Long>>>> {
        return adMobInterstitialPool.getAllAds()
    }

    override fun show(adUnitId: String, activity: Activity, callback: ShowAdCallBack?) {
        if (callback != null){
            interstitialAdmobManager.showAd(adUnitId, activity, object : InterstitialShowAdCallbacks {
                override fun onAdClicked() {
                    callback.onAdClicked.invoke()
                }

                override fun onAdDismissed() {
                    callback.onAdDismissed.invoke()
                }

                override fun onAdFailedToShow(error: AdError) {
                    callback.onAdFailedToShow.invoke()
                }

                override fun onAdImpression() {
                    callback.onAdImpression.invoke()
                }

                override fun onAdShowed() {
                    callback.onAdShowed.invoke()
                }

            })
        }else{
            interstitialAdmobManager.showAd(adUnitId, activity)
        }

    }

}



