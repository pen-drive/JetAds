package com.jet.ads.admob.open_ad

import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.jet.ads.common.callbacks.OpenAppShowAdCallbacks
import com.jet.ads.common.callbacks.ShowAdCallBack
import com.jet.ads.common.controller.AdsControl
import com.jet.ads.common.controller.ControlProvider
import java.lang.ref.WeakReference



internal class OpenAdAdmobSetupWithExtensionFunc(
    private val appLifecycleManager: AppLifecycleManager,
    private var controlLocator: ControlProvider = ControlProvider,
    private var appOpenAdManager: AppOpenAdManager? = AppOpenAdManager(),
) : com.jet.ads.common.app_open.AppOpenAdManager, AppLifecycleCallback {

    private var adUnitId: String? = null
    private var activityRef: WeakReference<ComponentActivity>? = null
    private var adsControlImpl: AdsControl? = null
    private var callbacks: OpenAppShowAdCallbacks? = null
    private var closeSplashScreenRef: WeakReference<(() -> Unit)>? = null


    override fun ComponentActivity.registerAppOpenAd(
        adUnitId: String,
        showAdsCallbacks: ShowAdCallBack?
    ) {
        registerAppOpenAdInternal(adUnitId, this, false, showAdsCallbacks, null)
    }


    override fun ComponentActivity.registerAppOpenAdOnColdStart(
        adUnitId: String,
        showAdsCallbacks: ShowAdCallBack?,
        onCloseSplashScreen: () -> Unit
    ) {
        registerAppOpenAdInternal(adUnitId, this, true, showAdsCallbacks, onCloseSplashScreen)
    }


    private fun registerAppOpenAdInternal(
        adUnitId: String,
        activity: ComponentActivity,
        showOnColdStart: Boolean,
        showAdsCallbacks: ShowAdCallBack?,
        closeSplashScreen: (() -> Unit)?
    ) {
        val controller = controlLocator.getAdsControl()

        if (!controller.areAdsEnabled().value) {
            closeSplashScreen?.invoke()
            return
        }

        if (!showOnColdStart) {
            closeSplashScreen?.invoke()
        }

        this.adUnitId = adUnitId
        this.adsControlImpl = controller
        this.activityRef = WeakReference(activity)
        this.closeSplashScreenRef = closeSplashScreen?.let { WeakReference(it) }
        this.callbacks = createCallbacks(showAdsCallbacks)

        appLifecycleManager.setShowOnColdStart(showOnColdStart)
        appLifecycleManager.registerCallback(this)

        if (!appLifecycleManager.isFirstEntry()) {
            safeCloseSplashScreen()
        }

        registerActivityForOpenAdSetup(onFailedToLoad = {
            safeCloseSplashScreen()
        }) {
            appLifecycleManager.notifyAdShown()
        }
    }


    override fun onAppStart() {
        if (adsControlImpl?.areAdsEnabled()?.value != true) return
        val activity = activityRef?.get() ?: return
        appOpenAdManager?.showAd(adUnitId ?: return, activity, callbacks)
    }


    private fun safeCloseSplashScreen() {
        closeSplashScreenRef?.get()?.invoke()
        closeSplashScreenRef = null
    }


    private fun registerActivityForOpenAdSetup(
        onFailedToLoad: (error: LoadAdError) -> Unit = {}, onAddLoad: () -> Unit
    ) {
        val activity = activityRef?.get() ?: return

        appOpenAdManager?.loadAd(
            adUnitId ?: return,
            activity,
            onFailedToLoad,
        ) {
            onAddLoad()
        }

        activity.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    onDestroy()
                    activity.lifecycle.removeObserver(this)
                }
            }
        })
    }


    private fun createCallbacks(
        showAdsCallbacks: ShowAdCallBack?
    ): OpenAppShowAdCallbacks {
        return object : OpenAppShowAdCallbacks {
            override fun onAdClicked() {
                showAdsCallbacks?.onAdClicked?.invoke()
            }

            override fun onAdDismissed() {
                safeCloseSplashScreen()
                showAdsCallbacks?.onAdDismissed?.invoke()
            }

            override fun onAdFailedToShow(error: AdError) {
                showAdsCallbacks?.onAdFailedToShow?.invoke()
            }

            override fun onAdImpression() {
                showAdsCallbacks?.onAdImpression?.invoke()
            }

            override fun onAdShowed() {
                showAdsCallbacks?.onAdShowed?.invoke()
            }
        }
    }


    private fun onDestroy() {
        appLifecycleManager.unregisterCallback(this)
        activityRef?.clear()
        appOpenAdManager = null
        callbacks = null
    }
}
