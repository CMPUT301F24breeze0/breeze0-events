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
import java.util.Map;

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

public class OrganizerMyListActivity extends AppCompatActivity implements OrganizerEventActivity.OnFragmentInteractionListener{
    private FirebaseFirestore db;
    private OverallStorageController overallStorageController;
    private ListView eventListView;
    private ArrayAdapter<String> eventListAdapter;
    private ArrayList<String> eventList_display;
    private ArrayList<Event> eventList;
    private int pos;
    public Event event;

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
        eventList = new ArrayList<>();
        eventListAdapter = new ArrayAdapter<>(this, R.layout.list_item_layout, eventList_display);
        eventListView.setAdapter(eventListAdapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection("OverallDB");
        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

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
        new_event_button.setOnClickListener(v->{
            // notify user if there's not network connection
            if(!isNetworkAvailable(OrganizerMyListActivity.this)){
                Toast.makeText(getApplicationContext(), "No Network Connection", Toast.LENGTH_LONG).show();
                return;
            }

            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("events");
            databaseRef.orderByKey().limitToLast(1).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@androidx.annotation.NonNull Task<DataSnapshot> task) {
                    Log.d("OrganizerMyListActivity","Task completed: " + task.isSuccessful());
                    String newEventId;
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().hasChildren()) {
                        String maxId = task.getResult().getChildren().iterator().next().getKey();
                        int nextId = Math.min(Integer.parseInt(maxId) + 1, 100); // id should be 1-100
                        newEventId = String.valueOf(nextId);
                        Log.d("OrganizerMyListActivity", "New event ID: " + newEventId);
                    } else {
                        Log.e("OrganizerMyListActivity", "Failed to fetch max ID or no events found.");
                        newEventId = "1";
                    }

                    // jump to organizer event activity and pass id info
                    Intent intent = new Intent(OrganizerMyListActivity.this, OrganizerEventActivity.class);
                    intent.putExtra("new_event_id", newEventId);
                    intent.putExtra("header_text", "Add New Event");
                    Log.d("OrganizerMyListActivity", "Starting OrganizerEventActivity with new event ID: " + newEventId);
                    startActivity(intent);
                }

            });
        });
        /*
        new_event_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrganizerEventActivity dialog = new OrganizerEventActivity();
                 dialog.show(getSupportFragmentManager(), "OrganizerEventActivity");

            }
        });*/

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
                alert.setNeutralButton("Delete",(dialogInterface, j) ->{
                    if(eventList.size() != 0){
                        Event item = eventList.get(pos);
                        // boolean check_done = item.getStatus();
                        eventListAdapter.remove(String.valueOf(item));
                        eventList.remove(item);
                        update();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Nothing to delete",Toast.LENGTH_LONG).show();
                    }
                });
                // edit event
                alert.setPositiveButton("Edit", (dialogInterface, j) -> {
                    Intent intent = new Intent(OrganizerMyListActivity.this, OrganizerEventActivity.class);
                    intent.putExtra("header_text", "Edit Event");
                    Event item = eventList.get(pos);
                    // intent.putExtra("event_id", item.getId());
                    startActivity(intent);
                });
                //alert.setPositiveButton("Edit",(dialogInterface, j) -> new OrganizerEventActivity().show(getSupportFragmentManager(),"Edit_Event"));
                // cancel button
                alert.setNegativeButton("Cancel",(dialog, which) -> {
                    dialog.dismiss();
                });
                alert.show();
                return true;
            }

        });


    }


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

    private void setEventList(Event event)  {
        this.event = event;
        update();
    }

    public void update() {
        eventListAdapter.notifyDataSetChanged();
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

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
                        Map<String, Object> data = document.getData();
                        overallStorageController.getEvent(String.valueOf(docId), new EventCallback() {
                            @Override
                            public void onSuccess(Event event) {
                                String eventInfo = "Name: " + event.getName() +
                                        "\nStart_date: " + event.getStartDate() +
                                        "\nEnd_date: " + event.getEndDate();
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
}
