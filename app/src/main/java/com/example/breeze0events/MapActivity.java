package com.example.breeze0events;

import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MapActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        Button back_button = findViewById(R.id.map_activity_back_button);

        // by clicking "Back" button:
        back_button.setOnClickListener(v -> {
            Intent intent = new Intent(MapActivity.this, OrganizerMainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
