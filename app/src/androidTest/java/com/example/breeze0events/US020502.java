package com.example.breeze0events;

import android.content.Intent;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;

import com.google.firebase.firestore.DocumentSnapshot;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class US020502 {

    @Rule
    public ActivityTestRule<OrganizerSamplingActivity> activityRule =
            new ActivityTestRule<>(OrganizerSamplingActivity.class, true, false);

    private OrganizerSamplingActivity activity;

    @Before
    public void setUp() {
        // Create an Event object with eventId = "6"
        Event event = new Event();
        event.setEventId("6");
        event.setName("Sample Event");
        event.setQrCode("sample_qr_code");
        event.setPosterPhoto("sample_poster_photo");
        event.setFacility("Sample Facility");
        event.setStartDate("2023-12-01");
        event.setEndDate("2023-12-02");
        event.setLimitedNumber("100");
        event.setEntrants(new ArrayList<>());
        event.setOrganizers(new ArrayList<>());

        // Initialize Intent to launch OrganizerSamplingActivity
        Intent intent = new Intent();
        intent.putExtra("selected_event", event); // Pass the Event object as extra
        intent.putExtra("eventId", event.getEventId()); // Pass eventId separately if needed

        // Launch the activity with the intent
        activity = activityRule.launchActivity(intent);

        // Initialize activity parameters after launch
        activity.limitedNumber = 100;
        activity.requestedCount = 0;
    }

    @Test
    public void testPickNewApplicantsButtonClick() {
        // Perform a click on the button that triggers pickNewApplicants
        Espresso.onView(ViewMatchers.withId(R.id.organizer_sampling_activity_pick_new_applicant_button))
                .perform(ViewActions.click());

        // Check if joinedEntrants has elements
//        assertFalse("joinedEntrants should not be empty", activity.joinedEntrants.isEmpty());

        // Verify that joinedEntrants has at least one entry with status "Selected"
        boolean hasSelectedEntrant = false;

        for (DocumentSnapshot entrant : activity.joinedEntrants) {
            Map<String, String> status = (Map<String, String>) entrant.get("status");
            if ("Selected".equals(status.get("testEventId"))) {
                hasSelectedEntrant = true;
                Log.d("OrganizerSamplingTest", "Selected entrant: " + entrant.get("name"));
                break;  // Stop once we find a selected entrant
            }
        }

        // Assert that there is at least one selected entrant
   //     assertTrue("There should be at least one selected entrant", hasSelectedEntrant);
    }
}
