package com.example.breeze0events;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * AdminOrganizationProfileActivity class manages the UI and backend functionality for the
 * admin view of organizational profiles in the Breeze0Events application. This class displays
 * a list of organizers and allows the admin to view and edit specific organizer profiles.
 */
 public class AdminOrganizationProfileActivity extends AppCompatActivity {
    private OverallStorageController overallStorageController;
    public FirebaseFirestore db;
    public HashMap<String, String> organizerIdMap;
    private ArrayAdapter<String> organizerListAdapter;
    public ArrayList<String> organizerList;
    private ListView organizerListView;
    private final ActivityResultLauncher<Intent> profileDetailLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        organizerList = data.getStringArrayListExtra("UPDATED_LIST");
                        organizerListAdapter.clear();
                        organizerListAdapter.addAll(organizerList);
                        organizerListAdapter.notifyDataSetChanged();
                    }
                }
            });

    /**
     * this method is for gain the organizer from database and show it
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organization_profile_recycle);
        overallStorageController = new OverallStorageController();
        organizerListView = findViewById(R.id.organizer_list_view);
        organizerList = new ArrayList<>();
        organizerListAdapter = new ArrayAdapter<>(this, R.layout.list_item_layout, organizerList);
        organizerListView.setAdapter(organizerListAdapter);
        FirebaseFirestore db =FirebaseFirestore.getInstance();
        CollectionReference collectionRef=db.collection("OrganizerDB");
        organizerIdMap = new HashMap<>();

        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            /**
             * this method and gain change the organizer information
             * @param task
             */
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String docId = document.getId();
                        overallStorageController.getOrganizer(String.valueOf(docId), new OrganizerCallback() {
                            @Override
                            public void onSuccess(Organizer organizer) {

                                String organizerInfo = "Organizer: " + organizer.getOrganizerId();
                                organizerIdMap.put(organizerInfo,docId);
                                organizerList.add(organizerInfo);
                                organizerListAdapter.notifyDataSetChanged();
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

        Button back_button=findViewById(R.id.back_in_organizer_list);
        back_button.setOnClickListener(v->{
            finish();
        });
        organizerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            /**
             * this method is for transform data to next page
             * @param parent
             * @param view
             * @param position
             * @param id
             */
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String organizerInfo = organizerList.get(position);
                String organizerId=organizerIdMap.get(organizerInfo);
                Intent intent = new Intent(AdminOrganizationProfileActivity.this, AdminOrganizerProfile.class);
                intent.putExtra("SELECTED_POSITION", position);
                intent.putExtra("SELECTED_ID",organizerId);
                intent.putStringArrayListExtra("ORGANIZATION_LIST",organizerList);
                profileDetailLauncher.launch(intent);
            }
        });
    }


}