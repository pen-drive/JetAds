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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.consumer.R
import com.example.consumer.presentation.components.Greeting
import com.example.consumer.presentation.components.SwitchAds
import com.jet.ads.admob.banner.AdaptiveBanner
import com.jet.ads.admob.interstitial.InterstitialAdLoader
import com.jet.ads.admob.rewarded.RewardedAdLoader
import com.jet.ads.admob.AdMobTestIds


@Composable
fun MainScreen(viewModel: MainViewModel) {

    val activity = LocalContext.current as Activity
    val scrollableState = rememberScrollState()

    val areAdsEnabled = viewModel.isToShowAdds.collectAsStateWithLifecycle().value


    /**
     * Next feature
     * */
//    val someInterstitial = viewModel.interstitialsLoaded.collectAsStateWithLifecycle().value
//    val someRewarded = viewModel.rewardedLoaded.collectAsStateWithLifecycle().value

    InterstitialAdLoader(AdMobTestIds.INTERSTITIAL)
    RewardedAdLoader(AdMobTestIds.REWARDED)

    Scaffold(bottomBar = {
        AdaptiveBanner(AdMobTestIds.ADAPTIVE_BANNER)
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


            SwitchAds(areAdsEnabled, toggleAds = {
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
                }, enabled =  areAdsEnabled) {
                    Text(stringResource(R.string.mostrar_interstitial))
                }


                Button(onClick = {
                    viewModel.onEvent(
                        MainScreenEvents.RewardedInterstitial(
                            AdMobTestIds.REWARDED, activity
                        )
                    )
                }, enabled = areAdsEnabled) {
                    Text(stringResource(R.string.mostrar_rewarded))
                }
            }



            Spacer(modifier = Modifier.height(25.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, null)
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = stringResource(R.string.test_open_ad_instructions))
            }
        }
    }
}



