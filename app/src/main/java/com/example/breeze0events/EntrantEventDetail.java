package com.example.breeze0events;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileOutputStream;
import java.io.IOException;
import android.util.Base64;

public class EntrantEventDetail extends AppCompatActivity {
    private TextView event_title;
    private TextView event_information;
    private ImageView QRcode;
    private Button event_join;
    private Button event_cancel;
    private ImageView event_poster;
    private OverallStorageController overallStorageController;
    private String eventID;
    private String eventLocation;
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
                eventID = event.getEventId();
                eventLocation = event.getFacility();
                event_title.setText(event.getName());
                String information = "Event Name: "+event.getName()
                        +"\nEvent Date: "+event.getStartDate()
                        +"\nSign up Due Date: "+event.getEndDate()
                        +"\nEvent Organizers: "+event.getOrganizers();
                event_information.setText(information);
                event_poster.setImageBitmap(decodeBase64Image(event.getPosterPhoto()));
                QRcode.setImageBitmap(decodeBase64Image(event.getQrCode()));
            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });

        event_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        event_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                overallStorageController.getEntrant(deviceId, new EntrantCallback() {
                    @Override
                    public void onSuccess(Entrant entrant) {
                        entrant.addEvents(eventID, eventLocation);
                        overallStorageController.updateEntrant(entrant);
                    }

                    @Override
                    public void onFailure(String errorMessage) {

                    }
                });
                Intent go_back = new Intent(EntrantEventDetail.this, EntrantMylistActivity.class);
                go_back.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(go_back);
            }
        });
    }
    public static Bitmap decodeBase64Image(String base64ImageString) {
        byte[] imageBytes = Base64.decode(base64ImageString, Base64.DEFAULT);
        // 解码 Base64 字符串为字节数组
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

    }
}
