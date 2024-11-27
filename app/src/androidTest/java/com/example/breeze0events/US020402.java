package com.example.breeze0events;

import android.content.Intent;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.GeneralClickAction;
import androidx.test.espresso.action.GeneralLocation;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Tap;
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
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;

@RunWith(AndroidJUnit4.class)
public class US020402 {

    private static final String MOCK_EVENT_ID = "1";
    private static final String UPDATED_EVENT_NAME = "Updated Event Name";
    private static final String UPDATED_START_DATE = "2024-01-01";
    private static final String UPDATED_END_DATE = "2024-01-10";
    private static final String UPDATED_ENTRANTS = "200";
    private static final String MOCK_EVENT_FACILITY = "Updated Facility";

    @Before
    public void setUp() {
        // Initialize Espresso Intents to monitor Intent calls during tests
        Intents.init();
        // Launch OrganizerEditEventActivity before each test case with a mock event ID
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.putExtra("event_id", MOCK_EVENT_ID);
        ActivityScenario.launch(OrganizerEditEventActivity.class);
    }

    @After
    public void tearDown() {
        // Release Intents after each test case to prevent interference with other tests
        Intents.release();
    }

    @Test
    public void testUpdateEventDetailsSuccessfully() {
        // Launch OrganizerEditEventActivity and assign a reference to the scenario
        ActivityScenario<OrganizerEditEventActivity> scenario = ActivityScenario.launch(OrganizerEditEventActivity.class);

        // Step 1: Set the event ID and facility using reflection (mocked for the test)
        scenario.onActivity(activity -> {
            // Set the TextView for event ID
            TextView textView = activity.findViewById(R.id.organizer_edit_event_activity_id);
            textView.setText(MOCK_EVENT_ID); // Set event ID to "1"

            try {
                // Access and modify the private field 'eventFacility' using reflection
                Field eventFacilityField = OrganizerEditEventActivity.class.getDeclaredField("eventFacility");
                eventFacilityField.setAccessible(true); // Make the field accessible
                eventFacilityField.set(activity, MOCK_EVENT_FACILITY); // Set eventFacility to the mock facility
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        // Step 2: Update event details
        onView(withId(R.id.event_name_bar)).perform(typeText(UPDATED_EVENT_NAME)); // Enter updated event name
        // Simulate a click on the start date bar and select a date from the calendar
        onView(withId(R.id.entrants_bar)).perform(typeText(UPDATED_ENTRANTS)); // Enter updated number of entrantsrm(click()); // Simulate a click on the entrants bar
        onView(withId(R.id.event_end_date_bar)).perform(click()); // Click the start date field

/*
        // Step 3: Click the "Save" button to save updates
        onView(withId(R.id.organizer_edit_event_activity_add_button)).perform(click());

        // Step 4: Verify that the event details were saved by attempting to retrieve it from storage
        OverallStorageController controller = new OverallStorageController();
        controller.getEvent(MOCK_EVENT_ID, new EventCallback() {
            @Override
            public void onSuccess(Event event) {
                // Check that the event details match the updates
                if (!event.getName().equals(UPDATED_EVENT_NAME) ||
                        !event.getStartDate().equals(UPDATED_START_DATE) ||
                        !event.getEndDate().equals(UPDATED_END_DATE) ||
                        !event.getLimitedNumber().equals(UPDATED_ENTRANTS)) {
                    fail("Event details do not match the expected updated values");
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                // Trigger a failure in the test if event retrieval fails
                fail("Failed to retrieve updated event: " + errorMessage);
            }
        });*/
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

        // Optionally, you could add a verification step to ensure the activity closes or navigates back
    }
}
