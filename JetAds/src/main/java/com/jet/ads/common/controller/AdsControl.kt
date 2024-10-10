package com.jet.ads.common.controller

import kotlinx.coroutines.flow.StateFlow

@Deprecated("Use JetAdsControl", replaceWith = ReplaceWith("JetAdsControl"))
interface AdsControl {

    fun areAdsEnabled(): StateFlow<Boolean>


    fun setAdsEnabled(enabled: Boolean)

}


interface JetAdsControl: AdsControl {

    fun isAdsEnabled(): StateFlow<Boolean>

    @Deprecated("Bad name, use isAdsEnabled", replaceWith = ReplaceWith("isAdsEnabled()"))
    override fun areAdsEnabled(): StateFlow<Boolean> {
        return isAdsEnabled()
    }

}