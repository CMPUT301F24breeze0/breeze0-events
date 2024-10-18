package com.example.breeze0events;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.CollectionReference;

public class OrganizerEventActivity extends AppCompatActivity {

    ArrayList<String> turn(ArrayList<Event> EventList){
        ArrayList<String> EventStringList = new ArrayList<>();
        for (Event event : EventList) {
            EventStringList.add("Name: "+event.name + '\n' + "Start Date: "+event.start_date + '\n' + "End Date: "+event.end_date + '\n');
        }
        return EventStringList;
    }

    private FirebaseFirestore db;
    private ArrayAdapter<String> adapter;
    private ArrayList<Event> eventList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_event_activity);

        Button button_mylist = findViewById(R.id.button_mylist);
        Button button_profile = findViewById(R.id.button_profile);
        Button button_events = findViewById(R.id.button_events);

        ListView listView = findViewById(R.id.my_list_view);

        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("OverallDB");

        adapter = new ArrayAdapter<>(this,
                R.layout.list_item_layout, R.id.text_item,
                turn(eventList));

        listView.setAdapter(adapter);

        db.collection("OverallDB")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (DocumentSnapshot document : querySnapshot) {
                                String event_id = document.getId();
                                String name = document.getString("name");
                                String QRcode = document.getString("QRcode");
                                String start_date = document.getString("start_date");
                                 String end_date = document.getString("end_date");
                                String poster_photo = document.getString("poster_photo");

                                ArrayList<String> entrants = (ArrayList<String>) document.get("entrants");
                                ArrayList<String> organizers = (ArrayList<String>) document.get("organizers");

                                Event event = new Event(event_id, name, QRcode, start_date, end_date, poster_photo, entrants, organizers);
                                eventList.add(event);

                                Log.d(TAG, "Event added: " + event.name);
                            }

                            adapter.clear();
                            adapter.addAll(turn(eventList));
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Log.w(TAG, "Error getting documents: ", task.getException());
                    }
                });

        button_mylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrganizerEventActivity.this, OrganizerMyListActivity.class);
                startActivity(intent);
            }
        });

        button_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrganizerEventActivity.this, OrganizerProfileActivity.class);
                startActivity(intent);
            }
        });
    }
}
