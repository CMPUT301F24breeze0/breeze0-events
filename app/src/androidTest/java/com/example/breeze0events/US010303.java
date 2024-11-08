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
public class US010303 {

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
    public void testGeneratedProfilePictureFromName() throws InterruptedException {
        // Step 1: New User Signup without uploading a profile image
        onView(withId(R.id.buttonFirstTimeUse)).perform(click());

        // Verify EntrantLoginActivity is launched
        Thread.sleep(2000);
        onView(withId(R.id.editTextName)).perform(replaceText("Jane Doe"));
        onView(withId(R.id.editTextEmail)).perform(replaceText("janedoe@example.com"));
        onView(withId(R.id.editTextPhone)).perform(replaceText("0987654321"));
        onView(withId(R.id.buttonSignUp)).perform(click());

        // Verify EntrantMylistActivity is launched after signup
        onView(withId(R.id.entrantName)).check(matches(isDisplayed()));

        // Step 2: Navigate to Profile to confirm generated profile image
        onView(withId(R.id.buttonProfile)).perform(click());

        // Verify EntrantProfileActivity is launched
        intended(hasComponent(EntrantProfileActivity.class.getName()));

        // Step 3: Verify Deterministically Generated Profile Image
        // Check if a generated profile image is displayed (assuming a specific image or placeholder shows up)
        onView(withId(R.id.profileImage)).check(matches(isDisplayed()));
        // Here, we might need to verify the exact placeholder or image generated based on the name "Jane Doe"

        // Additional verification checking visually for John is letter "J"
        // Manually check the generated profile image for "John"

        Thread.sleep(2000);
        onView(withId(R.id.buttonReturn)).perform(click());
        intended(hasComponent(EntrantMylistActivity.class.getName()));
    }
}
