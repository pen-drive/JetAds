package com.example.consumer.presentation

import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import leakcanary.LeakAssertions
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MemoryLeakTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)


    //todo need to be improved
    @Test
    fun testMemoryLeakOnActivityRecreation() {
        val scenario = activityRule.scenario

        scenario.moveToState(Lifecycle.State.RESUMED)
        scenario.recreate()
        scenario.moveToState(Lifecycle.State.RESUMED)
        scenario.close()

        LeakAssertions.assertNoLeaks()
    }
}

