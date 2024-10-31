package com.example.breeze0events;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

public class AdminEventActivity extends AppCompatActivity {
    private ListView eventListView;
    private ArrayAdapter<String> eventListAdapter;
    private ArrayList<String> eventList_display;
    private ArrayList<Event> eventList;
    public Event event;
    private OverallStorageController overallStorageController;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remove_events_page);
        Button return_button = findViewById(R.id.backButton);

        eventListView = findViewById(R.id.eventsList);
        eventList_display = new ArrayList<>();
        eventList = new ArrayList<>();
        overallStorageController = new OverallStorageController();

        eventListAdapter = new ArrayAdapter<>(this, R.layout.list_item_layout, eventList_display);
        eventListView.setAdapter(eventListAdapter);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection("OverallDB");

        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String docId = document.getId();
                        overallStorageController.getEvent(String.valueOf(docId), new EventCallback() {
                            @Override
                            public void onSuccess(Event event) {
                                String eventInfo = "Name: " + event.getName() + "\nStart_date: " + event.getStartDate()
                                        + "\nEnd_date: " + event.getEndDate();
                                eventList_display.add(eventInfo);
                                eventList.add(event);
                                eventListAdapter.notifyDataSetChanged();
                                Log.d("AdminEventData", "Event data fetched successfully: ");
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                Log.e("AdminEventData", "Failed to fetch organizer: " + errorMessage);

                            }
                        });
                    }
                } else {
                    Log.e("FirestoreError", "Error getting documents: ", task.getException());
                }
            }
        });

        return_button.setOnClickListener(v -> {
            finish();
        });

        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (eventList != null && position >= 0 && position < eventList.size()) {
                    Event selectedEvent = eventList.get(position);
                    Intent intent = new Intent(AdminEventActivity.this, AdminSelectedEvents.class);
                    intent.putExtra("selectedID", selectedEvent.getEventId());
                    startActivity(intent);
                } else {
                    Log.e("ItemClickError", "Invalid position or eventList is null");
                }
            }
        });
    }
}
