package com.example.breeze0events;

import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;

/**
 * OrganizerMapActivity provides for organizers to view a map.
 */
public class OrganizerMapActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        Button back_button = findViewById(R.id.map_activity_back_button);

        // by clicking "Back" button:
        back_button.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerMapActivity.this, OrganizerMyListActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
