package com.jet.ads.utils.pools

import android.os.SystemClock
import androidx.annotation.VisibleForTesting
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.rewarded.RewardedAd
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal interface AdPool<T> {
    fun saveAd(adUnitId: String, ad: T)
    fun getAd(adUnitId: String): Pair<T, Long>?
    fun deleteAdFromPool(adUnitId: String, ad: T?)
    fun clearPool()
    fun getAllAds(): Flow<List<Pair<String, Pair<T, Long>>>>
}

internal abstract class BaseAdPool<T : Any> : AdPool<T> {
    protected val ads: MutableMap<String, Pair<T, Long>> = mutableMapOf()
    private val _adsFlow = MutableStateFlow<List<Pair<String, Pair<T, Long>>>>(emptyList())
    private val adsFlow: StateFlow<List<Pair<String, Pair<T, Long>>>> get() = _adsFlow

    override fun saveAd(adUnitId: String, ad: T) {
        ads[adUnitId] = Pair(ad, SystemClock.elapsedRealtime())
        emitAds()
    }

    override fun getAd(adUnitId: String): Pair<T, Long>? {
        return ads[adUnitId]
    }

    override fun deleteAdFromPool(adUnitId: String, ad: T?) {
        clearCallback(ad)
        ads.remove(adUnitId)
        emitAds()
    }

    override fun getAllAds(): Flow<List<Pair<String, Pair<T, Long>>>> {
        return adsFlow
    }

    override fun clearPool() {
        ads.clear()
        emitAds()
    }



    abstract fun clearCallback(ad: T?)

    private fun emitAds() {
        _adsFlow.value = ads.toList()
    }
}


internal object AdMobInterstitialPool : BaseAdPool<InterstitialAd>() {
    override fun clearCallback(ad: InterstitialAd?) {
        ad?.fullScreenContentCallback = null
    }
}

internal object AdMobRewardedPool : BaseAdPool<RewardedAd>() {
    override fun clearCallback(ad: RewardedAd?) {
        ad?.fullScreenContentCallback = null
    }
}

internal object AppOpenAdmobPool : BaseAdPool<AppOpenAd>() {
    override fun clearCallback(ad: AppOpenAd?) {
        ad?.fullScreenContentCallback = null
    }
}

