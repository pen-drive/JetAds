package com.jet.ads.admob.banner

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.app.R
import com.jet.ads.admob.AdMobTestIds

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

@Preview
@Composable
private fun BannerPreview() {
    AdaptiveBanner(AdMobTestIds.ADAPTIVE_BANNER)
}