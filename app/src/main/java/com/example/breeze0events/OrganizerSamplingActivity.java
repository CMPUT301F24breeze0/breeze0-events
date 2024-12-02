package com.example.breeze0events;

import static com.google.common.primitives.UnsignedInts.max;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
/**
 * OrganizerSamplingActivity allows event organizers to manage entrant sampling for events.
 * The activity provides functionalities to:
 * - Load and display entrant details.
 * - Categorize entrants based on their statuses (Requested, Accepted, Rejected, Joined).
 * - Pick new applicants to join the event.
 * - Finalize the event by rejecting remaining requests.
 */
public class OrganizerSamplingActivity extends AppCompatActivity {
    public OverallStorageController overallStorageController;
    private FirebaseFirestore db;
    private ListView entrantListViewRequested,entrantListViewAccepted,entrantListViewRejected;
    ArrayAdapter<String> entrantAdapterRequested,entrantAdapterAccepted,entrantAdapterRejected;
    ArrayList<String> entrantDisplayRequested,entrantDisplayAccepted,entrantDisplayRejected;
    ArrayList<DocumentSnapshot> joinedEntrants;
    private TextView remainingSlotsTextView;
    int limitedNumber; // Maximum number of entrants allowed
    int requestedCount,acceptedCount; // Number of entrants already requested
    public String eventId; // The ID of the current event
    Event selectedEvent;
    Button finalizeButton;
    /**
     * Initializes the activity and sets up UI components.
     *
     * @param savedInstanceState The saved instance state.
     */
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
        entrantListViewRequested = findViewById(R.id.organizer_sampling_activity_requested);
        entrantListViewAccepted = findViewById(R.id.organizer_sampling_activity_accepted);
        entrantListViewRejected = findViewById(R.id.organizer_sampling_activity_rejected);
        remainingSlotsTextView = findViewById(R.id.display_entrant_number_text);
        entrantDisplayRequested = new ArrayList<>();
        entrantDisplayAccepted = new ArrayList<>();
        entrantDisplayRejected = new ArrayList<>();
        joinedEntrants = new ArrayList<>();

        entrantAdapterRequested = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, entrantDisplayRequested);
        entrantListViewRequested.setAdapter(entrantAdapterRequested);

        entrantAdapterAccepted = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, entrantDisplayAccepted);
        entrantListViewAccepted.setAdapter(entrantAdapterAccepted);

        entrantAdapterRejected = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, entrantDisplayRejected);
        entrantListViewRejected.setAdapter(entrantAdapterRejected);

        // Assume eventId is passed via intent
       //  eventId = getIntent().getStringExtra("eventId");

        // Load data
        loadEventDetails(eventId);
       // loadEntrantsWithJoinedStatus();

        Button pickApplicantButton = findViewById(R.id.organizer_sampling_activity_pick_new_applicant_button);
        Button backButton = findViewById(R.id.organizer_sampling_activity_back_button);
        Button refreshButton = findViewById(R.id.organizer_sampling_activity_refresh_button);
        Button cancelButton = findViewById(R.id.cancel_button);
        finalizeButton = findViewById(R.id.finalist_button);

        // by clicking back button
        backButton.setOnClickListener(v -> finish());

        // by clicking pick new applicant button
        pickApplicantButton.setOnClickListener(v -> pickNewApplicants());

        // by clicking refresh button
        refreshButton.setOnClickListener(v -> loadEntrantsWithJoinedStatus());

        // Cancel entrants who requested to join the event
        cancelButton.setOnClickListener(v-> changeRequestToReject());


// Finalize the event by setting its limited number to 0
        finalizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Fetch the event details using the provided eventId
                overallStorageController.getEvent(eventId, new EventCallback() {
                    @Override
                    public void onSuccess(Event event) {
                        if (event != null) {
                            // Log the event retrieval for debugging purposes
                            Log.d("FinalizeEvent", "Successfully retrieved event: " + eventId);

                            // Set the event's limited number to 0 to finalize it
                            event.setLimitedNumber("0");

                            // Update the event in the database
                            overallStorageController.updateEvent(event);

                            // Provide feedback to the user after the update
                            Log.d("FinalizeEvent", "Event finalized successfully: " + eventId);
                            Toast.makeText(OrganizerSamplingActivity.this, "Event finalized successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            // Handle the case where the event is not found
                            Log.e("FinalizeEvent", "Event not found: " + eventId);
                            Toast.makeText(OrganizerSamplingActivity.this, "Event not found. Please check the event ID.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        // Log and display an error message if event retrieval fails
                        Log.e("FinalizeEvent", "Failed to retrieve event: " + errorMessage);
                        Toast.makeText(OrganizerSamplingActivity.this, "Failed to retrieve event. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
                changeRequestToReject();
            }
        });
    }
    /**
     * Loads event details such as the limited number of entrants allowed.
     *
     * @param eventId The ID of the event to load.
     */
    private void loadEventDetails(String eventId) {
        overallStorageController.getEvent(eventId, new EventCallback() {
            @Override
            public void onSuccess(Event event) {
                selectedEvent = event;

                // limitedNumber = Integer.parseInt(event.getLimitedNumber());

                try {
                    limitedNumber = Integer.parseInt(selectedEvent.getLimitedNumber());
                } catch (NumberFormatException e) {
                    Log.e("OrganizerSampling", "Invalid limitedNumber format: " + selectedEvent.getLimitedNumber());
                    limitedNumber = 0;
                }
                loadEntrantsWithJoinedStatus();
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("OrganizerSampling", "Failed to load event details: " + errorMessage);
                Toast.makeText(OrganizerSamplingActivity.this, "Failed to load event details", Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     * Loads entrants from the database and categorizes them based on their statuses.
     */
    private void loadEntrantsWithJoinedStatus() {
        entrantDisplayRequested.clear();
        entrantDisplayAccepted.clear();
        entrantDisplayRejected.clear();
        joinedEntrants.clear();
        requestedCount = 0;
        acceptedCount = 0;

        Log.d("OrganizerSampling", "eventId: " + eventId);

        db.collection("EntrantDB").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("OrganizerSampling", "Database query successful");

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("OrganizerSampling", "Processing document: " + document.getId());

                            Map<String, String> eventsMap = (Map<String, String>) document.get("events");
                            Map<String, String> statusMap = (Map<String, String>) document.get("status");

                            Log.d("OrganizerSampling", "Document eventsMap: " + eventsMap);
                            Log.d("OrganizerSampling", "Document statusMap: " + statusMap);

                            if (eventsMap != null && statusMap != null && eventsMap.containsKey(eventId) && statusMap.containsKey(eventId)) {
                                String status = statusMap.get(eventId);
                                Log.d("OrganizerSampling", "Found matching event with status: " + status);

                                if ("Requested".equals(status)) {
                                    String entrantName = document.getString("name");
                                    entrantDisplayRequested.add(entrantName != null ? entrantName : "Unknown Entrant");
                                    Log.d("OrganizerSampling", "Added entrant with status Selected: " + entrantName);
                                    requestedCount++;
                                    Log.d("OrganizerSampling", "Incremented requested count, current count: " + requestedCount);
                                } else if ("Joined".equals(status)) {
                                    joinedEntrants.add(document);
                                } else if ("Accepted".equals(status)){
                                    String entrantName = document.getString("name");
                                    entrantDisplayAccepted.add(entrantName != null ? entrantName : "Unknown Entrant");
                                    Log.d("OrganizerSampling", "Added entrant with status Selected: " + entrantName);
                                    acceptedCount++;
                                    Log.d("OrganizerSampling", "Incremented requested count, current count: " + acceptedCount);
                                }  else if ("Rejected".equals(status)){
                                    String entrantName = document.getString("name");
                                    entrantDisplayRejected.add(entrantName != null ? entrantName : "Unknown Entrant");
                                    Log.d("OrganizerSampling", "Added entrant with status Selected: " + entrantName);
                                }
                            } else {
                                Log.e("OrganizerSampling", "Invalid document structure for entrant: " + document.getId());
                            }
                        }

                        entrantAdapterRequested.notifyDataSetChanged();
                        entrantAdapterRejected.notifyDataSetChanged();
                        entrantAdapterAccepted.notifyDataSetChanged();

                        updateRemainingSlots();
                        Log.d("OrganizerSampling", "Total entrants displayed: " + entrantDisplayRequested.size());
                    } else {
                        Log.e("OrganizerSampling", "Failed to load entrants", task.getException());
                        Toast.makeText(OrganizerSamplingActivity.this, "Failed to load entrants", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    /**
     * Updates the text view displaying the remaining slots for entrants.
     */
    private void updateRemainingSlots() {
        int remainingSlots = max(limitedNumber - requestedCount - acceptedCount,0);
        remainingSlotsTextView.setText("Remaining slots: " + remainingSlots);
    }
    /**
     * Randomly picks new applicants from the list of joined entrants.
     */
    void pickNewApplicants() {
        int remainingSlots = max(limitedNumber - requestedCount - acceptedCount,0) ;
        if (remainingSlots <= 0) {
            Toast.makeText(this, "No remaining slots available", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("OrganizerSampling", "Remaining slots before selection: " + remainingSlots);

        Collections.shuffle(joinedEntrants);
        List<DocumentSnapshot> selectedEntrants = joinedEntrants.subList(0, Math.min(remainingSlots, joinedEntrants.size()));
        for (DocumentSnapshot entrant : selectedEntrants) {
            Map<String, String> statusMap = (Map<String, String>) entrant.get("status");
            Map<String, String> eventsMap = (Map<String, String>) entrant.get("events");

            if (eventsMap != null && statusMap != null && eventsMap.containsKey(eventId) && "Joined".equals(statusMap.get(eventId))) {
                statusMap.put(eventId, "Requested");
                String notificationMessage = "You are selected to join the event.";
                db.collection("EntrantDB").document(entrant.getId())
                        .update(
                                "status", statusMap,
                                "notifications", FieldValue.arrayUnion(new NewPair<>(selectedEvent.getName(), notificationMessage))
                        )
                        .addOnSuccessListener(aVoid -> Log.d("OrganizerSampling", "Status and notification updated to Selected for entrant: " + entrant.getId()))
                        .addOnFailureListener(e -> Log.e("OrganizerSampling", "Failed to update status or notification", e));
            }
        }

        loadEntrantsWithJoinedStatus();
    }
    /**
     * Changes the status of all "Requested" entrants to "Rejected."
     */
    void changeRequestToReject(){
        // Access the EntrantDB collection in the Firestore database
        db.collection("EntrantDB").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Log success when the database query is successful
                        Log.d("OrganizerSampling", "Database query successful");

                        // Iterate through all documents retrieved from the EntrantDB collection
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("OrganizerSampling", "Processing document: " + document.getId());

                            // Retrieve the events and status maps from the current document
                            Map<String, String> eventsMap = (Map<String, String>) document.get("events");
                            Map<String, String> statusMap = (Map<String, String>) document.get("status");

                            // Log the contents of the maps for debugging purposes
                            Log.d("OrganizerSampling", "Document eventsMap: " + eventsMap);
                            Log.d("OrganizerSampling", "Document statusMap: " + statusMap);

                            // Check if both maps are not null and contain the eventId as a key
                            if (eventsMap != null && statusMap != null && eventsMap.containsKey(eventId) && statusMap.containsKey(eventId)) {
                                // Retrieve the status of the current event
                                String status = statusMap.get(eventId);
                                Log.d("OrganizerSampling", "Found matching event with status: " + status);

                                // If the status is "Requested", update it to "Rejected"
                                if ("Requested".equals(status)) {
                                    statusMap.put(eventId, "Rejected");

                                    // Update the status map in the Firestore document
                                    db.collection("EntrantDB").document(document.getId()).update(
                                                    "status", statusMap
                                            ).addOnSuccessListener(aVoid ->
                                                    Log.d("OrganizerSampling", "Status updated to Rejected for entrant: " + document.getId()))
                                            .addOnFailureListener(e ->
                                                    Log.e("OrganizerSampling", "Failed to update status", e));
                                }
                            } else {
                                // Log an error if the document structure is invalid or does not contain the eventId
                                Log.e("OrganizerSampling", "Invalid document structure for entrant: " + document.getId());
                            }
                        }
                    } else {
                        // Log and display an error if the database query fails
                        Log.e("OrganizerSampling", "Failed to load entrants", task.getException());
                        Toast.makeText(OrganizerSamplingActivity.this, "Failed to load entrants", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}