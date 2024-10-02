package com.jet.ads.common.callbacks

import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardItem


interface ShowAdCallbacks

//INTERSTITIAL
interface InterstitialShowAdCallbacks : ShowAdCallbacks {
    fun onAdClicked()
    fun onAdDismissed()
    fun onAdFailedToShow(error: AdError)
    fun onAdImpression()
    fun onAdShowed()
}

//REWARDED
interface RewardedCallBack: ShowAdCallbacks {
    fun onRewarded(rewardItem: RewardItem)
}

interface FullRewardedShowAdCallbacks : RewardedCallBack {
    fun onAdClicked()
    fun onAdDismissed()
    fun onAdFailedToShow(error: AdError)
    fun onAdImpression()
    fun onAdShowed()
}

interface OnlyOnRewardedCallback : RewardedCallBack {}



data class BannerCallBack(
    val onAdLoaded: () -> Unit = {},
    val onAdFailedToLoad: (error: LoadAdError) -> Unit = {},
    val onAdImpression: () -> Unit = {},
    val onAdClicked: () -> Unit = {},
)



data class ShowAdCallBack(
    val onAdClicked: () -> Unit = {},
    val onAdDismissed: () -> Unit = {},
    val onAdFailedToShow: () -> Unit = {},
    val onAdImpression: () -> Unit = {},
    val onAdShowed: () -> Unit = {},
)




//OPEN APP
interface OpenAppShowAdCallbacks : ShowAdCallbacks {
    fun onAdClicked()
    fun onAdDismissed()
    fun onAdFailedToShow(error: AdError)
    fun onAdImpression()
    fun onAdShowed()
}
