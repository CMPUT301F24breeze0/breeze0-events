package com.example.breeze0events;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class AdminentrantProfile extends AppCompatActivity {
    /**
     * This class is for browse the profile and delete the profile
     */
    private ArrayList<String> entrantList;
    private ArrayList<String> eventList;
    private Map.Entry<String,String> eventidMap;
    private String id;
    private int position;
    private OverallStorageController overallStorageController;
    protected void onCreate(Bundle savedInstanceState){
        /**
         * In this function i gain the selected entrant's information from last page and display and
         * detail page, also there is a delete button which can click to delete the entrant
         * @param savedInstanceState
         */
        super.onCreate(savedInstanceState);
        entrantList=new ArrayList<>();
        eventList=new ArrayList<>();
        setContentView(R.layout.organization_profile_detail);
        overallStorageController = new OverallStorageController();
        id=getIntent().getStringExtra("SELECTED_ID");
        position=getIntent().getIntExtra("SELECTED_POSITION",-1);
        entrantList=getIntent().getStringArrayListExtra("Entrant_LIST");
        EditText name=findViewById(R.id.editName);
        TextView device=findViewById(R.id.device_content);

        overallStorageController.getEntrant(String.valueOf(id), new EntrantCallback() {
            @Override
            public void onSuccess(Entrant entrant) {
                //System.out.println(entrant.getEventsName());
                eventidMap=entrant.getEventsName().entrySet().iterator().next();
                for (String value : entrant.getEventsName().keySet()) {
                    eventList.add(value);
                };
                name.setText(entrant.getEntrantId());
                device.setText(entrant.getDevice());
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("AdminOrganizerProfile", "Failed to fetch organizer: " + errorMessage);
            }
        });
        Button back_button=findViewById(R.id.back_in_profile_detail);
        String eventid;
        back_button.setOnClickListener(v->{
            Intent intent1=new Intent(AdminentrantProfile.this,AdminEntrantProfileActivity.class);
            startActivity(intent1);
        });
        Button delete_button=findViewById(R.id.delete);
        delete_button.setOnClickListener(v->{
            for(int i=0;i<eventList.size();i++){
                //eventid=eventList.get(i);
                System.out.println(eventList.get(i));
                overallStorageController.getEvent(String.valueOf(eventList.get(i)),new EventCallback(){
                    @Override
                    public void onSuccess(Event event){
                        event.getEntrants().remove(id);
                        overallStorageController.deleteEntrant(String.valueOf(id));
                        Log.d("event","envent sucessfully delete");
                    }
                    @Override
                    public void onFailure(String errorMessage) {
                        Log.e("event", "Failed to fetch event: " + errorMessage);
                    }
                });
            }

            entrantList.remove(position);
            Intent result=new Intent();
            result.putStringArrayListExtra("UPDATED_LIST",entrantList);
            setResult(RESULT_OK,result);
            finish();
        });
    }
}
