package com.example.breeze0events;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.filters.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.IdlingPolicies;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * UI test for deleting an event from the admin interface.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class US030101 {
    private OverallStorageController overallStorageController;
    private String mockEventId;

    /**
     * Matcher to find a list item with specific text.
     */
    public static Matcher<Object> withItemContent(final String text) {
        return new TypeSafeMatcher<Object>() {
            @Override
            public boolean matchesSafely(Object item) {
                return item.toString().contains(text);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("List item with text: " + text);
            }
        };
    }

    @Before
    public void setup() {
        // Set global timeout for idling policies
        IdlingPolicies.setMasterPolicyTimeout(6, TimeUnit.SECONDS);

        // Initialize OverallStorageController
        overallStorageController = new OverallStorageController();

        // Create and add a mock event
        Event mockEvent = new Event(
                "test_event_id_to_delete",
                "Test Event for Deletion",
                "QRCode123",
                "mockPosterUrl",
                "Mock Facility",
                "2023-11-07",
                "2023-11-08",
                "100",
                "false",
                Arrays.asList("entrant1"),
                Arrays.asList("organizer1")
        );
        overallStorageController.addEvent(mockEvent);
        mockEventId = mockEvent.getEventId();
    }

    @After
    public void tearDown() {
        // Ensure the mock event is removed from the database
        if (overallStorageController != null) {
            overallStorageController.deleteEvent(mockEventId);
        }
    }

    @Test
    public void testRemoveEvent() throws InterruptedException {
        // Launch the AdminEventActivity
        ActivityScenario<AdminEventActivity> scenario = ActivityScenario.launch(AdminEventActivity.class);

        // Wait for the database to load (use IdlingResource for production code)
        Thread.sleep(4000);

        // Verify the event list is displayed
        onView(withId(R.id.eventsList)).check(ViewAssertions.matches(isDisplayed()));

        // Select the mock event from the list
        onData(withItemContent("Test Event for Deletion"))
                .inAdapterView(withId(R.id.eventsList))
                .perform(click());

        // Verify event name is displayed
        onView(withId(R.id.EventName)).check(ViewAssertions.matches(isDisplayed()));

        // Test the "QR Code" button
        onView(withId(R.id.QRCodeButton)).perform(click());
        onView(withId(R.id.QRcode)).check(ViewAssertions.matches(isDisplayed()));
        Espresso.pressBack(); // Return to previous screen

        // Test the "Detail" button
        onView(withId(R.id.DetailButton)).perform(click());
        onView(withId(R.id.EventDetail)).check(ViewAssertions.matches(isDisplayed()));

        // Test the "Delete" button to remove the event
        onView(withId(R.id.Deletebutton)).perform(click());
        Espresso.pressBack(); // Return to the list view

        // Verify the event is no longer in the list
        onView(withId(R.id.eventsList))
                .check(ViewAssertions.matches(not(withText("Test Event for Deletion"))));

        // Wait to ensure database reflects changes
        Thread.sleep(5000);

        // Verify the event is removed from the database
        overallStorageController.getEvent(mockEventId, new EventCallback() {
            @Override
            public void onSuccess(Event event) {
                // Fail if the event still exists
                fail("Event was not deleted from the database.");
            }

            @Override
            public void onFailure(String errorMessage) {
                // Expecting failure here as the event should not exist
                assertNull("Event successfully deleted from database", null);
            }
        });
    }
}
