package com.example.breeze0events;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;
import androidx.test.filters.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Intent;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.ArrayList;
import androidx.test.espresso.IdlingPolicies;
import java.util.concurrent.TimeUnit;
 
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;

@RunWith(AndroidJUnit4.class)
public class US030401Test {

    @Before
    public void setup() {
        IdlingPolicies.setMasterPolicyTimeout(5, TimeUnit.SECONDS);
        Intents.init();
        ActivityScenario.launch(AdminEventActivity.class);
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testEventListDisplayedAndEventDetailsOpened() throws InterruptedException {
        ActivityScenario<AdminEventActivity> scenario = ActivityScenario.launch(AdminEventActivity.class);
        Thread.sleep(4000);
        onView(withId(R.id.eventsList))
                .check(ViewAssertions.matches(isDisplayed()));;

        onData(anything())
                .inAdapterView(withId(R.id.eventsList))
                .atPosition(0)
                .check(ViewAssertions.matches(isDisplayed()));
    }
}
