package com.jet.ads.admob.banner.pre_load_banner

import android.app.Activity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

class AdmobBannerProvider<TAd, TCallbacks>(
    private val bannerSizeHandler: BannerSizeHandler
) : BannerProvider<AdView, TCallbacks> {


    override fun load(
        adUnitId: String,
        bannerSizes: BannerSizes,
        context: Activity,
        onAdLoaded: (AdView) -> Unit,
        onAdFailedToLoad: (LoadAdError) -> Unit
    ) {
        val adView = AdView(context)

        adView.apply {
            this.adUnitId = adUnitId
            setAdSize(bannerSizeHandler.getAdSize(context, width))
        }

        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                onAdLoaded(adView)
            }

            override fun onAdFailedToLoad(error: LoadAdError) {

            }

            override fun onAdImpression() {

            }

            override fun onAdClicked() {

            }
        }


        when (bannerSizes) {
            BannerSizes.BANNER -> adView.setAdSize(AdSize.BANNER)
            BannerSizes.LARGE_BANNER -> adView.setAdSize(AdSize.LARGE_BANNER)
            BannerSizes.MEDIUM_RECTANGLE -> adView.setAdSize(AdSize.MEDIUM_RECTANGLE)
            BannerSizes.FULL_BANNER -> adView.setAdSize(AdSize.FULL_BANNER)
            BannerSizes.LEADERBOARD -> adView.setAdSize(AdSize.LEADERBOARD)
        }

        adView.loadAd(AdRequest.Builder().build())
    }

    override fun show(
        adUnitId: String,
        adPair: Pair<AdView?, Long>,
        activity: Activity,
        callbacks: TCallbacks?,
        onDismiss: () -> Unit
    ) {
        TODO("Not yet implemented")
    }
}