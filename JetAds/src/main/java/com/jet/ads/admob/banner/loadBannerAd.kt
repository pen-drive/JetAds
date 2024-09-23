package com.jet.ads.admob.banner

import android.content.Context
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.jet.ads.common.callbacks.BannerCallBack


//internal fun loadAdaptiveBannerAd2(
//    adUnit: String,
//    context: Context,
//    width: Int,
//    isPreviewMode: Boolean,
//    showAdCallBack: BannerCallBack? = null
//): AdView {
//    val adView = AdView(context)
//
//
//    if (isPreviewMode) {
//        return adView
//    }
//
//    adView.apply {
//        adUnitId = adUnit
//        setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, width))
//    }
//
//    handleCallBacks(showAdCallBack, adView)
//
//    adView.loadAd(AdRequest.Builder().build())
//    return adView
//}
//
//
//internal fun handleCallBacks(showAdCallBack: BannerCallBack?, adView: AdView) {
//    showAdCallBack?.let { callback ->
//        adView.adListener = object : AdListener() {
//            override fun onAdLoaded() {
//                callback.onAdLoaded.invoke()
//            }
//
//            override fun onAdFailedToLoad(error: LoadAdError) {
//                callback.onAdFailedToLoad.invoke(error)
//            }
//
//            override fun onAdImpression() {
//                callback.onAdImpression.invoke()
//            }
//
//            override fun onAdClicked() {
//                callback.onAdClicked.invoke()
//            }
//        }
//
//    }
//
//}