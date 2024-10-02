package com.jet.ads.common.interstitial

import com.jet.ads.admob.interstitial.InterstitialAdmob

object InterstitialsControllerFactory {
    fun admobController(): InterstitialsController {
        return InterstitialAdmob()
    }
}