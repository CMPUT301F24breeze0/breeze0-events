package com.example.breeze0events;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class US030501 {

    @Before
    public void setup() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testEntrantButtonLaunchesAdminEntrantProfileActivity() {
        try (ActivityScenario<AdminOperateActivity> scenario = ActivityScenario.launch(AdminOperateActivity.class)) {
            onView(withId(R.id.entrant)).perform(click());
            intended(hasComponent(AdminEntrantProfileActivity.class.getName()));
        }
    }
    @Test
    public void testEntrantViewProfile() {
        try (ActivityScenario<AdminOperateActivity> scenario = ActivityScenario.launch(AdminOperateActivity.class)) {
            onView(withId(R.id.entrant)).perform(click());
            onData(anything())
                    .inAdapterView(withId(R.id.entrant_list_view))
                    .atPosition(0)
                    .perform(click());

        }
    }

    @Test
    public void testOrganizationButtonLaunchesAdminOrganizationProfileActivity() {
        try (ActivityScenario<AdminOperateActivity> scenario = ActivityScenario.launch(AdminOperateActivity.class)) {
            onView(withId(R.id.organization)).perform(click());

            intended(hasComponent(AdminOrganizationProfileActivity.class.getName()));
        }
    }
    @Test
    public void testOrganizationViewProfile() {
        try (ActivityScenario<AdminOperateActivity> scenario = ActivityScenario.launch(AdminOperateActivity.class)) {
            onView(withId(R.id.organization)).perform(click());
            onData(anything())
                    .inAdapterView(withId(R.id.organizer_list_view))
                    .atPosition(0)
                    .perform(click());

        }


    }
}
