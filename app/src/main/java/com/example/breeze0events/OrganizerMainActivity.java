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

public class OrganizerMainActivity extends AppCompatActivity {
    ArrayList<String> turn(ArrayList<Event> EventList){
        ArrayList<String>EventStringList=new ArrayList<>();
        for (Event event:EventList) {
            EventStringList.add(event.name + '\n' + event.start_date + '\n' + event.end_date + '\n');
        }
        return EventStringList;
    }
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_main_activity);

        Button map_button = findViewById(R.id.map_button);
        Button my_facility_button = findViewById(R.id.my_facility_button);
        Button new_event_button = findViewById(R.id.new_event_button);
        ListView entrantListView = findViewById(R.id.organizer_event_list);
        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("OverallDB");
        // Initialize the ArrayList
        ArrayList<Event> eventList = new ArrayList<>();
        Adapter adapter = new ArrayAdapter<>(this,
                R.layout.list_item_layout, R.id.text_item,
                turn(eventList));

        // 这行有报错，改一下
        //entrantListView.setAdapter(adapter);

        db.collection("OverallDB");
/*        db.collection("OverallDB")
>>>>>>> main:app/src/main/java/com/example/breeze0events/OrganizerEventActivity.java
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (DocumentSnapshot document : querySnapshot) {
                                // Extract data from the Firestore document
                                System.out.println(1);
                                String event_id = document.getId();
                                String name = document.getString("name");
                                String QRcode = document.getString("QRcode");
                                String start_date = document.getString("start_date");
                                String end_date = document.getString("end_date");
                                String poster_photo = document.getString("poster_photo");

                                // Convert the entrants and organizers fields to ArrayLists
                                ArrayList<String> entrants = (ArrayList<String>) document.get("entrants");
                                ArrayList<String> organizers = (ArrayList<String>) document.get("organizers");

                                // Create a new Event object
                                Event event = new Event(event_id, name, QRcode, start_date, end_date, poster_photo, entrants, organizers);

                                // Add the Event object to the list
                                eventList.add(event);

                                // Log the event data for debugging
                                Log.d(TAG, "Event added: " + event.name);
                            }


                        }
                    } else {
                        Log.w(TAG, "Error getting documents: ", task.getException());
                    }
                });*/

        // by clicking "Map" button:
        map_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrganizerMainActivity.this, MapActivity.class);
                startActivity(intent);
            }
                                      });
        // by clicking "My Facility" button:
        my_facility_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrganizerMainActivity.this, OrganizerFacilityActivity.class);
                startActivity(intent);
            }
        });


    }
}
