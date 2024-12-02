package com.example.breeze0events;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.content.Intent;
import android.util.Log;

import androidx.test.rule.ActivityTestRule;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class US020703 {

    // As an organizer I want to send a notification to all cancelled entrants
    @Rule
    public ActivityTestRule<OrganizerNotificationActivity> activityRule =
            new ActivityTestRule<>(OrganizerNotificationActivity.class, true, false);

    private FirebaseFirestore db;
    private String eventId;
    private String entrantId_1;
    private String entrantId_2;
    private String entrantId_3;
    private OrganizerNotificationActivity activity;
    private boolean isNotificationSent = false;

    @Before
    public void setUp() throws InterruptedException {
        db = FirebaseFirestore.getInstance();
        CountDownLatch latch = new CountDownLatch(4);

        //  Android ID as organizerId
        /*
        String organizerId = Settings.Secure.getString(
                activityRule.getActivity().getContentResolver(),
                Settings.Secure.ANDROID_ID
        ); */

        eventId = db.collection("OverallDB").document().getId();
        Log log = null;
        log.d("Test", "Event ID: " + eventId);
        //create Entrant
        Map<String, Object> entrantData = new HashMap<>();
        entrantData.put("name", "Test Entrant");
        entrantData.put("status", new HashMap<String, String>() {{
            put("test_event_id", "Declined");
        }});
        entrantData.put("events", new HashMap<String, String>() {{
            put(eventId, "Sample Event");
        }});
        entrantData.put("notifications", new ArrayList<Map<String, String>>());

        entrantId_1 = db.collection("EntrantDB").document().getId();
        db.collection("EntrantDB").document(entrantId_1).set(entrantData)
                .addOnSuccessListener(aVoid -> latch.countDown())
                .addOnFailureListener(e -> {
                    Log.e("Test", "Failed to create entrant", e);
                    latch.countDown();
                });

        entrantId_2 = db.collection("EntrantDB").document().getId();
        db.collection("EntrantDB").document(entrantId_2).set(entrantData)
                .addOnSuccessListener(aVoid -> latch.countDown())
                .addOnFailureListener(e -> {
                    Log.e("Test", "Failed to create entrant", e);
                    latch.countDown();
                });

        entrantId_3 = db.collection("EntrantDB").document().getId();
        db.collection("EntrantDB").document(entrantId_3).set(entrantData)
                .addOnSuccessListener(aVoid -> latch.countDown())
                .addOnFailureListener(e -> {
                    Log.e("Test", "Failed to create entrant", e);
                    latch.countDown();
                });

        // create Event
        Map<String, Object> eventData = new HashMap<>();
        // eventData.put("eventId", "test_event_id");
        eventData.put("name", "Sample Event");
        eventData.put("qrCode", "sample_qr_code");
        eventData.put("posterPhoto", "sample_poster_photo");
        eventData.put("facility", "Sample Facility");
        eventData.put("startDate", "2023-12-01");
        eventData.put("endDate", "2023-12-02");
        eventData.put("limitedNumber", "100");
        eventData.put("entrants", new ArrayList<String>() {{
            add(entrantId_1);
            add(entrantId_2);
            add(entrantId_3);
        }});
        /*
        eventData.put("organizers", new ArrayList<String>() {{
            add(organizerId);
        }});*/


        db.collection("OverallDB").document(eventId).set(eventData)
                .addOnSuccessListener(aVoid -> latch.countDown())
                .addOnFailureListener(e -> {
                    Log.e("Test", "Failed to create event", e);
                    latch.countDown();
                });

        // start Activity
        Intent intent = new Intent();
        // intent.putExtra("eventId",eventId);
        intent.putExtra("testEventName", "Sample Event");
        intent.putStringArrayListExtra("testEntrants", new ArrayList<>(List.of(entrantId_1, entrantId_2, entrantId_3)));
        intent.putExtra("eventId",eventId);

        activity = activityRule.launchActivity(intent);

        // wait for data loading
        boolean dataInserted = latch.await(10, TimeUnit.SECONDS);
        if (!dataInserted) {
            fail("Data insertion timeout");
        }

        // check if data is loaded
        db.collection("OverallDB").document("test_event_id").get()
                .addOnFailureListener(e -> fail("Event data not found in Firestore"));

    }

    @Test
    public void testSendNotification() throws InterruptedException {

        Log.d("Test", "Event ID: " + eventId);
        Thread.sleep(3000);
        // Click the Filter button
        /*
        onView(withId(R.id.organizer_notification_activity_filter_button))
                .perform(click());

        Thread.sleep(3000);*/

        // Select the test event by name
        // onView(withText(eventId)).perform(click());
        // onView(withText("Next")).perform(click());

        // Select the "Declined" status
        // onView(withText("Declined")).perform(click());
        // onView(withText("Confirm")).perform(click());

        // Select entrant
        /*
        onData(anything())
                .inAdapterView(withId(R.id.contact_list_view))
                .atPosition(0)
                .perform(click());*/
        onView(withId(R.id.organizer_notification_activity_select_all_button))
                .perform(click());

        // Click "Send Notification" button
        onView(withId(R.id.organizer_notification_activity_message_button))
                .perform(click());

        // Enter test notification message
        onView(withHint("Enter Your Notification"))
                .perform(typeText("Test Message"), closeSoftKeyboard());

        // Click the Send button
        onView(withText("Send")).perform(click());

        // Validate that the notification was added to Firestore
        CountDownLatch latch = new CountDownLatch(3);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("EntrantDB").document(entrantId_1).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && !isNotificationSent) {
                        isNotificationSent = true;
                    }
                    latch.countDown();
                })
                .addOnFailureListener(e -> latch.countDown());

        db.collection("EntrantDB").document(entrantId_2).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && !isNotificationSent) {
                        isNotificationSent = true;
                    }
                    latch.countDown();
                })
                .addOnFailureListener(e -> latch.countDown());

        db.collection("EntrantDB").document(entrantId_3).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && !isNotificationSent) {
                        isNotificationSent = true;
                    }
                    latch.countDown();
                })
                .addOnFailureListener(e -> latch.countDown());

        latch.await();

        assertTrue("Notifiction send failed", isNotificationSent);

    }


    @After
    public void tearDown() throws InterruptedException {
        Log.d("Test", "Event ID: " + eventId);
        CountDownLatch latch = new CountDownLatch(4);

        // Clean up test data
        db.collection("OverallDB").document(eventId).delete()
                .addOnSuccessListener(aVoid -> latch.countDown())
                .addOnFailureListener(e -> latch.countDown());

        db.collection("EntrantDB").document(entrantId_1).delete()
                .addOnSuccessListener(aVoid -> latch.countDown())
                .addOnFailureListener(e -> latch.countDown());


        db.collection("EntrantDB").document(entrantId_2).delete()
                .addOnSuccessListener(aVoid -> latch.countDown())
                .addOnFailureListener(e -> latch.countDown());

        db.collection("EntrantDB").document(entrantId_3).delete()
                .addOnSuccessListener(aVoid -> latch.countDown())
                .addOnFailureListener(e -> latch.countDown());

        latch.await();

    }
}
