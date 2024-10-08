package com.jet.ads.admob.banner.pre_load_banner

import android.app.Activity
import com.google.android.gms.ads.AdSize

class BannerSizeHandler {


    fun getAdSize(context: Activity, width: Int): AdSize{
        AdSize.MEDIUM_RECTANGLE
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, width)
    }
}
