package com.example.breeze0events;

import android.content.Intent;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;

import com.google.firebase.firestore.DocumentSnapshot;
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class US020503 {

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
        CountDownLatch latch = new CountDownLatch(2);

        // Create an Event object with eventId = "6"
        Event event = new Event();
        event.setEventId("test_event_id");
        event.setName("Sample Event");
        event.setQrCode("sample_qr_code");
        event.setPosterPhoto("sample_poster_photo");
        event.setFacility("Sample Facility");
        event.setStartDate("2023-12-01");
        event.setEndDate("2023-12-02");
        event.setLimitedNumber("100");
        ArrayList<String> entrants=new ArrayList<>();
        entrants.add("test_entrant_id");
        event.setEntrants(entrants);
        ArrayList<String> organizers=new ArrayList<>();
        organizers.add("933f7579293bbf16");
        event.setOrganizers(organizers);

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

        db.collection("EntrantDB").document("test_entrant_id").set(entrantData)
                .addOnSuccessListener(aVoid -> latch.countDown())
                .addOnFailureListener(e -> {
                    Log.e("US020502", "Failed to add entrant", e);
                    latch.countDown();  // Proceed to avoid test hanging
                });

        latch.await();

        // Initialize Intent to launch OrganizerSamplingActivity
        Intent intent = new Intent();
        intent.putExtra("selected_event", event); // Pass the Event object as extra
        intent.putExtra("eventId", event.getEventId()); // Pass eventId separately if needed

        // Launch the activity with the intent
        activity = activityRule.launchActivity(intent);

        // Initialize activity parameters after launch
        activity.limitedNumber = 100;
        activity.requestedCount = 0;

    }

    @Test
    public void testPickNewApplicantsButtonClick() throws InterruptedException {
        // Perform a click on the button that triggers pickNewApplicants
        Thread.sleep(3000);

        // Check if joinedEntrants has elements
        assertFalse("joinedEntrants should not be empty", activity.joinedEntrants.isEmpty());
       
        // Verify that joinedEntrants has at least one entry with status "Requested"
        boolean hasJoinedEntrant = false;

        for (DocumentSnapshot entrant : activity.joinedEntrants) {
            Map<String, String> status = (Map<String, String>) entrant.get("status");
            if ("Joined".equals(status.get("test_event_id"))) {
                hasJoinedEntrant = true;
                Log.d("OrganizerSamplingTest", "Selected entrant: " + entrant.get("name"));
                break;  // Stop once we find a selected entrant
            }
        }

        // Assert that there is at least one selected entrant
        assertTrue("There should be at least one selected entrant", hasJoinedEntrant);
        Espresso.onView(ViewMatchers.withId(R.id.organizer_sampling_activity_pick_new_applicant_button))
                .perform(ViewActions.click());
        assertTrue("joinedEntrants should not be empty", activity.joinedEntrants.isEmpty());
    }

    @After
    public void tearDown() {
        // Clean up Firestore or other resources if necessary
        if (activity != null) {
            activity.finish();
        }

        // Clean up Firestore documents created during the test
        db.collection("OverallDB").document("test_event_id").delete()
                .addOnSuccessListener(aVoid -> Log.d("US020502", "Test event deleted successfully"))
                .addOnFailureListener(e -> Log.e("US020502", "Failed to delete test event", e));

        db.collection("EntrantDB").document("test_entrant_id").delete()
                .addOnSuccessListener(aVoid -> Log.d("US020502", "Test entrant deleted successfully"))
                .addOnFailureListener(e -> Log.e("US020502", "Failed to delete test entrant", e));
    }
}
