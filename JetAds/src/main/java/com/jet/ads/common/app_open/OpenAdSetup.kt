package com.jet.ads.common.app_open

import androidx.activity.ComponentActivity
import com.jet.ads.common.callbacks.ShowAdCallBack


@Deprecated(
    message = "This interface is deprecated. Use AppOpenAdManager instead.",
    replaceWith = ReplaceWith("AppOpenAdManager")
)
interface OpenAdSetup {


    fun registerAppOpenAd(
        adUnitId: String,
        activity: ComponentActivity,
        showOnColdStart: Boolean = false,
        showAdsCallbacks: ShowAdCallBack? = null,
        closeSplashScreen: () -> Unit = {}
    )

}


interface AppOpenAdManager {

    /**
     * Registers the activity to show App Open Ads when the app moves to the background and comes back to the foreground.
     * If you also want to show the ad during a cold start, use the method: registerAppOpenAdForColdStart.
     *
     * @param adUnitId The ID of the ad unit.
     * @param showAdsCallbacks Optional callbacks for ad events.
     */
    fun ComponentActivity.registerAppOpenAd(
        adUnitId: String,
        showAdsCallbacks: ShowAdCallBack? = null,
    )

    /**
     * Registers the activity to show App Open Ads when the app moves to the background, returns to the foreground, and also during a cold start.
     * If you do not wish to show the ad during a cold start, use the method: registerAppOpenAd.
     *
     * @param adUnitId The ID of the ad unit.
     * @param showAdsCallbacks Optional callbacks for ad events.
     * @param onCloseSplashScreen callback to close the splash screen after the ad is shown, or failed to show.
     */
    fun ComponentActivity.registerAppOpenAdForColdStart(
        adUnitId: String, showAdsCallbacks: ShowAdCallBack? = null, onCloseSplashScreen: () -> Unit
    )
}
