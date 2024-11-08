package com.example.breeze0events;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.release;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

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

@RunWith(AndroidJUnit4.class)
public class US010202 {

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
    public void testUpdateProfileInformation() throws InterruptedException {
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

        // Step 2: Navigate to Profile Update
        onView(withId(R.id.buttonProfile)).perform(click());

        // Verify EntrantProfileActivity is launched
        Thread.sleep(1000);
        intended(hasComponent(EntrantProfileActivity.class.getName()));

        // Step 3: Update Profile Information
        onView(withId(R.id.editTextName)).perform(replaceText("Jane Doe"));
        onView(withId(R.id.editTextEmail)).perform(replaceText("janedoe@example.com"));
        onView(withId(R.id.editTextPhone)).perform(replaceText("0987654321"));
        onView(withId(R.id.buttonUpdateProfile)).perform(click());  // Assuming there's a save button

        // Step 4: Confirm return to EntrantMylistActivity without specifying exact intent count
        Thread.sleep(1000);
        onView(withId(R.id.entrantName)).check(matches(withText("Jane Doe")));

        // Step 5: Verify the backend has the updated information
        storageController.getEntrant(deviceId, new EntrantCallback() {
            @Override
            public void onSuccess(Entrant entrant) {
                if ("Jane Doe".equals(entrant.getName()) &&
                        "janedoe@example.com".equals(entrant.getEmail()) &&
                        "0987654321".equals(entrant.getPhoneNumber())) {
                    Log.d("Test", "Profile information updated successfully.");
                } else {
                    throw new AssertionError("Profile information did not update correctly.");
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                throw new AssertionError("Failed to retrieve entrant profile: " + errorMessage);
            }
        });
    }

}
