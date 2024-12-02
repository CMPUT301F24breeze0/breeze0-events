package com.example.breeze0events;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
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

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;


/**
 * The AdminEventActivity class displays a list of events fetched from Firebase.
 * It allows the admin to refresh the event list, select an event to view details, and
 * handles communication with the AdminSelectedEvents activity, send data of event info.
 */

public class AdminEventActivity extends AppCompatActivity {
    private ListView eventListView;
    private AdminEventListAdapter adminEventListAdapter;
    private ArrayAdapter<String> eventListAdapter;
    private ArrayList<String> eventListDisplay = new ArrayList<>();
    private ArrayList<Event> eventList = new ArrayList<>();
    public Event event;
    private Button refreshButton;
    private ImageView poster;
    private OverallStorageController overallStorageController;

    private final ActivityResultLauncher<Intent> eventsLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        ArrayList<String> updatedList = data.getStringArrayListExtra("UPDATED_LIST");
                        if (updatedList != null) {
                            eventList.clear();
                            for (String updatedEventId : updatedList) {
                                overallStorageController.getEvent(updatedEventId, new EventCallback() {
                                    @Override
                                    public void onSuccess(Event event) {
                                        eventList.add(event);
                                        adminEventListAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onFailure(String errorMessage) {
                                        Log.e("eventsLauncher", "Failed to fetch event: " + errorMessage);
                                    }
                                });
                            }
                        }
                    }
                }
            });


    @Override
    /**
     * Initializes the activity, sets up the UI elements, loads the initial events from Firebase,
     * sets up click listeners for the refresh and return buttons as well as the ListView items.
     *
     * @param savedInstanceState The saved instance state of the activity.
     *
     */
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.remove_events_page);
        Button return_button = findViewById(R.id.back_in_main);
        refreshButton = findViewById(R.id.refreshButton);


        eventListView = findViewById(R.id.eventsList);
        overallStorageController = new OverallStorageController();

        //eventListAdapter = new ArrayAdapter<>(this, R.layout.list_item_layout, eventListDisplay);
        //eventListView.setAdapter(eventListAdapter);
        AdminEventListAdapter adminEventListAdapter = new AdminEventListAdapter(this, eventList);
        eventListView.setAdapter(adminEventListAdapter);

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
                                //String info = "Name: " + event.getName() + "\nStart_date: " + event.getStartDate()
                                        //+ "\nEnd_date: " + event.getEndDate();
                                //eventListDisplay.add(info);
                                eventList.add(event);
//                                eventListAdapter.notifyDataSetChanged();
//                                Log.d("AdminEventData", "Event data fetched successfully: ");
//                            }
                                if (adminEventListAdapter != null) {
                                    adminEventListAdapter.notifyDataSetChanged();
                                } else {
                                    Log.e("AdminEventActivity", "Adapter is null!");
                                }
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

        refreshButton.setOnClickListener(v -> refreshEventList(adminEventListAdapter));

        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (eventList != null && position >= 0 && position < eventList.size()) {
                    Event selectedEvent = eventList.get(position);
                    String posterPhotoPath = savePosterPhotoToFile(selectedEvent.getPosterPhoto());
                    Intent intent = new Intent(AdminEventActivity.this, AdminSelectedEvents.class);
                    intent.putExtra("selectedID", selectedEvent.getEventId());
                    intent.putStringArrayListExtra("eventListDisplay", eventListDisplay);
                    intent.putExtra("poster_photo",posterPhotoPath);
                    //intent.putExtra("poster_photo",selectedEvent.getPosterPhoto());
                    eventsLauncher.launch(intent);

                } else {
                    Log.e("ItemClickError", "Invalid position or eventList is null");
                }
            }
        });
    }


    /**
     * Saves a Base64-encoded image to a file in the app's cache directory.
     *
     * @param base64Image The Base64-encoded image string.
     * @return The file path where the image is saved, or null if saving fails.
     */
    private String savePosterPhotoToFile(String base64Image) {
        if (base64Image == null || base64Image.isEmpty()) {
            Log.e("ImageSaveError", "Base64 image is null or empty.");
            return null;
        }
        try {
            Bitmap bitmap = decodeBase64Image(base64Image);
            if (bitmap == null) {
                Log.e("ImageSaveError", "Decoded bitmap is null.");
                return null;
            }
            File file = new File(getCacheDir(), "poster_" + System.currentTimeMillis() + ".jpg");
            try (FileOutputStream fos = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            }
            return file.getAbsolutePath();
        } catch (Exception e) {
            Log.e("ImageSaveError", "Error saving image", e);
            return null;
        }
    }

    /**
     * Decodes a Base64-encoded image string into a Bitmap.
     *
     * @param base64ImageString The Base64-encoded image string.
     * @return The decoded Bitmap or null if decoding fails.
     */
    public static Bitmap decodeBase64Image(String base64ImageString) {
        try {
            byte[] imageBytes = Base64.decode(base64ImageString, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        } catch (Exception e) {
            Log.e("DecodeError", "Error decoding Base64 image", e);
            return null;
        }
    }
    /**
     * Refreshes the event list by clearing current data and fetching updated data from Firebase.
     * Displays a toast message upon completion.
     */

    private void refreshEventList(AdminEventListAdapter adminEventListAdapter) {
        eventList.clear();
        eventListDisplay.clear();
        //eventListAdapter.notifyDataSetChanged();
        adminEventListAdapter.notifyDataSetChanged();


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
                                //eventListAdapter.notifyDataSetChanged();
                                adminEventListAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                Log.e("AdminEventActivity", "Failed to fetch event data: " + errorMessage);
                            }
                        });
                    }
                    Toast.makeText(AdminEventActivity.this, "Refreshed", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("FirestoreError", "Error to fetch DB: ", task.getException());
                }
            }
        });

    }
}
