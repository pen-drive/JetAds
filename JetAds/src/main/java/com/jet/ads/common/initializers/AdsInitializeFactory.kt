package com.jet.ads.common.initializers

import com.jet.ads.admob.AdmobInitializer
import com.jet.ads.di.JetAds

object AdsInitializeFactory {
    fun admobInitializer(): AdsInitializer {
        return AdmobInitializer(
            adMobRewardedPool = JetAds.module.adMobRewardedPool,
            adMobInterstitialPool =  JetAds.module.admobInterstitialPool,
            adMobAppOpenPool = JetAds.module.adMobAppOpenPool
        )
    }
}