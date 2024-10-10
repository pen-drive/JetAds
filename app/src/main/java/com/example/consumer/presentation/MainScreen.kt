package com.example.consumer.presentation

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.consumer.R
import com.example.consumer.presentation.components.Greeting
import com.example.consumer.presentation.components.SwitchAds
import com.jet.ads.admob.interstitial.LoadInterstitial
import com.jet.ads.admob.rewarded.LoadRewarded
import com.jet.ads.admob.AdMobTestIds
import com.jet.ads.admob.banner.pre_load_banner.Banner
import com.jet.ads.admob.banner.pre_load_banner.LoadBanner
import com.jet.ads.admob.banner.pre_load_banner.BannerSizes


@Composable
fun MainScreen(viewModel: MainViewModel, goToBannersScreen: () -> Unit = {}) {

    val activity = LocalContext.current as Activity
    val scrollableState = rememberScrollState()

    val isAdsEnabled = viewModel.isToShowAdds.collectAsStateWithLifecycle().value


    /**
     * Next feature
     * */
//    val someInterstitial = viewModel.interstitialsLoaded.collectAsStateWithLifecycle().value
//    val someRewarded = viewModel.rewardedLoaded.collectAsStateWithLifecycle().value

    LoadInterstitial(AdMobTestIds.INTERSTITIAL)
    LoadRewarded(AdMobTestIds.REWARDED)

    LoadBanner(AdMobTestIds.FIXED_SIZE_BANNER, bannerSizes = BannerSizes.BANNER)

    Scaffold(bottomBar = {
        Banner(adUnit = AdMobTestIds.FIXED_SIZE_BANNER)
//        AdaptiveBanner(AdMobTestIds.ADAPTIVE_BANNER)
    }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollableState)
                .padding(it)
                .padding(horizontal = 34.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Greeting()
            Spacer(modifier = Modifier.height(40.dp))


            SwitchAds(isAdsEnabled, toggleAds = {
                viewModel.onEvent(MainScreenEvents.ToggleAds)
            })


            Row(
                horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = {
                    viewModel.onEvent(
                        MainScreenEvents.ShowInterstitial(
                            AdMobTestIds.INTERSTITIAL, activity
                        )
                    )
                }, enabled = isAdsEnabled) {
                    Text(stringResource(R.string.mostrar_interstitial))
                }


                Button(onClick = {
                    viewModel.onEvent(
                        MainScreenEvents.RewardedInterstitial(
                            AdMobTestIds.REWARDED, activity
                        )
                    )
                }, enabled = isAdsEnabled) {
                    Text(stringResource(R.string.mostrar_rewarded))
                }
            }



            Spacer(modifier = Modifier.height(25.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, null)
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = stringResource(R.string.test_open_ad_instructions))
            }
            Spacer(modifier = Modifier.height(25.dp))


            Button(onClick = {
                goToBannersScreen()
            }) {
                Text("Go to Banners screen")
            }
        }
    }
}



