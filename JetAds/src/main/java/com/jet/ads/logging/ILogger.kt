package com.jet.ads.logging

import android.content.Context



/**
 * Basic logs for consumer of the lib.
 *
 * */
interface ILogger {

    fun checkConsumerIsInDebugMode(context: Context)
    fun i(tag: String? = null, message: String)
    fun e(tag: String? = null, message: String)
    fun w(tag: String? = null, message: String)
    fun d(tag: String? = null, message: String)


    fun adRewarded(adId: String, rewardType: String, rewardAmount: Int)

    fun adLoaded(adId: String)
    fun adFailedToLoad(adId: String, error: String)
    fun adDisplayed(adId: String)
    fun adClosed(adId: String)

    fun adClicked(adId: String)
    fun adImpressionRecorded(adId: String)
    fun adFailedToShow(adId: String, error: String)

}
