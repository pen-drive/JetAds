package com.example.consumer.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jet.ads.common.initializers.AdsInitializer
import com.jet.ads.common.initializers.AdsInitializeFactory
import com.jet.ads.common.app_open.OpenAdSetup
import com.jet.ads.common.app_open.AppOpenAdManagerFactory
import com.jet.ads.admob.AdMobTestIds
import com.jet.ads.common.controller.JetAdsAdsControlImpl


class MainActivity : AppCompatActivity(),
    AdsInitializer by AdsInitializeFactory.admobInitializer(),
    OpenAdSetup by AppOpenAdManagerFactory.admobAppOpenInitializer()
{

    private val adsControl = JetAdsAdsControlImpl
    private var keepSplashScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splash = installSplashScreen()

        initializeAds(this, adsControl = adsControl)
        splash.setKeepOnScreenCondition { keepSplashScreen }
        registerAppOpenAd(AdMobTestIds.APP_OPEN, this, showOnColdStart = false) {
            keepSplashScreen = false

        }

        setContent {
            val viewModel: MainViewModel = viewModel()
            MainScreen(viewModel = viewModel)
        }
    }
}


