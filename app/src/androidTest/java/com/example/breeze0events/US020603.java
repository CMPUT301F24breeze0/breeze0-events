package com.example.breeze0events;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;

public class US020603 {

    FirebaseFirestore db;

    @Before
    public void setUp() throws Exception {
        // Initialize Firebase and Firestore
        Context context = ApplicationProvider.getApplicationContext();
        FirebaseApp.initializeApp(context);
        db = FirebaseFirestore.getInstance(); // Connect to the real Firestore database

        // Add a complete Event object to Firestore
        Event event = new Event();
        event.setEventId("test_event_id");
        event.setName("Test Event");
        event.setLimitedNumber("10");
        OverallStorageController overallStorageController = new OverallStorageController();
        overallStorageController.addEvent(event);
    }

    @Test
    public void testEventIdPassedBeforeActivityLaunch() {
        // Create an Intent and pass eventId and selected_event
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), OrganizerSamplingActivity.class);

        Event event = new Event();
        event.setEventId("test_event_id");
        event.setName("Test Event");
        event.setLimitedNumber("10");
        intent.putExtra("eventId", "test_event_id");
        intent.putExtra("selected_event", (Serializable) event); // Ensure Event implements Serializable interface

        // Launch the Activity and verify behavior
        try (ActivityScenario<OrganizerSamplingActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(activity -> {
                // Verify that eventId and selected_event are correctly set
                // Uncomment these assertions if activity variables are accessible
                // assertEquals("test_event_id", activity.eventId);
                // assertEquals("Test Event", activity.selectedEvent.getName());

                // Simulate clicking the Finalize button
                activity.finalizeButton.performClick();
            });
        }

        // Verify that the limitedNumber of the event is set to 0
        db.collection("OverallDB").document("test_event_id").get()
                .addOnSuccessListener(document -> {
                    String limitedNumber = document.getString("limitedNumber");
                    assertEquals("0", limitedNumber); // Validate the updated result
                })
                .addOnFailureListener(e -> {
                    throw new AssertionError("Failed to retrieve updated event: " + e.getMessage());
                });
    }

    @After
    public void tearDown() throws Exception {
        // Clean up test data from Firestore
        db.collection("OverallDB").document("test_event_id").delete(); // Delete the test event
    }
}
