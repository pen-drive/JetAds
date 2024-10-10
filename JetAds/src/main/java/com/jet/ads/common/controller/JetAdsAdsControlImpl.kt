package com.jet.ads.common.controller

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Default implementation provided by JetAds for controlling ad visibility across the app.
 *
 * Note: This implementation does NOT persist the ad visibility state. If you need persistent behavior,
 * consider implementing your own `AdsControl` interface. If you do implement your own,
 * be sure to pass it to `initializeAds()` in the `MainActivity`.
 */
object JetAdsAdsControlImpl : AdsControl {

    private val _isToShowAdds = MutableStateFlow(true)

    @Deprecated("Bad name, use isAdsEnabled", replaceWith = ReplaceWith("isAdsEnabled()"))
    override fun areAdsEnabled(): StateFlow<Boolean> {
        return _isToShowAdds.asStateFlow()
    }

    override fun isAdsEnabled(): StateFlow<Boolean> {
        return _isToShowAdds.asStateFlow()
    }

    override fun setAdsEnabled(enabled: Boolean) {
        _isToShowAdds.value = enabled
    }

}

