package com.example.consumer.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jet.ads.admob.AdMobTestIds
import com.jet.ads.admob.banner.pre_load_banner.LoadBanner
import com.jet.ads.admob.banner.pre_load_banner.BannerSizes
import com.jet.ads.common.app_open.AppOpenAdManager
import com.jet.ads.common.app_open.AppOpenAdManagerFactory
import com.jet.ads.common.initializers.AdsInitializeFactory
import com.jet.ads.common.initializers.AdsInitializer


const val mainScreen = "mainScreen"
const val bannersScreen = "bannersScreen"

class MainActivity : AppCompatActivity(), AdsInitializer by AdsInitializeFactory.admobInitializer(),
    AppOpenAdManager by AppOpenAdManagerFactory.admobAppOpenAdManager() {


    private var keepSplashScreen = false // <-- for control de splash screen
    private lateinit var navHostController: NavHostController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeAds() // <-- initialize the lib

        installSplashScreen().setKeepOnScreenCondition { keepSplashScreen }

//        registerAppOpenAdForColdStart(AdMobTestIds.APP_OPEN, onCloseSplashScreen = {
//            keepSplashScreen = false
//        })

        setContent {
            val viewModel: MainViewModel = viewModel()

            LoadBanner(AdMobTestIds.FIXED_SIZE_BANNER, bannerSizes = BannerSizes.LARGE_BANNER)
            LoadBanner(AdMobTestIds.FIXED_SIZE_BANNER, bannerSizes = BannerSizes.MEDIUM_RECTANGLE)


            navHostController = rememberNavController()


            NavHost(
                navController = navHostController, startDestination = mainScreen
            ) {

                composable(mainScreen) {
                    MainScreen(viewModel = viewModel, goToBannersScreen = {
                        navHostController.navigate(bannersScreen)
                    })
                }


                composable(bannersScreen) {
                    BannersScreen(onNavigateUp = {
                        navHostController.navigateUp()
                    })
                }

            }
        }
    }
}


