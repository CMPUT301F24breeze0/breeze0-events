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
    private ListView eventListView;
    private ArrayAdapter<String> eventListAdapter;
    private ArrayList<String> eventList_display;
    private ArrayList<Event> eventList;
    int limit=100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_main_activity);

        Button map_button = findViewById(R.id.map_button);
        Button my_facility_button = findViewById(R.id.my_facility_button);
        Button new_event_button = findViewById(R.id.new_event_button);

        overallStorageController = new OverallStorageController();
        eventListView = findViewById(R.id.organizer_event_list);
        eventList_display = new ArrayList<>();
        eventList=new ArrayList<>();
        eventListAdapter = new ArrayAdapter<>(this, R.layout.list_item_layout, eventList_display);
        eventListView.setAdapter(eventListAdapter);

        for (int i=0; i<=limit; i++) {
            overallStorageController.getEvent(String.valueOf(i), new EventCallback() {
                @Override
                public void onSuccess(Event event) {
                 //   event.setStartDate("2001-10-21");
                 //   overallStorageController.updateEvent("2",event);
                    String eventInfo = "Name: " + event.getName() + "\nStart_date: " + event.getStartDate()
                            + "\nEnd_date: " + event.getEndDate();
                    eventList_display.add(eventInfo);
                    eventList.add(event);
                    eventListAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(String errorMessage) {

                }
            });
        }
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
