package com.example.breeze0events;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.core.utilities.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * EntrantMylistActivity represents the main page for the Entrant containing the wishlist,
 * profile of Entrant.
 */
public class EntrantMylistActivity extends AppCompatActivity implements
        EntrantMyListAdapter.OnUnjoinListener,
        EntrantMyListAdapter.EventNameProvider,
        EntrantMyListAdapter.ViewListener {

    private ImageView profileImage;
    private TextView entrantName;
    private Button QR_Scan;
    private Button Notification;
    private Button ProfileModify;
    private ListView mylist;
    private EntrantMyListAdapter entrantAdapter;
    private OverallStorageController overallStorageController;
    private Entrant myEntrant;
    private List<NewPair<String, String>> eventsList;
    private String deviceId;
    private TextView notificationBadge;

    /**
     * Initializes the activity, sets up UI components, and loads the entrant's profile and event list.
     *
     * @param savedInstanceState the saved state of the activity
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_main_activity);

        // Initialize UI components
        profileImage = findViewById(R.id.profileImage);
        entrantName = findViewById(R.id.entrantName);
        mylist = findViewById(R.id.entrant_mylist);

        overallStorageController = new OverallStorageController();
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Floating button for event search
        FloatingActionButton eventSearch = findViewById(R.id.buttonEventSearch);
        eventSearch.setOnClickListener(v -> {
            Intent onlineSearch = new Intent(EntrantMylistActivity.this, EntrantSearchingActivity.class);
            startActivity(onlineSearch);
        });

        // QR Scan button
        QR_Scan = findViewById(R.id.buttonQRScan);
        QR_Scan.setOnClickListener(v -> {
            Intent intent = new Intent(EntrantMylistActivity.this, EntrantQRScanActivity.class);
            startActivity(intent);
        });

        // Profile modification button
        ProfileModify = findViewById(R.id.buttonProfile);
        ProfileModify.setOnClickListener(v -> {
            Intent intent = new Intent(EntrantMylistActivity.this, EntrantProfileActivity.class);
            intent.putExtra("deviceId", deviceId); // Pass the deviceId to load the correct profile
            startActivityForResult(intent, 100);
        });

        // Notification button
        Notification = findViewById(R.id.buttonNotification);
        notificationBadge = findViewById(R.id.notification_badge);
        notificationBadge.setVisibility(View.GONE);
        Notification.setOnClickListener(v -> {
            Intent intent = new Intent(EntrantMylistActivity.this, EntrantNotificationActivity.class);
            startActivity(intent);
            notificationBadge.setVisibility(View.GONE);
        });

        // Load profile and events
        updateProfile();

        // Check for notifications
        checkForNotifications();
    }

    /**
     * Checks for notifications and updates the badge visibility accordingly.
     */
    private void checkForNotifications() {
        overallStorageController.getEntrant(deviceId, new EntrantCallback() {
            @Override
            public void onSuccess(Entrant entrant) {
                List<NewPair<String, String>> notifications = entrant.getNotifications();
                if (!notifications.isEmpty()) {
                    // Show badge if there are unread notifications
                    notificationBadge.setVisibility(View.VISIBLE);
                    notificationBadge.setText(String.valueOf(notifications.size())); // Optional: show count
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                // Handle failure
            }
        });
    }

    /**
     * Decodes a Base64-encoded image string to a Bitmap.
     *
     * @param base64ImageString the Base64 string representing the image
     * @return the decoded Bitmap
     */
    public static Bitmap decodeBase64Image(String base64ImageString) {
        byte[] imageBytes = Base64.decode(base64ImageString, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    /**
     * Updates the UI of the entrant's event list.
     */
    public void updateUI() {
        entrantAdapter.notifyDataSetChanged();
    }

    /**
     * Handles the "Unjoin" action, removing the entrant from an event and updating the database.
     *
     * @param eventId the ID of the event to unjoin
     * @param id      the position of the event in the list
     */
    @Override
    public void onUnjoin(String eventId, int id) {
        myEntrant.UnjoinEvent(eventId);
        myEntrant.removeGeoPoint(eventId);
        overallStorageController.updateEntrant(myEntrant);
        eventsList.remove(id);
        updateUI();
        overallStorageController.getEvent(eventId, new EventCallback() {
            @Override
            public void onSuccess(Event event) {
                event.removeEntrant(deviceId);
                overallStorageController.updateEvent(event);
            }

            @Override
            public void onFailure(String errorMessage) {
                // Handle failure
            }
        });
    }

    /**
     * Gets the name of an event based on its ID.
     *
     * @param eventId the ID of the event
     * @return the name of the event
     */
    @Override
    public String getEventNameById(String eventId) {
        return myEntrant.getName(eventId);
    }

    /**
     * Handles new intents and updates the event status if required.
     *
     * @param intent the new intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        String updateEventId = intent.getStringExtra("update");
        if (Objects.equals(updateEventId, "SetImage") || Objects.equals(updateEventId, "SetNotification")) {
            updateProfile();
            return;
        }
        if (updateEventId != null) {
            overallStorageController.getEntrant(deviceId, new EntrantCallback() {
                @Override
                public void onSuccess(Entrant entrant) {
                    myEntrant = entrant;
                    updateEventsList();
                }

                @Override
                public void onFailure(String errorMessage) {
                    // Handle failure
                }
            });
        }

        checkForNotifications();
    }

    /**
     * Updates the event list from the entrant's data and refreshes the adapter.
     */
    private void updateEventsList() {
        Map<String, String> eventsStatus = myEntrant.getEventsStatus();
        eventsList = new ArrayList<>();
        for (String eventId : eventsStatus.keySet()) {
            String eventStatus = eventsStatus.get(eventId);
            if (eventStatus != null) {
                eventsList.add(new NewPair<>(eventId, eventStatus));
            }
        }
        entrantAdapter = new EntrantMyListAdapter(this, eventsList);
        mylist.setAdapter(entrantAdapter);
        updateUI();
    }

    /**
     * Opens the details of an event when selected by the entrant.
     *
     * @param eventId the ID of the event to view
     */
    @Override
    public void onView(String eventId) {
        Intent intent = new Intent(EntrantMylistActivity.this, EntrantEventDetail.class);
        intent.putExtra("eventID", eventId);
        startActivity(intent);
    }

    /**
     * Handles the result from profile modification, updating the profile image and name if
     * they were changed.
     *
     * @param requestCode the request code
     * @param resultCode  the result code indicating success or failure
     * @param data        the intent data containing updated profile information
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            String updatedName = data.getStringExtra("updatedName");
            String updatedProfileImageString = data.getStringExtra("updatedProfileImage");

            entrantName.setText(updatedName);
            profileImage.setImageBitmap(decodeBase64Image(updatedProfileImageString));
        }
    }

    /**
     * Updates the entrant's profile data, including the profile image, name, and list of joined events.
     */
    private void updateProfile() {
        overallStorageController.getEntrant(deviceId, new EntrantCallback() {
            @Override
            public void onSuccess(Entrant entrant) {
                myEntrant = entrant;
                entrantName.setText(entrant.getName());
                profileImage.setImageBitmap(decodeBase64Image(entrant.getProfilePhoto()));
                updateEventsList();
            }

            @Override
            public void onFailure(String errorMessage) {
                // Handle failure
            }
        });
    }
}
