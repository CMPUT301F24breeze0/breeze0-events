package com.example.breeze0events;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AdminSelectedEvents extends AppCompatActivity {
    TextView EventName;
    Button backButton,DetailButton,QRCodeButton,imageButton;
    Event selected_event;
    OverallStorageController overallStorageController;
    String facilityName;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_guide_fragment); // Set the layout file

        // Retrieve the Event object from the Intent
        String Id = (String)getIntent().getSerializableExtra("selectedID");


        // Initialize the back button

        backButton = findViewById(R.id.backButton);
        DetailButton=findViewById(R.id.DetailButton);
        QRCodeButton=findViewById(R.id.QRCodeButton);
        EventName=findViewById(R.id.EventName);
        imageButton=findViewById(R.id.imageButton);

        // Set click listener for back button
        backButton.setOnClickListener(v -> {
            finish(); // Close current activity
        });

        overallStorageController=new OverallStorageController();
        overallStorageController.getEvent(Id, new EventCallback() {
            @Override
            public void onSuccess(Event event) {
                selected_event=event;
                EventName.setText(selected_event.getName());
            }

            @Override
            public void onFailure(String errorMessage) {
            }
        });


        DetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminSelectedEvents.this, AdminEventDetail.class);
                intent.putExtra("selectedID", selected_event.getEventId());
                intent.putExtra("start_date",selected_event.getStartDate());
                intent.putExtra("end_date",selected_event.getEndDate());
                startActivity(intent);
            }
        });
    }
}
