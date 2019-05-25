package com.tapura.podmorecasts

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.matcher.ViewMatchers.withId

class ArrangeRobot {
    fun clickInSearchBar() {
        onView(withId(R.id.action_search)).perform(click())
    }

    fun typeInSearchBar(text: String) {
        onView(withId(android.support.design.R.id.search_src_text)).perform(typeText(text))
    }
}

inline fun arrange(block: ArrangeRobot.() -> Unit) = ArrangeRobot().apply(block)