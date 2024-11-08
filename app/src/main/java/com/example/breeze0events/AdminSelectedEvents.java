package com.example.breeze0events;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;


/**
 * The AdminSelectedEvents class displays the details of a selected event, allowing the admin
 * to view additional event information, navigate to more detailed views, or view the QR code
 * associated with the selected event.
 */

public class AdminSelectedEvents extends AppCompatActivity {
    TextView EventName;
    Button backButton,DetailButton,QRCodeButton,imageButton;
    Event selected_event;
    OverallStorageController overallStorageController;
    String facilityName;

    /**
     * Initializes the activity, sets up UI components, and retrieves the selected event details from
     * the intent. Allows the admin to view event details, navigate to event detail view, or view the QR code.
     *
     * @param savedInstanceState The saved instance state of the activity.
     *
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_guide_fragment); // Set the layout file

        // Retrieve the Event object from the Intent
        String Id = (String)getIntent().getSerializableExtra("selectedID");
        ArrayList<String> eventListDisplay = getIntent().getStringArrayListExtra("eventListDisplay");

        backButton = findViewById(R.id.backButton);
        DetailButton=findViewById(R.id.DetailButton);
        QRCodeButton=findViewById(R.id.QRCodeButton);
        EventName=findViewById(R.id.EventName);
        imageButton=findViewById(R.id.imageButton);


        backButton.setOnClickListener(v -> {
            finish();
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
                intent.putStringArrayListExtra("eventListDisplay", eventListDisplay);
                startActivity(intent);
            }
        });

        QRCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminSelectedEvents.this, AdminQRcode.class);
                intent.putExtra("qrcode", selected_event.getQrCode());
                startActivity(intent);
            }
        });

    }
}
