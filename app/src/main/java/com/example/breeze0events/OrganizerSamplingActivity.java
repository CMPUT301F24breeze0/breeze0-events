package com.example.breeze0events;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class OrganizerSamplingActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private OverallStorageController overallStorageController;
    private ListView waitList;
    private ArrayAdapter<String> waitListAdapter;
    private ArrayList<String> waitList_display;
    public Event event;

    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_sampling_activity);

        backButton = findViewById(R.id.organizer_sampling_activity_back_button);

        backButton.setOnClickListener(v -> {finish();});

    }
}
