package com.jet.ads.admob.open_ad

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.junit.Before
import org.junit.Test

class AppLifecycleManagerTest {

    private lateinit var appLifecycleManager: AppLifecycleManager
    private lateinit var mockLifecycleOwner: LifecycleOwner
    private lateinit var mockLifecycle: Lifecycle
    private lateinit var mockCallback1: AppLifecycleCallback
    private lateinit var mockCallback2: AppLifecycleCallback

    @Before
    fun setup() {
        mockkObject(ProcessLifecycleOwner)
        mockLifecycleOwner = mockk(relaxed = true)
        mockLifecycle = mockk(relaxed = true)
        every { ProcessLifecycleOwner.get().lifecycle } returns mockLifecycle

        appLifecycleManager = AppLifecycleManager(mockLifecycle)

        mockCallback1 = mockk(relaxed = true)
        mockCallback2 = mockk(relaxed = true)
    }

    @Test
    fun `test initial state`() {
        assertThat(appLifecycleManager.isFirstEntry()).isTrue()
    }

    @Test
    fun `test setShowOnColdStart`() {
        appLifecycleManager.setShowOnColdStart(true)
        appLifecycleManager.setShowOnColdStart(false) // This should not change the state

        appLifecycleManager.registerCallback(mockCallback1)
        appLifecycleManager.notifyAdShown()

        verify(exactly = 1) { mockCallback1.onAppStart() }
    }

    @Test
    fun `test notifyAdShown when showOnFirstEntry is true`() {
        appLifecycleManager.setShowOnColdStart(true)
        appLifecycleManager.registerCallback(mockCallback1)

        appLifecycleManager.notifyAdShown()

        verify(exactly = 1) { mockCallback1.onAppStart() }
        assertThat(appLifecycleManager.isFirstEntry()).isFalse()
    }

    @Test
    fun `test notifyAdShown when showOnFirstEntry is false`() {
        appLifecycleManager.setShowOnColdStart(false)
        appLifecycleManager.registerCallback(mockCallback1)

        appLifecycleManager.notifyAdShown()

        verify(exactly = 0) { mockCallback1.onAppStart() }
        assertThat(appLifecycleManager.isFirstEntry()).isTrue()
    }

    @Test
    fun `test registerCallback and unregisterCallback`() {
        appLifecycleManager.registerCallback(mockCallback1)
        appLifecycleManager.registerCallback(mockCallback2)

        appLifecycleManager.setShowOnColdStart(true)
        appLifecycleManager.notifyAdShown()

        verify(exactly = 1) { mockCallback1.onAppStart() }
        verify(exactly = 1) { mockCallback2.onAppStart() }

        appLifecycleManager.unregisterCallback(mockCallback1)

        appLifecycleManager.onStart(mockLifecycleOwner)

        verify(exactly = 1) { mockCallback1.onAppStart() } // No additional call
        verify(exactly = 2) { mockCallback2.onAppStart() } // One more call
    }

    @Test
    fun `test onStart when hasFirstEntry is true`() {
        appLifecycleManager.registerCallback(mockCallback1)
        appLifecycleManager.setShowOnColdStart(true)
        appLifecycleManager.notifyAdShown()

        clearMocks(mockCallback1)

        appLifecycleManager.onStart(mockLifecycleOwner)

        verify(exactly = 1) { mockCallback1.onAppStart() }
    }


}