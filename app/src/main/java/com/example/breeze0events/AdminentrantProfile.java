package com.example.breeze0events;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class AdminentrantProfile extends AppCompatActivity {
    private ArrayList<String> entrantList;
    private List<String> eventList;
    private String id;
    private int position;
    private OverallStorageController overallStorageController;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        entrantList=new ArrayList<>();
        eventList=new ArrayList<>();
        setContentView(R.layout.organization_profile_detail);
        overallStorageController = new OverallStorageController();
        id=getIntent().getStringExtra("SELECTED_ID");
        position=getIntent().getIntExtra("SELECTED_POSITION",-1);
        entrantList=getIntent().getStringArrayListExtra("ENTRANT_LIST");
        EditText name=findViewById(R.id.editName);
        TextView device=findViewById(R.id.device_content);

        overallStorageController.getEntrant(String.valueOf(id), new EntrantCallback() {
            @Override
            public void onSuccess(Entrant entrant) {
                name.setText(entrant.getEntrantId());
                device.setText(entrant.getDevice());
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("AdminOrganizerProfile", "Failed to fetch organizer: " + errorMessage);
            }
        });
        Button back_button=findViewById(R.id.back_in_profile_detail);
        back_button.setOnClickListener(v->{
            Intent intent1=new Intent(AdminentrantProfile.this,AdminEntrantProfileActivity.class);
            startActivity(intent1);
        });
        Button delete_button=findViewById(R.id.delete);
        delete_button.setOnClickListener(v->{
            overallStorageController.deleteEntrant(String.valueOf(id));

            entrantList.remove(position);
            Intent result=new Intent();
            result.putStringArrayListExtra("UPDATED_LIST",entrantList);
            setResult(RESULT_OK,result);
            finish();
        });
    }
}
