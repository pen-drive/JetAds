package com.jet.ads.admob.interstitial

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.jet.ads.utils.InterstitialManagerFactory

@Composable
fun InterstitialAdLoader(
    adId: String,
) {
    val context = LocalContext.current.applicationContext

    LaunchedEffect(key1 = adId, key2 = context) {
        val interstitialManager: InterstitialAdManager =
            InterstitialManagerFactory.getAdmobInterstitialManager()

        interstitialManager.loadAd(adId, context)
    }

}