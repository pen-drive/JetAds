package com.jet.ads.admob.rewarded

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.jet.ads.utils.RewardedManagerFactory

@Composable
fun RewardedAdLoader(adId: String) {
    val context = LocalContext.current.applicationContext

    val rewardedManager: RewardedAdManager = RewardedManagerFactory.getAdmobRewardedManager()

    LaunchedEffect(Unit) {
        rewardedManager.loadAd(adId, context)
    }
}