package com.example.breeze0events;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

import com.google.firebase.firestore.FirebaseFirestore;

@RunWith(AndroidJUnit4.class)
public class US020501 {

    // US020501: As an organizer I want to send a notification to chosen entrants to sign up for events.
    //This is the notification that they "won" the lottery.
    // latch
    @Rule
    public ActivityTestRule<OrganizerSamplingActivity> activityRule =
            new ActivityTestRule<>(OrganizerSamplingActivity.class, true, false);

    private OrganizerSamplingActivity activity;
    private FirebaseFirestore db;

    @Before
    public void setUp() throws InterruptedException {
        // Initialize Firestore instance
        db = FirebaseFirestore.getInstance();
        CountDownLatch latch = new CountDownLatch(2);

        // Create an Event object with eventId = "test_event_id"
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventId", "test_event_id");
        eventData.put("name", "Sample Event");
        eventData.put("qrCode", "sample_qr_code");
        eventData.put("posterPhoto", "sample_poster_photo");
        eventData.put("facility", "Sample Facility");
        eventData.put("startDate", "2023-12-01");
        eventData.put("endDate", "2023-12-02");
        eventData.put("limitedNumber", "100");
        eventData.put("entrants", new ArrayList<String>() {{
            add("test_entrant_id");
        }});
        eventData.put("organizers", new ArrayList<String>() {{
            add("933f7579293bbf16");
        }});

        // Add the Event to Firestore
        db.collection("OverallDB").document("test_event_id").set(eventData)
                .addOnSuccessListener(aVoid -> latch.countDown())
                .addOnFailureListener(e -> {
                    Log.e("US020502", "Failed to add event", e);
                    latch.countDown();  // Proceed to avoid test hanging
                });

        // Add a sample Entrant with status "Joined"
        Map<String, Object> entrantData = new HashMap<>();
        entrantData.put("name", "Test Entrant");
        entrantData.put("events", new HashMap<String, String>() {{
            put("test_event_id", "Sample Event");
        }});
        entrantData.put("status", new HashMap<String, String>() {{
            put("test_event_id", "Joined");
        }});
        entrantData.put("notifications", new ArrayList<Map<String, String>>());

        db.collection("EntrantDB").document("test_entrant_id").set(entrantData)
                .addOnSuccessListener(aVoid -> latch.countDown())
                .addOnFailureListener(e -> {
                    Log.e("US020502", "Failed to add entrant", e);
                    latch.countDown();  // Proceed to avoid test hanging
                });

        latch.await();

        // Initialize Intent to launch OrganizerSamplingActivity
        Intent intent = new Intent();
        intent.putExtra("eventId", "test_event_id");

        // Launch the activity with the intent
        activity = activityRule.launchActivity(intent);
    }

    @Test
    public void testNotificationUpdateAfterButtonClick() throws InterruptedException {
        // Perform the button click
        Espresso.onView(ViewMatchers.withId(R.id.organizer_sampling_activity_pick_new_applicant_button))
                .perform(ViewActions.click());

        // Wait for Firestore to complete updates
        CountDownLatch latch = new CountDownLatch(1);

        db.collection("EntrantDB").document("test_entrant_id").get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        ArrayList<Map<String, String>> notifications =
                                (ArrayList<Map<String, String>>) documentSnapshot.get("notifications");
                        assertTrue("Notifications should not be empty", notifications != null && !notifications.isEmpty());
                    } else {
                        fail("Entrant document not found");
                    }
                    latch.countDown();
                })
                .addOnFailureListener(e -> {
                    fail("Failed to retrieve entrant document: " + e.getMessage());
                    latch.countDown();
                });

        latch.await();  // Wait for Firestore updates to complete
    }

    @After
    public void tearDown() {
        // Clean up Firestore or other resources if necessary
        if (activity != null) {
            activity.finish();
        }

        // Clean up Firestore documents created during the test
        db.collection("OverallDB").document("test_event_id").delete()
                .addOnSuccessListener(aVoid -> Log.d("US020501", "Test event deleted successfully"))
                .addOnFailureListener(e -> Log.e("US020501", "Failed to delete test event", e));

        db.collection("EntrantDB").document("test_entrant_id").delete()
                .addOnSuccessListener(aVoid -> Log.d("US020501", "Test entrant deleted successfully"))
                .addOnFailureListener(e -> Log.e("US020501", "Failed to delete test entrant", e));
    }
}


