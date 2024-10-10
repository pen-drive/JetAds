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
import com.jet.ads.common.app_open.AppOpenAdManager


class MainActivity : AppCompatActivity(),
    AdsInitializer by AdsInitializeFactory.admobInitializer(),
    AppOpenAdManager by AppOpenAdManagerFactory.admobAppOpenAdManager()
{

    private var keepSplashScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeAds()

        installSplashScreen().setKeepOnScreenCondition { keepSplashScreen }

        registerAppOpenAdOnColdStart(AdMobTestIds.APP_OPEN, onCloseSplashScreen = {
            keepSplashScreen = false
        })

        setContent {
            val viewModel: MainViewModel = viewModel()
            MainScreen(viewModel = viewModel)
        }
    }
}


