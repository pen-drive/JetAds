package com.jet.ads.admob.open_ad

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.PlatformTextInputInterceptor
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdError
import com.jet.ads.common.app_open.OpenAdSetup
import com.jet.ads.common.callbacks.OpenAppShowAdCallbacks
import com.jet.ads.common.callbacks.ShowAdCallBack
import com.jet.ads.common.controller.AdsControl
import com.jet.ads.common.controller.ControlProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds


sealed class AdLoadState {
    object Timeout : AdLoadState()
    object Success : AdLoadState()
}


interface AppLifecycleCallback {
    fun onAppStart()
}

internal class OpenAdAdmobSetup(
    private val appLifecycleManager: AppLifecycleManager,
    private var controlLocator: ControlProvider = ControlProvider,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,

) : OpenAdSetup, AppLifecycleCallback {

    private var adUnitId: String? = null
    private var appOpenAdManager: AppOpenAdManager? = null
    private var activityRef: WeakReference<ComponentActivity>? = null
    private var adsControlImpl: AdsControl? = null
    private var callbacks: OpenAppShowAdCallbacks? = null


    private var timeOutController = true
    private val timeOut: Duration = 2.seconds


    override fun registerOpenAppAd(
        adUnitId: String,
        activity: ComponentActivity,
        showOnColdStart: Boolean,
        showAdsCallbacks: ShowAdCallBack?,
        closeSplashScreen: () -> Unit
    ) {
        val controller = controlLocator.getAdsControl()

        if (!controller.areAdsEnabled().value) return

        appLifecycleManager.setShowOnColdStart(showOnColdStart)

        this.adUnitId = adUnitId
        this.appOpenAdManager = AppOpenAdManager()
        this.adsControlImpl = controller
        this.activityRef = WeakReference(activity)
        this.callbacks = object : OpenAppShowAdCallbacks {
            override fun onAdClicked() {
                showAdsCallbacks?.onAdClicked?.invoke()
            }

            override fun onAdDismissed() {
                closeSplashScreen()
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

        if (!appLifecycleManager.isFirstEntry()) {
            closeSplashScreen()
        }

        appLifecycleManager.registerCallback(this)

        registerActivityForOpenAdSetup {
            if (!timeOutController) {
                appLifecycleManager.notifyAdShown()
            }else{
                closeSplashScreen()
            }
        }
    }

    override fun onAppStart() {
        if (adsControlImpl?.areAdsEnabled()?.value != true) return
        val activity = activityRef?.get() ?: return
        appOpenAdManager?.showAd(adUnitId ?: return, activity, callbacks)
    }

    private fun registerActivityForOpenAdSetup(onAddLoad: () -> Unit) {
        if (adsControlImpl?.areAdsEnabled()?.value != true) return

        val activity = activityRef?.get() ?: return

        appOpenAdManager?.loadAd(adUnitId ?: return, activity) {
            timeOutController = false
            onAddLoad()
        }.also {

            CoroutineScope(dispatcher).launch {
                delay(timeOut)
                withContext(Dispatchers.Main) {
                    onAddLoad()
                }
            }

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

    private fun onDestroy() {
        appLifecycleManager.unregisterCallback(this)
        activityRef?.clear()
        appOpenAdManager = null
        callbacks = null
    }
}

