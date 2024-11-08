package com.example.breeze0events;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.Intents.release;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.anything;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.provider.MediaStore;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

@RunWith(AndroidJUnit4.class)
public class US010601 {

    @Rule
    public ActivityTestRule<EntrantPreLoginActivity> activityRule =
            new ActivityTestRule<>(EntrantPreLoginActivity.class, true, false);

    private String deviceId;
    private OverallStorageController storageController;

    @Before
    public void setUp() {
        init();

        Context context = ApplicationProvider.getApplicationContext();
        deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        storageController = new OverallStorageController();

        // Launch the initial activity
        activityRule.launchActivity(null);
    }

    @After
    public void tearDown() {
        release();
        storageController.deleteEntrant(deviceId);
    }

    @Test
    public void testViewEventDetailsByScanningQRCode() throws InterruptedException {
        // Step 1: New User Signup
        onView(withId(R.id.buttonFirstTimeUse)).perform(click());

        Thread.sleep(2000);

        // Fill in personal information
        onView(withId(R.id.editTextName)).perform(replaceText("John"));
        onView(withId(R.id.editTextEmail)).perform(replaceText("john@example.com"));
        onView(withId(R.id.editTextPhone)).perform(replaceText("1234567890"));
        onView(withId(R.id.buttonSignUp)).perform(click());

        Thread.sleep(1000);

        // Step 2: Search and Join an Event
        onView(withId(R.id.buttonEventSearch)).perform(click());

        Thread.sleep(2000);
        onData(anything()).inAdapterView(withId(R.id.event_search_view)).atPosition(2).perform(click());

        Thread.sleep(1000);

        // Long-click on QR code to download it
        onView(withId(R.id.Entrent_event_QRcode)).perform(longClick());

        // Simulate saving to virtual location (simulated here without checking actual storage)

        // Step 3: Cancel and Relaunch App
        onView(withId(R.id.entrant_event_cancel)).perform(click());

        // Relaunch the app and login using "already have an account"
        activityRule.finishActivity();
        activityRule.launchActivity(new Intent());

        onView(withId(R.id.buttonAlreadyHaveAccount)).perform(click());
        onView(withText("Continue")).perform(click());

        // Step 4: Mock image upload in QR scan
        onView(withId(R.id.buttonQRScan)).perform(click());
        onView(withText("Upload Image")).perform(click());

        // Step 4.1: Prepare a URI and stub the intent to return this URI
        File downloadFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyAppImages");
        File qrImageFile = new File(downloadFolder, "downloaded_image.jpg");  // Assuming a file name
        Uri qrImageUri = Uri.fromFile(qrImageFile);

        Intent resultData = new Intent();
        resultData.setData(qrImageUri);
        Instrumentation.ActivityResult result =
                new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

        intending(hasAction(Intent.ACTION_PICK)).respondWith(result);

        // Confirm navigation to EntrantEventDetail if QR code processed successfully
        intended(hasComponent(EntrantEventDetail.class.getName()));
    }
}
