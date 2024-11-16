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
        signUpDueDay = findViewById(R.id.duedate);
        eventDescription = findViewById(R.id.description);
        overallStorageController = new OverallStorageController();
        encryptedPosterImage = getEncryptedImageFromStorage();

        String id = (String) getIntent().getSerializableExtra("selectedID");
        eventListDisplay = getIntent().getStringArrayListExtra("eventListDisplay");

        overallStorageController.getEvent(String.valueOf(id), new EventCallback() {
            @Override
            public void onSuccess(Event event) {
                selected_event = event;
                eventTitle.setText("Event Detail");
                eventName.setText("Event Name: " + selected_event.getName());
                eventDate.setText("Event start from " + event.getStartDate() + " - " + event.getEndDate());
                maxEntrants.setText("Max number of entrants: " + event.getLimitedNumber());
                signUpDueDay.setText("Sign-up due: " + /* add due date if available */ "");
                //eventDescription.setText(event.getDescription());

                Log.d("AdminEventDetail", "Admins data fetched successfully: ");
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("AdminEventDetail", "Failed to fetch admins: " + errorMessage);
            }
        });

        if (encryptedPosterImage != null) {
            displayDecryptedPosterImage(encryptedPosterImage);
        }

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
                .setPositiveButton("Confirm", (dialog, which) -> {
                    deleteEvent(id);
                })
                .setNegativeButton("No, missClick", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
        }


    /**
     * Deletes the specified event and updates the event list display. Sends the updated event list
     * back to the previous activity.
     *
     * @param id The ID of the event to be deleted.
     */
    private void deleteEvent(String id) {
        overallStorageController.deleteEvent(id);
        Log.d("AdminEventDetail", "Delete function called from OverallStorageController");

        if (eventList != null) {
            eventList.removeIf(event -> event.getEventId().equals(id));
        }

        eventListDisplay.removeIf(eventInfo -> eventInfo.contains(id));

        Intent resultIntent = new Intent();
        resultIntent.putExtra("UPDATED_LIST", eventListDisplay);
        setResult(RESULT_OK, resultIntent);

        Toast.makeText(this, "Event deleted successfully", Toast.LENGTH_SHORT).show();
        finish();
    }


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