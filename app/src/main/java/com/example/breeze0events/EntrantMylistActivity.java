package com.example.breeze0events;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

// This is the main page of the Entrant containing the wishlist, profile of Entrant
public class EntrantMylistActivity extends AppCompatActivity  {
    private ImageView profileImage;
    private TextView entrantName;
    private Button QR_Scan;
    private TextView eventName;
    private Button EventStatus;
    private Button Blacklist;
    private Button QuietMode;
    private Button ProfileModify;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_main_activity);

        // Initialize UI
        profileImage = findViewById(R.id.profileImage);
        entrantName = findViewById(R.id.entrantName);
        QR_Scan = findViewById(R.id.buttonQRScan);
        eventName = findViewById(R.id.eventName);
        EventStatus = findViewById(R.id.buttonEventStatus);
        Blacklist = findViewById(R.id.buttonBlacklist);
        QuietMode = findViewById(R.id.buttonQuietMode);
        ProfileModify = findViewById(R.id.buttonProfile);

        // Event Searching Functionality
        FloatingActionButton EventSearch = findViewById(R.id.buttonEventSearch);
        EventSearch.setOnClickListener(v->{
            Intent OnlineSearching = new Intent(EntrantMylistActivity.this, EntrantSearchingActivity.class);
            startActivity(OnlineSearching);
        });
    }
}
