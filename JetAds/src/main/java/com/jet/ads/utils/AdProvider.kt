package com.jet.ads.utils

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.LoadAdError


interface AdProvider<TAd, TCallbacks> {
    fun load(
        adUnitId: String,
        context: Context,
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
