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
        // Initialize Intents to capture outgoing intents
        init();

        // Launch the initial activity
        activityRule.launchActivity(new Intent());
    }

    @After
    public void tearDown() {
        // Release Intents after test
        release();
    }

    @Test
    public void testProvidePersonalInformation() throws InterruptedException{
        // Step 1: Click on "First Time Use" button in EntrantPreLoginActivity
        onView(withId(R.id.buttonFirstTimeUse)).perform(click());

        // Step 2: Verify EntrantLoginActivity is launched
        Thread.sleep(10000);
        intended(hasComponent(EntrantLoginActivity.class.getName()));

        // Step 3: Fill in personal information in EntrantLoginActivity
        onView(withId(R.id.editTextName)).perform(replaceText("John Doe"));
        onView(withId(R.id.editTextEmail)).perform(replaceText("johndoe@example.com"));
        onView(withId(R.id.editTextPhone)).perform(replaceText("1234567890"));

        // Step 4: Click on "Sign Up" button
        onView(withId(R.id.buttonSignUp)).perform(click());

        // Verify that EntrantMylistActivity is launched after signup
        intended(hasComponent(EntrantMylistActivity.class.getName()));
    }
}
