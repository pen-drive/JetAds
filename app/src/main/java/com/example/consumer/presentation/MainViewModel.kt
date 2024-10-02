package com.example.consumer.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.rewarded.RewardedAd
import com.jet.ads.common.callbacks.ShowAdCallBack
import com.jet.ads.common.interstitial.InterstitialsController
import com.jet.ads.common.interstitial.InterstitialsControllerFactory
import com.jet.ads.common.rewarded.RewardedControllerFactory
import com.jet.ads.common.controller.JetAdsAdsControlImpl
import com.jet.ads.common.rewarded.RewardsController
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


    private val interstitialsController: InterstitialsController = InterstitialsControllerFactory.admobController()
    private val rewardsController: RewardsController = RewardedControllerFactory.admobController()

    init {
        viewModelScope.launch {

            launch {
                interstitialsController.loadedAds().collect { list ->
                    _interstitialsLoaded.value = list
                }
            }
            launch {
                rewardsController.loadedAds().collect { list ->
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
                rewardsController.show(event.adId, event.activity,
                    ShowAdCallBack( //<-- you can use callbacks
                    onAdClicked = {
                        // handle onAdClicked
                    },
                    onAdDismissed = {},
                    onAdFailedToShow = {},
//                    onAdImpression = {},
                    onAdShowed = {}
                ),
                onRewarded = { rewardedItem ->
                    // handle onRewarded
                })
            }

            is MainScreenEvents.ShowInterstitial -> {
                interstitialsController.show(event.adId, event.activity,
                    callback = ShowAdCallBack( //<-- you can use callbacks
                    onAdClicked = {
                        // handle onAdClicked
                    },
                    onAdDismissed = {},
                    onAdFailedToShow = {},
//                    onAdImpression = {},
                    onAdShowed = {}
                ))
            }

            MainScreenEvents.ToggleAds -> {
                JetAdsAdsControlImpl.setAdsEnabled(!_isToShowAdds.value)
            }
        }
    }

}