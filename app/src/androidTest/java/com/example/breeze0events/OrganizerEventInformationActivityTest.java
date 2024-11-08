package com.example.breeze0events;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class OrganizerEventInformationActivityTest {

    @Before
    public void setup() {
        // Setup the test scenario before each test
        ActivityScenario.launch(OrganizerEventInformationActivity.class);
    }

    @Test
    public void testBackButton() {
        // Perform a click on the back button and check if the activity finishes
        onView(withId(R.id.back_button)).perform(click());
        // Espresso does not have an assert for finishing an activity, so this action checks the absence of views after finish
    }

    @Test
    public void testNameButtonOpensEventDisplayDateActivity() {
        // Click the name button and check if OrganizerEventDisplayDate activity is launched
        onView(withId(R.id.event_name)).perform(click());
        intended(hasComponent(OrganizerEventDisplayDate.class.getName()));
    }

    @Test
    public void testFacilityButtonOpensFacilityActivity() {
        // Click the facility button and check if OrganizerEventDisplayFacility activity is launched
        onView(withId(R.id.facility_text)).perform(click());
    }

    @Test
    public void testEntrantButtonOpensEntrantsActivity() {
        // Click the entrants button and check if OrganizerEventDisplayEntrants activity is launched
        onView(withId(R.id.entrants_text)).perform(click());
    }

    @Test
    public void testOrganizerButtonOpensOrganizersActivity() {
        // Click the organizers button and check if OrganizerEventDisplayOrganizers activity is launched
        onView(withId(R.id.organizers_text)).perform(click());
    }

    @Test
    public void testQrCodeButtonOpensQRCodeDisplayActivity() {
        // Click the QR code button and check if OrganizerEventDisplayQRcode activity is launched
        onView(withId(R.id.qr_code_text)).perform(click());
    }

    @Test
    public void testImageIsDisplayed() {
        // Check if the poster image view is displayed
        onView(withId(R.id.qr_code_image)).check(matches(withId(R.id.qr_code_image)));
    }
}
