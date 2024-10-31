package com.example.breeze0events;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AdminOrganizerProfile extends AppCompatActivity {
    private ArrayList<String> organizerList;
   private Organizer organizer;
   private String id;
   private int position;
    private OverallStorageController overallStorageController;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        organizerList=new ArrayList<>();
        setContentView(R.layout.profile_detail);
        overallStorageController = new OverallStorageController();
        id=getIntent().getStringExtra("SELECTED_ID");
        position=getIntent().getIntExtra("SELECTED_POSITION",-1);
        organizerList=getIntent().getStringArrayListExtra("ORGANIZATION_LIST");
        EditText name=findViewById(R.id.editName);

        overallStorageController.getOrganizer(String.valueOf(id), new OrganizerCallback() {
            @Override
            public void onSuccess(Organizer organizer) {
                Log.d("AdminOrganizerProfile", "Organizer data fetched successfully: " + organizer.getOrganizerId());
                name.setText(organizer.getOrganizerId());
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("AdminOrganizerProfile", "Failed to fetch organizer: " + errorMessage);
            }
        });

        Button delete_button=findViewById(R.id.delete);
        delete_button.setOnClickListener(v->{
            organizerList.remove(position);
            Intent result=new Intent();
            result.putStringArrayListExtra("UPDATED_LIST",organizerList);
            setResult(RESULT_OK,result);
            finish();
        });
    }
}