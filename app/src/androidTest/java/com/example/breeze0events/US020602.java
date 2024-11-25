package com.example.breeze0events;

import android.content.Intent;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class US020602 {

    @Rule
    public ActivityTestRule<OrganizerSamplingActivity> activityRule =
            new ActivityTestRule<>(OrganizerSamplingActivity.class, true, false);

    private OrganizerSamplingActivity activity;
    private FirebaseFirestore db;
    private OverallStorageController overallStorageController;

    @Before
    public void setUp() throws InterruptedException {
        // Initialize Firestore instance
        db = FirebaseFirestore.getInstance();
        CountDownLatch latch = new CountDownLatch(1);

        // Create an Event object with eventId = "test_event_id"
        Event event = new Event();
        event.setEventId("test_event_id");
        event.setName("Sample Event");
        event.setQrCode("sample_qr_code");
        event.setPosterPhoto("sample_poster_photo");
        event.setFacility("Sample Facility");
        event.setStartDate("2023-12-01");
        event.setEndDate("2023-12-02");
        event.setLimitedNumber("100");
        ArrayList<String> entrants = new ArrayList<>();
        entrants.add("test_entrant_id");
        event.setEntrants(entrants);
        ArrayList<String> organizers = new ArrayList<>();
        organizers.add("organizer_id");
        event.setOrganizers(organizers);

        // Add the Event to Firestore
        overallStorageController = new OverallStorageController();
        overallStorageController.addEvent(event);

        // Add a sample Entrant with status "Rejected"
        Map<String, Object> entrantData = new HashMap<>();
        entrantData.put("name", "Rejected Entrant");
        entrantData.put("events", new HashMap<String, String>() {{
            put("test_event_id", "Sample Event");
        }});
        entrantData.put("status", new HashMap<String, String>() {{
            put("test_event_id", "Rejected");
        }});

        db.collection("EntrantDB").document("test_entrant_id").set(entrantData)
                .addOnSuccessListener(aVoid -> latch.countDown())
                .addOnFailureListener(e -> {
                    Log.e("RejectedListUITest", "Failed to add entrant", e);
                    latch.countDown();  // Proceed to avoid test hanging
                });

        // Wait for Firestore setup to complete
        latch.await();

        // Launch the OrganizerSamplingActivity with the event
        Intent intent = new Intent();
        intent.putExtra("selected_event", event);
        intent.putExtra("eventId", event.getEventId());
        activity = activityRule.launchActivity(intent);
    }

    @Test
    public void testRejectedListContainsEntrant() throws InterruptedException {
        // Wait for the activity to load the data
        Thread.sleep(2000); // Optional: Adjust this based on expected load time

        // Check if the rejectedList contains the entrant with status "Rejected"
        boolean isEntrantInRejectedList = false;
        for (String entrant : activity.entrantDisplayRejected) {
            if ("Rejected Entrant".equals(entrant)) {
                isEntrantInRejectedList = true;
                break;
            }
        }

        // Assert that the rejected list contains the expected entrant
        assertTrue("Rejected list should contain the entrant with status 'Rejected'", isEntrantInRejectedList);
    }

    @After
    public void tearDown() {
        // Clean up Firestore or other resources if necessary
        if (activity != null) {
            activity.finish();
        }

        // Delete test data from Firestore
        db.collection("OverallDB").document("test_event_id").delete()
                .addOnSuccessListener(aVoid -> Log.d("RejectedListUITest", "Test event deleted successfully"))
                .addOnFailureListener(e -> Log.e("RejectedListUITest", "Failed to delete test event", e));

        db.collection("EntrantDB").document("test_entrant_id").delete()
                .addOnSuccessListener(aVoid -> Log.d("RejectedListUITest", "Test entrant deleted successfully"))
                .addOnFailureListener(e -> Log.e("RejectedListUITest", "Failed to delete test entrant", e));
    }
}
