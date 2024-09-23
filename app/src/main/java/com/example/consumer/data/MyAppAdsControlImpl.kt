package com.example.consumer.data

import com.jet.ads.common.controller.AdsControl
import kotlinx.coroutines.flow.StateFlow

class MyAppAdsControlImpl(
    private val dao: String,
): AdsControl {
    override fun areAdsEnabled(): StateFlow<Boolean> {
        TODO("Not yet implemented")
    }

    override fun setAdsEnabled(enabled: Boolean) {
        TODO("Not yet implemented")
    }
}