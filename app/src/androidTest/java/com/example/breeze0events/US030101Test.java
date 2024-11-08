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
import android.content.Intent;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
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
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class US030101Test {
    private OverallStorageController overallStorageController;
    private String mockEventId;

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
                "test_event_id_to_delete",
                "Test Event for Deletion",
                "QRCode123",
                "mockPosterUrl",
                "Mock Facility",
                "2023-11-07",
                "2023-11-08",
                "100",
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
    public void testRemoveEvent() throws InterruptedException { //

        ActivityScenario<AdminEventActivity> scenario = ActivityScenario.launch(AdminEventActivity.class);
        Thread.sleep(4000);
        // figure it out database showing slowly, giving time delay to solve this
        onView(withId(R.id.eventsList)).check(ViewAssertions.matches(isDisplayed()));


//        onData(anything())
//                .inAdapterView(withId(R.id.eventsList))
//                .atPosition(0)
//                .perform(click());

        // due to mock event randomly show in the listview i choose to use text
        onData(withItemContent("Test Event for Deletion"))
                .inAdapterView(withId(R.id.eventsList))
                .perform(click());

        // eventName
        onView(withId(R.id.EventName)).check(ViewAssertions.matches(isDisplayed()));

        // QR Code test
        onView(withId(R.id.QRCodeButton)).perform(click());

        onView(withId(R.id.QRcode)).check(ViewAssertions.matches(isDisplayed()));

        Espresso.pressBack();

        // "Detail" button test
        onView(withId(R.id.DetailButton)).perform(click());

        onView(withId(R.id.EventDetail)).check(ViewAssertions.matches(isDisplayed()));

        // "Delete" button to remove the event test
        onView(withId(R.id.Deletebutton)).perform(click());

        Espresso.pressBack();

        // test delete successful or not
        onView(withId(R.id.eventsList))
                .check(ViewAssertions.matches(not(withText("Test Event for Deletion"))));

        Thread.sleep(5000);
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
