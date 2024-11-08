package com.example.breeze0events;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.release;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class US010201 {

    @Rule
    public ActivityTestRule<EntrantPreLoginActivity> activityRule =
            new ActivityTestRule<>(EntrantPreLoginActivity.class, true, false);

    @Before
    public void setUp() {
        init();
        activityRule.launchActivity(new Intent());
    }

    @After
    public void tearDown() {
        // Release Intents after test
        release();
    }

    @Test
    public void testProvidePersonalInformation() throws InterruptedException {
        // Step 1: Click on "First Time Use" button in EntrantPreLoginActivity
        onView(withId(R.id.buttonFirstTimeUse)).perform(click());

        // Step 2: Verify EntrantLoginActivity is launched
        Thread.sleep(1000); // Adding a short delay for UI response
        intended(hasComponent(EntrantLoginActivity.class.getName()));

        // Step 3: Fill in personal information in EntrantLoginActivity
        onView(withId(R.id.editTextName)).perform(replaceText("John Doe"));
        onView(withId(R.id.editTextEmail)).perform(replaceText("johndoe@example.com"));
        onView(withId(R.id.editTextPhone)).perform(replaceText("1234567890"));

        // Skip clicking on the profile image, assuming no image is selected

        // Step 4: Click on "Sign Up" button to submit form
        onView(withId(R.id.buttonSignUp)).perform(click());

        // Step 5: Verify that EntrantMylistActivity is launched after signup
        Thread.sleep(1000); // Short delay to ensure intent transition
        intended(hasComponent(EntrantMylistActivity.class.getName()));
    }
}
