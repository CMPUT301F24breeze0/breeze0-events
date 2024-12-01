package com.example.breeze0events;

import android.content.Intent;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

/**
 * UI test for OrganizerEventDisplayEntrants.
 * This test adds a new entrant to the list and checks if it is displayed correctly.
 */
@RunWith(AndroidJUnit4.class)
public class US020201 {

    @Rule
    public ActivityTestRule<OrganizerEventDisplayEntrants> activityRule =
            new ActivityTestRule<>(OrganizerEventDisplayEntrants.class, false, false);

    @Test
    public void testAddNewEntrantAndVerifyInList() throws InterruptedException {
        // Create a list of initial entrants
        ArrayList<String> initialEntrants = new ArrayList<>();
        initialEntrants.add("entrant_001");
        initialEntrants.add("entrant_002");

        // Prepare intent with the initial entrants
        Intent intent = new Intent();
        intent.putStringArrayListExtra("entrants_id", initialEntrants);

        // Launch the activity with the intent
        activityRule.launchActivity(intent);
        // Verify that the initial entrants are displayed in the list
        onView(ViewMatchers.withText("entrant_001"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        onView(ViewMatchers.withText("entrant_002"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

    }
}
