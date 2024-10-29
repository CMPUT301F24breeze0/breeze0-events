package com.example.breeze0events;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AdminOrganizationProfileActivity extends AppCompatActivity {
    private OverallStorageController overallStorageController;
    private ListView organizationListView;
    private ArrayAdapter<String> organizationListAdapter;
    private ArrayList<String> organizationList;    
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organization_profile_recycle);

    }
}
