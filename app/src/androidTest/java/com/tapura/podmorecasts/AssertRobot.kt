package com.tapura.podmorecasts

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.matcher.ViewMatchers.withId
import org.hamcrest.Matchers.greaterThan

class AssertRobot {
    fun assertListGreaterThan(i: Int) {
        onView(withId(R.id.recycler_view_podcasts_discover_list)).check(RecyclerViewItemCountAssertion(greaterThan<Int>(0)))

    }
}
inline fun assert(block: AssertRobot.() -> Unit)= AssertRobot().apply(block)

