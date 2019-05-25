package com.tapura.podmorecasts

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.pressImeActionButton
import android.support.test.espresso.matcher.ViewMatchers.withId

class ActRobot {
    fun clickOkInSearchBar() {
        onView(withId(android.support.design.R.id.search_src_text)).perform().perform(pressImeActionButton())
    }

    fun wait01(i: Long) {
        Thread.sleep(i)
    }
}

inline fun action(block: ActRobot.() -> Unit)= ActRobot().apply(block)
