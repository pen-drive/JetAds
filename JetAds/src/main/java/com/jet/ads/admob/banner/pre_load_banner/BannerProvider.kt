package com.jet.ads.admob.banner.pre_load_banner

import android.app.Activity
import com.google.android.gms.ads.LoadAdError


interface BannerProvider<TAd, TCallbacks> {
    fun load(
        adUnitId: String,
        bannerSizes: BannerSizes,
        context: Activity,
        onAdLoaded: (TAd) -> Unit,
        onAdFailedToLoad: (LoadAdError) -> Unit
    )

    fun show(
        adUnitId: String,
        adPair: Pair<TAd?, Long>,
        activity: Activity,
        callbacks: TCallbacks?,
        onDismiss: () -> Unit,
    )
}
