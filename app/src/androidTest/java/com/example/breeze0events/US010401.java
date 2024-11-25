package com.example.breeze0events;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.release;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasToString;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import android.util.Base64;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class US010401 {
    @Rule
    public ActivityTestRule<OverallLoginPage> activityRule =
            new ActivityTestRule<>(OverallLoginPage.class, true, false);

    private String deviceId;
    private OverallStorageController storageController;
    private Context context;

    @Before
    public void setUp() {
        init();
        context = ApplicationProvider.getApplicationContext();
        deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        storageController = new OverallStorageController();

        activityRule.launchActivity(null);
    }

    @After
    public void tearDown() {
        release();
        storageController.deleteEntrant(deviceId);
    }

    @Test
    public void testReceiveWinNotification() throws InterruptedException{
//        // initialize the event to send win notification
//        onView(withId(R.id.organizer_button)).perform(click());
//
//        onView(withId(R.id.new_event_button)).perform(click());
//
//        onView(withId(R.id.event_name_bar)).perform(replaceText("Demo Event"));
//        onView(withId(R.id.event_start_date_bar)).perform(replaceText("2024-10-10"));
//        onView(withId(R.id.event_end_date_bar)).perform(replaceText("2024-12-10"));
//        onView(withId(R.id.entrants_bar)).perform(replaceText("1"));
//        onView(withId(R.id.organizer_event_activity_facility_button)).perform(click());
//        Thread.sleep(2000);
//        onData(anything())
//                .inAdapterView(withId(R.id.facility_list_view))
//                .atPosition(0)
//                .perform(click());
//        onView(withId(R.id.organizer_event_activity_generate_qr_button)).perform(click());
//        onView(withId(R.id.organizer_edit_event_activity_add_button)).perform(click());
//        activityRule.finishActivity();
//        activityRule.launchActivity(null);

        // Entrant part
        String QRHash = QRHashGenerator.generateHash("1");
//        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.cmput301poster);
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
//        byte[] byteArray = byteArrayOutputStream.toByteArray();
//        QRHash, Base64.encodeToString(byteArray, Base64.DEFAULT)

        Event event = new Event("1", "Test",QRHash, null,"1","2024-10-05", "2024-12-12","1", "false",null, null);
        storageController.updateEvent(event);

        Thread.sleep(1000);
        onView(withId(R.id.entrant_button)).perform(click());

        onView(withId(R.id.buttonFirstTimeUse)).perform(click());

        // Verify EntrantLoginActivity is launched
        Thread.sleep(3000);
        onView(withId(R.id.editTextName)).perform(replaceText("Jane Doe"));
        onView(withId(R.id.editTextEmail)).perform(replaceText("janedoe@example.com"));
        onView(withId(R.id.editTextPhone)).perform(replaceText("0987654321"));
        onView(withId(R.id.buttonSignUp)).perform(click());

        // Verify EntrantMylistActivity is launched after signup
        Thread.sleep(1000);
        onView(withId(R.id.buttonEventSearch)).perform(click());

        Thread.sleep(5000);

        // Click on the first event in the event_search_view ListView
        onData(anything()).inAdapterView(withId(R.id.event_search_view)).atPosition(0).perform(click());

        Thread.sleep(5000);

        onView(withId(R.id.entrant_event_join)).perform(click());

        Thread.sleep(5000);
        storageController.getEntrant(deviceId, new EntrantCallback() {
            @Override
            public void onSuccess(Entrant entrant) {
                entrant.set_add_Event("1", "Test","Requested");
                List<NewPair<String, String>> notification = new ArrayList<>();
                entrant.addNotification("Test", "This is offer for your application to this event");
                storageController.updateEntrant(entrant);
            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });

        Thread.sleep(5000);

        onView(withId(R.id.buttonNotification)).perform(click());

        Thread.sleep(1000);
        onView(withId(R.id.notification_message)).perform(click());

        Thread.sleep(3000);
        onView(withText("View MyList")).perform(click());

        Thread.sleep(2000);
        onView(withId(R.id.buttonEventStatus)).perform(click());

        Thread.sleep(2000);
        onView(withText("ACCEPT")).perform(click());






    }
}
