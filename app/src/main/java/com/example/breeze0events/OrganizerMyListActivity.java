package com.example.breeze0events;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.content.Intent;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.Query;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Activity for organizing events within an event management application.
 * This activity provides functionalities to display, create, delete, and edit events.
 */
public class OrganizerMyListActivity extends AppCompatActivity implements OrganizerEventActivity.OnFragmentInteractionListener{
    private FirebaseFirestore db;
    private OverallStorageController overallStorageController;
    private ListView eventListView;
    private ArrayAdapter<String> eventListAdapter;
    private ArrayList<String> eventList_display;
    private ArrayList<Event> eventList;
    private int pos;
    public Event event;
    private String newEventId;

    /**
     * Initializes the UI components and loads the events from Firebase.
     * @param savedInstanceState The saved instance state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_main_activity);
        Button map_button = findViewById(R.id.map_button);
        Button my_facility_button = findViewById(R.id.my_facility_button);
        Button new_event_button = findViewById(R.id.new_event_button);
        Button refresh_button = findViewById(R.id.refresh_button);

        overallStorageController = new OverallStorageController();
        eventListView = findViewById(R.id.organizer_event_list);
        eventList_display = new ArrayList<>();
        eventList = new ArrayList<>();
        eventListAdapter = new ArrayAdapter<>(this, R.layout.list_item_layout, eventList_display);
        eventListView.setAdapter(eventListAdapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection("OverallDB");
        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        loadEventsFromFirebase();

        overallStorageController.getOrganizer(androidId, new OrganizerCallback() {
            @Override
            public void onSuccess(Organizer organizer) {

            }

            @Override
            public void onFailure(String errorMessage) {
                Organizer organizer=new Organizer(androidId, androidId,new ArrayList<>());
                overallStorageController.addOrganizer(organizer);
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

        // by clicking "New" button
        findSmallestAvailableId();
        new_event_button.setOnClickListener(v->{

            if (!isNetworkAvailable(OrganizerMyListActivity.this)) {
                Toast.makeText(getApplicationContext(), "No Network Connection", Toast.LENGTH_LONG).show();
                return;
            }

            Intent intent = new Intent(OrganizerMyListActivity.this, OrganizerEventActivity.class);
            intent.putExtra("new_event_id", newEventId);
            intent.putExtra("header_text", "Add New Event");
            Log.d("OrganizerMyListActivity", "Starting OrganizerEventActivity with new event ID: " + newEventId);
            startActivity(intent);
        });

        // by clicking "Refresh" button
        refresh_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshEventList();
                // loadEventsFromFirebase();
            }
        });
        // refreshEventList();


        // By short-clicking anything on the list, display event details
        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected event item
                Event selectedEvent = eventList.get(position);

                // Create an Intent to navigate to OrganizerEventInformationActivity
                Intent intent = new Intent(OrganizerMyListActivity.this, OrganizerEventInformationActivity.class);

                // Pass relevant event information to the target Activity (e.g., event ID or name)
                intent.putExtra("selected_event_id",  selectedEvent.getEventId());
                // Start OrganizerEventInformationActivity
                startActivity(intent);
            }
        });


        // by long clicking anything on the list, the organizer can choose to delete the event or edit the event
        eventListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                pos = position;
                setEventList(eventList.get(pos));
                AlertDialog.Builder alert = new AlertDialog.Builder(OrganizerMyListActivity.this);
                alert.setTitle("Delete/ Edit");
                alert.setMessage("Do you want to delete or edit this event?");
                alert.show();
                // delete event
                alert.setNeutralButton("Delete", (dialogInterface, j) -> {
                    if(eventList.size() != 0) {
                        Event item = eventList.get(pos);
                        String eventIdToDelete = item.getEventId();
                        String organizerId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                        deleteEntrant(eventIdToDelete);
                        overallStorageController.deleteEvent(eventIdToDelete);
                        overallStorageController.deleteEventWithOrganizerCheck(eventIdToDelete, organizerId);

                        // update listview after delete
                        eventList_display.remove(pos);
                        eventList.remove(pos);
                        eventListAdapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), "Event deleted successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Nothing to delete", Toast.LENGTH_LONG).show();
                    }
                });

                // edit event
                alert.setPositiveButton("Edit", (dialogInterface, j) -> {
                    Intent intent = new Intent(OrganizerMyListActivity.this, OrganizerEditEventActivity.class);
                    Event item = eventList.get(pos);
                    intent.putExtra("event_id", item.getEventId()); // Pass the event ID for editing
                    startActivity(intent);
                });
                // cancel button
                alert.setNegativeButton("Cancel",(dialog, which) -> {
                    dialog.dismiss();
                });
                alert.show();
                return true;
            }

        });


    }

    /**
     * Called when the OK button is pressed in the new event creation dialog.
     * @param newEvent The newly created Event object.
     */
    public void onOkPressed(Event newEvent) {
        overallStorageController.addEvent(newEvent); // add to db
        String eventInfo = "Name: " + newEvent.getName() +
                "\nStart_date: " + newEvent.getStartDate() +
                "\nEnd_date: " + newEvent.getEndDate();
        eventList_display.add(eventInfo);
        eventList.add(newEvent);
        eventListAdapter.notifyDataSetChanged();
        Log.d("Event","Event to add: " + newEvent.toString());
    }

    /**
     * Sets the selected event for display and updates the UI.
     * @param event The selected Event object.
     */
    private void setEventList(Event event)  {
        this.event = event;
        update();
    }

    /**
     * Updates the UI by notifying the adapter of data changes.
     */
    public void update() {
        eventListAdapter.notifyDataSetChanged();
    }

    /**
     * Checks if there is an active network connection.
     * @param context The context of the current activity.
     * @return True if network is available, false otherwise.
     */
    // there has to be a network connection when creating new event, this method will check if there's network connection
    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Loads events from Firebase Firestore and displays them in the event list.
     */
    // load from firebase
    private void loadEventsFromFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection("OverallDB");

        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String docId = document.getId();
                        String androidId=Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                        List<String> organizers=(List<String>) document.get("organizers");

                        if(!Objects.equals(organizers.get(0), androidId))
                            continue;
                        Map<String, Object> data = document.getData();
                        overallStorageController.getEvent(String.valueOf(docId), new EventCallback() {
                            @Override
                            public void onSuccess(Event event) {
                                String eventInfo = "Name: " + event.getName() +
                                        "\nStart date: " + event.getStartDate() +
                                        "\nEnd date: " + event.getEndDate();
                                eventList_display.add(eventInfo);
                                eventList.add(event);
                                eventListAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                            }
                        });
                    }
                } else {
                    Log.e("FirestoreError", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void findSmallestAvailableId() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection("OverallDB");

        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // find all the list from eventId
                    ArrayList<Integer> existingIds = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        try {
                            int eventId = Integer.parseInt(document.getId());
                            existingIds.add(eventId);
                        } catch (NumberFormatException e) {
                            Log.e("OrganizerMyListActivity", "Invalid eventId format: " + document.getId());
                        }
                    }

                    newEventId = findNextAvailableId(existingIds);
                    Log.d("OrganizerMyListActivity", "Calculated new event ID: " + newEventId);
                } else {
                    Log.e("FirestoreError", "Error getting documents: ", task.getException());
                    newEventId = "1";
                }
            }
        });
    }// calculate the next available id


    private String findNextAvailableId(ArrayList<Integer> existingIds) {
        for (int i = 1; i <= 100; i++) {
            if (!existingIds.contains(i)) {
                return String.valueOf(i);
            }
        }
        return null;
    }

    private void refreshEventList() {
        eventList.clear();
        eventList_display.clear();
        eventListAdapter.notifyDataSetChanged();
        loadEventsFromFirebase();
    }

    private void deleteEntrant(String eventId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("EntrantDB").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String entrantId;
                overallStorageController=new OverallStorageController();
                Log.d("DeleteEntrant", "Database query successful");
                for (QueryDocumentSnapshot document : task.getResult()){
                    entrantId=document.getId();
                    overallStorageController.getEntrant(entrantId, new EntrantCallback() {
                        @Override
                        public void onSuccess(Entrant entrant) {
                            entrant.UnjoinEvent(eventId);
                            overallStorageController.updateEntrant(entrant);
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Log.d("DeleteEntrant", "Database query failed");
                        }
                    });
                }
            } else {
                Log.e("DeleteEntrant", "Failed to query EntrantDB: " + task.getException().getMessage());
            }
        });
    }
}




