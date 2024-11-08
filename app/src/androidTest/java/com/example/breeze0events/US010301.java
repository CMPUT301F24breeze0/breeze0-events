package com.example.breeze0events;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.release;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import android.content.Context;
import android.provider.Settings;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class US010301 {

    @Rule
    public ActivityTestRule<EntrantPreLoginActivity> activityRule =
            new ActivityTestRule<>(EntrantPreLoginActivity.class, true, false);

    private String deviceId;
    private OverallStorageController storageController;

    @Before
    public void setUp() {
        init(); // Initialize Intents for intent verification

        Context context = ApplicationProvider.getApplicationContext();
        deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        storageController = new OverallStorageController();

        // Launch the initial activity
        activityRule.launchActivity(null);
    }

    @After
    public void tearDown() {
        release(); // Release Intents
        storageController.deleteEntrant(deviceId); // Clean up entrant data after test
    }

    @Test
    public void testProfileImageUpdate() throws InterruptedException {
        // Step 1: New User Signup
        onView(withId(R.id.buttonFirstTimeUse)).perform(click());

        // Verify EntrantLoginActivity is launched
        Thread.sleep(2000);
        onView(withId(R.id.editTextName)).perform(replaceText("John Doe"));
        onView(withId(R.id.editTextEmail)).perform(replaceText("johndoe@example.com"));
        onView(withId(R.id.editTextPhone)).perform(replaceText("1234567890"));
        onView(withId(R.id.buttonSignUp)).perform(click());

        // Confirm navigation to EntrantMylistActivity
        onView(withId(R.id.entrantName)).check(matches(isDisplayed()));

        // Step 2: Navigate to Profile
        onView(withId(R.id.buttonProfile)).perform(click());

        // Verify EntrantProfileActivity is launched
        intended(hasComponent(EntrantProfileActivity.class.getName()));

        // Step 3: Click on Profile Image and Verify Alert Dialog
        onView(withId(R.id.profileImage)).perform(click());
        onView(withText("Upload")).check(matches(isDisplayed())).perform(click());

        // Manually select an image from the gallery if needed

        Thread.sleep(2000);
        // Finally, navigate back to EntrantMylistActivity
        intended(hasComponent(EntrantMylistActivity.class.getName()));
    }
}
