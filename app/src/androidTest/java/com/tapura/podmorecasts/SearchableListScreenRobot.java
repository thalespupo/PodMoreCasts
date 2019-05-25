package com.tapura.podmorecasts;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.greaterThan;

public class SearchableListScreenRobot {
    public SearchableListScreenRobot assertListGreaterThan(int i) {
        onView(withId(R.id.recycler_view_podcasts_discover_list)).check(new RecyclerViewItemCountAssertion(greaterThan(i)));

        return this;
    }
}
