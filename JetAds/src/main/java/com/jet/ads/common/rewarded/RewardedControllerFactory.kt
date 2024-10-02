package com.jet.ads.common.rewarded

import com.jet.ads.admob.rewarded.RewardsControllerAdmob

object RewardedControllerFactory {
    fun admobController(): RewardsController {
        return RewardsControllerAdmob()
    }
}


