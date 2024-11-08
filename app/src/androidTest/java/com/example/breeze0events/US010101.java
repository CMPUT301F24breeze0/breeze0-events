package com.example.breeze0events;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.release;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.CoreMatchers.anything;

import android.content.Context;
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

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class US010101 {

    @Rule
    public ActivityTestRule<EntrantPreLoginActivity> activityRule =
            new ActivityTestRule<>(EntrantPreLoginActivity.class, true, false);

    private String deviceId;
    private OverallStorageController storageController;

    @Before
    public void setUp() {
        init();

        Context context = ApplicationProvider.getApplicationContext();
        deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        storageController = new OverallStorageController();

        activityRule.launchActivity(null);
    }

    @After
    public void tearDown() {
        release();
        storageController.deleteEntrant(deviceId);
    }

    @Test
    public void testNewUserSignupAndJoinEvent() throws InterruptedException {
        // Step 1: New User Signup
        onView(withId(R.id.buttonFirstTimeUse)).perform(click());

        // Verify EntrantLoginActivity is launched
        Thread.sleep(2000);
        intended(hasComponent(EntrantLoginActivity.class.getName()));

        // Fill in personal information
        onView(withId(R.id.editTextName)).perform(replaceText("John Doe"));
        onView(withId(R.id.editTextEmail)).perform(replaceText("johndoe@example.com"));
        onView(withId(R.id.editTextPhone)).perform(replaceText("1234567890"));
        onView(withId(R.id.buttonSignUp)).perform(click());

        // Verify EntrantMylistActivity is launched after signup
        Thread.sleep(1000);
        intended(hasComponent(EntrantMylistActivity.class.getName()));

        // Step 2: Search for an Event
        onView(withId(R.id.buttonEventSearch)).perform(click());

        // Verify EntrantSearchingActivity is launched
        Thread.sleep(1000);
        intended(hasComponent(EntrantSearchingActivity.class.getName()));

        // Click on the first event in the event_search_view ListView
        onData(anything()).inAdapterView(withId(R.id.event_search_view)).atPosition(0).perform(click());

        // Verify EntrantEventDetail is launched
        Thread.sleep(1000);
        intended(hasComponent(EntrantEventDetail.class.getName()));

        // Step 3: Join the Event
        onView(withId(R.id.entrant_event_join)).perform(click());

        final String eventId = "1"; // replace with actual event ID for testing
        Thread.sleep(2000);

        storageController.getEvent(eventId, new EventCallback() {
            @Override
            public void onSuccess(Event event) {
                List<String> entrants = event.getEntrants();
                if (entrants.contains(deviceId)) {
                    Log.d("Test", "Entrant successfully joined the event");
                    // Remove entrant to clean up
                    entrants.remove(deviceId);
                    event.setEntrants(entrants);
                    storageController.updateEvent(event);
                } else {
                    throw new AssertionError("Entrant not found in event entrants list");
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                throw new AssertionError("Failed to retrieve event: " + errorMessage);
            }
        });
    }
}
