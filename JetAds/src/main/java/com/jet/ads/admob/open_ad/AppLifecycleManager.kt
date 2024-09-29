package com.jet.ads.admob.open_ad

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner

/**
 *  THIS MUST BE A SINGLETON OBJECT!!!!
 * */
class AppLifecycleManager(
    private val lifecycle: Lifecycle = ProcessLifecycleOwner.get().lifecycle
) : DefaultLifecycleObserver {

    private val callbacks: MutableList<AppLifecycleCallback> = mutableListOf()
    private var hasFirstEntry = false
    private var showOnFirstEntry: Boolean = false
    private var entries = 0

    init {
        lifecycle.addObserver(this)
    }

    fun setShowOnColdStart(showOnFirstEntry: Boolean) {
        if (entries > 0) return
        this.showOnFirstEntry = showOnFirstEntry
        entries++
    }

    fun isFirstEntry(): Boolean = !hasFirstEntry


    fun notifyAdShown() {
        if (showOnFirstEntry && !hasFirstEntry) {
            hasFirstEntry = true
            callbacks.forEach { it.onAppStart() }
        }
    }

    fun registerCallback(callback: AppLifecycleCallback) {
        callbacks.add(callback)
    }

    fun unregisterCallback(callback: AppLifecycleCallback) {
        callbacks.remove(callback)
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)

        callbacks.forEach { it.onAppStart() }
    }

}
