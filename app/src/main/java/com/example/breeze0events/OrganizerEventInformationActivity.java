package com.example.breeze0events;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class OrganizerEventInformationActivity extends AppCompatActivity {

    Button backButton,organizerButton,entrantButton,nameButton,facilityButton,qrCodeButton;
    Event selected_event;
    OverallStorageController overallStorageController;
    String facilityName;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_specific_event_information); // Set the layout file

        // Retrieve the Event object from the Intent
        String Id = (String)getIntent().getSerializableExtra("selected_event_id");

        // Initialize the back button
        backButton = findViewById(R.id.back_button);
        organizerButton=findViewById(R.id.organizers_text);
        entrantButton=findViewById(R.id.entrants_text);
        nameButton=findViewById(R.id.event_name);
        facilityButton=findViewById(R.id.facility_text);
        qrCodeButton=findViewById(R.id.qr_code_text);
        // Set click listener for back button
        backButton.setOnClickListener(v -> {
            // Return to OrganizerMyListActivity and destroy current activity
            finish(); // Close current activity
        });
        overallStorageController=new OverallStorageController();
        overallStorageController.getEvent(Id, new EventCallback() {
            @Override
            public void onSuccess(Event event) {
               selected_event=event;
                nameButton.setText(selected_event.getName());
            }

            @Override
            public void onFailure(String errorMessage) {
            }
        });
        nameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrganizerEventInformationActivity.this, OrganizerEventDisplayDate.class);
                intent.putExtra("start_date",selected_event.getStartDate());
                intent.putExtra("end_date",selected_event.getEndDate());
                startActivity(intent);
            }
        });
        facilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String facilityId=selected_event.getFacility();
                overallStorageController=new OverallStorageController();
                overallStorageController.getFacility(facilityId, new FacilityCallback() {
                    @Override
                    public void onSuccess(Facility facility) {
                        facilityName=facility.getLocation();
                        Intent intent = new Intent(OrganizerEventInformationActivity.this, OrganizerEventDisplayFacility.class);
                        intent.putExtra("facility_id",facilityId);
                        intent.putExtra("facility_name",facilityName);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(String errorMessage) {

                    }
                });
            }
        });
        entrantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrganizerEventInformationActivity.this, OrganizerEventDisplayEntrants.class);
                intent.putStringArrayListExtra("entrants_id", new ArrayList<>(selected_event.getEntrants()));
                startActivity(intent);
            }
        });
        organizerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrganizerEventInformationActivity.this, OrganizerEventDisplayOrganizers.class);
                intent.putStringArrayListExtra("organizers_id", new ArrayList<>(selected_event.getOrganizers()));
                startActivity(intent);
            }
        });
        qrCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrganizerEventInformationActivity.this, OrganizerEventDisplayQRcode.class);
                intent.putExtra("qrcode", selected_event.getQrCode());
                startActivity(intent);
            }
        });
    }
}
