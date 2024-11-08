package com.example.breeze0events;

import android.content.Intent;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class AdminOperateActicityTest {
    @Rule
    public ActivityScenarioRule<AdminOperateActivity> activityScenarioRule =
            new ActivityScenarioRule<>(AdminOperateActivity.class);

    @Before
    public void setUp() {
        // Initialize Espresso Intents before each test
        Intents.init();
    }

    @After
    public void tearDown() {
        // Release Espresso Intents after each test
        Intents.release();
    }

    @Test
    public void testOrganizationButtonOpensOrganizationProfile() {
        // Perform click on organization button
        onView(withId(R.id.organization)).perform(click());
        // Check if the intent was triggered for AdminOrganizationProfileActivity
        intended(hasComponent(AdminOrganizationProfileActivity.class.getName()));
    }
    @Test
    public void testBackButtonReturnsInToAdminLogin() {
        // Perform click on back button
        onView(withId(R.id.back_in_main)).perform(click());
        // Check if the intent was triggered for AdminLoginActivity
        intended(hasComponent(AdminLoginActivity.class.getName()));
    }

}
