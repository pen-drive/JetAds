package com.jet.ads.utils.retry

interface RetryPolicy {
    fun retry(retry: () -> Unit)
}

