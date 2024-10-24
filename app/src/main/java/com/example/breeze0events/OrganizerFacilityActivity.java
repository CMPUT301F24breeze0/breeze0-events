
package com.example.breeze0events;

import android.widget.ArrayAdapter;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class OrganizerFacilityActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private OverallStorageController overallStorageController;
    private ListView facilityListView;
    private ArrayAdapter<String> facilityListAdapter;
    private ArrayList<String> facilityList;
    int limit=100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_facility_activity);

        Button back_button = findViewById(R.id.organizer_facility_activity_back_button);
        Button new_facility_button = findViewById(R.id.new_facility_button);

        overallStorageController = new OverallStorageController();
        facilityListView = findViewById(R.id.organizer_facility_list);
        facilityList = new ArrayList<>();
        facilityListAdapter = new ArrayAdapter<>(this, R.layout.list_item_layout, facilityList);
        facilityListView.setAdapter(facilityListAdapter);

        for(int i=1; i<=100; i++) {
            overallStorageController.getFacility(String.valueOf(i), new FacilityCallback() {
                @Override
                public void onSuccess(Facility facility) {

                    String facilityInfo = "Facility: " + facility.getLocation();
                    facilityList.add(facilityInfo);

                    facilityListAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(String errorMessage) {

                }
            });
        }

        // by clicking "Back" button:
        back_button.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerFacilityActivity.this, OrganizerMyListActivity.class);
            startActivity(intent);
            finish();
        });

    }


}
