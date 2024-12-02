package com.example.breeze0events;

import android.content.Intent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.assertion.ViewAssertions;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class US020601 {

    @Rule
    public ActivityTestRule<OrganizerSamplingActivity> activityRule =
            new ActivityTestRule<>(OrganizerSamplingActivity.class, true, false);

    private OrganizerSamplingActivity activity;

    @Before
    public void setUp() {
        // Set up an intent with necessary extras
        Intent intent = new Intent();
        intent.putExtra("eventId", "testEventId");
        Event testEvent = new Event();
        testEvent.setEventId("testEventId");
        intent.putExtra("selected_event", testEvent);

        // Launch the activity
        activity = activityRule.launchActivity(intent);

        // Manually populate entrantDisplayList with sample data
        activity.runOnUiThread(() -> {
            activity.entrantDisplayRequested.add("John Doe");
            activity.entrantDisplayRequested.add("Jane Smith");
            activity.entrantDisplayRequested.add("Alex Johnson");

            // Notify the adapter that data has changed
            activity.entrantAdapterRequested.notifyDataSetChanged();
        });
    }

    @Test
    public void testEntrantDisplayListShowsItems() throws InterruptedException {
        Thread.sleep(2000);
        // Verify that each name is displayed in the ListView
        Espresso.onView(ViewMatchers.withId(R.id.organizer_sampling_activity_requested))
                .check(ViewAssertions.matches(ViewMatchers.hasDescendant(withText("John Doe"))));

        Espresso.onView(ViewMatchers.withId(R.id.organizer_sampling_activity_requested))
                .check(ViewAssertions.matches(ViewMatchers.hasDescendant(withText("Jane Smith"))));

        Espresso.onView(ViewMatchers.withId(R.id.organizer_sampling_activity_requested))
                .check(ViewAssertions.matches(ViewMatchers.hasDescendant(withText("Alex Johnson"))));
    }
}
