package com.jet.ads.utils.manager

import android.app.Activity
import android.content.Context
import com.jet.ads.utils.AdNotAvailableException
import com.jet.ads.utils.AdProvider
import com.jet.ads.common.controller.ControlProvider
import com.jet.ads.utils.expiration.AdExpirationHandler
import com.jet.ads.utils.pools.AdPool
import com.jet.ads.utils.retry.RetryPolicy

internal abstract class BaseAdManager<TAd, TCallbacks>(
    private val controlProvider: ControlProvider,
    private val adPool: AdPool<TAd>,
    private val adProvider: AdProvider<TAd, TCallbacks>,
    private val adExpirationHandler: AdExpirationHandler,
    private val retryPolicy: RetryPolicy
) {


    /**
     * When loading an ad, the application context is used to avoid potential memory leaks.
     * However, the documentation recommends using the activity context for mediation,
     * as some ad networks require it
     * ([AdMob Mediation Documentation](https://developers.google.com/admob/android/mediation#initialize_your_ad_object_with_an_activity_instance)).
     *
     * When adding mediation support, an alternative should be found to use the activity context
     * in a way that does not cause memory leaks.
     */
    fun loadAd(adUnitId: String, context: Context, onAdLoaded: () -> Unit = {}) {
        if (!controlProvider.getAdsControl().areAdsEnabled().value) return

        val appContext = context.applicationContext

        adProvider.load(adUnitId, appContext, { ad ->
            adPool.saveAd(adUnitId, ad)
            onAdLoaded()
        }, { error ->
            retryPolicy.retry {
                loadAd(adUnitId, appContext, onAdLoaded)
            }
        })
    }

    fun showAd(
        adUnitId: String, activity: Activity, callbacks: TCallbacks? = null
    ) {

        if (!controlProvider.getAdsControl().areAdsEnabled().value) return

        val adPair = adPool.getAd(adUnitId)
        if (adPair != null) {
            val expired = adExpirationHandler.isAdExpired(adPair)
            if (expired) {
                loadAd(adUnitId, activity)
                adPool.deleteAdFromPool(adUnitId, adPair.first)
            } else {
                adProvider.show(adUnitId, adPair, activity, callbacks, onDismiss = {
                    adPool.deleteAdFromPool(adUnitId, adPair.first)
                    loadAd(adUnitId, activity)
                })
            }
        } else {
            loadAd(adUnitId, activity)
        }
    }


}


