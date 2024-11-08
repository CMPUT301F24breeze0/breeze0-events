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

public class AdminEntrantProfileActivity extends AppCompatActivity  {


        private OverallStorageController overallStorageController;
        private FirebaseFirestore db;
        private HashMap<String, String> entrantIdMap;
        private ArrayAdapter<String> entrantListAdapter;
        private ArrayList<String> entrantList;
        private ListView entrantListView;
        private final ActivityResultLauncher<Intent> profileDetailLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            entrantList = data.getStringArrayListExtra("UPDATED_LIST");
                            entrantListAdapter.clear();
                            entrantListAdapter.addAll(entrantList);
                            entrantListAdapter.notifyDataSetChanged();
                        }
                    }
                });
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.entrant_profile_list);
            overallStorageController = new OverallStorageController();
            entrantListView = findViewById(R.id.entrant_list_view);
            entrantList = new ArrayList<>();
            entrantListAdapter = new ArrayAdapter<>(this, R.layout.list_item_layout, entrantList);
            entrantListView.setAdapter(entrantListAdapter);
            FirebaseFirestore db =FirebaseFirestore.getInstance();
            CollectionReference collectionRef=db.collection("EntrantDB");
            entrantIdMap = new HashMap<>();
            collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.d("FirestoreDebug", "Query successful, documents fetched: " + task.getResult().size());
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("FirestoreDebug", "Document ID: " + document.getId());
                            String docId = document.getId();
                            System.out.println(docId);
                            overallStorageController.getEntrant(String.valueOf(docId), new EntrantCallback() {
                                @Override
                                public void onSuccess(Entrant entrant) {

                                    String entrantInfo = "Entrant: " + entrant.getEntrantId();
                                    entrantIdMap.put(entrantInfo,docId);
                                    System.out.println(entrantInfo);
                                    entrantList.add(entrantInfo);
                                    entrantListAdapter.notifyDataSetChanged();
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
                Intent intent1=new Intent(AdminEntrantProfileActivity.this,AdminOperateActivity.class);
                startActivity(intent1);
            });
            entrantListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String entrantInfo = entrantList.get(position);
                    String entrantId=entrantIdMap.get(entrantInfo);
                    Intent intent = new Intent(AdminEntrantProfileActivity.this, AdminentrantProfile.class);
                    intent.putExtra("SELECTED_POSITION", position);
                    intent.putExtra("SELECTED_ID",entrantId);
                    intent.putStringArrayListExtra("Entrant_LIST",entrantList);
                    profileDetailLauncher.launch(intent);
                }
            });
        }



}
