package com.jet.ads.logging


import android.content.Context
import android.content.pm.ApplicationInfo
import android.util.Log

internal object Logger : ILogger {

    private var isDebugMode: Boolean = false
    private const val DEFAULT_TAG = "JetAds"

    override fun checkConsumerIsInDebugMode(context: Context) {
        isDebugMode = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    }

    override fun i(tag: String?, message: String) {
        if (isDebugMode) {
            Log.i(tag ?: DEFAULT_TAG, message)
        }
    }

    override fun e(tag: String?, message: String) {
        if (isDebugMode) {
            Log.e(tag ?: DEFAULT_TAG, message)
        }
    }

    override fun w(tag: String?, message: String) {
        if (isDebugMode) {
            Log.w(tag ?: DEFAULT_TAG, message)
        }
    }

    override fun d(tag: String?, message: String) {
        if (isDebugMode) {
            Log.d(tag ?: DEFAULT_TAG, message)
        }
    }

    override fun adRewarded(adId: String, rewardType: String, rewardAmount: Int) {
        i(adId, "User rewarded with $rewardAmount $rewardType for ad $adId.")
    }

    override fun adLoaded(adId: String) {
        i(adId, "Ad $adId loaded successfully.")
    }

    override fun adFailedToLoad(adId: String, error: String) {
        e(adId, "Failed to load ad $adId: $error")
    }

    override fun adDisplayed(adId: String) {
        i(adId, "Ad $adId displayed successfully.")
    }

    override fun adClosed(adId: String) {
        i(adId, "Ad $adId was closed.")
    }

    override fun adClicked(adId: String) {
        i(adId, "Ad $adId was clicked.")
    }

    override fun adImpressionRecorded(adId: String) {
        i(adId, "Impression recorded for ad $adId.")
    }

    override fun adFailedToShow(adId: String, error: String) {
        e(adId, "Failed to show ad $adId: $error")
    }
}
