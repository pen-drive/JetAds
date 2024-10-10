package com.jet.ads.common.initializers

import androidx.activity.ComponentActivity
import com.jet.ads.common.controller.AdsControl
import com.jet.ads.common.controller.JetAdsAdsControlImpl
import com.jet.ads.common.controller.JetAdsControl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow

interface AdsInitializer {

    /**
     * You should call this method on the OnCreate of MainActivity!
     *
     *
     * @param adsControl if you don't pass one, it will use the default one, JetAdsAdsControlImpl.
     * but if you want to implements yours fell free to.
     * */
    @Deprecated("this method is deprecated")
    fun initializeAds(
        context: ComponentActivity,
        backgroundScope: CoroutineScope = CoroutineScope(Dispatchers.IO),
        adsControl: AdsControl = JetAdsAdsControlImpl,
    ): Flow<Boolean>


    fun ComponentActivity.initializeAds(
        adsControl: JetAdsControl = JetAdsAdsControlImpl,
    ): Flow<Boolean>


}