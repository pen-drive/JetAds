package com.jet.ads.common.rewarded

import com.jet.ads.admob.rewarded.RewardedAdmob

object RewardedFactory {
    fun admobRewarded(): Rewarded {
        return RewardedAdmob()
    }
}


