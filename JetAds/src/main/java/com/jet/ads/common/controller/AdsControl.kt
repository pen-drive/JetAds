package com.jet.ads.common.controller

import kotlinx.coroutines.flow.StateFlow

interface AdsControl {
    fun areAdsEnabled(): StateFlow<Boolean>
    fun setAdsEnabled(enabled: Boolean)

}