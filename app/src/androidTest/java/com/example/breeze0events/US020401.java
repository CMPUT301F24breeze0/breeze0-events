package com.example.breeze0events;

import android.content.Intent;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.intent.Intents;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;

@RunWith(AndroidJUnit4.class)
public class US020401 {

    @Before
    public void setUp() {
        // Initialize Espresso Intents to monitor Intent calls during tests
        Intents.init();
        // Launch the OrganizerEventActivity before each test case
        ActivityScenario.launch(OrganizerEventActivity.class);
    }

    @After
    public void tearDown() {
        // Release Intents after each test case to prevent interference with other tests
        Intents.release();
    }

    @Test
    public void testGenerateQRFillFieldsSelectFacilityAndAddEvent() {
        // Launch OrganizerEventActivity and assign a reference to the scenario
        ActivityScenario<OrganizerEventActivity> scenario = ActivityScenario.launch(OrganizerEventActivity.class);

        // Step 1: Set the event ID using onActivity to ensure it's done on the main thread
        scenario.onActivity(activity -> {
            // Set the TextView for event ID
            TextView textView = activity.findViewById(R.id.organizer_edit_event_activity_id);
            textView.setText("1"); // Set event ID to "1"

            try {
                // Access and modify the private field 'eventFacility' using reflection
                Field eventFacilityField = OrganizerEventActivity.class.getDeclaredField("eventFacility");
                eventFacilityField.setAccessible(true); // Make the field accessible
                eventFacilityField.set(activity, "1"); // Set eventFacility to "1"
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        // Step 2: Fill in the event details
        onView(withId(R.id.event_name_bar)).perform(typeText("Sample Event")); // Enter event name
        onView(withId(R.id.event_start_date_bar)).perform(typeText("2023-12-01")); // Enter start date
        onView(withId(R.id.event_end_date_bar)).perform(typeText("2023-12-10")); // Enter end date
        onView(withId(R.id.entrants_bar)).perform(typeText("100")); // Enter the number of entrants

        // Step 3: Click the "Add" button to save the event
        onView(withId(R.id.organizer_edit_event_activity_add_button)).perform(click());
        OverallStorageController controller = new OverallStorageController();
        controller.getEvent("1", new EventCallback() {
            @Override
            public void onSuccess(Event event) {

            }

            @Override
            public void onFailure(String errorMessage) {
                // Trigger a failure in the test if event retrieval fails
                fail("Failed to retrieve event: " + errorMessage);
            }
        });
    }

    @Test
    public void testUploadPosterButtonOpensGallery() {
        // Test that clicking the "Upload Poster" button triggers an Intent to open the gallery

        // Step 1: Click the "Upload Poster" button
        onView(withId(R.id.organizer_edit_event_activity_poster_upload_button)).perform(click());

        // Step 2: Verify that the correct Intent to open the gallery is triggered
        intended(hasAction(Intent.ACTION_PICK)); // Checks that an Intent with action ACTION_PICK was sent
    }

    @Test
    public void testBackButtonIsClickable() {
        // Test that clicking the "Back" button performs the back navigation

        // Step 1: Click the "Back" button
        onView(withId(R.id.organizer_edit_event_activity_back_button)).perform(click());

        // Note: Optionally, you could add a verification step to ensure the activity closes or navigates back
    }
}
