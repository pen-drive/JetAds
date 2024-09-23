package com.jet.ads.common.app_open

import com.jet.ads.admob.open_ad.AppOpenAdManager
import com.jet.ads.admob.open_ad.OpenAdAdmobSetup
import com.jet.ads.common.controller.ControlProvider
import com.jet.ads.di.JetAds

object AppOpenAdManagerFactory {
    fun admobAppOpenInitializer(): OpenAdSetup {

        return OpenAdAdmobSetup(JetAds.module.appLifecycleManager, )
    }
}