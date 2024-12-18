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
 * Activity class for displaying a list of organizers associated with an event for the organizer view.
 * This class retrieves organizer IDs passed via intent and displays them in a list format.
 */
public class OrganizerEventDisplayOrganizers extends AppCompatActivity {

    Button backButton;
    ListView dateListView;
    ArrayAdapter<String> dateListAdapter;
    ArrayList<String> date_list=new ArrayList<>();

    /**
     * Sets up the activity, including the display of organizer IDs and the back button functionality.
     *
     * @param savedInstanceState
     * If the activity is being re-initialized after previously being shut down,
     * this Bundle contains the data it most recently supplied.
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
        ArrayList<String> organizers=getIntent().getStringArrayListExtra("organizers_id");
        if(organizers!=null) {
            for (String organizer : organizers) {
                date_list.add("Organizer ID:");
                date_list.add(organizer);
            }
        }
        dateListAdapter = new ArrayAdapter<>(this, R.layout.list_item_layout, date_list);
        dateListView.setAdapter(dateListAdapter);
    }
}
