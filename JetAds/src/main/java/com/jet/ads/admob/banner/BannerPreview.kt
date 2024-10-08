package com.jet.ads.admob.banner

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.jet.ads.R
import com.jet.ads.admob.AdMobTestIds
import com.jet.ads.admob.banner.pre_load_banner.BannerSizes

@Composable
fun BannerPreview(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .heightIn(80.dp)
            .background(Color(0xFFD6F0FF))
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painterResource(R.drawable.jetads),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = modifier.size(60.dp)
            )
            Text(
                fontWeight = FontWeight.ExtraBold, text = "JetAds Adaptive banner preview."
            )
        }
    }
}


/**
 * Preview for all different banner sizes
 * */
@Preview()
@Composable
fun BannersPreview(
    modifier: Modifier = Modifier, bannerSizes: BannerSizes = BannerSizes.LARGE_BANNER
) {

    val text = when (bannerSizes) {
        BannerSizes.BANNER -> "JetAds BANNER banner preview."
        BannerSizes.LARGE_BANNER -> "JetAds LARGE_BANNER banner preview."
        BannerSizes.MEDIUM_RECTANGLE -> "JetAds MEDIUM_RECTANGLE banner preview."
        BannerSizes.FULL_BANNER -> "JetAds FULL_BANNER banner preview."
        BannerSizes.LEADERBOARD -> "JetAds LEADERBOARD banner preview."
        BannerSizes.ANCHORED_ADAPTIVE_BANNER -> "JetAds ANCHORED_ADAPTIVE_BANNER banner preview."
    }

    Column(
        modifier = Modifier
            .height(bannerSizes.height)
            .width(bannerSizes.width)
            .background(Color(0xFFD6F0FF)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painterResource(R.drawable.jetads),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = modifier.size(60.dp)
            )
            Text(
                fontWeight = FontWeight.ExtraBold, text = text, textAlign = TextAlign.Center
            )
        }
    }
}


@Preview
@Composable
private fun BannerPreview() {
    AdaptiveBanner(AdMobTestIds.ADAPTIVE_BANNER)
}