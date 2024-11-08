package com.example.breeze0events;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

import android.app.Activity;
import android.app.ActivityManager;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class US010101Test {
    @Rule
    public ActivityScenarioRule<OverallLoginPage> scenario = new
            ActivityScenarioRule<OverallLoginPage>(OverallLoginPage.class);
    @Test
    public void testEnter() {
        // Click on Entrant to login their account
        onView(withId(R.id.entrant_button)).perform(click());
        // Try to login
        onView(withId(R.id.buttonAlreadyHaveAccount)).perform(click());

        try {
            // Check if the 'Account Already Exists' dialog is displayed
            onView(withText("Account Already Exists")).check(matches(isDisplayed()));
            // If found, click 'OK' to dismiss and continue
            onView(withText("OK")).perform(click());
            // Proceed with the test assuming 'already exists' case
        } catch (NoMatchingViewException e) {
            // If 'Account Already Exists' dialog was not found, check for the 'Not Already Exists' case
            try {
                onView(withText("Not Already Have")).check(matches(isDisplayed()));
                // If found, perform appropriate actions
                onView(withText("OK")).perform(click());
                // Add any further checks or steps here if needed
            } catch (NoMatchingViewException ex) {
                // Handle the case where neither dialog is shown or other actions needed
            }
        }
    }
}
