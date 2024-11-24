package com.example.breeze0events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class US020604 {

    FirebaseFirestore db;
    String testEventId = "test_event_id";

    @Before
    public void setUp() throws Exception {
        // Initialize Firebase and Firestore
        Context context = ApplicationProvider.getApplicationContext();
        FirebaseApp.initializeApp(context);
        db = FirebaseFirestore.getInstance();

        // Add a test event to the Firestore database
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventId", testEventId);
        eventData.put("name", "Test Event");
        eventData.put("limitedNumber", "10");
        db.collection("OverallDB").document(testEventId).set(eventData);

        // Add entrants with "Requested" status for the test event
        for (int i = 1; i <= 3; i++) {
            Map<String, Object> entrantData = new HashMap<>();
            entrantData.put("name", "Entrant " + i);
            Map<String, String> statusMap = new HashMap<>();
            statusMap.put(testEventId, "Requested");
            entrantData.put("status", statusMap);

            db.collection("EntrantDB").document("entrant_" + i).set(entrantData);
        }
    }

    @Test
    public void testCancelButtonUpdatesStatusToRejected() throws InterruptedException {
        // Prepare an Intent to launch the OrganizerSamplingActivity
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), OrganizerSamplingActivity.class);
        intent.putExtra("eventId", testEventId);
        intent.putExtra("selected_event", new Event(testEventId, "Test Event", null, null, null, null, null, "10", null, null, null));

        // Launch the activity using the prepared Intent
        try (ActivityScenario<OrganizerSamplingActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(activity -> {
                // Simulate clicking the Cancel button
                activity.findViewById(R.id.cancel_button).performClick();
            });
        }

        // Verify that all entrants with "Requested" status are updated to "Rejected"
        db.collection("EntrantDB").get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot document : querySnapshot) {
                        Map<String, String> statusMap = (Map<String, String>) document.get("status");
                        if (statusMap != null && statusMap.containsKey(testEventId)) {
                            String status = statusMap.get(testEventId);
                            assertEquals("Rejected", status); // Ensure status is updated to "Rejected"
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    throw new AssertionError("Failed to verify entrant status updates: " + e.getMessage());
                });

    }

    @After
    public void tearDown() throws Exception {
        // Remove test data from Firestore
        db.collection("OverallDB").document(testEventId).delete();
        for (int i = 1; i <= 3; i++) {
            db.collection("EntrantDB").document("entrant_" + i).delete();
        }
    }
}
