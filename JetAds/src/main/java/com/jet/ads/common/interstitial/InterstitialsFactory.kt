package com.jet.ads.common.interstitial

import com.jet.ads.admob.interstitial.InterstitialAdmob

object InterstitialsFactory {
    fun admobInterstitial(): Interstitials {
        return InterstitialAdmob()
    }
}