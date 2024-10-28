package com.example.breeze0events;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EntrantEventDetail extends AppCompatActivity {
    private TextView event_title;
    private TextView event_information;
    private ImageView QRcode;
    private Button event_join;
    private Button event_cancel;
    private ImageView event_poster;
    private OverallStorageController overallStorageController;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_event_show);

        // initialize
        event_title = findViewById(R.id.entrant_event_title);
        event_information = findViewById(R.id.Event_information);
        QRcode = findViewById(R.id.Entrent_event_QRcode);
        event_join = findViewById(R.id.entrant_event_join);
        event_cancel = findViewById(R.id.entrant_event_cancel);
        event_poster = findViewById(R.id.Entrant_event_poster);
        overallStorageController = new OverallStorageController();
        Intent intent = getIntent();
        String id = intent.getStringExtra("eventID");

        // Receive data from firebase
        overallStorageController.getEvent(String.valueOf(id), new EventCallback() {
            // FIXME: 2024/10/27 Add functionality to show Max number of entrants
            @Override
            public void onSuccess(Event event) {
                event_title.setText(event.getName());
                String information = "Event Name: "+event.getName()
                        +"\nEvent Date: "+event.getStartDate()
                        +"\nSign up Due Date: "+event.getEndDate()
                        +"\nEvent Organizers: "+event.getOrganizers();
                event_information.setText(information);
            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });
    }
}
