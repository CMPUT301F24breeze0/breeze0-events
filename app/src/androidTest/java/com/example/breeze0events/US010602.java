package com.example.breeze0events;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.Intents.release;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.anything;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class US010602 {

    @Rule
    public ActivityTestRule<EntrantPreLoginActivity> activityRule =
            new ActivityTestRule<>(EntrantPreLoginActivity.class, true, false);

    private String deviceId;
    private OverallStorageController storageController;
    private final String testEventId = "1"; // Replace with the actual event ID you want to test

    @Before
    public void setUp() {
        init();

        Context context = ApplicationProvider.getApplicationContext();
        deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        storageController = new OverallStorageController();

        // Launch the initial activity
        activityRule.launchActivity(null);
    }

    @After
    public void tearDown() {
        release();
        storageController.deleteEntrant(deviceId); // Clean up the entrant's profile
    }

    @Test
    public void testViewJoinAndRemoveEntrantFromEvent() throws InterruptedException {
        // Step 1: New User Signup
        onView(withId(R.id.buttonFirstTimeUse)).perform(click());

        Thread.sleep(2000);

        // Fill in personal information
        onView(withId(R.id.editTextName)).perform(replaceText("John"));
        onView(withId(R.id.editTextEmail)).perform(replaceText("john@example.com"));
        onView(withId(R.id.editTextPhone)).perform(replaceText("1234567890"));
        onView(withId(R.id.buttonSignUp)).perform(click());

        Thread.sleep(1000);

        // Step 2: Search and Join an Event
        onView(withId(R.id.buttonEventSearch)).perform(click());

        Thread.sleep(2000);
        onData(anything()).inAdapterView(withId(R.id.event_search_view)).atPosition(0).perform(click());

        Thread.sleep(1000);

        // Long-click on QR code to download it
        onView(withId(R.id.Entrent_event_QRcode)).perform(longClick());

        // Step 3: Cancel and Relaunch App
        onView(withId(R.id.entrant_event_cancel)).perform(click());

        // Relaunch the app and login using "already have an account"
        activityRule.finishActivity();
        activityRule.launchActivity(new Intent());

        onView(withId(R.id.buttonAlreadyHaveAccount)).perform(click());
        onView(withText("Continue")).perform(click());

        // Step 4: Mock image upload in QR scan
        onView(withId(R.id.buttonQRScan)).perform(click());
        onView(withText("Upload Image")).perform(click());

        // Step 4.1: Prepare a URI and stub the intent to return this URI
        File downloadFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyAppImages");
        File qrImageFile = new File(downloadFolder, "downloaded_image.jpg");  // Assuming a file name
        Uri qrImageUri = Uri.fromFile(qrImageFile);

        Intent resultData = new Intent();
        resultData.setData(qrImageUri);
        Instrumentation.ActivityResult result =
                new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

        intending(hasAction(Intent.ACTION_PICK)).respondWith(result);

        Thread.sleep(10000);

        // Confirm navigation to EntrantEventDetail if QR code processed successfully
        onView(withId(R.id.entrant_event_join)).check(matches(isDisplayed()));

        // Step 5: Join the Event by clicking the Join button
        onView(withId(R.id.entrant_event_join)).perform(click());

        // Step 6: Verify that entrant has joined the event in the backend
        storageController.getEvent(testEventId, new EventCallback() {
            @Override
            public void onSuccess(Event event) {
                List<String> entrants = event.getEntrants();
                if (entrants.contains(deviceId)) {
                    Log.d("Test", "Entrant successfully joined the event");

                    // Step 7: Remove entrant from the event
                    entrants.remove(deviceId);
                    event.setEntrants(entrants);
                    storageController.updateEvent(event);

                    // Verify entrant was removed successfully
                    storageController.getEvent(testEventId, new EventCallback() {
                        @Override
                        public void onSuccess(Event updatedEvent) {
                            List<String> updatedEntrants = updatedEvent.getEntrants();
                            if (!updatedEntrants.contains(deviceId)) {
                                Log.d("Test", "Entrant successfully removed from the event");
                            } else {
                                throw new AssertionError("Entrant was not removed from event entrants list");
                            }
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            throw new AssertionError("Failed to retrieve updated event: " + errorMessage);
                        }
                    });
                } else {
                    throw new AssertionError("Entrant was not added to event entrants list");
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                throw new AssertionError("Failed to retrieve event: " + errorMessage);
            }
        });
    }
}
