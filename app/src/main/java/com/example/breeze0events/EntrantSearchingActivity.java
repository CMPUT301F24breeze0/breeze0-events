package com.example.breeze0events;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class EntrantSearchingActivity extends AppCompatActivity implements EntrantKeywordFilter.OnFragmentInteractionListener{
    private TextView search_title;
    private Button filter;
    private ListView event_search_List;
    private ArrayList<String> dataList;
    ArrayAdapter<String> eventAdapter;
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private String keyword = null;
    private String location = null;
    @Override
    public void UpdateKeyword(String keyword, String location) {
        this.keyword = keyword;
        this.location = location;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_event_search);

        // Initialization
        search_title = findViewById(R.id.search_title);
        filter = findViewById(R.id.filter);
        event_search_List = findViewById(R.id.event_search_view);
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("OveralDB");
        dataList = new ArrayList<String>();

        // Synchronize local data from database
        // FIXME: 2024/10/27 Failed to connect the database with unkonwn reason
        eventsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String eventName = document.getString("name");
                        dataList.add(eventName);
                        Log.d("Firestore", "Event Name: " + eventName);
                    }
                    // 处理或展示事件名称的列表
                    updateUI();
                } else {
                    Log.w("Firestore", "Error getting documents.", task.getException());
                }
            }
        });

        // ListView Adaption
        eventAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        event_search_List.setAdapter(eventAdapter);

        // keyword_filter functionality
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EntrantKeywordFilter dialog = new EntrantKeywordFilter();
                dialog.show(getSupportFragmentManager(), "EntrantKeywordFilter");
            }
        });
    }
    private void updateUI(){

    }
}
