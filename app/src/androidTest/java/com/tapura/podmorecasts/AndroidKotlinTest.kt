package com.tapura.podmorecasts

import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AndroidKotlinTest {

    @get:Rule
    var rule = ActivityTestRule(SplashActivity::class.java)

    @Test
    @Throws(InterruptedException::class)
    fun whenSearching_shouldShowResultsCorrectlyTestRobot() {
        arrange {
            clickInSearchBar()
            typeInSearchBar("Nerdcast")
        }

        action {
            clickOkInSearchBar()
            wait01(3000L)
        }

        assert {
            assertListGreaterThan(0)
        }
    }

    @Test
    @Throws(InterruptedException::class)
    fun whenSearching_shouldShowResultsCorrectlyTestRobotJake() {
        mainScreen {
            clickInSearchBox()
            typeInSearchBox("Nerdcast")
            clickOkInSearchBox()
            wait(3000)
        }

        SearchableListScreenRobot()
                .assertListGreaterThan(0)
    }
}

inline fun mainScreen(block: MainScreenRobot.() -> Unit) = MainScreenRobot().apply(block)




