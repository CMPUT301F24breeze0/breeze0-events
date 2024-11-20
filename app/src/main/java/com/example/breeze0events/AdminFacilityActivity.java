package com.example.breeze0events;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;



/**
 * The AdminFacilityActivity provides a user interface for administrators to view a list of facilities.
 * It retrieves facility data from Firestore and displays the facilities' locations in a ListView.
 */

public class AdminFacilityActivity extends AppCompatActivity {
    private ListView facilityListView;
    private ArrayAdapter<String> facilityListAdapter;
    private ArrayList<String> facilityListDisplay = new ArrayList<>();;
    private ArrayList<Facility> facilityList = new ArrayList<>();;
    private OverallStorageController overallStorageController;
    public Facility facility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_facility_page);
        Button return_button = findViewById(R.id.back_in_main);

        facilityListView = findViewById(R.id.facilityList);
        facilityListAdapter = new ArrayAdapter<>(this, R.layout.list_item_layout, facilityListDisplay);
        facilityListView.setAdapter(facilityListAdapter);
        overallStorageController = new OverallStorageController();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection("FacilityDB");

        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String id = document.getId();
                        overallStorageController.getFacility(String.valueOf(id), new FacilityCallback() {
                            @Override
                            public void onSuccess(Facility facility) {
                                String info = "Facility Location:\n" + facility.getLocation();
                                facilityList.add(facility);
                                facilityListDisplay.add(info);
                                facilityListAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                Log.e("AdminFacilityActivity", "Failed to fetch facility data: " + errorMessage);
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

        facilityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (facilityList != null && position >= 0 && position < facilityList.size()) {
                    Facility selectedFacility = facilityList.get(position);
                    Log.d("FacilityListActivity", "Clicked facility ID: " + selectedFacility.getFacilityId());

                    Intent intent = new Intent(AdminFacilityActivity.this, AdminFacilityDetail.class);
                    intent.putExtra("selectedID", selectedFacility.getFacilityId());
                    //intent.putExtra("location", selectedFacility.getLocation());
                    intent.putStringArrayListExtra("facilityListDisplay", facilityListDisplay);
                    startActivity(intent);

                } else {
                    Log.e("ItemClickError", "Invalid position or facilityList is null");
                }
            }
        });

    }
}
