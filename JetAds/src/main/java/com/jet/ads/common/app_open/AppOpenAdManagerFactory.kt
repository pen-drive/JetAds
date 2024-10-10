package com.jet.ads.common.app_open

import com.jet.ads.admob.open_ad.OpenAdAdmobSetup
import com.jet.ads.admob.open_ad.OpenAdAdmobSetupWithExtensionFunc
import com.jet.ads.di.JetAds

object AppOpenAdManagerFactory {


    @Deprecated("This method is deprecated because OpenAdSetup is also deprecated.", ReplaceWith("AppOpenAdManagerFactory.admobAppOpenAdManager()"))
    fun admobAppOpenInitializer(): OpenAdSetup {
        return OpenAdAdmobSetup(JetAds.module.appLifecycleManager)
    }


    fun admobAppOpenAdManager(): AppOpenAdManager {
        return OpenAdAdmobSetupWithExtensionFunc(JetAds.module.appLifecycleManager)
    }
}