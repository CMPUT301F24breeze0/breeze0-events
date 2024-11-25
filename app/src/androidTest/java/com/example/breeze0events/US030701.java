package com.example.breeze0events;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.filters.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.IdlingPolicies;

import java.util.concurrent.TimeUnit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * UI test for deleting a facility from the admin interface.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class US030701 {
    private OverallStorageController overallStorageController;
    private String mockFacilityId;

    /**
     * Matcher to find a list item with specific text.
     */
    public static Matcher<Object> withItemContent(final String text) {
        return new TypeSafeMatcher<Object>() {
            @Override
            public boolean matchesSafely(Object item) {
                return item.toString().contains(text);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("List item with text: " + text);
            }
        };
    }

    @Before
    public void setup() {
        IdlingPolicies.setMasterPolicyTimeout(6, TimeUnit.SECONDS);

        overallStorageController = new OverallStorageController();

        Facility mockFacility = new Facility(
                "Mock test id",
                "Mock Violated Facility Location",
                "Mock device"
        );
        overallStorageController.addFacility(mockFacility);
        mockFacilityId = mockFacility.getFacilityId();
    }

    @After
    public void tearDown() {
        if (overallStorageController != null) {
            overallStorageController.deleteFacility(mockFacilityId);
        }
    }

    @Test
    public void testRemoveFacility() throws InterruptedException {
        ActivityScenario<AdminFacilityActivity> scenario = ActivityScenario.launch(AdminFacilityActivity.class);

        Thread.sleep(4000);

        onView(withId(R.id.facilityList)).check(ViewAssertions.matches(isDisplayed()));

        onData(withItemContent("Mock Violated Facility Location"))
                .inAdapterView(withId(R.id.facilityList))
                .perform(click());

        onView(withId(R.id.facilityName)).check(ViewAssertions.matches(isDisplayed()));

        onView(withId(R.id.Deletebutton)).perform(click());
        onView(withText("Delete"))
                .inRoot(isDialog())
                .check(ViewAssertions.matches(isDisplayed()))
                .perform(click());

        Thread.sleep(1000);

        onView(withId(R.id.refresh_button)).perform(click());
        Thread.sleep(700);

        onView(withId(R.id.facilityList))
                .check(ViewAssertions.matches(not(withText("Mock Violated Facility Location"))));

        Thread.sleep(4000);

        overallStorageController.getFacility(mockFacilityId, new FacilityCallback() {
            @Override
            public void onSuccess(Facility facility) {
                fail("Facility was not deleted from the database.");
            }

            @Override
            public void onFailure(String errorMessage) {
                assertNull("Facility successfully deleted from database", null);
            }
        });
    }
}

