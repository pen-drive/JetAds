package com.jet.ads.admob.banner

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.jet.ads.admob.banner.pre_load_banner.BannerManager
import com.jet.ads.admob.banner.pre_load_banner.BannerSizes
import com.jet.ads.utils.BannerManagerFactory


@Composable
fun BannerLoader(adId: String, modifier: Modifier = Modifier, bannerSizes: BannerSizes) {
    val context = LocalContext.current as Activity
    val currentWidth = LocalConfiguration.current.screenWidthDp

    val bannerManager: BannerManager = BannerManagerFactory.getAdmobBannerManager()

    LaunchedEffect(Unit) {
        bannerManager.loadAd(adId, bannerSizes, context)

    }
}