package com.example.breeze0events;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
    private ArrayList<String> eventListDisplay = new ArrayList<>();
    ;
    private ArrayList<Event> eventList = new ArrayList<>();
    ;
    public Event event;
    private Button refreshButton;
    private OverallStorageController overallStorageController;
    private final ActivityResultLauncher<Intent> eventsLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        eventListDisplay = data.getStringArrayListExtra("UPDATED_LIST");
                        eventListAdapter.clear();
                        if (eventListDisplay != null) {
                            eventListAdapter.addAll(eventListDisplay);
                        }
                        eventListAdapter.notifyDataSetChanged();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remove_events_page);
        Button return_button = findViewById(R.id.backButton);
        refreshButton = findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(v -> refreshEventList());

        eventListView = findViewById(R.id.eventsList);
        overallStorageController = new OverallStorageController();

        eventListAdapter = new ArrayAdapter<>(this, R.layout.list_item_layout, eventListDisplay);
        eventListView.setAdapter(eventListAdapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection("OverallDB");

        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String id = document.getId();
                        overallStorageController.getEvent(String.valueOf(id), new EventCallback() {
                            @Override
                            public void onSuccess(Event event) {
                                String info = "Name: " + event.getName() + "\nStart_date: " + event.getStartDate()
                                        + "\nEnd_date: " + event.getEndDate();
                                eventListDisplay.add(info);
                                eventList.add(event);
                                eventListAdapter.notifyDataSetChanged();
                                Log.d("AdminEventData", "Event data fetched successfully: ");
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                Log.e("AdminEventData", "Failed to fetch admins: " + errorMessage);

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
                    intent.putStringArrayListExtra("eventListDisplay", eventListDisplay);
                    eventsLauncher.launch(intent);

                } else {
                    Log.e("ItemClickError", "Invalid position or eventList is null");
                }
            }
        });
    }

    private void refreshEventList() {
        eventList.clear();
        eventListDisplay.clear();
        eventListAdapter.notifyDataSetChanged();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection("OverallDB");

        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String id = document.getId();
                        overallStorageController.getEvent(String.valueOf(id), new EventCallback() {
                            @Override
                            public void onSuccess(Event event) {
                                String info = "Name: " + event.getName() + "\nStart_date: " + event.getStartDate()
                                        + "\nEnd_date: " + event.getEndDate();
                                eventListDisplay.add(info);
                                eventList.add(event);
                                eventListAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                Log.e("AdminEventActivity", "Failed to fetch event data: " + errorMessage);
                            }
                        });
                    }
                    Toast.makeText(AdminEventActivity.this, "Event list refreshed", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("FirestoreError", "Error to fetch DB: ", task.getException());
                }
            }
        });

    }
}
