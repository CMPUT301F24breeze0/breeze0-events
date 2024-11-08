package com.example.breeze0events;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class US020201 {

    @Rule
    public ActivityTestRule<OrganizerMyListActivity> activityRule = new ActivityTestRule<>(OrganizerMyListActivity.class);

    @Before
    public void setUp() {
        // Initialize Intents to monitor activity launches
        Intents.init();
    }

    @After
    public void tearDown() {
        // Release Intents
        Intents.release();
    }

    @Test
    public void testUIElementsPresence() {
        // Verify that the buttons are displayed
        onView(withId(R.id.map_button)).check(matches(withText("Map")));
        onView(withId(R.id.my_facility_button)).check(matches(withText("My Facility")));
        onView(withId(R.id.new_event_button)).check(matches(withText("New")));
    }

    @Test
    public void testMapButtonLaunchesOrganizerMapActivity() {
        // Click the map button and verify if it launches OrganizerMapActivity
        onView(withId(R.id.map_button)).perform(click());
        intended(hasComponent(OrganizerMapActivity.class.getName()));
    }

    @Test
    public void testMyFacilityButtonLaunchesOrganizerFacilityActivity() {
        // Click the my_facility_button and verify if it launches OrganizerFacilityActivity
        onView(withId(R.id.my_facility_button)).perform(click());
        intended(hasComponent(OrganizerFacilityActivity.class.getName()));
    }

    @Test
    public void testNewEventButtonLaunchesOrganizerEventActivity() {
        // Click the new_event_button and verify if it launches OrganizerEventActivity
        onView(withId(R.id.new_event_button)).perform(click());
        intended(hasComponent(OrganizerEventActivity.class.getName()));
    }

    @Test
    public void testListItemClickLaunchesOrganizerEventInformationActivity() {
        // Assuming at least one item in the ListView, click it and verify intent to OrganizerEventInformationActivity
        onData(anything()).inAdapterView(withId(R.id.organizer_event_list)).atPosition(0).perform(click());
        intended(hasComponent(OrganizerEventInformationActivity.class.getName()));
    }
}
