package com.example.breeze0events;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.action.ViewActions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.action.ViewActions.click;

@RunWith(AndroidJUnit4.class)
public class OrganizerEditEventActivityTest {

    @Before
    public void setUp() {
        // Launch OrganizerEditEventActivity before each test
        ActivityScenario.launch(OrganizerEditEventActivity.class);
    }

    // Test if the upload poster button is clickable
    @Test
    public void testUploadPosterButtonClickable() {
        onView(withId(R.id.organizer_edit_event_activity_poster_upload_button))
                .perform(click());  // Perform click action on the upload poster button
    }

    // Test if the save button is clickable
    @Test
    public void testSaveButtonClickable() {
        onView(withId(R.id.organizer_edit_event_activity_add_button))
                .perform(click());  // Perform click action on the save button
    }

    // Test if the back button is clickable
    @Test
    public void testBackButtonClickable() {
        onView(withId(R.id.organizer_edit_event_activity_back_button))
                .perform(click());  // Perform click action on the back button
    }

    // Test if the select facility button is clickable
    @Test
    public void testSelectFacilityButtonClickable() {
        onView(withId(R.id.organizer_event_activity_facility_button))
                .perform(click());  // Perform click action on the select facility button
    }
}
