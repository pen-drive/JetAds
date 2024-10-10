package com.jet.ads.admob.banner.pre_load_banner

import android.app.Activity
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
import com.jet.ads.admob.banner.BannersPreview
import com.jet.ads.common.callbacks.BannersCallBack
import com.jet.ads.common.controller.ControlProvider
import com.jet.ads.utils.BannerManagerFactory


@Composable
fun LoadBanner(adId: String, bannerSizes: BannerSizes , modifier: Modifier = Modifier, ) {
    val context = LocalContext.current as Activity
    val currentWidth = LocalConfiguration.current.screenWidthDp

    val bannerManager: BannerManager = BannerManagerFactory.getAdmobBannerManager()

    LaunchedEffect(Unit) {
        bannerManager.loadAd(adId, bannerSizes, context)

    }
}


@Composable
fun Banner(
    adUnit: String,
    modifier: Modifier = Modifier,
    bannerSizes: BannerSizes = BannerSizes.ANCHORED_ADAPTIVE_BANNER,
    safeTopMarginDp: Dp = 12.dp,
    safeAreaColor: Color = Color.Transparent,
    bannerCallBack: BannersCallBack? = null
) {
    val currentWidth = LocalConfiguration.current.screenWidthDp
    val adsControl = ControlProvider.getAdsControl()
    val isAdsEnable by adsControl.areAdsEnabled().collectAsStateWithLifecycle(initialValue = true)
    val isPreviewMode = LocalInspectionMode.current

    val bannerManager: BannerManager = BannerManagerFactory.getAdmobBannerManager()
    var adView by remember { mutableStateOf<AdView?>(null) }

    LaunchedEffect(isAdsEnable, currentWidth) {
        if (isAdsEnable && !isPreviewMode) {
            adView?.destroy()
            adView = bannerManager.getAd(adUnit)
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
                    BannersPreview(bannerSizes = bannerSizes)
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