package com.jet.ads.common.rewarded

import android.app.Activity
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.jet.ads.common.callbacks.ShowAdCallBack
import kotlinx.coroutines.flow.Flow


interface Rewarded {
    fun loadedAds(): Flow<List<Pair<String, Pair<RewardedAd, Long>>>>
    fun show(
        adUnitId: String,
        activity: Activity,
        callBack: ShowAdCallBack? = null,
        onRewarded: (item: RewardItem) -> Unit
    )

}