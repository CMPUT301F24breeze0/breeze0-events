package com.example.breeze0events;

import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.firestore.FirebaseFirestore;

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
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static org.hamcrest.Matchers.anything;

@RunWith(AndroidJUnit4.class)
public class US020701 {

    // US020701: As an organizer I want to send notifications to all entrants on the waiting list

    @Rule
    public ActivityTestRule<OrganizerNotificationActivity> activityRule =
            new ActivityTestRule<>(OrganizerNotificationActivity.class, true, false);

    private FirebaseFirestore db;
    private String testEventId = "test_event_id";
    private String testEntrantId = "test_entrant_id";
    private String testMessage = "Test Notification";

    @Before
    public void setUp() throws InterruptedException {
        db = FirebaseFirestore.getInstance();
        CountDownLatch latch = new CountDownLatch(2);

        // start Activity
        Intent intent = new Intent();
        activityRule.launchActivity(intent);

        //  Android ID as organizerId
        String organizerId = Settings.Secure.getString(
                activityRule.getActivity().getContentResolver(),
                Settings.Secure.ANDROID_ID
        );

        // create Event
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
            add(organizerId);
        }});

        db.collection("OverallDB").document("test_event_id").set(eventData)
                .addOnSuccessListener(aVoid -> latch.countDown())
                .addOnFailureListener(e -> {
                    Log.e("Test", "Failed to create event", e);
                    latch.countDown();
                });

        //create Entrant
        Map<String, Object> entrantData = new HashMap<>();
        entrantData.put("name", "Test Entrant");
        entrantData.put("status", new HashMap<String, String>() {{
            put("test_event_id", "Joined");
        }});
        entrantData.put("events", new HashMap<String, String>() {{
            put("test_event_id", "Sample Event");
        }});
        entrantData.put("notifications", new ArrayList<Map<String, String>>());

        db.collection("EntrantDB").document("test_entrant_id").set(entrantData)
                .addOnSuccessListener(aVoid -> latch.countDown())
                .addOnFailureListener(e -> {
                    Log.e("Test", "Failed to create entrant", e);
                    latch.countDown();
                });

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

        Thread.sleep(3000);
        // Click the Filter button
        onView(withId(R.id.organizer_notification_activity_filter_button))
                .perform(click());

        Thread.sleep(3000);

        // Select the test event by name
        onView(withText("test_event_id")).perform(click());
        onView(withText("Next")).perform(click());

        // Select the "Joined" status
        onView(withText("Joined")).perform(click());
        onView(withText("Confirm")).perform(click());

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
                .perform(typeText(testMessage), closeSoftKeyboard());

        // Click the Send button
        onView(withText("Send")).perform(click());

        // Validate that the notification was added to Firestore
        CountDownLatch latch = new CountDownLatch(1);

        db.collection("EntrantDB").document(testEntrantId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<Map<String, String>> notifications =
                                (List<Map<String, String>>) documentSnapshot.get("notifications");
                        boolean messageFound = false;

                        if (notifications != null) {
                            for (Map<String, String> notification : notifications) {
                                if (notification.containsValue(testMessage)) {
                                    messageFound = true;
                                    break;
                                }
                            }
                        }

                        assertTrue("Notification should be added", messageFound);
                    } else {
                        fail("Entrant document not found");
                    }
                    latch.countDown();
                })
                .addOnFailureListener(e -> {
                    fail("Failed to retrieve entrant document: " + e.getMessage());
                    latch.countDown();
                });

        latch.await();
    }


/*
    @After
    public void tearDown() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2);

        // Clean up test data
        db.collection("OverallDB").document(testEventId).delete()
                .addOnSuccessListener(aVoid -> latch.countDown())
                .addOnFailureListener(e -> latch.countDown());

        db.collection("EntrantDB").document(testEntrantId).delete()
                .addOnSuccessListener(aVoid -> latch.countDown())
                .addOnFailureListener(e -> latch.countDown());

        latch.await();
    } */
}
