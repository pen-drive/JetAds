package com.jet.ads.admob.open_ad

import com.google.android.gms.ads.appopen.AppOpenAd
import com.jet.ads.admob.providers.AdmobOpenAppProvider
import com.jet.ads.utils.AdProvider
import com.jet.ads.common.callbacks.OpenAppShowAdCallbacks
import com.jet.ads.common.controller.ControlProvider
import com.jet.ads.utils.expiration.AdExpirationHandler
import com.jet.ads.utils.expiration.DefaultAdExpirationHandler
import com.jet.ads.utils.manager.BaseAdManager
import com.jet.ads.utils.pools.AppOpenAdmobPool
import com.jet.ads.utils.retry.DefaultRetryPolicy
import com.jet.ads.utils.retry.RetryPolicy

internal class AppOpenAdManager(
    private val controlProvider: ControlProvider = ControlProvider,
    private val adPool: AppOpenAdmobPool = AppOpenAdmobPool,
    private val adProvider: AdProvider<AppOpenAd, OpenAppShowAdCallbacks> = AdmobOpenAppProvider(),
    private val adExpirationHandler: AdExpirationHandler = DefaultAdExpirationHandler(),
    private val retryPolicy: RetryPolicy = DefaultRetryPolicy(),
) : BaseAdManager<AppOpenAd, OpenAppShowAdCallbacks>(
    controlProvider, adPool, adProvider, adExpirationHandler, retryPolicy
)