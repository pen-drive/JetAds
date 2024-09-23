package com.jet.ads.di

import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.rewarded.RewardedAd
import com.jet.ads.admob.open_ad.AppLifecycleManager
import com.jet.ads.utils.pools.AdMobInterstitialPool
import com.jet.ads.utils.pools.AdMobRewardedPool
import com.jet.ads.utils.pools.AdPool
import com.jet.ads.utils.pools.AppOpenAdmobPool


internal interface JetAdsModule {
    val admobInterstitialPool: AdPool<InterstitialAd>
    val adMobRewardedPool: AdPool<RewardedAd>
    val adMobAppOpenPool: AdPool<AppOpenAd>
    val appLifecycleManager: AppLifecycleManager
}

internal class JetAdsModuleImpl() : JetAdsModule {

    override val admobInterstitialPool: AdPool<InterstitialAd> by lazy {
        AdMobInterstitialPool
    }
    override val adMobRewardedPool: AdPool<RewardedAd> by lazy {
        AdMobRewardedPool
    }
    override val adMobAppOpenPool: AdPool<AppOpenAd> by lazy {
        AppOpenAdmobPool
    }

    override val appLifecycleManager: AppLifecycleManager by lazy {
        AppLifecycleManager()
    }

}


internal interface JetAdsLib {
    fun initializeLibDeps()

}


internal object JetAds : JetAdsLib {


    val module: JetAdsModule = JetAdsModuleImpl()


    override fun initializeLibDeps() {

    }

}