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
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
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
        IdlingPolicies.setMasterPolicyTimeout(6, TimeUnit.SECONDS);

        overallStorageController = new OverallStorageController();

        Event mockEvent = new Event(
                "Mock test ID",
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
        if (overallStorageController != null) {
            overallStorageController.deleteEvent(mockEventId);
        }
    }

    @Test
    public void testRemoveEvent() throws InterruptedException {
        ActivityScenario<AdminEventActivity> scenario = ActivityScenario.launch(AdminEventActivity.class);

        // use IdlingResource for production code to wait database loading
        Thread.sleep(4000);

        onView(withId(R.id.eventsList)).check(ViewAssertions.matches(isDisplayed()));

        onData(withItemContent("Test Event for Deletion"))
                .inAdapterView(withId(R.id.eventsList))
                .perform(click());


        onView(withId(R.id.EventName)).check(ViewAssertions.matches(isDisplayed()));


        onView(withId(R.id.QRCodeButton)).perform(click());
        onView(withId(R.id.QRcode)).check(ViewAssertions.matches(isDisplayed()));
        Espresso.pressBack();


        onView(withId(R.id.DetailButton)).perform(click());
        onView(withId(R.id.EventDetail)).check(ViewAssertions.matches(isDisplayed()));


        onView(withId(R.id.Deletebutton)).perform(click());
        onView(withText("Delete"))
                .inRoot(isDialog())
                .check(ViewAssertions.matches(isDisplayed()))
                .perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.back_in_main)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.eventsList))
                .check(ViewAssertions.matches(not(withText("Test Event for Deletion"))));

        // Wait to ensure database reflects changes
        Thread.sleep(5000);


        overallStorageController.getEvent(mockEventId, new EventCallback() {
            @Override
            public void onSuccess(Event event) {
                fail("Event was not deleted from the database.");
            }

            @Override
            public void onFailure(String errorMessage) {
                assertNull("Event successfully deleted from database", null);
            }
        });
    }
}
