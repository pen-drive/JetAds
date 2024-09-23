package com.jet.ads.utils.retry

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class DefaultRetryPolicy(private val dispatcher: CoroutineDispatcher = Dispatchers.IO) : RetryPolicy {

    private val adTimeout: Long = 60 * 60 * 1000
    private var retryCount = 0
    private val maxRetryCount = 5
    private val initialDelay = 2000L
    private val maxDelay = adTimeout

    override fun retry(retry: () -> Unit) {
        CoroutineScope(dispatcher).launch {
            var delayTime = initialDelay
            while (retryCount < maxRetryCount) {
                delay(delayTime)
                try {
                    retry()
                    retryCount = 0
                    break
                } catch (e: Exception) {
                    retryCount++
                    delayTime = (delayTime * 2).coerceAtMost(maxDelay)
                }
            }
        }
    }
}