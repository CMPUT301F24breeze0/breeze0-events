package com.example.breeze0events;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * EntrantMylistActivity represents the main page for the Entrant containing the wishlist,
 * profile of Entrant
 */
// This is the main page of the Entrant containing the wishlist, profile of Entrant
public class EntrantMylistActivity extends AppCompatActivity implements EntrantMyListAdapter.OnUnjoinListener, EntrantMyListAdapter.EventNameProvider, EntrantMyListAdapter.ViewListener{
    private ImageView profileImage;
    private TextView entrantName;
    private Button QR_Scan;
    private TextView eventName;
    private Button EventStatus;
    private Button Blacklist;
    private Button QuietMode;
    private Button ProfileModify;
    private ListView mylist;
    private EntrantMyListAdapter EntrantAdapter;
    private OverallStorageController overallStorageController;
    private Entrant myEntrant;
    private  List<Pair<String, String>> eventsList;
    private String deviceId;

    /**
     * Initializes the activity, sets up UI components, and loads the entrant's profile and event list.
     *
     * @param savedInstanceState the saved state of the activity
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_main_activity);

        // Initialize UI
        profileImage = findViewById(R.id.profileImage);
        entrantName = findViewById(R.id.entrantName);
        QR_Scan = findViewById(R.id.buttonQRScan);

        Blacklist = findViewById(R.id.buttonBlacklist);
        QuietMode = findViewById(R.id.buttonQuietMode);
        ProfileModify = findViewById(R.id.buttonProfile);
        overallStorageController = new OverallStorageController();
        mylist = findViewById(R.id.entrant_mylist);
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Event Searching Functionality
        FloatingActionButton EventSearch = findViewById(R.id.buttonEventSearch);
        EventSearch.setOnClickListener(v->{
            Intent OnlineSearching = new Intent(EntrantMylistActivity.this, EntrantSearchingActivity.class);
            startActivity(OnlineSearching);
        });
        updateProfile();
        mylist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String eventId = eventsList.get(i).getLeft();
                Intent intent = new Intent(EntrantMylistActivity.this, EntrantEventDetail.class);
                intent.putExtra("eventID", eventId);
                startActivity(intent);
                return false;
            }
        });

        QR_Scan = findViewById(R.id.buttonQRScan);
        QR_Scan.setOnClickListener(v -> {
            Intent intent = new Intent(EntrantMylistActivity.this, EntrantQRScanActivity.class);
            startActivity(intent);
        });

        ProfileModify = findViewById(R.id.buttonProfile);
        ProfileModify.setOnClickListener(v -> {
            Intent intent = new Intent(EntrantMylistActivity.this, EntrantProfileActivity.class);
            intent.putExtra("deviceId", deviceId); // Pass the deviceId to load the correct profile
            startActivityForResult(intent, 100);
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
        // Decode Base64 string to byte array
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

    }
    /**
     * Updates the UI of the entrant's event list.
     */
    public void updateUI(){
        EntrantAdapter.notifyDataSetChanged();
    }

    /**
     * Handles the "Unjoin" action, removing the entrant from an event and updating the database.
     *
     * @param eventId the ID of the event to unjoin
     * @param id      the position of the event in the list
     */
    // Unjoin event
    @Override
    public void onUnjoin(String eventId, int id) {
        myEntrant.UnjoinEvent(eventId);
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

            }
        });
    }

    /**
     * Gets the name of an event based on its ID.
     *
     * @param eventId the ID of the event
     * @return the name of the event
     */
    // Get event name
    @Override
    public String getEventNameById(String eventId) {
        return myEntrant.getName(eventId);
    }

    /**
     * Refreshes the list of events when a new intent is received. This is used for updating
     * event statuses.
     *
     * @param intent
     * the new intent containing the event update information
     */
    // Update event status
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        String update_event_id = intent.getStringExtra("update");
        if (Objects.equals(update_event_id, "SetImage")){
            updateProfile();
            return;
        }
        if (update_event_id != null) {
            overallStorageController.getEntrant(deviceId, new EntrantCallback() {
                @Override
                public void onSuccess(Entrant entrant) {
                    myEntrant = entrant;
                    eventsList.add(new Pair<>(update_event_id, myEntrant.getStatus(update_event_id)));
                    updateUI();
                }
                @Override
                public void onFailure(String errorMessage) {
                }
            });
        }
    }

    /**
     * Opens the details of an event when selected by the entrant.
     *
     * @param eventId the ID of the event to view
     */
    // View event
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
    // Get updated profile data on Mylist
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
    // Update profile
    private void updateProfile(){
        overallStorageController.getEntrant(deviceId, new EntrantCallback() {
            @Override
            public void onSuccess(Entrant entrant) {
                myEntrant = entrant;
                entrantName.setText(entrant.getName());
                profileImage.setImageBitmap(decodeBase64Image(entrant.getProfilePhoto()));
                Map<String, String> eventsStatus = entrant.getEventsStatus();
                eventsList = new ArrayList<>();
                for (String eventId:  eventsStatus.keySet()) {
                    String eventStatus = eventsStatus.get(eventId);
                    if (eventStatus != null) {
                        eventsList.add(new Pair<>(eventId, eventStatus));
                    }
                }
                EntrantAdapter = new EntrantMyListAdapter(EntrantMylistActivity.this, eventsList);
                mylist.setAdapter(EntrantAdapter);
            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });
    }
}
