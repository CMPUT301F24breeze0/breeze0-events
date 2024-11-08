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
 * OrganizerEventDisplayDate is an activity for organizers to view event-related date information.
 * This activity displays the start date, end date, and limited number of participants for an event
 * passed to it through an intent.
 */
public class OrganizerEventDisplayDate extends AppCompatActivity {
   Button backButton;
   ListView dateListView;
   ArrayAdapter<String> dateListAdapter;
   ArrayList<String> date_list=new ArrayList<>();

    /**
     * Called when the activity is created. This initializes the UI components and sets up the data
     * for displaying event details like start date, end date, and limited number of participants.
     *
     * @param savedInstanceState
     * If the activity is being re-initialized after previously being shut down,
     * this Bundle contains recent data; otherwise, it is null.
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
        date_list.add("Start Date:");
        date_list.add((String)getIntent().getSerializableExtra("start_date"));
        date_list.add("End Date:");
        date_list.add((String)getIntent().getSerializableExtra("end_date"));
        date_list.add("Limited Number:");
        date_list.add((String)getIntent().getSerializableExtra("limitedNumber"));
        dateListAdapter = new ArrayAdapter<>(this, R.layout.list_item_layout, date_list);
        dateListView.setAdapter(dateListAdapter);
    }
}
