package com.example.breeze0events;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class AdminEventDetail extends AppCompatActivity {
    private FirebaseFirestore db;
    private OverallStorageController overallStorageController;
    private TextView eventTitle;
    private TextView eventName;
    private TextView eventDate;
    private TextView maxEntrants;
    private TextView signUpDueDay;
    private TextView eventDescription;

    private String eventID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_detail_fragment);
        eventTitle = findViewById(R.id.EventDetail);
        eventName = findViewById(R.id.EventName);
        eventDate = findViewById(R.id.EventDate);
        maxEntrants = findViewById(R.id.MaxEntrants);
        signUpDueDay = findViewById(R.id.duedate);
        eventDescription = findViewById(R.id.description);
        overallStorageController = new OverallStorageController();
        Intent intent = getIntent();

        String id = intent.getStringExtra("eventID");


        overallStorageController.getEvent(String.valueOf(id), new EventCallback() {
            @Override
            public void onSuccess(Event event) {

                eventID = event.getEventId();
                eventTitle.setText("Event Details");
                eventName.setText(event.getName());
                eventDate.setText(event.getStartDate() + " - " + event.getEndDate());
                maxEntrants.setText(String.valueOf(event.getEntrants().size()));
                signUpDueDay.setText("Sign-up due: " + /* add due date if available */ "");
                //eventDescription.setText(event.getDescription());

                Log.d("AdminOrganizerProfile", "Organizer data fetched successfully: ");
            }
            @Override
            public void onFailure(String errorMessage) {
                Log.e("AdminOrganizerProfile", "Failed to fetch organizer: " + errorMessage);

            }
        });
    }
}
