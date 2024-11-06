package com.example.breeze0events;

import android.content.Intent;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.action.ViewActions;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.action.ViewActions.click;
import static org.hamcrest.Matchers.anything;

@RunWith(AndroidJUnit4.class)
public class OrganizerFacilityActivityTest {

    @Rule
    public ActivityScenarioRule<OrganizerFacilityActivity> activityRule =
            new ActivityScenarioRule<>(OrganizerFacilityActivity.class);

    @Test
    public void testBackButtonIsClickable() {
        // Test if the "Back" button is clickable
        onView(withId(R.id.organizer_facility_activity_back_button)).perform(click());
    }

    @Test
    public void testNewFacilityButtonIsClickable() {
        // Test if the "New Facility" button is clickable
        onView(withId(R.id.new_facility_button)).perform(click());
    }

    @Test
    public void testFacilityListItemIsClickable() {
        // Assume there is at least one item in the ListView for this test
        onData(anything()).inAdapterView(withId(R.id.organizer_facility_list)).atPosition(0).perform(click());
    }

    @Test
    public void testFacilityListItemLongClick() {
        // Assume there is at least one item in the ListView for this test
        onData(anything()).inAdapterView(withId(R.id.organizer_facility_list)).atPosition(0).perform(ViewActions.longClick());
    }
}
