package com.jet.ads.admob.banner

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.ads.AdView
import com.jet.ads.common.callbacks.BannerCallBack
import com.jet.ads.common.controller.ControlProvider

/**
 * Displays a banner ad with measures to minimize accidental clicks.
 * The banner initially occupies a fixed size to prevent layout shifts when the ad loads,
 * reducing the chance of accidental clicks. Additionally, a top margin can be added for further safety.
 *
 * @param safeTopMarginDp - The top margin above the banner to help prevent accidental clicks.
 * Set to 0.dp if no margin is desired.
 * @param safeAreaColor - The color of the top margin area. This color is applied to the margin space.
 */
@Composable
fun AdaptiveBanner(
    adUnit: String,
    modifier: Modifier = Modifier,
    safeTopMarginDp: Dp = 12.dp,
    safeAreaColor: Color = Color.White,
    bannerCallBack: BannerCallBack? = null
) {
    val currentWidth = LocalConfiguration.current.screenWidthDp
    val adsControl = ControlProvider.getAdsControl()
    val isAdsEnable by adsControl.areAdsEnabled().collectAsStateWithLifecycle(initialValue = true)
    val isPreviewMode = LocalInspectionMode.current

    val context = LocalContext.current
    val appContext = LocalContext.current.applicationContext

    var adView by remember { mutableStateOf<AdView?>(null) }

    LaunchedEffect(isAdsEnable, currentWidth) {
        if (isAdsEnable && !isPreviewMode) {
            adView?.destroy()
            adView = loadAdaptiveBannerAd2(
                adUnit, appContext, currentWidth, isPreviewMode, bannerCallBack
            )
        } else {
            adView?.destroy()
            adView = null
        }
    }

    if (isAdsEnable) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(safeAreaColor)
                .heightIn(80.dp)
        ) {
            Spacer(
                modifier = Modifier
                    .height(safeTopMarginDp)
                    .testTag("SafeTopMargin")
            )

            Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxWidth()) {
                if (!isPreviewMode) {
                    adView?.let { adv ->
                        AndroidView(modifier = Modifier
                            .fillMaxWidth()
                            .testTag("AdView"),
                            factory = { FrameLayout(it) },
                            update = { layout ->
                                layout.removeAllViews()
                                if (adv.parent != null) {
                                    (adv.parent as? ViewGroup)?.removeView(adv)
                                }
                                layout.addView(adv)
                            })
                    }
                } else {
                    BannerPreview()
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            adView?.destroy()
            adView = null
        }
    }
}


