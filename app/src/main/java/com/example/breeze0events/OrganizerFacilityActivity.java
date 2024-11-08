
package com.example.breeze0events;

import android.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.HashSet;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Activity for managing facilities in the organizer's view.
 * Allows organizers to view, add, edit, and delete facilities in the database.
 */
public class OrganizerFacilityActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private OverallStorageController overallStorageController;
    private ListView facilityListView;
    private ArrayAdapter<String> facilityListAdapter;
    private ArrayList<String> facilityList;
    private ArrayList<String> facilityIdList;
    private String newFacilityId;
    private int pos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_facility_activity);

        Button back_button = findViewById(R.id.organizer_facility_activity_back_button);
        Button new_facility_button = findViewById(R.id.new_facility_button);
        Button refresh_button = findViewById(R.id.organizer_facility_activity_refresh_button);

        overallStorageController = new OverallStorageController();
        facilityListView = findViewById(R.id.organizer_facility_list);
        facilityList = new ArrayList<>();
        facilityListAdapter = new ArrayAdapter<>(this, R.layout.list_item_layout, facilityList);
        facilityListView.setAdapter(facilityListAdapter);
        facilityIdList = new ArrayList<>();

        db = FirebaseFirestore.getInstance();
        overallStorageController = new OverallStorageController();

        loadFacilities();

        // by clicking "Back" button:
        back_button.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerFacilityActivity.this, OrganizerMyListActivity.class);
            startActivity(intent);
            finish();
        });

        // by clicking "New" button
        new_facility_button.setOnClickListener(v -> findSmallestAvailableId());

        // by clicking "Refresh" button
        refresh_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFacilities();
            }
        });

        // by long clicking anything on the list, the organizer can choose to delete or edit the facility
        facilityListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                pos = position;
                String selectedFacilityId = facilityIdList.get(pos);
                String selectedFacilityName = facilityList.get(pos);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OrganizerFacilityActivity.this);
                alertDialogBuilder.setTitle("Delete or Edit Facility");
                alertDialogBuilder.setMessage("Do you want to delete or edit this facility?");

                // cencel
                alertDialogBuilder.setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss());

                // edit facility
                alertDialogBuilder.setPositiveButton("Edit", (dialog, which) -> {
                    Intent editIntent = new Intent(OrganizerFacilityActivity.this, EditFacilityActivity.class);
                    editIntent.putExtra("facility_id", selectedFacilityId);  // Pass the facility ID
                    editIntent.putExtra("facility_name", selectedFacilityName);  // Pass the facility name
                    startActivity(editIntent);
                });

                // delete facility
                alertDialogBuilder.setNegativeButton("Delete", (dialog, which) -> {
                    overallStorageController.deleteFacility(selectedFacilityId);
                    facilityList.remove(pos);
                    facilityIdList.remove(pos);
                    facilityListAdapter.notifyDataSetChanged();
                    Toast.makeText(OrganizerFacilityActivity.this, selectedFacilityName + " has been deleted.", Toast.LENGTH_SHORT).show();
                });

                alertDialogBuilder.show();

                return true;
            }
        });

        /*
        facilityListView.setOnItemLongClickListener((parent, view, position,id) -> {
            String selectedFacilityId = facilityIdList.get(position);
            String selectedFacilityName = facilityList.get(position);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Delete or Edit Facility");
            alertDialogBuilder.setMessage("Do you want to delete or edit this facility?");
            alertDialogBuilder.setPositiveButton("Edit", (dialog, which) -> {
                // edit facility will be implemented later
                Toast.makeText(this, "Edit facility feature coming soon!", Toast.LENGTH_SHORT).show();
            });
            alertDialogBuilder.setNegativeButton("Delete", (dialog, which) -> {
                overallStorageController.deleteFacility(selectedFacilityId);
                facilityList.remove(position);
                facilityIdList.remove(position);
                facilityListAdapter.notifyDataSetChanged();
                Toast.makeText(this, selectedFacilityName + " has been deleted.", Toast.LENGTH_SHORT).show();
            });
            alertDialogBuilder.setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss());

            alertDialogBuilder.show();
            return true;
        });*/
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

    /**
     * Retrieves facility list data from FireStore and updates the ListView.
     */
    private void loadFacilities() {
        CollectionReference collectionRef = db.collection("FacilityDB");
        collectionRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                facilityList.clear();
                facilityIdList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String facilityInfo = "Facility: " + document.getString("location");
                    String facilityId = document.getId();

                    facilityList.add(facilityInfo);
                    facilityIdList.add(facilityId);
                }
                facilityListAdapter.notifyDataSetChanged();
            } else {
                Log.e("FirestoreError", "Error getting documents: ", task.getException());
            }
        });
    }

    /**
     * Finds the smallest available facility ID and opens the AddFacilityActivity.
     */
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

                    newFacilityId = findNextAvailableId(existingIds);
                    Log.d("OrganizerFacilityActivity", "Calculated new facility ID: " + newFacilityId);

                    Intent intent = new Intent(OrganizerFacilityActivity.this, AddFacilityActivity.class);
                    intent.putExtra("new_facility_id", newFacilityId);
                    startActivity(intent);
                } else {
                    Log.e("FirestoreError", "Error getting documents: ", task.getException());
                    newFacilityId = "1";
                }
            }
        });
    }

    /**
     * Finds the next available ID that is not used by any existing facility.
     * @param existingIds List of IDs currently used by facilities.
     * @return The next available ID as a string.
     */
    private String findNextAvailableId(ArrayList<Integer> existingIds) {
        for (int i = 1; i <= 100; i++) {
            if (!existingIds.contains(i)) {
                return String.valueOf(i);
            }
        }
        return "1";
    }
}
