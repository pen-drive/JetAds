package com.jet.ads.admob.banner.pre_load_banner

import androidx.compose.ui.unit.Dp


import androidx.compose.ui.unit.dp

enum class BannerSizes(val width: Dp, val height: Dp) {

    /**
     * Banner ad size (320x50 density-independent pixels)
     */
    BANNER(320.dp, 50.dp),

    /**
     * Large banner ad size (320x100 density-independent pixels)
     */
    LARGE_BANNER(320.dp, 100.dp),

    /**
     * Medium rectangle ad size (300x250 density-independent pixels)
     */
    MEDIUM_RECTANGLE(300.dp, 250.dp),

    /**
     * Full banner ad size (468x60 density-independent pixels)
     */
    FULL_BANNER(468.dp, 60.dp),

    /**
     * Leaderboard ad size (728x90 density-independent pixels)
     */
    LEADERBOARD(728.dp, 90.dp),

    /**
     * Anchored adaptive banner with dynamic width and fixed height
     * Use -1.dp for width or height to indicate adaptive behavior
     */
    ANCHORED_ADAPTIVE_BANNER(80.dp, (100).dp),
}
