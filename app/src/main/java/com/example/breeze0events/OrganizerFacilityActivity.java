package com.example.breeze0events;

import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatActivity;

public class OrganizerFacilityActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_facility_activity);

        Button back_button = findViewById(R.id.organizer_facility_activity_back_button);
        Button new_facility_button = findViewById(R.id.new_facility_button);

        // by clicking "Back" button:
        back_button.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerFacilityActivity.this, OrganizerMainActivity.class);
            startActivity(intent);
            finish();
        });

    }


}
