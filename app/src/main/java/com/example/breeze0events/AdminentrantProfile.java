package com.example.breeze0events;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This class is for browse the profile and delete the profile
 *
 */

public class AdminentrantProfile extends AppCompatActivity {
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
        setContentView(R.layout.entrantprofiledetail);
        overallStorageController = new OverallStorageController();
        id=getIntent().getStringExtra("SELECTED_ID");
        position=getIntent().getIntExtra("SELECTED_POSITION",-1);
        entrantList=getIntent().getStringArrayListExtra("Entrant_LIST");
        TextView name=findViewById(R.id.setName);
        TextView email=findViewById(R.id.setEmail);
        ImageView image=findViewById(R.id.profileImage);

        overallStorageController.getEntrant(String.valueOf(id), new EntrantCallback() {
            @Override
            public void onSuccess(Entrant entrant) {
                //System.out.println(entrant.getEventsName());
                //eventidMap=entrant.getEventsName().entrySet().iterator().next();
                for (String value : entrant.getEventsName().keySet()) {
                    eventList.add(value);
                };
                name.setText(entrant.getName());
                email.setText(entrant.getEmail());
                try {
                    Bitmap decryptedBitmap = decodeBase64Image(entrant.getProfilePhoto());

                    image.setImageBitmap(decryptedBitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("ImageError", "Error decrypting and setting image: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("AdminOrganizerProfile", "Failed to fetch organizer: " + errorMessage);
            }
        });
        Button back_button=findViewById(R.id.back_in_profile_detail);
        String eventid;
        back_button.setOnClickListener(v->{
            finish();
        });
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Button delete_button=findViewById(R.id.delete_in_profile_detail);
        delete_button.setOnClickListener(v->{
            if(!eventList.isEmpty()) {
                for (int i = 0; i < eventList.size(); i++) {
                    System.out.println(id);
                    overallStorageController.getEvent(String.valueOf(eventList.get(i)), new EventCallback() {
                        @Override
                        public void onSuccess(Event event) {
                            System.out.println(event.getEntrants());
                            overallStorageController.updateEvent(event);
                            overallStorageController.deleteEntrant(String.valueOf(id));
                            System.out.println(id);
                            Log.d("event", "envent sucessfully delete");
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Log.e("event", "Failed to fetch event: " + errorMessage);
                        }
                    });
                }
            }
            else{overallStorageController.deleteEntrant(String.valueOf(id));}

            entrantList.remove(position);
            Intent result=new Intent();
            result.putStringArrayListExtra("UPDATED_LIST",entrantList);
            setResult(RESULT_OK,result);
            finish();
        });
    }
    public static Bitmap decodeBase64Image(String base64ImageString) {
        byte[] imageBytes = Base64.decode(base64ImageString, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }
}
