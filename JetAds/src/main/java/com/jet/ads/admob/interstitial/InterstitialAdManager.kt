package com.jet.ads.admob.interstitial

import com.google.android.gms.ads.interstitial.InterstitialAd
import com.jet.ads.admob.providers.AdmobInterstitialProvider
import com.jet.ads.utils.AdProvider
import com.jet.ads.common.callbacks.InterstitialShowAdCallbacks
import com.jet.ads.common.controller.ControlProvider
import com.jet.ads.utils.expiration.AdExpirationHandler
import com.jet.ads.utils.expiration.DefaultAdExpirationHandler
import com.jet.ads.utils.manager.BaseAdManager
import com.jet.ads.utils.pools.AdMobInterstitialPool
import com.jet.ads.utils.retry.DefaultRetryPolicy
import com.jet.ads.utils.retry.RetryPolicy

internal class InterstitialAdManager(

    private val controlProvider: ControlProvider = ControlProvider,
    private val adMobInterstitialPool: AdMobInterstitialPool = AdMobInterstitialPool,
    private val adProvider: AdProvider<InterstitialAd, InterstitialShowAdCallbacks> = AdmobInterstitialProvider(),
    private val adExpirationHandler: AdExpirationHandler = DefaultAdExpirationHandler(),
    private val retryPolicy: RetryPolicy = DefaultRetryPolicy(),

    ) : BaseAdManager<InterstitialAd, InterstitialShowAdCallbacks>(
    controlProvider, adMobInterstitialPool, adProvider, adExpirationHandler, retryPolicy
)