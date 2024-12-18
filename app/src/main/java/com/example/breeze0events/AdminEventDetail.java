package com.example.breeze0events;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

/**
 * The AdminEventDetail activity provides an interface for administrators to view detailed information
 * about a specific event. It allows the administrator to view event details, display an event poster,
 * and delete the event if necessary.
 */
public class AdminEventDetail extends AppCompatActivity {
    private OverallStorageController overallStorageController;
    private TextView eventTitle;
    private TextView eventName;
    private TextView eventDate;
    private TextView maxEntrants;
    private TextView signUpDueDay;
    private TextView eventDescription;
    private ImageView poster;
    private String encryptedPosterImage;
    Event selected_event;
    private String eventID;
    private ArrayList<Event> eventList;
    private ArrayList<String> eventListDisplay;

    /**
     * Called when the activity is first created. This method initializes the UI elements,
     * retrieves the selected event's details, such as name, date, maxEntrants, signUpDueDay,
     * and description. Sets up the delete functionality for the event.
     *
     * @param savedInstanceState
     * If the activity is being re-initialized after previously being shut down,
     * this Bundle contains the most recent data; otherwise, it is null.
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_detail_fragment);

        Button return_button = findViewById(R.id.cancelButton);
        Button delete_button = findViewById(R.id.Deletebutton);
        poster = findViewById(R.id.poster);
        eventTitle = findViewById(R.id.EventDetail);
        eventName = findViewById(R.id.EventName);
        eventDate = findViewById(R.id.EventDate);
        maxEntrants = findViewById(R.id.MaxEntrants);
        // signUpDueDay = findViewById(R.id.duedate);
        // beventDescription = findViewById(R.id.description);
        overallStorageController = new OverallStorageController();
        encryptedPosterImage = getEncryptedImageFromStorage();

        String id = (String) getIntent().getSerializableExtra("selectedID");
        eventListDisplay = getIntent().getStringArrayListExtra("eventListDisplay");

        overallStorageController.getEvent(String.valueOf(id), new EventCallback() {
            @Override
            public void onSuccess(Event event) {
                selected_event = event;
                eventTitle.setText("         Event Detail");
                eventName.setText("Title: " + selected_event.getName());
                eventDate.setText("Start From " + event.getStartDate() + "\nTo " + event.getEndDate());
                maxEntrants.setText("Max No. of Entrants: " + event.getLimitedNumber());
                //signUpDueDay.setText("Sign-up due: " + /* add due date if available */ "");
                //eventDescription.setText(event.getDescription());
                String encryptedPosterImage = event.getPosterPhoto(); // Retrieve encrypted image from event
                if (encryptedPosterImage != null && !encryptedPosterImage.isEmpty()) {
                    displayDecryptedPosterImage(encryptedPosterImage);
                } else {
                    Toast.makeText(AdminEventDetail.this, "No poster available for this event.", Toast.LENGTH_SHORT).show();
                }

                Log.d("AdminEventDetail", "Event details loaded successfully.");
            }


            @Override
            public void onFailure(String errorMessage) {
                Log.e("AdminEventDetail", "Failed to fetch admins: " + errorMessage);
            }
        });


        return_button.setOnClickListener(v -> {
            finish();
        });

        delete_button.setOnClickListener(v -> {
            if (id != null && !id.isEmpty()) {
                showDeleteConfirmationDialog(id);
            } else {
                Log.e("AdminEventDetail", "Invalid event ID");
            }
        });
    }


    /**
     * Show a confirmation dialog before deleting an event.
     *
     * @param id The ID of the event to be deleted.
     */
    private void showDeleteConfirmationDialog(String id) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Event")
                .setMessage("Are you sure to delete this event?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteEvent(id);
                })
                .setNegativeButton("No, MissClick", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
        }


    /**
     * Deletes the specified event and updates the event list display. And also delete event from
     * corresponding organizer. Sends the updated event list back to the previous activity.
     *
     * @param id The ID of the event to be deleted.
     */
    private void deleteEvent(String id) {
        if (selected_event == null) {
            Log.e("AdminEventDetail", "no event");
            return;
        }

        ArrayList<String> organizers = new ArrayList<>(selected_event.getOrganizers());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("OverallDB").document(id).delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("AdminEventDetail", "Event successfully deleted from OverallDB");

                    for (String organizerId : organizers) {
                        db.collection("OrganizerDB").document(organizerId)
                                .update("events", com.google.firebase.firestore.FieldValue.arrayRemove(id))
                                .addOnSuccessListener(aVoid1 -> Log.d("AdminEventDetail", "Event removed from Organizer: " + organizerId))
                                .addOnFailureListener(e -> Log.e("AdminEventDetail", "Failed to update Organizer: " + organizerId, e));
                    }

                    for (String entrantId : selected_event.getEntrants()) {
                        db.collection("EntrantDB").document(entrantId)
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        Map<String, Object> eventsMap = (Map<String, Object>) documentSnapshot.get("events");
                                        Map<String, Object> geolocationMap = (Map<String, Object>) documentSnapshot.get("Geolocation");
                                        Map<String, Object> statusMap = (Map<String, Object>) documentSnapshot.get("status");

                                        if (eventsMap != null) eventsMap.remove(id);
                                        if (geolocationMap != null) geolocationMap.remove(id);
                                        if (statusMap != null) statusMap.remove(id);

                                        db.collection("EntrantDB").document(entrantId)
                                                .update("events", eventsMap,
                                                        "Geolocation", geolocationMap,
                                                        "status", statusMap)
                                                .addOnSuccessListener(aVoid1 -> Log.d("AdminEventDetail", "Event, Geolocation, and Status removed for Entrant: " + entrantId))
                                                .addOnFailureListener(e -> Log.e("AdminEventDetail", "Failed to update Entrant: " + entrantId, e));
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("AdminEventDetail", "Failed to fetch Entrant: " + entrantId, e));
                    }



                    if (eventList != null) {
                        eventList.removeIf(event -> event.getEventId().equals(id));
                    }
                    eventListDisplay.removeIf(eventInfo -> eventInfo.contains(id));

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("UPDATED_LIST", eventListDisplay);
                    setResult(RESULT_OK, resultIntent);

                    Toast.makeText(this, "Event deleted successfully.", Toast.LENGTH_SHORT).show();
                    BackToEventList();
                    //finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("AdminEventDetail", "Failed to delete event from OverallDB", e);
                });
    }


    /**
     * In in case user click the details again, but it is not updated yet
     */
    private void BackToEventList() {
        Intent intent = new Intent(AdminEventDetail.this, AdminEventActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);  //reference: https://stackoverflow.com/questions/23718356/why-does-flag-activity-clear-top-not-work
        startActivity(intent);
        finish();
    }

    // old version of deleteEvent, in case new one has bugs
//    private void deleteEvent(String id) {
//        overallStorageController.deleteEvent(id);
//        Log.d("AdminEventDetail", "Delete function called from OverallStorageController");
//
//        if (eventList != null) {
//            eventList.removeIf(event -> event.getEventId().equals(id));
//        }
//
//        eventListDisplay.removeIf(eventInfo -> eventInfo.contains(id));
//
//        Intent resultIntent = new Intent();
//        resultIntent.putExtra("UPDATED_LIST", eventListDisplay);
//        setResult(RESULT_OK, resultIntent);
//
//        Toast.makeText(this, "Event deleted successfully", Toast.LENGTH_SHORT).show();
//        finish();
//    }


    /**
     * Retrieves the encrypted image data from shared preferences.
     *
     * @return A string representing the encrypted image data.
     */
    private String getEncryptedImageFromStorage() {
        return getSharedPreferences("EventDetails", MODE_PRIVATE).getString("encryptedPosterImage", null);
    }


    /**
     * Displays a decrypted event poster image on the ImageView.
     *
     * @param encryptedImage The encrypted image string to decrypt and display.
     */

    private void displayDecryptedPosterImage(String encryptedImage) {
        try {
            Bitmap decryptedPosterImage = ImageHashGenerator.decryptImage(encryptedImage);
            if (decryptedPosterImage != null) {
                poster.setImageBitmap(decryptedPosterImage);
            } else {
                Toast.makeText(this, "Failed to decrypt image", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error decrypting image: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}


//old version of delete function, new version just easy for javadoc comments
//        delete_button.setOnClickListener(v -> {
//            if (id != null && !id.isEmpty()) {
//                overallStorageController.deleteEvent(id);
//                Log.d("AdminEventDetail", "Delete function called from OverallStorageController");
//                if (eventList != null) {
//                    eventList.removeIf(event -> event.getEventId().equals(id));
//                }
//
//                eventListDisplay.removeIf(eventInfo -> eventInfo.contains(id));
//
//                //if (eventListDisplay  != null) {
//                    //eventListDisplay.clear();
//                    //for (Event event : eventList) {
//                       // String info = "Name: " + event.getName() + "\nStart_date: " + event.getStartDate()
//                                //+ "\nEnd_date: " + event.getEndDate();
//                        //eventListDisplay.add(info);}}
//
//                Intent resultIntent = new Intent();
//                resultIntent.putExtra("UPDATED_LIST", eventListDisplay);
//                setResult(RESULT_OK, resultIntent);
//
//                Toast.makeText(this, "Event deleted successfully", Toast.LENGTH_SHORT).show();
//                finish();
//            } else {
//                Log.e("AdminEventDetail", "Invalid event ID");
//            }
//        });