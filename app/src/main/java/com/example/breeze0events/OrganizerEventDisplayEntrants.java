package com.example.breeze0events;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;


/**
 * OrganizerEventDisplayEntrants is an activity that displays the list of entrants
 * registered for a specific event.
 * This class is used to view a list of entrant IDs passed to it via an intent,
 * allowing organizers to review who has signed up for an event.
 */
public class OrganizerEventDisplayEntrants extends AppCompatActivity {
    Button backButton;
    ListView dateListView;
    ArrayAdapter<String> dateListAdapter;
    ArrayList<String> date_list=new ArrayList<>();

    /**
     * Called when the activity is created. This initializes the UI components and sets up the data
     * for displaying the list of entrants by their IDs.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied.
     */
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_display_list); // Set the layout file
        backButton=findViewById(R.id.organizer_facility_activity_back_button);
        dateListView=findViewById(R.id.organizer_facility_list);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ArrayList<String> organizers=getIntent().getStringArrayListExtra("entrants_id");
        if(organizers!=null) {
            for (String entrant : organizers) {
                date_list.add("Entrant ID:");
                date_list.add(entrant);
            }
        }
        dateListAdapter = new ArrayAdapter<>(this, R.layout.list_item_layout, date_list);
        dateListView.setAdapter(dateListAdapter);
    }
}
