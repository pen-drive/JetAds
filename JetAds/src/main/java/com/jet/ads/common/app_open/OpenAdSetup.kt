package com.jet.ads.common.app_open

import androidx.activity.ComponentActivity
import com.jet.ads.common.callbacks.ShowAdCallBack

interface OpenAdSetup {
    fun registerAppOpenAd(
        adUnitId: String,
        activity: ComponentActivity,
        showOnColdStart: Boolean = false,
        showAdsCallbacks: ShowAdCallBack? = null,
        closeSplashScreen: () -> Unit = {}
    )
}