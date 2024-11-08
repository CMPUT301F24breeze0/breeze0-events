package com.example.breeze0events;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.filters.LargeTest;

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
import androidx.test.espresso.IdlingPolicies;
import java.util.concurrent.TimeUnit;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.not;
@RunWith(AndroidJUnit4.class)
@LargeTest
public class US030101Test {

    @Before
    public void setup() {

        IdlingPolicies.setMasterPolicyTimeout(5, TimeUnit.SECONDS);

    }

    @Test
    public void testRemoveEvent() throws InterruptedException { //

        ActivityScenario<AdminEventActivity> scenario = ActivityScenario.launch(AdminEventActivity.class);
        Thread.sleep(4000);
        // figure it out database showing slowly, giving time delay to solve this
        onView(withId(R.id.eventsList)).check(ViewAssertions.matches(isDisplayed()));

        // select the first event in the ListView
        onData(anything())
                .inAdapterView(withId(R.id.eventsList))
                .atPosition(0)
                .perform(click());

        // eventName
        onView(withId(R.id.EventName)).check(ViewAssertions.matches(isDisplayed()));

        // QR Code test
        onView(withId(R.id.QRCodeButton)).perform(click());

        onView(withId(R.id.QRcode)).check(ViewAssertions.matches(isDisplayed()));

        Espresso.pressBack();

        // "Detail" button test
        onView(withId(R.id.DetailButton)).perform(click());

        onView(withId(R.id.EventDetail)).check(ViewAssertions.matches(isDisplayed()));

        // "Delete" button to remove the event test
        onView(withId(R.id.Deletebutton)).perform(click());

        Espresso.pressBack();

        // test delete successful or not
        onView(withId(R.id.eventsList))
                .check(ViewAssertions.matches(not(withText("Event Name: <Deleted Event Name>"))));
    }
}
