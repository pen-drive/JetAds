package com.jet.ads.common.controller

import kotlinx.coroutines.flow.StateFlow

interface AdsControl {

    @Deprecated("Bad name, use isAdsEnabled", replaceWith = ReplaceWith("isAdsEnabled()"))
    fun areAdsEnabled(): StateFlow<Boolean>


    fun isAdsEnabled(): StateFlow<Boolean>


    fun setAdsEnabled(enabled: Boolean)

}