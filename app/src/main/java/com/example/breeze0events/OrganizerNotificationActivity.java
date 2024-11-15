package com.example.breeze0events;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class OrganizerNotificationActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private OverallStorageController overallStorageController;
    private ListView contactListView;
    private ArrayAdapter<String> contactListAdapter;
    private ArrayList<String> contactList_display;
    private int pos;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_notification_activity);

        Button backButton = findViewById(R.id.organizer_notification_activity_back_button);
        Button refreshButton = findViewById(R.id.organizer_notification_activity_refresh_button);
        Button filterButton = findViewById(R.id.organizer_notification_activity_filter_button);
        Button messageButton = findViewById(R.id.organizer_notification_activity_message_button);

        overallStorageController = new OverallStorageController();

        // By clicking "Back" button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // by clicking "Refresh" button

        // By clicking "Filter" button

        // By clicking "Message" button

        // By selecting any contact on the list
    }
}
