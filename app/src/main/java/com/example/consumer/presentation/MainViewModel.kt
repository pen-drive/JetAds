package com.example.consumer.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.rewarded.RewardedAd
import com.jet.ads.common.interstitial.Interstitials
import com.jet.ads.common.interstitial.InterstitialsFactory
import com.jet.ads.common.rewarded.RewardedFactory
import com.jet.ads.common.callbacks.ShowAdCallBack
import com.jet.ads.common.controller.JetAdsAdsControlImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _isToShowAdds = MutableStateFlow(true)
    val isToShowAdds = _isToShowAdds.asStateFlow()

    private val _interstitialsLoaded =
        MutableStateFlow<List<Pair<String, Pair<InterstitialAd, Long>>>>(emptyList())
    val interstitialsLoaded = _interstitialsLoaded.asStateFlow()

    private val _rewardedLoaded =
        MutableStateFlow<List<Pair<String, Pair<RewardedAd, Long>>>>(emptyList())
    val rewardedLoaded = _rewardedLoaded.asStateFlow()


    private val interstitials: Interstitials = InterstitialsFactory.admobInterstitial()
    private val rewarded = RewardedFactory.admobRewarded()

    init {
        viewModelScope.launch {

            launch {
                interstitials.loadedAds().collect { list ->
                    _interstitialsLoaded.value = list
                }
            }
            launch {
                rewarded.loadedAds().collect { list ->
                    _rewardedLoaded.value = list
                }
            }

            launch {
                // If u have used other AdsControl use it.
                JetAdsAdsControlImpl.areAdsEnabled().collect {
                    Log.d("AAA", "Controll : $it")
                    _isToShowAdds.value = it
                }
            }
        }
    }


    /**
     *  ATTENTION! You are passing the Activity to the ViewModel. This is safe as long as you
     *  don't store the Activity instance in any field within the ViewModel.
     *  The Activity should only be used temporarily for immediate actions, such as displaying ads.
     *  You are not required to pass the Activity this way; alternatively, you can handle
     *  these operations directly within the screen (Composable).
     * */
    fun onEvent(event: MainScreenEvents) {
        when (event) {
            is MainScreenEvents.RewardedInterstitial -> {
                rewarded.show(event.adId, event.activity) {

                }
            }

            is MainScreenEvents.ShowInterstitial -> {
                interstitials.show(event.adId, event.activity)
            }

            MainScreenEvents.ToggleAds -> {
                JetAdsAdsControlImpl.setAdsEnabled(!_isToShowAdds.value)
            }
        }
    }

}