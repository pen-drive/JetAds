package com.jet.ads.admob.banner

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jet.ads.common.controller.AdsControl
import com.jet.ads.common.controller.ControlProvider
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AdaptiveBannerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockAdsControl: AdsControl
    private lateinit var mockControlProvider: ControlProvider
    private lateinit var adsEnabledFlow: MutableStateFlow<Boolean>

    @Before
    fun setup() {
        mockAdsControl = mockk(relaxed = true)
        mockControlProvider = mockk(relaxed = true)
        adsEnabledFlow = MutableStateFlow(true)

        every { mockAdsControl.areAdsEnabled() } returns adsEnabledFlow
        every { mockControlProvider.getAdsControl() } returns mockAdsControl

        mockkObject(ControlProvider)
        every { ControlProvider.getAdsControl() } returns mockAdsControl
    }

    @Test
    fun whenAdsEnabled_adViewIsDisplayed() {
        composeTestRule.setContent {
            AdaptiveBanner(adUnit = "test_ad_unit")
        }

        composeTestRule.onNodeWithTag("AdView").assertExists()
    }

    @Test
    fun whenAdsDisabled_adViewIsNotDisplayed() {
        adsEnabledFlow.value = false

        composeTestRule.setContent {
            AdaptiveBanner(adUnit = "test_ad_unit")
        }

        composeTestRule.onNodeWithTag("AdView").assertDoesNotExist()
    }

    @Test
    fun whenInPreviewMode_bannerPreviewIsDisplayed() {
        composeTestRule.setContent {
            CompositionLocalProvider(LocalInspectionMode provides true) {
                AdaptiveBanner(adUnit = "test_ad_unit")
            }
        }

        composeTestRule.onNodeWithText("JetAds Adaptive banner preview.").assertExists()
    }

    @Test
    fun safeTopMarginIsApplied() {
        val testMargin = 20.dp

        composeTestRule.setContent {
            AdaptiveBanner(adUnit = "test_ad_unit", safeTopMarginDp = testMargin)
        }

        composeTestRule.onNodeWithTag("SafeTopMargin")
            .assertExists()
            .assertHeightIsEqualTo(testMargin)
    }
}