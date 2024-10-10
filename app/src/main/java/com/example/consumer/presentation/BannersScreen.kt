package com.example.consumer.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jet.ads.admob.AdMobTestIds
import com.jet.ads.admob.banner.AdaptiveBanner
import com.jet.ads.admob.banner.pre_load_banner.Banner

@Composable
fun BannersScreen(modifier: Modifier = Modifier, onNavigateUp: () -> Unit = {}) {


    Scaffold(bottomBar = {
        AdaptiveBanner(AdMobTestIds.ADAPTIVE_BANNER)
    }, topBar = {
        AdaptiveBanner(AdMobTestIds.ADAPTIVE_BANNER)
    }) {


        Column(Modifier.padding(it)) {
            Banner(adUnit = AdMobTestIds.FIXED_SIZE_BANNER)


        }
    }


}