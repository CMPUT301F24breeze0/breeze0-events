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
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingPolicies;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.assertion.ViewAssertions;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class US030201 {
    private Context context;
    private OverallStorageController overallStorageController;
    private String mockOrganizerId;
    private String mockEntrantId;

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
        // Get the application context for any operations requiring it
        context = ApplicationProvider.getApplicationContext();
        IdlingPolicies.setMasterPolicyTimeout(6, TimeUnit.SECONDS);
        Organizer mockorganizer = new Organizer(
                "123",
                "123",
                Arrays.asList("event1")
        );
        Entrant mockentrant= new Entrant(
                "12345",
                "good name",
                "123456@gmail.com",
                "123456789",
                "123456",
                "123456789",
                Map.of("entrantid","entrantName"),
                Map.of("entranId","entranName"),
                new ArrayList<>()

        );
        overallStorageController = new OverallStorageController();
        mockOrganizerId = mockorganizer.getOrganizerId();
        mockEntrantId=mockentrant.getEntrantId();
        overallStorageController.addOrganizer(mockorganizer);
        overallStorageController.addEntrant(mockentrant);
    }

    @After
    public void tearDown() {
        if (overallStorageController != null) {
            overallStorageController.deleteOrganizer(mockOrganizerId);
        }
    }

    @Test

    public void testRemoveOrganizationProfile() {
        try (ActivityScenario<AdminOperateActivity> scenario = ActivityScenario.launch(AdminOperateActivity.class)) {
            // Open the organizer list
            Thread.sleep(4000);
            onView(withId(R.id.organization)).perform(click());

            // Click on the first organizer item
            Thread.sleep(4000);
            onData(withItemContent("Organizer: 123"))
                    .inAdapterView(withId(R.id.organizer_list_view))
                    .perform(click());

            // Perform the delete action

            onView(withId(R.id.delete)).perform(click());
            Thread.sleep(4000);
            onView(withText("Organizer: 123")).check(ViewAssertions.doesNotExist());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        overallStorageController.getOrganizer(mockOrganizerId, new OrganizerCallback() {
            @Override
            public void onSuccess(Organizer organizer) {
                // Fail if the event still exists
                fail("Organizer was not deleted from the database.");
            }

            @Override
            public void onFailure(String errorMessage) {
                // Expecting failure here as the event should not exist
                assertNull("Organizer successfully deleted from database", null);
            }
        });
    }
    @Test
    public void testRemoveEntrantProfile() {
        try (ActivityScenario<AdminOperateActivity> scenario = ActivityScenario.launch(AdminOperateActivity.class)) {
            // Open the entrant list
            Thread.sleep(4000);
            onView(withId(R.id.entrant)).perform(click());
            Thread.sleep(4000);
            // Click on the first entrant item
            onData(withItemContent("Entrant: 12345"))
                    .inAdapterView(withId(R.id.entrant_list_view))
                    .perform(click());

            // Perform the delete action
            Thread.sleep(4000);
            onView(withId(R.id.delete)).perform(click());
            Thread.sleep(4000);
            onView(withText("Entrant: 12345")).check(ViewAssertions.doesNotExist());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}



