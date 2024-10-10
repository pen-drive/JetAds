package com.example.consumer.data

import com.jet.ads.common.controller.AdsControl
import com.jet.ads.common.controller.JetAdsControl
import kotlinx.coroutines.flow.StateFlow

class MyAppAdsControlImpl(
    private val dao: String,
): JetAdsControl {

    override fun isAdsEnabled(): StateFlow<Boolean> {
        TODO("Not yet implemented")
    }

    override fun setAdsEnabled(enabled: Boolean) {
        TODO("Not yet implemented")
    }

}