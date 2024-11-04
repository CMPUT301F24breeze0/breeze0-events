package com.example.breeze0events;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class AdminFacilityActivity extends AppCompatActivity {
    private ListView facilityListView;
    private ArrayAdapter<String> facilityListAdapter;
    private ArrayList<String> facilityListDisplay = new ArrayList<>();;
    private ArrayList<String> facilityList = new ArrayList<>();;
    private OverallStorageController overallStorageController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_facility_page);
        Button return_button = findViewById(R.id.backButton);


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
                                facilityList.add(info);
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
    }
}
