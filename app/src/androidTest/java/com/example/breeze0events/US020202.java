package com.example.breeze0events;

import android.content.Intent;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.firestore.FirebaseFirestore;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class US020202 {

    @Rule
    public ActivityTestRule<OrganizerMapActivity> activityRule =
            new ActivityTestRule<>(OrganizerMapActivity.class, true, false);

    private OrganizerMapActivity activity;
    private FirebaseFirestore db;

    @Before
    public void setUp() throws InterruptedException {
        db = FirebaseFirestore.getInstance();
        CountDownLatch latch = new CountDownLatch(1);

        // Add a test entrant to EntrantDB
        String entrantId = "test_entrant_id";
        String eventId = "test_event_id";

        Map<String, Object> entrantData = new HashMap<>();
        entrantData.put("name", "Test Entrant");
        entrantData.put("Geolocation", new HashMap<String, Object>() {{
            put(eventId, new com.google.firebase.firestore.GeoPoint(53.5461, -113.4938)); // Edmonton coordinates
        }});

        db.collection("EntrantDB").document(entrantId).set(entrantData)
                .addOnSuccessListener(aVoid -> {
                    // Add a corresponding event in OverallDB
                    Map<String, Object> eventData = new HashMap<>();
                    eventData.put("eventId", eventId);
                    eventData.put("geolocation", "true");

                    db.collection("OverallDB").document(eventId).set(eventData)
                            .addOnSuccessListener(aVoid1 -> latch.countDown())
                            .addOnFailureListener(e -> {
                                Log.e("OrganizerMapUITest", "Failed to add event", e);
                                latch.countDown();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("OrganizerMapUITest", "Failed to add entrant", e);
                    latch.countDown();
                });

        // Wait for Firestore setup to complete
        latch.await();

        // Launch the OrganizerMapActivity
        Intent intent = new Intent();
        activity = activityRule.launchActivity(intent);
    }

    @Test
    public void testMapMarkerAdded() throws InterruptedException {
        // Wait for map markers to be added
        Thread.sleep(3000); // Adjust sleep time based on expected load time

        // Get the MapView from the activity
        MapView mapView = activity.findViewById(R.id.mapView);

        // Check if a marker with the expected title exists
        boolean markerExists = false;
        for (int i = 0; i < mapView.getOverlays().size(); i++) {
            if (mapView.getOverlays().get(i) instanceof Marker) {
                Marker marker = (Marker) mapView.getOverlays().get(i);
                System.out.println(marker.getTitle());
                if (marker.getTitle() != null && marker.getTitle().contains("Test Entrant joined test_event_id")) {
                    markerExists = true;
                    break;
                }
            }
        }

        // Assert that the marker exists
        assertTrue("Marker for the test entrant should exist on the map", markerExists);
    }

    @After
    public void tearDown() {
        // Clean up Firestore
        db.collection("EntrantDB").document("test_entrant_id").delete()
                .addOnSuccessListener(aVoid -> Log.d("OrganizerMapUITest", "Test entrant deleted successfully"))
                .addOnFailureListener(e -> Log.e("OrganizerMapUITest", "Failed to delete test entrant", e));

        db.collection("OverallDB").document("test_event_id").delete()
                .addOnSuccessListener(aVoid -> Log.d("OrganizerMapUITest", "Test event deleted successfully"))
                .addOnFailureListener(e -> Log.e("OrganizerMapUITest", "Failed to delete test event", e));

        if (activity != null) {
            activity.finish();
        }
    }
}
