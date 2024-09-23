package com.jet.ads.admob.rewarded

import android.app.Activity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.jet.ads.common.rewarded.Rewarded
import com.jet.ads.common.callbacks.ShowAdCallBack
import com.jet.ads.common.callbacks.FullRewardedShowAdCallbacks
import com.jet.ads.common.callbacks.OnlyOnRewardedCallback
import com.jet.ads.utils.pools.AdMobInterstitialPool
import com.jet.ads.utils.pools.AdMobRewardedPool
import kotlinx.coroutines.flow.Flow


internal class RewardedAdmob(
    private val rewardedAdmobManager: RewardedAdManager = RewardedAdManager(),
    private val adMobRewardedPool: AdMobRewardedPool = AdMobRewardedPool,
) : Rewarded {

    override fun loadedAds(): Flow<List<Pair<String, Pair<RewardedAd, Long>>>> {
        return adMobRewardedPool.getAllAds()
    }

    override fun show(
        adUnitId: String, activity: Activity,
        callBack: ShowAdCallBack?,
        onRewarded: (item: RewardItem) -> Unit,
    ) {
        if (callBack != null){
            rewardedAdmobManager.showAd(
                adUnitId,
                activity,
                callbacks = object : FullRewardedShowAdCallbacks {
                    override fun onAdClicked() {
                        callBack.onAdClicked.invoke()
                    }

                    override fun onAdDismissed() {
                        callBack.onAdDismissed.invoke()

                    }

                    override fun onAdFailedToShow(error: AdError) {
                        callBack.onAdFailedToShow.invoke()

                    }

                    override fun onAdImpression() {
                        callBack.onAdImpression.invoke()

                    }

                    override fun onAdShowed() {
                        callBack.onAdShowed.invoke()

                    }

                    override fun onRewarded(rewardItem: RewardItem) {
                        onRewarded(rewardItem)
                    }
                }
            )
        }else{
            rewardedAdmobManager.showAd(
                adUnitId,
                activity,
                callbacks = object : OnlyOnRewardedCallback {
                    override fun onRewarded(rewardItem: RewardItem) {
                        onRewarded(rewardItem)
                    }
                }
            )
        }
    }
}


