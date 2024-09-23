package com.jet.ads.common.controller

internal object ControlProvider {

    private var adsControl: AdsControl = JetAdsAdsControlImpl

    fun setAdControl(adsControl: AdsControl){
        ControlProvider.adsControl = adsControl
    }

    fun getAdsControl(): AdsControl {
        return adsControl
    }

}