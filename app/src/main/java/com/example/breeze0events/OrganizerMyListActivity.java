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
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;

public class OrganizerMyListActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private OverallStorageController overallStorageController;
    private ListView entrantListView;
    private ArrayAdapter<String> entrantListAdapter;
    private ArrayList<String> entrantList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_main_activity);

        Button map_button = findViewById(R.id.map_button);
        Button my_facility_button = findViewById(R.id.my_facility_button);
        Button new_event_button = findViewById(R.id.new_event_button);
        ListView entrantListView = findViewById(R.id.organizer_event_list);

        overallStorageController = new OverallStorageController();
        entrantListView = findViewById(R.id.organizer_event_list);
        entrantList = new ArrayList<>();
        entrantListAdapter = new ArrayAdapter<>(this, R.layout.list_item_layout, entrantList);
        entrantListView.setAdapter(entrantListAdapter);

        overallStorageController.getEvent("1", new EventCallback() {
            @Override
            public void onSuccess(Event event) {

                String eventInfo = "Name: " + event.getName()+"\nStart_date: "+event.getStartDate()
                        +"\nEnd_date: "+event.getEndDate();
                entrantList.add(eventInfo);

                entrantListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorMessage) {

                Toast.makeText(OrganizerMyListActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

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
