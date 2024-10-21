package com.example.breeze0events;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;

public class OrganizerMyListActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_main_activity);

        Button map_button = findViewById(R.id.map_button);
        Button my_facility_button = findViewById(R.id.my_facility_button);
        Button new_event_button = findViewById(R.id.new_event_button);
        ListView entrantListView = findViewById(R.id.organizer_event_list);

        // by clicking "Map" button:
        map_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrganizerMyListActivity.this, OrganizerMapActivity.class);
                startActivity(intent);
            }
                                      });
        // by clicking "My Facility" button:
        my_facility_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrganizerMyListActivity.this, OrganizerFacilityActivity.class);
                startActivity(intent);
            }
        });


    }
}
