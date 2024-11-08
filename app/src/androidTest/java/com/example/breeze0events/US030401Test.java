package com.example.breeze0events;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;
import static org.junit.Assert.assertTrue;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Intent;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.ArrayList;
import androidx.test.espresso.IdlingPolicies;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class US030401Test {

    private OverallStorageController overallStorageController;
    private String mockEventId;
    private String mockEventName = "Mock Event for Browse Test";

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
    public void setup() throws InterruptedException {
        IdlingPolicies.setMasterPolicyTimeout(5, TimeUnit.SECONDS);
        Intents.init();
        overallStorageController = new OverallStorageController();
        // ActivityScenario.launch(AdminEventActivity.class);

        Event mockEvent = new Event(
                "browse_test_event_id",
                "Browse test Event",
                "QRCode123",
                "mockPoster",
                "Mock Facility",
                "2024-11-07",
                "2024-11-08",
                "10",
                Arrays.asList("entrant1"),
                Arrays.asList("organizer1")
        );
        overallStorageController.addEvent(mockEvent);
        mockEventId = mockEvent.getEventId();
        verifyEventInDatabase();
    }


    private void verifyEventInDatabase() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);

        overallStorageController.getEvent(mockEventId, new EventCallback() {
            @Override
            public void onSuccess(Event event) {
                if (event != null && event.getEventId().equals(mockEventId)) {
                    latch.countDown(); 
                }
            }

            @Override
            public void onFailure(String errorMessage) {
            }
        });
        assertTrue("Failed to confirm event added to database", latch.await(5, TimeUnit.SECONDS));
    }

    @After
    public void tearDown() {
        overallStorageController.deleteEvent(mockEventId);
        Intents.release();
    }

    @Test
    public void testEventListDisplayedAndEventDetailsOpened() throws InterruptedException {
        ActivityScenario<AdminEventActivity> scenario = ActivityScenario.launch(AdminEventActivity.class);
        Thread.sleep(4000);
        onView(withId(R.id.eventsList))
                .check(ViewAssertions.matches(isDisplayed()));;

//        onData(anything())
//                .inAdapterView(withId(R.id.eventsList))
//                .atPosition(0)
//                .check(ViewAssertions.matches(isDisplayed()));

        onData(withItemContent("Browse test Event"))
                .inAdapterView(withId(R.id.eventsList))
                .check(ViewAssertions.matches(isDisplayed()));

//        Thread.sleep(70000);
//        overallStorageController.getEvent(mockEventId, new EventCallback() {
//            @Override
//            public void onSuccess(Event event) {
//                assertEquals("Retrieved event ID should match", mockEventId, event.getEventId());
//            }
//
//            @Override
//            public void onFailure(String errorMessage) {
//                fail("Failed to retrieve event from database: " + errorMessage);
//            }
//        });
    }
}