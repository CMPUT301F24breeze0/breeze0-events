package com.example.breeze0events;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdminEventDetail extends AppCompatActivity {
    private OverallStorageController overallStorageController;
    private TextView eventTitle;
    private TextView eventName;
    private TextView eventDate;
    private TextView maxEntrants;
    private TextView signUpDueDay;
    private TextView eventDescription;
    Event selected_event;
    private String eventID;
    private ArrayList<Event> eventList;
    private ArrayList<String> eventListDisplay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_detail_fragment);

        Button return_button = findViewById(R.id.cancelButton);
        Button delete_button = findViewById(R.id.Deletebutton);
        eventTitle = findViewById(R.id.EventDetail);
        eventName = findViewById(R.id.EventName);
        eventDate = findViewById(R.id.EventDate);
        maxEntrants = findViewById(R.id.MaxEntrants);
        signUpDueDay = findViewById(R.id.duedate);
        eventDescription = findViewById(R.id.description);
        overallStorageController = new OverallStorageController();

        String id = (String)getIntent().getSerializableExtra("selectedID");
        eventListDisplay = getIntent().getStringArrayListExtra("eventListDisplay");

        overallStorageController.getEvent(String.valueOf(id), new EventCallback() {
            @Override
            public void onSuccess(Event event) {
                selected_event=event;
                eventTitle.setText("Event Detail");
                eventName.setText("Event Name: " + selected_event.getName());
                eventDate.setText("Event start from " + event.getStartDate() + " - " + event.getEndDate());
                maxEntrants.setText("Max number of entrants: " + event.getEntrants().size());
                signUpDueDay.setText("Sign-up due: " + /* add due date if available */ "");
                //eventDescription.setText(event.getDescription());

                Log.d("AdminEventDetail", "Admins data fetched successfully: ");
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
                overallStorageController.deleteEvent(id);
                Log.d("AdminEventDetail", "Delete function called from OverallStorageController");
                if (eventList != null) {
                    eventList.removeIf(event -> event.getEventId().equals(id));
                }


                eventListDisplay.removeIf(eventInfo -> eventInfo.contains(id));


                //if (eventListDisplay  != null) {
                    //eventListDisplay.clear();
                    //for (Event event : eventList) {
                       // String info = "Name: " + event.getName() + "\nStart_date: " + event.getStartDate()
                                //+ "\nEnd_date: " + event.getEndDate();
                        //eventListDisplay.add(info);}}



                Intent resultIntent = new Intent();
                resultIntent.putExtra("UPDATED_LIST", eventListDisplay);
                setResult(RESULT_OK, resultIntent);

                Toast.makeText(this, "Event deleted successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Log.e("AdminEventDetail", "Invalid event ID");
            }
        });


    }
}
