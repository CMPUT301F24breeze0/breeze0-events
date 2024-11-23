package com.example.breeze0events;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingPolicies;
import androidx.test.espresso.assertion.ViewAssertions;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Tests for removing organization and entrant profiles.
 */
public class US030201 {
    private Context context;
    private OverallStorageController overallStorageController;
    private String mockOrganizerId;
    private String mockEntrantId;

    /**
     * Custom matcher to find list items with specific text.
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
    public void setUp() {
        // Initialize the context and storage controller
        context = ApplicationProvider.getApplicationContext();
        IdlingPolicies.setMasterPolicyTimeout(10, TimeUnit.SECONDS);

        // Create mock Organizer and Entrant
        Organizer mockOrganizer = new Organizer(
                "123",
                "Test Organizer",
                Arrays.asList("event1")
        );

        Entrant mockEntrant = new Entrant(
                "12345",
                "Test Entrant",
                "testentrant@gmail.com",
                "123456789",
                "profilePhotoUrl",
                "device123",
                Map.of("event1", "Test Event"),
                Map.of("event1", "Requested"),
                new ArrayList<>(),
                null
        );

        // Add Organizer and Entrant to the database
        overallStorageController = new OverallStorageController();
        mockOrganizerId = mockOrganizer.getOrganizerId();
        mockEntrantId = mockEntrant.getEntrantId();
        overallStorageController.addOrganizer(mockOrganizer);
        overallStorageController.addEntrant(mockEntrant);
    }

    @After
    public void tearDown() {
        // Clean up mock Organizer and Entrant from the database
        if (overallStorageController != null) {
            overallStorageController.deleteOrganizer(mockOrganizerId);
            overallStorageController.deleteEntrant(mockEntrantId);
        }
    }

    /**
     * Test removing an Organizer profile.
     */
    @Test
    public void testRemoveOrganizationProfile() {
        try (ActivityScenario<AdminOperateActivity> scenario = ActivityScenario.launch(AdminOperateActivity.class)) {
            // Open the organizer list
            Thread.sleep(4000); // Simulate delay
            onView(withId(R.id.organization)).perform(click());

            // Click on the specific organizer item
            onData(withItemContent("Test Organizer"))
                    .inAdapterView(withId(R.id.organizer_list_view))
                    .perform(click());

            // Perform the delete action
            onView(withId(R.id.delete)).perform(click());
            Thread.sleep(4000); // Wait for deletion

            // Assert the organizer is no longer displayed in the list
            onView(withText("Test Organizer")).check(ViewAssertions.doesNotExist());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Verify deletion from the database
        overallStorageController.getOrganizer(mockOrganizerId, new OrganizerCallback() {
            @Override
            public void onSuccess(Organizer organizer) {
                fail("Organizer was not deleted from the database.");
            }

            @Override
            public void onFailure(String errorMessage) {
                assertNull("Organizer successfully deleted from database", null);
            }
        });
    }

    /**
     * Test removing an Entrant profile.
     */
    @Test
    public void testRemoveEntrantProfile() {
        try (ActivityScenario<AdminOperateActivity> scenario = ActivityScenario.launch(AdminOperateActivity.class)) {
            // Open the entrant list
            Thread.sleep(4000); // Simulate delay
            onView(withId(R.id.entrant)).perform(click());

            // Click on the specific entrant item
            onData(withItemContent("Test Entrant"))
                    .inAdapterView(withId(R.id.entrant_list_view))
                    .perform(click());

            // Perform the delete action
            onView(withId(R.id.delete)).perform(click());
            Thread.sleep(4000); // Wait for deletion

            // Assert the entrant is no longer displayed in the list
            onView(withText("Test Entrant")).check(ViewAssertions.doesNotExist());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Verify deletion from the database
        overallStorageController.getEntrant(mockEntrantId, new EntrantCallback() {
            @Override
            public void onSuccess(Entrant entrant) {
                fail("Entrant was not deleted from the database.");
            }

            @Override
            public void onFailure(String errorMessage) {
                assertNull("Entrant successfully deleted from database", null);
            }
        });
    }
}
