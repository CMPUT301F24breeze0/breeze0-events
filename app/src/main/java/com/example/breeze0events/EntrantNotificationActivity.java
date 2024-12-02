package com.example.breeze0events;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * EntrantNotificationActivity represents the notification box for the entrant.
 */
public class EntrantNotificationActivity extends AppCompatActivity implements EntrantNotificationAdapter.OnNotificationListener {
    private ListView notificationListView;
    private Button quietModeButton, backButton;
    private EntrantNotificationAdapter entrantNotificationAdapter;
    private List<NewPair<String, String>> notificationsList;
    private String deviceId;
    private OverallStorageController overallStorageController;
    private boolean isQuietModeEnabled = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_notification);

        // Initialize UI components
        notificationListView = findViewById(R.id.notification_list);
        quietModeButton = findViewById(R.id.quietMode_Button);
        backButton = findViewById(R.id.back_in_main);

        // Get device ID
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        overallStorageController = new OverallStorageController();
        notificationsList = new ArrayList<>();
        entrantNotificationAdapter = new EntrantNotificationAdapter(EntrantNotificationActivity.this, notificationsList);
        notificationListView.setAdapter(entrantNotificationAdapter);

        // Fetch notifications
        updateNotifications();

        // Quiet Mode Toggle
        quietModeButton.setOnClickListener(v -> toggleQuietMode());

        // Back to My List
        backButton.setOnClickListener(v -> finish());
    }

    /**
     * Fetches notifications from the database and updates the list.
     */
    private void updateNotifications() {
        overallStorageController.getEntrant(deviceId, new EntrantCallback() {
            @Override
            public void onSuccess(Entrant entrant) {
                if (!isQuietModeEnabled) {
                    notificationsList.clear(); // Clear existing notifications to prevent duplicates
                    notificationsList.addAll(entrant.getNotifications());
                    entrantNotificationAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                // Handle failure
            }
        });
    }

    /**
     * Toggles the quiet mode state.
     */
    private void toggleQuietMode() {
        isQuietModeEnabled = !isQuietModeEnabled;
        quietModeButton.setText(isQuietModeEnabled ? "Disable Quiet Mode" : "Enable Quiet Mode");

        if (!isQuietModeEnabled) {
            updateNotifications();
        }
    }

    @Override
    public void OnNotification() {
        Intent intent = new Intent(EntrantNotificationActivity.this, EntrantMylistActivity.class);
        intent.putExtra("update", "SetNotification");
        startActivity(intent);
    }
}
