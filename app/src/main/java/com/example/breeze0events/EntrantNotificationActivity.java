package com.example.breeze0events;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * EntrantNotificationActivity represents receives notification from Admin or Organizers the
 * information or messages in history can be viewed here
 */
public class EntrantNotificationActivity extends AppCompatActivity implements EntrantNotificationAdapter.OnNotificationListener{
    private ListView notificationListView;
    private Button viewBlackListButton;
    private EntrantNotificationAdapter entrantNotificationAdapter;
    private List<Pair<String, String>> notificationsList;
    private String deviceId;
    private OverallStorageController overallStorageController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_notification);

        notificationListView = findViewById(R.id.notification_list);
        viewBlackListButton = findViewById(R.id.BlackList_Button);

        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        overallStorageController.getEntrant(deviceId, new EntrantCallback() {
            @Override
            public void onSuccess(Entrant entrant) {
                notificationsList = entrant.getNotifications();
                entrantNotificationAdapter = new EntrantNotificationAdapter(EntrantNotificationActivity.this, notificationsList, entrant);
                notificationListView.setAdapter(entrantNotificationAdapter);
            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });

        viewBlackListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EntrantNotificationActivity.this, BlackListActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void OnNotification(String eventId, int id) {
        Intent intent = new Intent(EntrantNotificationActivity.this, EntrantMylistActivity.class);
        intent.putExtra("update", "SetNotification");
        intent.putExtra("notification", eventId);
        startActivity(intent);
    }
}
