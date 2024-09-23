package com.jet.ads.utils

import com.jet.ads.admob.interstitial.InterstitialAdManager
import com.jet.ads.admob.rewarded.RewardedAdManager

/**
 * Add support for other ad SDKs in the future
 * */
internal object RewardedManagerFactory {

    fun getAdmobRewardedManager(): RewardedAdManager {
        return RewardedAdManager()
    }

}


/**
 * Add support for other ad SDKs in the future
 * */
internal object InterstitialManagerFactory {

    fun getAdmobInterstitialManager(): InterstitialAdManager {
        return InterstitialAdManager()
    }

}