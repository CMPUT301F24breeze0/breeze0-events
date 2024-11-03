package com.example.breeze0events;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class OverallLoginPageTest {

    @Rule
    public ActivityTestRule<OverallLoginPage> activityRule = new ActivityTestRule<>(OverallLoginPage.class);

    @Before
    public void setUp() {
        // Initialize Intents before the tests to monitor launched activities
        Intents.init();
    }

    @After
    public void tearDown() {
        // Release Intents after tests
        Intents.release();
    }

    @Test
    public void testEntrantButtonLaunchesEntrantPreLoginActivity() {
        // Perform click on the entrant button
        onView(withId(R.id.entrant_button)).perform(click());

        // Verify if the intended activity was launched
        intended(hasComponent(EntrantPreLoginActivity.class.getName()));
    }

    @Test
    public void testOrganizerButtonLaunchesOrganizerMyListActivity() {
        // Perform click on the organizer button
        onView(withId(R.id.organizer_button)).perform(click());

        // Verify if the intended activity was launched
        intended(hasComponent(OrganizerMyListActivity.class.getName()));
    }

    @Test
    public void testAdminButtonLaunchesAdminLoginActivity() {
        // Perform click on the admin button
        onView(withId(R.id.admin_button)).perform(click());

        // Verify if the intended activity was launched
        intended(hasComponent(AdminLoginActivity.class.getName()));
    }
}
