package com.jet.ads.admob.rewarded

import com.google.android.gms.ads.rewarded.RewardedAd
import com.jet.ads.admob.providers.AdmobRewardedProvider
import com.jet.ads.utils.AdProvider
import com.jet.ads.common.callbacks.RewardedCallBack
import com.jet.ads.common.controller.ControlProvider
import com.jet.ads.utils.expiration.AdExpirationHandler
import com.jet.ads.utils.expiration.DefaultAdExpirationHandler
import com.jet.ads.utils.manager.BaseAdManager
import com.jet.ads.utils.pools.AdMobRewardedPool
import com.jet.ads.utils.retry.DefaultRetryPolicy
import com.jet.ads.utils.retry.RetryPolicy

internal class RewardedAdManager(
    private val controlProvider: ControlProvider = ControlProvider,
    private val adMobRewardedPool: AdMobRewardedPool = AdMobRewardedPool,
    private val adProvider: AdProvider<RewardedAd, RewardedCallBack> = AdmobRewardedProvider(),
    private val adExpirationHandler: AdExpirationHandler = DefaultAdExpirationHandler(),
    private val retryPolicy: RetryPolicy = DefaultRetryPolicy()
) : BaseAdManager<RewardedAd, RewardedCallBack>(
    controlProvider, adMobRewardedPool, adProvider, adExpirationHandler, retryPolicy
)

