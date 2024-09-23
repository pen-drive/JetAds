package com.example.consumer.presentation

import android.app.Activity

sealed interface MainScreenEvents {

    data class ShowInterstitial(val adId: String, val activity: Activity): MainScreenEvents
    data class RewardedInterstitial(val adId: String, val activity: Activity): MainScreenEvents
    object ToggleAds: MainScreenEvents

}