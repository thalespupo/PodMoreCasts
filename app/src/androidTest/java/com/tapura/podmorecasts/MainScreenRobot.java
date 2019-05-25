package com.tapura.podmorecasts;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class MainScreenRobot {
    public MainScreenRobot clickInSearchBox() {
        onView(withId(R.id.action_search)).perform(click());
        return this;
    }

    public MainScreenRobot typeInSearchBox(String text) {
        onView(withId(android.support.design.R.id.search_src_text)).perform(typeText(text));

        return this;
    }

    public MainScreenRobot clickOkInSearchBox() {
        onView(withId(android.support.design.R.id.search_src_text)).perform().perform(pressImeActionButton());

        return this;
    }

    public MainScreenRobot wait(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return this;
    }
}
