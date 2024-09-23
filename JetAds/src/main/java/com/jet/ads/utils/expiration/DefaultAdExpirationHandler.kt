package com.jet.ads.utils.expiration

import android.os.SystemClock
import com.google.android.gms.ads.interstitial.InterstitialAd

class DefaultAdExpirationHandler(
    private val adTimeout: Long = 60 * 60 * 1000
) : AdExpirationHandler {


    override fun <T> isAdExpired(adPair: Pair<T?, Long>): Boolean {
        val timestamp = adPair.second
        return SystemClock.elapsedRealtime() - timestamp >= adTimeout
    }


}