package com.jet.ads.common.controller

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class JetAdsAdsControlImplTest {

    @Before
    fun setup() {
        // Reset the state before each test
        JetAdsAdsControlImpl.setAdsEnabled(true)
    }

    @Test
    fun `areAdsEnabled returns true by default`() = runTest {
        val result = JetAdsAdsControlImpl.areAdsEnabled().first()
        assertThat(result).isTrue()
    }

    @Test
    fun `setAdsEnabled changes the state`() = runTest {
        JetAdsAdsControlImpl.setAdsEnabled(false)
        val result = JetAdsAdsControlImpl.areAdsEnabled().first()
        assertThat(result).isFalse()
    }

    @Test
    fun `multiple calls to setAdsEnabled update the state correctly`() = runTest {
        JetAdsAdsControlImpl.setAdsEnabled(false)
        assertThat(JetAdsAdsControlImpl.areAdsEnabled().first()).isFalse()

        JetAdsAdsControlImpl.setAdsEnabled(true)
        assertThat(JetAdsAdsControlImpl.areAdsEnabled().first()).isTrue()

        JetAdsAdsControlImpl.setAdsEnabled(false)
        assertThat(JetAdsAdsControlImpl.areAdsEnabled().first()).isFalse()
    }
}