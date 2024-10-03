package com.jet.ads.admob

import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.initialization.AdapterStatus
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.rewarded.RewardedAd
import com.jet.ads.di.JetAds
import com.jet.ads.di.JetAdsLib
import com.jet.ads.common.initializers.AdsInitializer
import com.jet.ads.common.controller.AdsControl
import com.jet.ads.common.controller.ControlProvider
import com.jet.ads.utils.pools.AdPool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference


internal class AdmobInitializer(
    private val jetAdsLibs: JetAdsLib = JetAds,
    private val controlLocator: ControlProvider = ControlProvider,
    private val adMobRewardedPool: AdPool<RewardedAd>,
    private val adMobInterstitialPool: AdPool<InterstitialAd>,
    private val adMobAppOpenPool: AdPool<AppOpenAd>,
) : AdsInitializer, LifecycleEventObserver {

    private val _adsInitializationStatus = MutableStateFlow(false)
    private val adsInitializationStatus = _adsInitializationStatus.asStateFlow()

    private lateinit var backgroundScope: CoroutineScope
    private var activityRef: WeakReference<ComponentActivity>? = null

    override fun initializeAds(
        context: ComponentActivity, backgroundScope: CoroutineScope, adsControl: AdsControl
    ): Flow<Boolean> {
        controlLocator.setAdControl(adsControl)

        if (!adsControl.areAdsEnabled().value) return MutableStateFlow(true)

        this.backgroundScope = backgroundScope
        this.activityRef = WeakReference(context)

        context.lifecycle.addObserver(this)

        return adsInitializationStatus
    }



    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        val activity = activityRef?.get() ?: return

        handleSdkInitialization(event, activity)
        clearLibToAvoidMemoryLeak(event, activity)
    }



    private fun handleSdkInitialization(event: Lifecycle.Event, activity: ComponentActivity) {
        if (event == Lifecycle.Event.ON_CREATE) {
            backgroundScope.launch {
                MobileAds.initialize(activity) { initializationStatus ->
                    val initializationState =
                        initializationStatus.adapterStatusMap.values.firstOrNull()?.initializationState
                    if (initializationState == AdapterStatus.State.READY) {
                        _adsInitializationStatus.value = true
                    }
                }
            }
        } else return
    }


    private fun clearLibToAvoidMemoryLeak(event: Lifecycle.Event, activity: ComponentActivity) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            adMobRewardedPool.clearPool()
            adMobInterstitialPool.clearPool()
            adMobAppOpenPool.clearPool()
            activity.lifecycle.removeObserver(this)
            activityRef?.clear()
            activityRef = null
        } else return
    }
}
