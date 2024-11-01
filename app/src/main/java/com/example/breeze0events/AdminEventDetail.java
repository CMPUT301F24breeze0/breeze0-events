package com.example.breeze0events;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class AdminEventDetail extends AppCompatActivity {
    private OverallStorageController overallStorageController;
    private TextView eventTitle;
    private TextView eventName;
    private TextView eventDate;
    private TextView maxEntrants;
    private TextView signUpDueDay;
    private TextView eventDescription;
    Event selected_event;
    private String eventID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_detail_fragment);

        Button return_button = findViewById(R.id.cancelButton);
        eventTitle = findViewById(R.id.EventDetail);
        eventName = findViewById(R.id.EventName);
        eventDate = findViewById(R.id.EventDate);
        maxEntrants = findViewById(R.id.MaxEntrants);
        signUpDueDay = findViewById(R.id.duedate);
        eventDescription = findViewById(R.id.description);
        overallStorageController = new OverallStorageController();
        String id = (String)getIntent().getSerializableExtra("selectedID");

        overallStorageController.getEvent(String.valueOf(id), new EventCallback() {
            @Override
            public void onSuccess(Event event) {
                selected_event=event;
                eventTitle.setText("Event Detail");
                eventName.setText("Event Name: " + selected_event.getName());
                eventDate.setText("Event start from " + event.getStartDate() + " - " + event.getEndDate());
                maxEntrants.setText("Max number of entrants: " + String.valueOf(event.getEntrants().size()));
                signUpDueDay.setText("Sign-up due: " + /* add due date if available */ "");
                //eventDescription.setText(event.getDescription());

                Log.d("AdminEventDetail", "Admins data fetched successfully: ");
            }
            @Override
            public void onFailure(String errorMessage) {
                Log.e("AdminEventDetail", "Failed to fetch admins: " + errorMessage);

            }
        });

        return_button.setOnClickListener(v -> {
            finish();
        });

    }
}
