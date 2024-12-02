package com.example.breeze0events;


import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
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

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import android.content.Intent;
import android.widget.ImageView;

import com.google.firebase.firestore.FirebaseFirestore;

/**
 * UI test for deleting a QR code in AdminQRcode activity.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class US030302 {
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
        IdlingPolicies.setMasterPolicyTimeout(5, TimeUnit.SECONDS);

        overallStorageController = new OverallStorageController();

        Event mockEvent = new Event(
                "Mock test ID",
                "Mock Event Name",
                "MockQRCodeValue",
                "mockPosterUrl",
                "MockFacility",
                "2024-11-11",
                "2025-11-11",
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
    public void testDeleteQRCode() throws InterruptedException {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AdminQRcode.class);
        intent.putExtra("qrcode", "MockQRCodeValue");
        intent.putExtra("eventId", "Mock test ID");
        ActivityScenario<AdminQRcode> scenario = ActivityScenario.launch(intent);

        Thread.sleep(3000);

        onView(withId(R.id.QRcode)).check(ViewAssertions.matches(isDisplayed()));

        onView(withId(R.id.delete_button)).perform(click());

        onView(withText("Delete"))
                .inRoot(isDialog())
                .check(ViewAssertions.matches(isDisplayed()))
                .perform(click());


        Thread.sleep(1000);

        onData(withItemContent("Mock Event Name"))
                .inAdapterView(withId(R.id.eventsList))
                .perform(click());

        Thread.sleep(1000);
        onView(withId(R.id.QRCodeButton)).perform(click());

        Thread.sleep(1000);
        onView(withId(R.id.QRcode)).check(ViewAssertions.matches(not(isDisplayed())));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("OverallDB").document("Mock test ID")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String qrCodeValue = task.getResult().getString("qrCode");
                        assertNull("QR code value should be null after deletion", qrCodeValue);
                    } else {
                        fail("Failed to fetch the event from Firestore");
                    }
                });

        Thread.sleep(1000);

        overallStorageController.getEvent(mockEventId, new EventCallback() {
            @Override
            public void onSuccess(Event event) {
                fail("QR code was not deleted from the database.");
            }

            @Override
            public void onFailure(String errorMessage) {
                assertNull("QR code successfully deleted from database", null);
            }
        });
    }
}
