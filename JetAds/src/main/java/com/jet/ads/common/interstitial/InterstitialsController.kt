package com.jet.ads.common.interstitial

import android.app.Activity
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.jet.ads.common.callbacks.ShowAdCallBack
import kotlinx.coroutines.flow.Flow

interface InterstitialsController {
    fun loadedAds(): Flow<List<Pair<String, Pair<InterstitialAd, Long>>>>
    fun show(adUnitId: String, activity: Activity, callback: ShowAdCallBack? = null)
}