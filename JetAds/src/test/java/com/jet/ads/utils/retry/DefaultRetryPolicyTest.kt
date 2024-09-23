package com.jet.ads.utils.retry

import com.google.common.truth.Truth.assertThat
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.Test

import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultRetryPolicyTest {

    private var testDispatcher: TestDispatcher = StandardTestDispatcher()




    @Test
    fun `retry should stop after successful attempt`() = runTest(testDispatcher) {
        var attempts = 0
        var succeeded = false

            DefaultRetryPolicy(testDispatcher).retry {
            attempts++
            if (attempts == 3) {
                succeeded = true
            } else {
                throw Exception("Simulated failure")
            }
        }

        advanceUntilIdle()


        assertThat(3).isAtLeast(attempts)
        assert(succeeded)
    }

    @Test
    fun `retry should respect max retry count`() = runTest(testDispatcher) {
        var attempts = 0

        DefaultRetryPolicy(testDispatcher).retry {
            attempts++
            throw Exception("Simulated failure")
        }

        advanceUntilIdle()

        assertThat(5).isAtLeast(attempts)
    }


    @Test
    fun `retry should use exponential backoff`() = runTest(testDispatcher) {
        val delays = mutableListOf<Long>()
        var attempts = 0

        DefaultRetryPolicy(testDispatcher).retry {
            if (attempts > 0) {
                delays.add(currentTime.milliseconds.inWholeMilliseconds - delays.sum())
            }
            attempts++
            throw Exception("Simulated failure")
        }

        advanceUntilIdle()

        assertEquals(listOf(6000L, 8000L, 16000L, 32000L), delays)
    }

    @Test
    fun `retry should reset count after success`() = runTest(testDispatcher) {
        var firstRunAttempts = 0
        var secondRunAttempts = 0
        var phase = 1

        val retryPolicy =  DefaultRetryPolicy(testDispatcher)

        retryPolicy.retry {
            if (phase == 1) {
                firstRunAttempts++
                if (firstRunAttempts < 3) throw Exception("Simulated failure")
                phase = 2
            }
        }

        advanceUntilIdle()


        retryPolicy.retry {
            if (phase == 2) {
                secondRunAttempts++
                if (secondRunAttempts < 5) throw Exception("Simulated failure")
            }
        }

        advanceUntilIdle()

        assertEquals(3, firstRunAttempts)
        assertEquals(5, secondRunAttempts)
    }

    @Test
    fun `retry should respect max delay`() = runTest(testDispatcher) {
        val delays = mutableListOf<Long>()
        var attempts = 0

        DefaultRetryPolicy(testDispatcher).retry {
            if (attempts > 0) {
                delays.add(currentTime.milliseconds.inWholeMilliseconds - delays.sum())
            }
            attempts++
            throw Exception("Simulated failure")
        }

        advanceUntilIdle()

        assert(delays.last() <= 3600000)
    }
}