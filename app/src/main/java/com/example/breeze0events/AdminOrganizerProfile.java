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

/**
 * AdminOrganizerProfile class provides a detailed view of a specific organizer profile within
 * the Breeze0Events application. This class allows the admin to view organizer information,
 * edit certain details, and delete the organizer and their associated events from the database.
 */
public class AdminOrganizerProfile extends AppCompatActivity {
    private ArrayList<String> organizerList;
    private List<String> eventList;
    private Organizer organizer;
    private String id;
    private int position;
    private OverallStorageController overallStorageController;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        organizerList=new ArrayList<>();
        eventList=new ArrayList<>();
        setContentView(R.layout.organization_profile_detail);
        overallStorageController = new OverallStorageController();
        id=getIntent().getStringExtra("SELECTED_ID");
        position=getIntent().getIntExtra("SELECTED_POSITION",-1);
        organizerList=getIntent().getStringArrayListExtra("ORGANIZATION_LIST");
        EditText name=findViewById(R.id.editName);
        TextView device=findViewById(R.id.device_content);

        overallStorageController.getOrganizer(String.valueOf(id), new OrganizerCallback() {
            @Override
            public void onSuccess(Organizer organizer) {
                Log.d("AdminOrganizerProfile", "Organizer data fetched successfully: " + organizer.getOrganizerId());
                eventList=organizer.getEvents();
                name.setText(organizer.getOrganizerId());
                device.setText(organizer.getDevice());
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("AdminOrganizerProfile", "Failed to fetch organizer: " + errorMessage);
            }
        });
        Button back_button=findViewById(R.id.back_in_profile_detail);
        back_button.setOnClickListener(v->{
            finish();
        });
        Button delete_button=findViewById(R.id.delete);
        delete_button.setOnClickListener(v->{
            String eventid;
            for(int i=0; i<eventList.size();i++){
                eventid=eventList.get(i);
                String finalEventid = eventid;
                System.out.println(eventid);
                overallStorageController.getEvent(String.valueOf(eventid),new EventCallback(){
                    @Override
                    /**
                     * to make change for the event
                     * @param event the event you want to find
                     */
                    public void onSuccess(Event event){
                        List<String> entrantList= new ArrayList<>();
                        entrantList=event.getEntrants();
                        for(int j=0;j<entrantList.size();j++){
                            overallStorageController.getEntrant(entrantList.get(j),new EntrantCallback(){
                                @Override
                                public void onSuccess(Entrant entrant){
                                    entrant.getEventsName().remove(finalEventid);
                                }
                                public void onFailure(String errorMessage) {
                                    Log.e("organizer", "Failed to fetch organizer: " + errorMessage);
                                }
                            });
                        }
                        overallStorageController.deleteEvent(finalEventid);
                    }
                    /**
                     * to show something if it can not find the event with the eventid
                     * @param errorMessage the message displayed when it can not find the event
                     */
                    public void onFailure(String errorMessage) {
                        Log.e("event", "Failed to fetch event: ");
                    }
                });
            }
            organizerList.remove(position);
            overallStorageController.deleteOrganizer(String.valueOf(id));
            Intent result=new Intent();
            result.putStringArrayListExtra("UPDATED_LIST",organizerList);
            setResult(RESULT_OK,result);
            finish();
        });
    }
}