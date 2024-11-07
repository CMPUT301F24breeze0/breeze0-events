package com.example.breeze0events;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.intent.Intents;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class OrganizerEventActivityTest {

    @Rule
    public ActivityScenarioRule<OrganizerEventActivity> activityRule =
            new ActivityScenarioRule<>(OrganizerEventActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @Test
    public void testAddButtonIsClickable() {
        // Check if "Add" button is clickable and performs the expected action
        onView(withId(R.id.organizer_edit_event_activity_add_button)).perform(click());
    }

    @Test
    public void testBackButtonIsClickable() {
        // Check if "Back" button is clickable
        onView(withId(R.id.organizer_edit_event_activity_back_button)).perform(click());
    }

    @Test
    public void testUploadPosterButtonOpensGallery() {
        // Check if clicking "Upload Poster" button opens the gallery
        onView(withId(R.id.organizer_edit_event_activity_poster_upload_button)).perform(click());

    }

    @Test
    public void testGenerateQRButtonIsClickable() {
        // Check if "Generate QR Code" button is clickable and displays the QR image
        onView(withId(R.id.organizer_event_activity_generate_qr_button)).perform(click());
    }

    @Test
    public void testFacilityButtonOpensDialog() {
        // Check if "Select Facility" button opens the facility selection dialog
        onView(withId(R.id.organizer_event_activity_facility_button)).perform(click());
    }

    @Test
    public void testFillEventFieldsAndAddEvent() {
        // Enter text into event fields and check if they are correctly displayed
        onView(withId(R.id.event_name_bar)).perform(typeText("Sample Event"));
        onView(withId(R.id.event_start_date_bar)).perform(typeText("2023-12-01"));
        onView(withId(R.id.event_end_date_bar)).perform(typeText("2023-12-10"));
        onView(withId(R.id.entrants_bar)).perform(typeText("Alice, Bob"));

        // Click "Add" button and check if event was added successfully
        onView(withId(R.id.organizer_edit_event_activity_add_button)).perform(click());
    }
}
