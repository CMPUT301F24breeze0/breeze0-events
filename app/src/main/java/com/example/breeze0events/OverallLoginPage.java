package com.example.breeze0events;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;

import com.example.breeze0events.databinding.OrganizerMainActivityBinding;

public class OverallLoginPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the layout for the activity
        setContentView(R.layout.overall_login_page);

        // Initialize UI elements by finding them in the layout
        Button entrantButton = findViewById(R.id.entrant_button);
        Button organizerButton = findViewById(R.id.organizer_button);
        Button adminButton = findViewById(R.id.admin_button);

        // Set click listener for Entrant Button to navigate to EntrantEventActivity
        entrantButton.setOnClickListener(v -> {
            Intent intent = new Intent(OverallLoginPage.this, EntrantEventActivity.class);
            startActivity(intent);
        });

        // Set click listener for Organizer Button to navigate to OrganizerEventActivity
        organizerButton.setOnClickListener(v -> {
            Intent intent = new Intent(OverallLoginPage.this, OrganizerMainActivityBinding.class);
            startActivity(intent);
        });

        // Set click listener for Admin Button to navigate to AdminOperateActivity
        adminButton.setOnClickListener(v -> {
            Intent intent = new Intent(OverallLoginPage.this, AdminOperateActivity.class);
            startActivity(intent);
        });
    }
}
