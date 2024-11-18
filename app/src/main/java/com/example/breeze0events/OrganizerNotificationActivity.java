package com.example.breeze0events;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.util.Pair;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class OrganizerNotificationActivity extends AppCompatActivity{
    private FirebaseFirestore db;
    private OverallStorageController overallStorageController;
    private ListView contactListView;
    private OrganizerNotificationCustomAdapter contactListAdapter;
    private ArrayList<Pair<String, String>> contactList_display;
    private int pos;
    private String organizerId;
    private HashSet<Integer> selectedPositions;
    private String selectedEventId = null;
    private boolean isAllSelected = false;
    private String selectedEventName = "";
    private List<String> selectedStatuses = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.organizer_notification_activity);

        Button backButton = findViewById(R.id.organizer_notification_activity_back_button);
        Button refreshButton = findViewById(R.id.organizer_notification_activity_refresh_button);
        Button filterButton = findViewById(R.id.organizer_notification_activity_filter_button);
        Button messageButton = findViewById(R.id.organizer_notification_activity_message_button);
        Button selectAllButton = findViewById(R.id.organizer_notification_activity_select_all_button);
        contactListView = findViewById(R.id.contact_list_view);
        contactList_display = new ArrayList<>();
        selectedPositions = new HashSet<>();
        // contactListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contactList_display);
        contactListAdapter = new OrganizerNotificationCustomAdapter(this, contactList_display, selectedPositions);
        contactListView.setAdapter(contactListAdapter);
        organizerId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        // findEntrants();

        overallStorageController = new OverallStorageController();

        // By clicking "Back" button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // by clicking "Refresh" button

        // By clicking "Filter" button
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEventFilterDialog();
            }
        });

        // By clicking "Message" button
        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPositions.isEmpty()) {
                    Toast.makeText(OrganizerNotificationActivity.this, "Please Select an Entrant", Toast.LENGTH_SHORT).show();
                } else {
                    showMessageDialog();
                }
            }
        });

        // By clicking "Select All" button
        selectAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contactList_display.isEmpty()) {
                    Toast.makeText(OrganizerNotificationActivity.this, "No entrants to select", Toast.LENGTH_SHORT).show();
                } else {
                    if (!isAllSelected) {
                        for (int i = 0; i < contactList_display.size(); i++) {
                            selectedPositions.add(i);
                        }
                        isAllSelected = true;
                        selectAllButton.setText("Deselect All");
                        Toast.makeText(OrganizerNotificationActivity.this, "All entrants selected", Toast.LENGTH_SHORT).show();
                    } else {
                        selectedPositions.clear();
                        isAllSelected = false;
                        selectAllButton.setText("Select All");
                        Toast.makeText(OrganizerNotificationActivity.this, "Selection cleared", Toast.LENGTH_SHORT).show();
                    }
                    contactListAdapter.notifyDataSetChanged();
                }
            }
        });

        // By selecting any contact on the list
        contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Toggle selection
                if (selectedPositions.contains(position)) {
                    selectedPositions.remove(position);
                } else {
                    selectedPositions.add(position);
                }
                contactListAdapter.notifyDataSetChanged(); // update ui
            }
        });

    }

    private void findEntrants() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection("OverallDB");

        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        try {
                            List<String> organizers = (List<String>) document.get("organizers");
                            int eventId = Integer.parseInt(document.getId());
                            List<String> entrants = (List<String>) document.get("entrants");

                            if (organizers != null && organizers.contains(organizerId)) {
                                contactList_display.clear();
                                // contactList_display.addAll(entrants);
                                for (String entrantId : entrants) {
                                    contactList_display.add(new Pair<>(entrantId, ""));
                                }
                                contactListAdapter.notifyDataSetChanged();
                                Log.d("OrganizerMyListActivity", "Event ID: " + eventId + ", Entrants: " + entrants);
                            }
                        } catch (ClassCastException e) {
                            Log.e("OrganizerMyListActivity", "Invalid data type for entrants: " + document.get("entrants"));
                        } catch (NumberFormatException e) {
                            Log.e("OrganizerMyListActivity", "Invalid eventId format: " + document.getId());
                        }
                    }

                } else {
                    Log.e("FirestoreError", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void showMessageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Send Notification");

        final EditText input = new EditText(this);
        input.setHint("Enter Your Notification");
        builder.setView(input);

        // edit text and send button
        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String message = input.getText().toString().trim();
                if (!message.isEmpty()) {
                    sendMessageToDatabase(message);
                } else {
                    Toast.makeText(OrganizerNotificationActivity.this, "Notification Cannot Be Empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Cancel button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    private void sendMessageToDatabase(String message) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (int position : selectedPositions) {
            String entrantId = contactList_display.get(position).first;

            db.collection("EntrantDB").document(entrantId)
                    .update("notifications", FieldValue.arrayUnion(new NewPair<>(selectedEventName, message)))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("DatabaseUpdate", "Notification Sent Successfully");
                                Toast.makeText(OrganizerNotificationActivity.this, "Notification Sent", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e("DatabaseUpdate", "Notification Sent Failed", task.getException());
                            }
                        }
                    });
        }
    }

    private void showEventFilterDialog() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("OrganizerDB").document(organizerId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();
                            List<String> events = (List<String>) document.get("events");

                            if (events != null && !events.isEmpty()) {
                                showStatusSelectionDialog(events);
                            } else {
                                Toast.makeText(OrganizerNotificationActivity.this, "No events found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("FirestoreError", "Error getting events", task.getException());
                        }
                    }
                });
    }


    private void showStatusSelectionDialog(List<String> eventIds) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Event");

        String[] eventArray = eventIds.toArray(new String[0]);

        // filter event
        builder.setSingleChoiceItems(eventArray, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedEventId = eventArray[which];
            }
        });

        // filter status
        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (selectedEventId != null) {
                    showStatusFilterDialog();
                } else {
                    Toast.makeText(OrganizerNotificationActivity.this, "Please select an event", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void showStatusFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Status");

        String[] statusArray = {"Requested", "Joined", "Accepted", "Rejected"};
        boolean[] checkedItems = {false, false, false, false};

        selectedStatuses.clear();

        builder.setMultiChoiceItems(statusArray, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    if (!selectedStatuses.contains(statusArray[which])) {
                        selectedStatuses.add(statusArray[which]);
                    }
                } else {
                    selectedStatuses.remove(statusArray[which]);
                }
            }
        });

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (selectedStatuses.isEmpty()) {
                    Toast.makeText(OrganizerNotificationActivity.this, "Please select at least one status", Toast.LENGTH_SHORT).show();
                } else {
                    loadFilteredEntrants();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void loadFilteredEntrants() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection("OverallDB");

        Log.d("LoadEntrants", "Attempting to load entrants for event id: " + selectedEventId);

        collectionRef.document(selectedEventId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();

                            selectedEventName = document.getString("name");

                            List<String> entrantIds = (List<String>) document.get("entrants");
                            contactList_display.clear();

                            if (entrantIds != null) {
                                for (String entrantId : entrantIds) {
                                    db.collection("EntrantDB").document(entrantId).get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful() && task.getResult() != null) {
                                                        DocumentSnapshot entrantDoc = task.getResult();

                                                        if (entrantDoc.contains("status")) {
                                                            Object statusObject = entrantDoc.get("status");
                                                            if (statusObject instanceof Map) {
                                                                Map<String, String> statuses = (Map<String, String>) statusObject;
                                                                String statusForEvent = statuses.get(selectedEventId);

                                                                Log.d("LoadFilteredEntrants", "Entrant ID: " + entrantId + ", Status for Event: " + statusForEvent);

                                                                if (statusForEvent != null && selectedStatuses.contains(statusForEvent)) {
                                                                    String entrantName = entrantDoc.getString("name");
                                                                    contactList_display.add(new Pair<>(entrantId, entrantName));
                                                                    Log.d("LoadFilteredEntrants", "Entrant added: " + entrantId + ", " + entrantName);

                                                                    contactListAdapter.notifyDataSetChanged();
                                                                } else {
                                                                    Log.d("LoadFilteredEntrants", "Entrant skipped: " + entrantId + ", Status: " + statusForEvent);
                                                                }
                                                            } else {
                                                                Log.e("LoadFilteredEntrants", "Status field is not a Map for Entrant ID: " + entrantId);
                                                            }
                                                        } else {
                                                            Log.d("LoadFilteredEntrants", "No status field for Entrant ID: " + entrantId);
                                                        }
                                                    }
                                                }
                                            });
                                }
                            } else {
                                Log.d("LoadFilteredEntrants", "No entrants found in document: " + document.getId());
                            }
                        } else {
                            Log.e("FirestoreError", "Error loading entrants", task.getException());
                        }
                    }
                });
    }

}
