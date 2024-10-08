package com.jet.ads.admob.banner.pre_load_banner

import com.google.android.gms.ads.AdView
import com.jet.ads.common.callbacks.BannerCallBack
import com.jet.ads.common.controller.ControlProvider
import com.jet.ads.utils.expiration.AdExpirationHandler
import com.jet.ads.utils.expiration.DefaultAdExpirationHandler
import com.jet.ads.utils.pools.AdMobBannerAdmobPool
import com.jet.ads.utils.retry.DefaultRetryPolicy
import com.jet.ads.utils.retry.RetryPolicy


internal class BannerManager(
    private val controlProvider: ControlProvider = ControlProvider,
    private val adMobBannerPool: AdMobBannerAdmobPool = AdMobBannerAdmobPool,
    private val adProvider: BannerProvider<AdView, BannerCallBack> = AdmobBannerProvider<AdView, BannerCallBack>(BannerSizeHandler()),
    private val adExpirationHandler: AdExpirationHandler = DefaultAdExpirationHandler(),
    private val retryPolicy: RetryPolicy = DefaultRetryPolicy()
) : BaseBannerAdManager<AdView, BannerCallBack>(
    controlProvider, adMobBannerPool, adProvider, adExpirationHandler, retryPolicy
)

