
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
import java.util.HashMap;
import java.util.HashSet;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.ArrayAdapter;

public class OrganizerFacilityActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private OverallStorageController overallStorageController;
    private ListView facilityListView;
    private ArrayAdapter<String> facilityListAdapter;
    private ArrayList<String> facilityList;
    private String newFacilityId;
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

        db = FirebaseFirestore.getInstance();
        overallStorageController = new OverallStorageController();




        // by clicking "Back" button:
        back_button.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerFacilityActivity.this, OrganizerMyListActivity.class);
            startActivity(intent);
            finish();
        });

        // by clicking "New" button
        new_facility_button.setOnClickListener(v -> findSmallestAvailableId());

        // by long clicking anything on the list, the organizer can choose to delete or edit the facility

    }


    private ArrayList<String> getFacilityListContent() {
        ArrayList<String> facilityList = new ArrayList<>();
        for (int i = 0; i < facilityListView.getAdapter().getCount(); i++) {
            facilityList.add((String) facilityListView.getAdapter().getItem(i));
        }
        return facilityList;
    }

    private void saveFacilityList() {
        ListView facilityListView = findViewById(R.id.organizer_facility_list);
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) facilityListView.getAdapter();

        ArrayList<String> facilityList = new ArrayList<>();
        for (int i = 0; i < adapter.getCount(); i++) {
            facilityList.add(adapter.getItem(i));
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet("facilityList", new HashSet<>(facilityList));
        editor.apply();
    }

    private void loadFacilities() {
        CollectionReference collectionRef = db.collection("FacilityDB");
        collectionRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                facilityList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String facilityInfo = "Facility: " + document.getString("location");
                    facilityList.add(facilityInfo);
                }
                facilityListAdapter.notifyDataSetChanged();
            } else {
                Log.e("FirestoreError", "Error getting documents: ", task.getException());
            }
        });
    }

    private void findSmallestAvailableId() {
        CollectionReference collectionRef = db.collection("FacilityDB");

        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Integer> existingIds = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        try {
                            int facilityId = Integer.parseInt(document.getId());
                            existingIds.add(facilityId);
                        } catch (NumberFormatException e) {
                            Log.e("OrganizerFacilityActivity", "Invalid facilityId format: " + document.getId());
                        }
                    }

                    // 找到 1 到 100 中最小的未占用 ID
                    newFacilityId = findNextAvailableId(existingIds);
                    Log.d("OrganizerFacilityActivity", "Calculated new facility ID: " + newFacilityId);

                    // 将新 ID 传递给 NewFacilityActivity
                    Intent intent = new Intent(OrganizerFacilityActivity.this, NewFacilityActivity.class);
                    intent.putExtra("new_facility_id", newFacilityId);
                    startActivity(intent);
                } else {
                    Log.e("FirestoreError", "Error getting documents: ", task.getException());
                    newFacilityId = "1"; // 默认 ID 为 1
                }
            }
        });
    }

    private String findNextAvailableId(ArrayList<Integer> existingIds) {
        for (int i = 1; i <= 100; i++) {
            if (!existingIds.contains(i)) {
                return String.valueOf(i);
            }
        }
        return "1"; // 默认返回 1
    }
}
