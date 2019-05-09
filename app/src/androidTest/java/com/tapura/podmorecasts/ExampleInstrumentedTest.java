package com.tapura.podmorecasts;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Rule
    public ActivityTestRule<SplashActivity> rule = new ActivityTestRule<>(SplashActivity.class);

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.tapura.podmorecasts", appContext.getPackageName());
    }

    @Test
    public void whenSearching_shouldShowResultsCorrectly() throws InterruptedException {
        onView(withId(R.id.action_search)).perform(click());
        onView(withId(android.support.design.R.id.search_src_text)).perform(typeText("Nerdcast"));
        onView(withId(android.support.design.R.id.search_src_text)).perform().perform(pressImeActionButton());
        Thread.sleep(3000);
        onView(withId(R.id.recycler_view_podcasts_discover_list)).check(new RecyclerViewItemCountAssertion(greaterThan(0)));
    }
}
