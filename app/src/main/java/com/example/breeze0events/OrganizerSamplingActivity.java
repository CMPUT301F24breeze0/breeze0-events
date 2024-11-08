package com.example.breeze0events;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrganizerSamplingActivity extends AppCompatActivity {
    private OverallStorageController overallStorageController;
    private FirebaseFirestore db;
    private ListView entrantListView;
    private ArrayAdapter<String> entrantAdapter;
    private ArrayList<String> entrantDisplayList;
    private ArrayList<DocumentSnapshot> joinedEntrants;
    private TextView remainingSlotsTextView;
    private int limitedNumber; // Maximum number of entrants allowed
    private int requestedCount; // Number of entrants already requested
    private String eventId; // The ID of the current event
    private Event selectedEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_sampling_activity);

        Log.d("OrganizerSampling", "onCreate started");
        Log.d("OrganizerSampling", "setContentView completed");

        eventId = getIntent().getStringExtra("eventId");
        selectedEvent = (Event) getIntent().getSerializableExtra("selected_event");

        if (eventId == null || selectedEvent == null || !eventId.equals(selectedEvent.getEventId())) {
            Log.e("OrganizerSampling", "eventId is null or does not match selectedEvent");
            finish();
            return;
        }

        overallStorageController = new OverallStorageController();
        db = FirebaseFirestore.getInstance();
        entrantListView = findViewById(R.id.organizer_sampling_activity_wait_list);
        remainingSlotsTextView = findViewById(R.id.display_entrant_number_text);
        entrantDisplayList = new ArrayList<>();
        joinedEntrants = new ArrayList<>();

        entrantAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, entrantDisplayList);
        entrantListView.setAdapter(entrantAdapter);

        // Assume eventId is passed via intent
       //  eventId = getIntent().getStringExtra("eventId");

        // Load data
        loadEventDetails(eventId);
        loadEntrantsWithJoinedStatus();

        Button pickApplicantButton = findViewById(R.id.organizer_sampling_activity_pick_new_applicant_button);
        Button backButton = findViewById(R.id.organizer_sampling_activity_back_button);
        Button refreshButton = findViewById(R.id.organizer_sampling_activity_refresh_button);

        // by clicking back button
        backButton.setOnClickListener(v -> finish());

        // by clicking pick new applicant button
        pickApplicantButton.setOnClickListener(v -> pickNewApplicants());

        // by clicking refresh button
        refreshButton.setOnClickListener(v -> loadEntrantsWithJoinedStatus());
    }

    private void loadEventDetails(String eventId) {
        overallStorageController.getEvent(eventId, new EventCallback() {
            @Override
            public void onSuccess(Event event) {
                selectedEvent = event;
                // limitedNumber = Integer.parseInt(event.getLimitedNumber()); // 确保 limitedNumber 是整数类型
                try {
                    limitedNumber = Integer.parseInt(selectedEvent.getLimitedNumber());
                } catch (NumberFormatException e) {
                    Log.e("OrganizerSampling", "Invalid limitedNumber format: " + selectedEvent.getLimitedNumber());
                    limitedNumber = 0; // 设置默认值，避免后续代码出错
                }
                loadEntrantsWithJoinedStatus();
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("OrganizerSampling", "Failed to load event details: " + errorMessage);
                Toast.makeText(OrganizerSamplingActivity.this, "Failed to load event details", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void loadEntrantsWithJoinedStatus() {
        entrantDisplayList.clear();
        joinedEntrants.clear();
        requestedCount = 0;

        // 使用 eventId 来过滤 EntrantDB 中符合条件的数据
        db.collection("EntrantDB").whereArrayContains("events", eventId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("OrganizerSampling", "Database query successful");

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            List<String> events = (List<String>) document.get("events");
                            List<String> statusList = (List<String>) document.get("status");

                            // 确保 events 和 status 列表存在且大小一致
                            if (events != null && statusList != null && events.size() == statusList.size()) {
                                for (int i = 0; i < events.size(); i++) {
                                    if (events.get(i).equals(eventId)) {
                                        String status = statusList.get(i);
                                        Log.d("OrganizerSampling", "Found matching event with status: " + status);

                                        if ("Joined".equals(status)) {
                                            String entrantName = document.getString("name");
                                            entrantDisplayList.add(entrantName != null ? entrantName : "Unknown Entrant");
                                            joinedEntrants.add(document);
                                            Log.d("OrganizerSampling", "Added entrant: " + entrantName);
                                        } else if ("Requested".equals(status)) {
                                            requestedCount++;
                                            Log.d("OrganizerSampling", "Incremented requested count, current count: " + requestedCount);
                                        }
                                    }
                                }
                            } else {
                                Log.e("OrganizerSampling", "Invalid document structure for entrant: " + document.getId());
                            }
                        }

                        // 通知适配器更新显示列表
                        entrantAdapter.notifyDataSetChanged();
                        updateRemainingSlots();
                        Log.d("OrganizerSampling", "Total entrants displayed: " + entrantDisplayList.size());
                    } else {
                        Log.e("OrganizerSampling", "Failed to load entrants", task.getException());
                        Toast.makeText(OrganizerSamplingActivity.this, "Failed to load entrants", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateRemainingSlots() {
        int remainingSlots = limitedNumber - requestedCount;
        remainingSlotsTextView.setText("Remaining slots: " + remainingSlots);
    }

    private void pickNewApplicants() {
        int remainingSlots = limitedNumber - requestedCount;
        if (remainingSlots <= 0) {
            Toast.makeText(this, "No remaining slots available", Toast.LENGTH_SHORT).show();
            return;
        }

        Collections.shuffle(joinedEntrants);
        List<DocumentSnapshot> selectedEntrants = joinedEntrants.subList(0, Math.min(remainingSlots, joinedEntrants.size()));

        for (DocumentSnapshot entrant : selectedEntrants) {
            List<String> statusList = (List<String>) entrant.get("status");
            List<String> eventsList = (List<String>) entrant.get("events");

            for (int i = 0; i < eventsList.size(); i++) {
                if (eventsList.get(i).equals(eventId) && "Joined".equals(statusList.get(i))) {
                    statusList.set(i, "Requested");
                }
            }

            db.collection("EntrantDB").document(entrant.getId()).update("status", statusList)
                    .addOnSuccessListener(aVoid -> Log.d("OrganizerSampling", "Status updated to Requested"))
                    .addOnFailureListener(e -> Log.e("OrganizerSampling", "Failed to update status", e));
        }

        loadEntrantsWithJoinedStatus();
    }
}