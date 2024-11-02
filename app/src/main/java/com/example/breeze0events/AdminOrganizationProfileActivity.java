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

public class AdminOrganizationProfileActivity extends AppCompatActivity {
    private OverallStorageController overallStorageController;
    private FirebaseFirestore db;
    private HashMap<String, String> organizerIdMap;
    private ArrayAdapter<String> organizerListAdapter;
    private ArrayList<String> organizerList;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organization_profile_recycle);
        overallStorageController = new OverallStorageController();
        organizerListView = findViewById(R.id.organizerListVie);
        organizerList = new ArrayList<>();
        organizerListAdapter = new ArrayAdapter<>(this, R.layout.list_item_layout, organizerList);
        organizerListView.setAdapter(organizerListAdapter);
        FirebaseFirestore db =FirebaseFirestore.getInstance();
        CollectionReference collectionRef=db.collection("OrganizerDB");
        organizerIdMap = new HashMap<>();
        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
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
        Button back_button=findViewById(R.id.back_in_organ_list);
        back_button.setOnClickListener(v->{
            Intent intent1=new Intent(AdminOrganizationProfileActivity.this,AdminOperateActivity.class);
            startActivity(intent1);
        });
        organizerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
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