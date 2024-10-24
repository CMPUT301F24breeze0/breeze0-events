
package com.example.breeze0events;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

public class OrganizerFacilityActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private OverallStorageController overallStorageController;
    private ListView facilityListView;
    private ArrayAdapter<String> facilityListAdapter;
    private ArrayList<String> facilityList;
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

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection("FacilityDB");

        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String docId = document.getId();
                        overallStorageController.getFacility(String.valueOf(docId), new FacilityCallback() {
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
                } else {
                    Log.e("FirestoreError", "Error getting documents: ", task.getException());
                }
            }
        });


        // by clicking "Back" button:
        back_button.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerFacilityActivity.this, OrganizerMyListActivity.class);
            startActivity(intent);
            finish();
        });

    }


}
