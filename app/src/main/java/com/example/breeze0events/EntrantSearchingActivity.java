package com.example.breeze0events;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class EntrantSearchingActivity extends AppCompatActivity implements EntrantKeywordFilter.OnFragmentInteractionListener{
    private TextView search_title;
    private Button filter;
    private ListView event_search_List;
    private ArrayList<Event> dataList;
    SearchingAdapter eventAdapter;
    private String keyword = "";
    private String location = "";
    private Button refreshButton;
    private OverallStorageController overallStorageController;
    int limit = 20, semaphore = 0;
    @Override
    public void UpdateKeyword(String keyword, String location) {
        this.keyword = keyword;
        this.location = location;
        updateList();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_event_search);

        // Initialization
        search_title = findViewById(R.id.search_title);
        filter = findViewById(R.id.filter);
        event_search_List = findViewById(R.id.event_search_view);
        refreshButton = findViewById(R.id.refresh);
        overallStorageController = new OverallStorageController();
        dataList = new ArrayList<Event>();

        // ListView Adaption
        eventAdapter = new SearchingAdapter(this,  dataList);
        event_search_List.setAdapter(eventAdapter);
        updateList();

        // keyword_filter functionality
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EntrantKeywordFilter dialog = new EntrantKeywordFilter();
                dialog.show(getSupportFragmentManager(), "EntrantKeywordFilter");
            }
        });

        // refresh functionality
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateList();
            }
        });

        // event details functionality
        event_search_List.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), EntrantEventDetail.class);
                intent.putExtra("eventID", dataList.get(i).getEventId());
                startActivity(intent);
            }
        });
    }

    // Synchronize data by loading from database
    private void updateList(){
        if (semaphore != 0){
            Toast.makeText(this,"Loading data", Toast.LENGTH_SHORT).show();
            return;
        }
        dataList.clear();
        eventAdapter.notifyDataSetChanged();
        for (int i=0; i<=limit; i++){
            semaphore++;
            overallStorageController.getEvent(String.valueOf(i), new EventCallback() {
                @Override
                public void onSuccess(Event event) {
                    if (event.getName().toLowerCase().contains(keyword.toLowerCase()) &&
                                    event.getFacility().toLowerCase().contains(location.toLowerCase())){
                        dataList.add(event);
                        eventAdapter.notifyDataSetChanged();
                    }
                    semaphore--;
                }
                @Override
                public void onFailure(String errorMessage) {
                    semaphore--;
                    Log.e("EntrantSearchingActivity", "Error loading event: " + errorMessage);
                }
            });
        }
    }
}