package com.jet.ads.utils.expiration

interface AdExpirationHandler {
    fun  <T>isAdExpired(adPair: Pair<T?, Long>): Boolean
}