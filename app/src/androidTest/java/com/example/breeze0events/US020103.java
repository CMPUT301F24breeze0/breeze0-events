package com.example.breeze0events;

import android.content.Intent;
import android.provider.Settings;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class US020103 {

    private static final String MOCK_FACILITY_ID = "12345";
    private static final String MOCK_FACILITY_NAME = "Test Facility";
    private String deviceInfo;

    @Before
    public void setup() {
        // Retrieve the device ID (or any other unique identifier) used in the actual class
        deviceInfo = Settings.Secure.getString(
                InstrumentationRegistry.getInstrumentation().getTargetContext().getContentResolver(),
                Settings.Secure.ANDROID_ID
        );
    }

    @Test
    public void testAddFacilitySuccessfully() {
        // Create an Intent to start AddFacilityActivity with a mock facility ID
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), AddFacilityActivity.class);
        intent.putExtra("new_facility_id", MOCK_FACILITY_ID);

        // Launch the activity with the given intent
        try (ActivityScenario<AddFacilityActivity> scenario = ActivityScenario.launch(intent)) {
            // Check if the facility ID is displayed correctly
            Espresso.onView(withId(R.id.edit_facility_activity_facility_id_text))
                    .check(matches(withText(MOCK_FACILITY_ID)));

            // Type a facility name into the input field
            Espresso.onView(withId(R.id.facility_name_input))
                    .perform(typeText(MOCK_FACILITY_NAME), ViewActions.closeSoftKeyboard());

            // Click the add button
            Espresso.onView(withId(R.id.edit_facility_activity_update_button)).perform(click());

            // Verify that the facility was added successfully (mocked storage interaction)
            OverallStorageController overallStorageController = new OverallStorageController();
            overallStorageController.getFacility(MOCK_FACILITY_ID, new FacilityCallback() {
                @Override
                public void onSuccess(Facility facility) {
                    // Check if the facility name matches the expected value
                    if (!MOCK_FACILITY_NAME.equals(facility.getLocation())) {
                        fail("Facility name does not match the expected value");
                    }
                }

                @Override
                public void onFailure(String errorMessage) {
                    // Trigger a failure in the test if facility retrieval fails
                    fail("Failed to retrieve facility: " + errorMessage);
                }
            });
        }
    }
}
